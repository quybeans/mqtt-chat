name := "instagram-mqtt"

version := "1.0"

scalaVersion := "2.12.2"

val circeVersion = "0.8.0"

libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "0.9",
  "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "1.1.1",

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
)
