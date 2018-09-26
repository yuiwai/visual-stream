package com.yuiwai.vstream

sealed trait Node[+T] {
  val input: Input[T]
  val output: Output[T]
}
case class NodeId(id: Int) extends AnyVal {
  def pipe[T](f: NodeId => T): T = f(this)
}

sealed trait Input[+T]
case object NoInput extends Input[Nothing]

sealed trait Output[+T]
case object NoOutput extends Output[Nothing]

final case class SourceNode[T](output: Output[T]) extends Node[T] {
  val input: Input[T] = NoInput
}
final case class FlowNode[T](input: Input[T], output: Output[T]) extends Node[T]
final case class SinkNode[T](input: Input[T]) extends Node[T] {
  val output: Output[T] = NoOutput
}
case object SilentNode extends Node[Nothing] {
  override val input: Input[Nothing] = NoInput
  override val output: Output[Nothing] = NoOutput
}
