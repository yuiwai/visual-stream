package vstream.node

import vstream.core.Payload

trait SinkNode extends InputNode {
  def onAction(): Unit =
    if (hasPayload) dequeue()
     else inputEdge.demand()
  def onReceive(payload: Payload): Unit
  override def receive(payload: Payload): Unit = {
    onReceive(payload)
    super.receive(payload)
  }
}
case class IgnoreSinkNode() extends SinkNode with SingleInputNode {
  override def onReceive(payload: Payload): Unit = () // do nothing
}
case class TraceSinkNode() extends SinkNode with SingleInputNode {
  override def onReceive(payload: Payload): Unit = println(payload)
}
