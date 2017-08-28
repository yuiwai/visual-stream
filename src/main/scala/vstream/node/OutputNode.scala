package vstream.node

import vstream.core.Payload
import vstream.edge._

trait OutputNode extends Node {
  type OutputEdgeType <: OutputEdge
  def outputEdge: OutputEdgeType
  protected def emit(payload: Payload): Unit
  def onDemand(): Unit = if (queue.nonEmpty) {
    emit(dequeue())
  }
}
trait SingleOutputNode extends OutputNode {
  override type OutputEdgeType = SingleOutputEdge
  private var _outputEdge: OutputEdgeType = UnconnectedSingleOutputEdge
  override def outputEdge: SingleOutputEdge = _outputEdge
  override protected def emit(payload: Payload): Unit = outputEdge.send(payload)
  def connectTo(inputNode: SingleInputNode): SingleOutputNode = {
    _outputEdge = ConnectedSingleOutputEdge(inputNode)
    this
  }
}
trait MultipleOutputNode extends OutputNode {
  override type OutputEdgeType = MultipleOutputEdge
  override def emit(payload: Payload): Unit = outputEdge(select(payload)).send(payload)
  def select(payload: Payload): Int
}
