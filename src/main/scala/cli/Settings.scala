package cli

import java.net.URI
import org.backuity.clist._

import dsl._

object Settings extends Command {

  implicit val thresholdRead = Read.reads[Threshold[Double]] { str =>
    Threshold(attr = "some", predicates = Seq.empty)
  }

  var kafkaHost = arg[URI](name = "host", description = "Kafka host", required = true)
  var zkQuorum = arg[URI](name = "quorum", description = "ZooKeeper quorum", required = true)
  var groupId = opt[String](abbrev = "g", description = "Consumer group used by the application", default = "spark-thresholder")
  var inputTopic = arg[String](name = "input", description = "Kafka's topic with messages to check", required = true)
  var readingThreadsNum = opt[Int](abbrev = "n", description = "Number of consumer threads reading the data. If this is higher than the number of partitions in the Kafka topic, some threads will be idle.", default = 1)
  var outputTopic = arg[String](name = "output", description = "Kafka's topic to put notifications to", required = true)
  var thresholds = args[Seq[Threshold[Double]]](name = "threshold", description = "Message attributes to check threshold with, --threshold=value>80")
  var batchInterval = opt[Int](abbrev = "I", description = "Interval in sec batches of data should be processed by Spark", default = 1)

}
