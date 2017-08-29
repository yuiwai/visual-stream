package vstream.node

import vstream.core.Payload

trait FlowNode extends AnyRef with InputNode with OutputNode {
  override def onDemand(): Unit =
    if (hasPayload) emit(dequeue())
    else inputEdge.demand()
}
case class ThroughNode() extends FlowNode with SingleInputNode with SingleOutputNode
case class FilterNode(filter: Payload => Boolean) extends FlowNode with SingleInputNode with SingleOutputNode {
  override def receive(payload: Payload): Unit = if (filter(payload)) enqueue(payload)
}