import akka.stream.Materializer
import akka.stream.alpakka.mqtt.MqttConnectionSettings
import akka.stream.alpakka.mqtt.MqttQoS
import akka.stream.alpakka.mqtt.MqttSourceSettings
import akka.stream.alpakka.mqtt.scaladsl.MqttSource
import akka.stream.scaladsl.Keep
import akka.stream.scaladsl.Sink
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import akka.stream.alpakka.mqtt.MqttMessage
import akka.util.ByteString

final case class Alpakka(
  name: String,
  broker: String,
  topic: String,
  clientId: String,
  persistence: MemoryPersistence
) {
  def connect()(
   implicit materializer: Materializer
  ): Unit = {
    val connectionSettings = MqttConnectionSettings(
      broker,
      clientId,
      persistence
    )

    val messageCount = 7

    //#create-source
    val settings = MqttSourceSettings(
      connectionSettings.withClientId("source-spec/source"),
      Map(topic -> MqttQoS.AtLeastOnce)
    )

    val mqttSource = MqttSource(settings, bufferSize = 8)

    val (subscriptionFuture, result) = mqttSource
      .map(m => s"${m.topic}_${m.payload.utf8String}")
      .take(messageCount * 2)
      .toMat(Sink.seq)(Keep.both)
      .run()


    val messages = (0 until 10).flatMap { i =>
      println(i)
      Seq(
        MqttMessage(topic, ByteString(i.toString))
      )
    }
  }
}
