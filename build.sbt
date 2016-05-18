name := "Spark-Kafka Tresholder"

version := "0.0"

scalaVersion := "2.11.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.6.1",
  "org.apache.spark" %% "spark-streaming" % "1.6.1",
  "org.apache.spark" %% "spark-streaming-kafka" % "1.6.1",
  "org.backuity.clist" %% "clist-core"   % "2.0.2",
  "org.backuity.clist" %% "clist-macros" % "2.0.2" % "provided",
  "org.json4s" %% "json4s-native" % "3.3.0"
)

