package cli

import java.net.URI
import org.backuity.clist._

import dsl._
import cli.parsers._

object SettingsImplicits {

  implicit val filterRead = Read.reads[Filter] { str =>
    FilterParser(str) getOrElse Filter("", Seq.empty)
  }

  implicit val thresholdRead = Read.reads[Threshold] { str =>
    ThresholdParser(str) getOrElse Threshold("", Seq.empty)
  }

}

object Settings extends Command {
  import SettingsImplicits._

  var kafkaHost = arg[URI](name = "host", description = "Kafka host", required = true)
  var zkQuorum = arg[URI](name = "quorum", description = "ZooKeeper quorum", required = true)
  var groupId = opt[Option[String]](abbrev = "g", description = "Consumer group used by the application")
  var inputTopic = arg[String](name = "input", description = "Kafka's topic with messages to check", required = true)
  var readingThreadsNum = opt[Int](abbrev = "n", description = "Number of consumer threads reading the data. If this is higher than the number of partitions in the Kafka topic, some threads will be idle.", default = 1)
  var outputTopic = arg[String](name = "output", description = "Kafka's topic to put notifications to", required = true)
  var filters = args[Seq[Filter]](name = "filter", description = "Message attributes to check threshold with, --threshold=value>80")
  var thresholds = args[Seq[Threshold]](name = "threshold", description = "Message attributes to check threshold with, --threshold=value>80")
  var batchInterval = opt[Int](abbrev = "I", description = "Interval in sec batches of data should be processed by Spark", default = 1)

}
