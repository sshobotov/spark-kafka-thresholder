package cli

import java.text.ParseException

import scopt._
import dsl._
import cli.parsers._

object SettingsImplicits {

  implicit val stringToFilter = Read.reads[Filter] { str =>
    FilterParser(str) match {
      case Some(parsed) => parsed
      case _ => throw new IllegalArgumentException(s"Expected `$str` to be in format <field>:<value1>[,<valueN>]")
    }
  }

  implicit val thresholdRead = Read.reads[Threshold] { str =>
    ThresholdParser(str) match {
      case Some(parsed) => parsed
      case _ => throw new IllegalArgumentException(s"Expected `$str` to be in format <field>:<op1><value1>[,<opN><valueN>]")
    }
  }

}

case class Settings(appName: String = "", brokers: String = "", inputTopic: String = "", outputTopic: String = "",
                    filters: Seq[Filter] = Seq(), thresholds: Seq[Threshold] = Seq(), batchInterval: Int = 1)

object Settings {
  import SettingsImplicits._

  private val parser = new OptionParser[Settings]("") {
    opt[String]('n', "name") required() action { (value, settings) =>
      settings.copy(appName = value)
    } text "Application name to display in logs"

    opt[String]('b', "brokers") required() action { (value, settings) =>
      settings.copy(brokers = value)
    } text "Kafka brokers URIs"

    opt[String]('i', "input") required() action { (value, settings) =>
      settings.copy(inputTopic = value)
    } text "Kafka's topic with messages to check"

    opt[String]('o', "output") required() action { (value, settings) =>
      settings.copy(outputTopic = value)
    } text "Kafka's topic to put notifications to"

    opt[Seq[Filter]]('f', "filter") required() action { (value, settings) =>
      settings.copy(filters = value)
    } text "Message attributes to filter messages with, --filter=attr:value1,value2" unbounded()

    opt[Seq[Threshold]]('t', "threshold") required() action { (value, settings) =>
      settings.copy(thresholds = value)
    } text "Message attributes to check threshold with, --threshold=attr:>80" unbounded()

    opt[Int]('I', "interval") optional() action { (value, settings) =>
      settings.copy(batchInterval = value)
    } text "Interval in sec batches of data should be processed by Spark"
  }

  def parse(args: Seq[String]) = parser.parse(args, Settings())

}
