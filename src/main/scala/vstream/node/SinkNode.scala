package vstream.node

import vstream.core.{CountablePayload, Payload}

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
case class IgnoreSinkNode(nodeName: String) extends SinkNode with SingleInputNode {
  override def onReceive(payload: Payload): Unit = () // do nothing
}
case class TraceSinkNode(nodeName: String) extends SinkNode with SingleInputNode {
  override def onReceive(payload: Payload): Unit = println(payload)
}
case class SlowSink(nodeName: String, keepTime: Int = 3) extends SinkNode with SingleInputNode {
  override def receive(payload: Payload): Unit = {
    val wrapped = new CountablePayload {
      override val color: String = payload.color
    }
    super.receive(wrapped)
  }
  override def onReceive(payload: Payload): Unit = ()
  override def onAction(sequence: Int): Unit = {
    if (hasPayload) {
      val head = _queue.head.asInstanceOf[CountablePayload]
      if (head.count >= keepTime) dequeue()
      else {
        head.inc
        _queue.update(0, head)
      }
    }
    if (hasSpace) inputEdge.demand(sequence, this)
  }
}
trait SinkNodeLike {
  def onAction(sequence: Int): Unit
}
case class CompositeSinkNode(sinkNodes: Seq[SinkNode]) extends SinkNodeLike {
  override def onAction(sequence: Int): Unit = sinkNodes.foreach(_.onAction(sequence))
}
