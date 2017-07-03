import scala.concurrent.ExecutionContext.Implicits.global

import akka.stream.Materializer
import akka.stream.alpakka.mqtt.MqttConnectionSettings
import akka.stream.alpakka.mqtt.MqttMessage
import akka.stream.alpakka.mqtt.MqttQoS
import akka.stream.alpakka.mqtt.MqttSourceSettings
import akka.stream.alpakka.mqtt.scaladsl.MqttSink
import akka.stream.alpakka.mqtt.scaladsl.MqttSource
import akka.stream.scaladsl.Keep
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.circe.parser.decode
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

final case class Alpakka(
  name: String,
  broker: String,
  topic: String,
  clientId: String,
  persistence: MemoryPersistence
) {

  def connectAndSubscribe(
   implicit materializer: Materializer
  ): Unit = {
    val settings = MqttSourceSettings(
      getConnectionSettings,
      Map(topic -> MqttQoS.AtLeastOnce)
    )

    val mqttSource = MqttSource(settings, bufferSize = 8)
    println(s"Connecting to $broker")

    val (subscriptionFuture, _) = mqttSource
      .toMat(Sink.foreach(x => processMessage(x.payload.utf8String)))(Keep.both)
      .run()

    subscriptionFuture.onComplete { _ =>
      println("Connected")
    }
  }

  private def processMessage(raw: String) = {
    val message = decode[Message](raw).toTry.get
    println(message.toString)
  }

  def publish(
    message: Message
  )(implicit materializer: Materializer
  ): Unit = {
    val mess = MqttMessage(
      topic,
      ByteString(Message.encoder.apply(message).noSpaces)
    )

    Source
      .single(mess)
      .runWith(MqttSink(getConnectionSettings.withClientId("sink"), MqttQoS.AtLeastOnce))
  }

  def getConnectionSettings: MqttConnectionSettings = {
    MqttConnectionSettings(broker, clientId, persistence)
  }
}
