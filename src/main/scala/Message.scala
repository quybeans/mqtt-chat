import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder

final case class Message (
  author: String,
  message: String,
  time: Long
)

object Message {
  implicit val decoder: Decoder[Message] = deriveDecoder
  implicit val encoder: Encoder[Message] = deriveEncoder
}
