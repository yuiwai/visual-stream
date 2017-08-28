package vstream.node

import vstream.core.Payload
import vstream.edge._

trait InputNode extends Node {
  type InputEdgeType <: InputEdge
  def inputEdge: InputEdgeType
  def receive(payload: Payload): Unit = enqueue(payload)
}
trait SingleInputNode extends InputNode {
  override type InputEdgeType = SingleInputEdge
  protected var _inputEdge: InputEdgeType = UnconnectedSingleInputEdge
  override def inputEdge: SingleInputEdge = _inputEdge
  def connectFrom(singleOutputNode: SingleOutputNode): SingleInputNode = {
    _inputEdge = ConnectedSingleInputEdge(singleOutputNode)
    this
  }
}
trait MultipleInputNode extends InputNode {
  override type InputEdgeType = MultipleInputEdge
}
