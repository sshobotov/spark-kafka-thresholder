package cli

import java.net.URI
import org.backuity.clist._

import dsl._
import cli.parsers._

object SettingsImplicits {

  implicit val filterRead = Read.reads[Filter] { str =>
    FilterParser(str) match {
      case Some(parsed) => parsed
      case _ => throw ReadException(str, "in format <field>:<value1>[,<valueN>]")
    }
  }

  implicit val thresholdRead = Read.reads[Threshold] { str =>
    ThresholdParser(str) match {
      case Some(parsed) => parsed
      case _ => throw ReadException(str, "in format <field>:<op1><value1>[,<opN><valueN>]")
    }
  }

}

object Settings extends Command {
  import SettingsImplicits._

  var appName = arg[String](name = "name", description = "Application name to display in logs", required = true)
  var brokers = arg[String](name = "brokers", description = "Kafka brokers URIs", required = true)
  var inputTopic = arg[String](name = "input", description = "Kafka's topic with messages to check", required = true)
  var outputTopic = arg[String](name = "output", description = "Kafka's topic to put notifications to", required = true)
  var filters = args[Seq[Filter]](name = "filter", description = "Message attributes to filter messages with, --filter=attr:value1,value2")
  var thresholds = args[Seq[Threshold]](name = "threshold", description = "Message attributes to check threshold with, --threshold=attr:>80")
  var batchInterval = opt[Int](abbrev = "I", description = "Interval in sec batches of data should be processed by Spark", default = 1)

}
