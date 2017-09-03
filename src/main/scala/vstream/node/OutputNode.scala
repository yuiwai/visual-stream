package vstream.node

import vstream.core.Payload
import vstream.edge._

trait OutputNode extends Node {
  type OutputEdgeType <: OutputEdge
  def outputEdge: OutputEdgeType
  protected def emit(payload: Payload): Unit
  def onDemand(sequence: Int, demandant: InputNode): Unit = {
    outputEdge.flush()
    if (hasPayload && outputEdge.hasSpace) {
      emit(dequeue())
    }
  }
}
trait SingleOutputNode extends OutputNode {
  override type OutputEdgeType = SingleOutputEdge
  private var _outputEdge: OutputEdgeType = UnconnectedSingleOutputEdge
  override def outputEdge: SingleOutputEdge = _outputEdge
  override protected def emit(payload: Payload): Unit = outputEdge.put(payload)
  def connectTo(inputNode: SingleInputNode): SingleOutputNode = {
    _outputEdge = ConnectedSingleOutputEdge(inputNode)
    this
  }
}
trait MultipleOutputNode extends OutputNode {
  override type OutputEdgeType = MultipleOutputEdge
  private var _outputEdge: OutputEdgeType = OpenMultipleOutputEdge(Map.empty)
  override def outputEdge: MultipleOutputEdge = _outputEdge
  override def emit(payload: Payload): Unit
  def connectTo(inputNode: SingleInputNode): MultipleOutputNode = {
    _outputEdge = _outputEdge.connectTo(inputNode)
    this
  }
  def flushEdge(inputNode: InputNode): Unit = {
    outputEdge.edges(inputNode).flush()
  }
}
