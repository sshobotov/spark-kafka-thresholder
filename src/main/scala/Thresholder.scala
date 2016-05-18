import java.util.UUID
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.backuity.clist.Cli
import org.json4s._
import org.json4s.native.JsonMethods
import scala.collection.JavaConversions._

object Thresholder {

  def main(args: Array[String]): Unit = {
    Cli.parse(args).withCommand(cli.Settings) { case settings =>
      val conf = new SparkConf().setAppName("SparkKafkaThresholder")
      val ssc = new StreamingContext(conf, Seconds(settings.batchInterval))

      val input = KafkaUtils.createStream(ssc, settings.zkQuorum.toString, settings.groupId.getOrElse(UUID.randomUUID().toString),
        topics = Map(settings.inputTopic -> settings.readingThreadsNum))
      val output = outputProducer(settings.kafkaHost.toString)

      input
        .map(parseMessage)
        .filter(_.isDefined)
        .filter(_.get.value > 10)
        .foreachRDD(entries => {
          entries.foreach { case Some(entry) =>
            val message = s"At ${entry.timestamp} on ${entry.host} for ${entry.plugin} threshold ${} was "
            output.send(new ProducerRecord(settings.outputTopic, null, message))
          }
        })

      ssc.start()
      ssc.awaitTermination()
    }
  }

  def outputProducer(host: String) =
    new KafkaProducer[String, String](Map(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> host,
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringSerializer",
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringSerializer"
    ))

  def parseMessage(entry: (String, String)): Option[messages.Basic] = {
    implicit val formats = DefaultFormats

    val (_, value) = entry
    JsonMethods.parseOpt(value).flatMap(_.extractOpt[messages.Basic])
  }



}
