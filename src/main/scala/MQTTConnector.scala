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
    val clientId = "ark.maxim"

    println(s"1/Paho ")
    println(s"2/Alpakka ")
    print(s"Select mqtt lib type: ")
    val input = scala.io.StdIn.readLine()
    input match {
      case "1" =>
        Paho(name, broker, topic, clientId, persistence).connect()
      case "2" =>
        Alpakka(name, broker, topic, clientId, persistence).connect()
    }
  }

  def connectUsingAlpakka(): Unit = {

  }


}
