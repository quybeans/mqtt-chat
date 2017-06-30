import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

final case class Paho(
  name: String,
  broker: String,
  topic: String,
  clientId: String,
  persistence: MemoryPersistence
){
  def connect(): Unit = {
    val sampleClient = new MqttClient(
      broker,
      clientId,
      persistence
    )

    // Connecting
    println(s"Connecting to $broker")
    sampleClient.connect()
    println(s"Connected \n")

    // Subscribing
    sampleClient.subscribe(topic)
    val callback = new MqttCallback {
      override def messageArrived(topic: String, message: MqttMessage): Unit = {
        println(s"$message")
      }

      override def connectionLost(cause: Throwable): Unit = {
        println(cause)
      }

      override def deliveryComplete(token: IMqttDeliveryToken): Unit = {
      }
    }
    sampleClient.setCallback(callback)
    println(s"Subscribed to $topic")

    // Publishing message
    while (true) {
      print(s"$name: ")
      val content = scala.io.StdIn.readLine()
      sampleClient.publish(topic, new MqttMessage(s"$name: $content".getBytes()))
    }

    // Disconnecting
    sampleClient.disconnect()
    println(s"Disconnected")
  }
}
