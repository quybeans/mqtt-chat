import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import io.circe.parser.decode

final case class Paho(
  broker: String,
  topic: String,
  clientId: String,
  persistence: MemoryPersistence
){
  def connect: MqttClient = {
    val client = new MqttClient(
      broker,
      clientId,
      persistence
    )

    println(s"Connecting to $broker")
    client.connect()
    println(s"Connected \n")
    client
  }

  def subscribe(implicit client: MqttClient): Unit = {
    client.subscribe(topic)
    val callback = new MqttCallback {
      override def messageArrived(topic: String, json: MqttMessage): Unit = {
        val message = decode[Message](json.toString).toTry.get
        println(s"\n${message.author}: ${message.message}")
      }

      override def connectionLost(cause: Throwable): Unit = {
        println(cause)
      }

      override def deliveryComplete(token: IMqttDeliveryToken): Unit = {
      }
    }
    client.setCallback(callback)
    println(s"Subscribed to $topic")
  }

  def publish(
    message: Message
  )(implicit client: MqttClient): Unit = {
    client.publish(
      topic,
      new MqttMessage(
        Message.encoder.apply(message).noSpaces.getBytes())
    )
  }

  def disconnect(implicit client: MqttClient): Unit = {
    client.disconnect()
    println("Disconnected")
  }
}
