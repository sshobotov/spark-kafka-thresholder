name := "spark-kafka-thresholder"

version := "0.0"

scalaVersion := "2.10.6"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.6.1" % "provided",
  "org.apache.spark" %% "spark-streaming" % "1.6.1" % "provided",
  "org.apache.spark" %% "spark-streaming-kafka" % "1.6.1" % "provided",
  "com.github.scopt" %% "scopt" % "3.4.0",
  "org.json4s" %% "json4s-native" % "3.3.0"
)

resolvers += Resolver.sonatypeRepo("public")
