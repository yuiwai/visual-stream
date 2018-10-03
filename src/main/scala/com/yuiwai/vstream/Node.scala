package com.yuiwai.vstream

sealed trait Node[+T] {
  val input: Input[T]
  val output: Output[T]
}
case class NodeId[+N <: Node[_]](id: Int)(private[vstream] val node: N) {
  def pipe[T](f: NodeId[N] => T): T = f(this)
}

sealed trait Input[+T]
case object NoInput extends Input[Nothing]
case class SilentInput[T]() extends Input[T]

sealed trait Output[+T]
case object NoOutput extends Output[Nothing]
case class SilentOutput[T]() extends Output[T]

sealed trait ShapedNode
final case class SourceNode[T](output: Output[T]) extends Node[T] with ShapedNode {
  val input: Input[T] = NoInput
}
final case class FlowNode[T](input: Input[T], output: Output[T]) extends Node[T] with ShapedNode
final case class SinkNode[T](input: Input[T]) extends Node[T] with ShapedNode {
  val output: Output[T] = NoOutput
}
case object SilentNode extends Node[Nothing] {
  override val input: Input[Nothing] = NoInput
  override val output: Output[Nothing] = NoOutput
}
case class ThroughNode[T]() extends Node[T] {
  override val input: Input[T] = SilentInput[T]()
  override val output: Output[T] = SilentOutput[T]()
}
