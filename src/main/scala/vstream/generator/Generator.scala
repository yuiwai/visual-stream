package vstream.generator

import vstream.core.Payload

trait Generator {
  def generate(): Payload
}
class SingleGenerator[P <: Payload](gen: () => P) extends Generator {
  override def generate(): Payload = gen()
}
object SingleGenerator {
  def apply[P <: Payload](gen: () => P): SingleGenerator[P] = new SingleGenerator[P](gen)
  def apply[P <: Payload](payload: P): SingleGenerator[P] = apply(() => payload)
}
class RotationGenerator(payloads: Seq[Payload]) extends Generator {
  require(payloads.nonEmpty)
  private var index = 0
  override def generate(): Payload = {
    if (payloads.size <= index) index = 0
    val result = payloads(index)
    index = index + 1
    result
  }
}
object RotationGenerator {
  def apply(payloads: Seq[Payload]): RotationGenerator = new RotationGenerator(payloads)
}
