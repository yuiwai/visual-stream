package vstream.node

import vstream.core.Payload

trait SinkNode extends InputNode with SinkNodeLike {
  def onAction(sequence: Int): Unit = {
    if (hasPayload) dequeue()
    if (hasSpace) inputEdge.demand(sequence, this)
  }
  def onReceive(payload: Payload): Unit
  override def receive(payload: Payload): Unit = {
    onReceive(payload)
    super.receive(payload)
  }
}
case class IgnoreSinkNode(nodeId: Int) extends SinkNode with SingleInputNode {
  override def onReceive(payload: Payload): Unit = () // do nothing
}
case class TraceSinkNode(nodeId: Int) extends SinkNode with SingleInputNode {
  override def onReceive(payload: Payload): Unit = println(payload)
}
trait SinkNodeLike {
  def onAction(sequence: Int): Unit
}
case class CompositeSinkNode(sinkNodes: Seq[SinkNode]) extends SinkNodeLike {
  override def onAction(sequence: Int): Unit = sinkNodes.foreach(_.onAction(sequence))
}
