package vstream.node

import vstream.core.Payload

trait FlowNode extends AnyRef with InputNode with OutputNode {
  override def onDemand(): Unit = {
    outputEdge.flush()
    if (hasPayload && outputEdge.hasSpace) emit(dequeue())
    if (hasSpace) inputEdge.demand()
  }
}
case class ThroughNode() extends FlowNode with SingleInputNode with SingleOutputNode
case class FilterNode(filter: Payload => Boolean) extends FlowNode with SingleInputNode with SingleOutputNode {
  override def receive(payload: Payload): Unit = if (filter(payload)) enqueue(payload)
}