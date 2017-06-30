name := "instagram-mqtt"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "0.9",
  "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "1.1.1"
)
