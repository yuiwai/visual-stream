package vstream.node

import vstream.core.Payload
import vstream.edge.MultipleOutputEdge

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
case class Broadcast() extends FlowNode with SingleInputNode with MultipleOutputNode {
  override def emit(payload: Payload): Unit = for (singleOutputEdge <- outputEdge.edges) {
    singleOutputEdge.put(payload)
  }
  override def select(payload: Payload): Int = 0
  override def outputEdge: MultipleOutputEdge = ???
}