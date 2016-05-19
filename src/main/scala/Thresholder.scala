import java.util.Properties

import dsl.{PredicateExpr, Threshold, Filter}
import kafka.producer.{ProducerConfig, KeyedMessage, Producer}
import kafka.serializer.{StringEncoder, StringDecoder}
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.backuity.clist.Cli
import org.json4s._
import org.json4s.native.JsonMethods
import scala.language.implicitConversions

object Thresholder {

  implicit val formats = DefaultFormats

  def main(args: Array[String]): Unit = {
    Cli.parse(args).withCommand(cli.Settings) { case settings =>
      val conf = new SparkConf().setAppName(settings.appName)
      val ssc = new StreamingContext(conf, Seconds(settings.batchInterval))

      val input = inputStream(ssc, settings.brokers, settings.inputTopic)
      val output = outputProducer(settings.brokers)

      input
        .map(parseMessage)
        .filter(_.isDefined)
        .map(entry => ((entry.get \ "host").extract[String], entry.get))
        .groupByKey()
        .map { case (host, metrics) =>
          validateHostState(host, metrics, settings.filters, settings.thresholds)
        }
        .filter(_.nonEmpty)
        .foreachRDD(entries => {
          entries.foreach { alerts =>
            val outputMessages = alerts.map(new KeyedMessage[String, String](settings.outputTopic, _))
            output.send(outputMessages.toArray: _*)
          }
        })

      ssc.start()
      ssc.awaitTermination()
    }
  }

  def inputStream(ssc: StreamingContext, brokers: String, topic: String) =
    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, Map(
      "metadata.broker.list" -> brokers,
      "serializer.class" -> classOf[StringEncoder].getName,
      "auto.offset.reset" -> "smallest"
    ), topics = Set(topic))

  def outputProducer(brokers: String) =
    new Producer[String, String](new ProducerConfig(Map(
      "metadata.broker.list" -> brokers,
      "serializer.class" -> classOf[StringEncoder].getName
    )))

  def parseMessage(entry: (String, String)): Option[JValue] = {
    val (_, value) = entry
    JsonMethods.parseOpt(value)
  }

  def validateHostState(host: String, metrics: Iterable[JValue], filters: Seq[Filter], thresholds: Seq[Threshold]) = {
    metrics.filter(applyFilters(_, filters))
      .flatMap(applyThresholds(_, thresholds))
  }

  def applyFilters(metric: JValue, filters: Seq[Filter]) = {
    filters.exists(filter =>
      (metric \ filter.attr).extractOpt[String]
        .exists(attrValue => filter.values.contains(attrValue))
    )
  }

  def applyThresholds(metric: JValue, thresholds: Seq[Threshold]) = {
    thresholds.foldLeft(Seq.empty[String]) { (acc, threshold) =>
      (metric \ threshold.attr).extractOpt[Float] match {
        case Some(metricValue) => acc ++ threshold.predicates
          .filter(expr => expr.op(metricValue, expr.value))
          .map(buildMessage(metric, threshold.attr, _))
        case _ => acc
      }
    }
  }

  def buildMessage(metric: JValue, attr: String, failure: PredicateExpr) = {
    s"${JsonMethods.pretty(JsonMethods.render(metric))} failed with $attr at threshold ${failure.value}"
  }

  implicit def properties(properties: Map[String, String]): Properties =
    (new Properties /: properties) {
      case (a, (k, v)) =>
        a.put(k,v)
        a
    }

}
