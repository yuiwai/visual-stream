package vstream.core

trait Payload {
  val color: String
}
trait Red extends Payload {
  override val color: String = "red"
}
trait Green extends Payload {
  override val color: String = "green"
}
trait Blue extends Payload {
  override val color: String = "blue"
}
case object SamplePayload1 extends Payload with Red
case object SamplePayload2 extends Payload with Green
case object SamplePayload3 extends Payload with Blue

trait WrappedPayload extends Payload
