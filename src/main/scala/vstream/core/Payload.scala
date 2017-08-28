package vstream.core

trait Payload
case object DefaultPayload extends Payload
case object SamplePayload1 extends Payload
case object SamplePayload2 extends Payload
case object SamplePayload3 extends Payload

trait WrappedPayload extends Payload
