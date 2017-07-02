import Console.GREEN
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

object MQTTConnector {

  def main(args: Array[String]): Unit = {
    implicit val actor = ActorSystem("haru")
    implicit val materializer = ActorMaterializer()

    print(s"Enter your name: ")
    val name = scala.io.StdIn.readLine()

    val topic = "/test/ark/"
    val broker = "tcp://test.mosquitto.org:1883"
    val persistence = new MemoryPersistence()

    println(s"1.Paho")
    println(s"2.Alpakka")
    print("Select mqtt lib type: ")
    val input = scala.io.StdIn.readLine()
    input match {
      case "1" =>
        usePaho(name ,broker, topic, name, persistence)
      case "2" =>
        Alpakka(name, broker, topic, name, persistence).connect()
    }
  }


  private def usePaho(
    name: String,
    broker: String,
    topic: String,
    clientId: String,
    persistence: MemoryPersistence
  ) = {
    val connector = Paho(broker, topic, clientId, persistence)

    // Connect & sub
    implicit val client = connector.connect
    connector.subscribe

    // Publish
    while(true) {
      print(GREEN + s"$name: ")
      val message = scala.io.StdIn.readLine()
      if (message == "exit")
        connector.disconnect
      else
        connector.publish(
          Message(name, message, System.currentTimeMillis() / 1000)
        )
    }
  }
}
