package vstream.edge

import vstream.core.{Element, Payload}
import vstream.node.{InputNode, OutputNode}

trait Edge extends Element
trait InputEdge extends Edge {
  def demand(): Unit
}
trait SingleInputEdge extends InputEdge
case class ConnectedSingleInputEdge(fromNode: OutputNode) extends SingleInputEdge {
  override def demand(): Unit = fromNode.onDemand()
}
case object UnconnectedSingleInputEdge extends SingleInputEdge {
  def connectedFrom(fromNode: OutputNode): ConnectedSingleInputEdge = ConnectedSingleInputEdge(fromNode)
  override def demand(): Unit = sys.error("can't demand payload, edge is not connected.")
}
trait MultipleInputEdge extends InputEdge
case class OpenMultipleInputEdge() extends MultipleInputEdge {
  override def demand(): Unit = sys.error("can't demand payload, this contains unconnected edge.")
}
case class ClosedMultipleInputEdge() extends MultipleInputEdge {
  override def demand(): Unit = () // TODO demand to upstream
}
trait OutputEdge extends Edge {
  def flush(): Unit
}
trait SingleOutputEdge extends OutputEdge {
  def put(payload: Payload): Unit = enqueue(payload)
}
case class ConnectedSingleOutputEdge(targetNode: InputNode) extends SingleOutputEdge {
  override def flush(): Unit = if (hasPayload && targetNode.hasSpace) targetNode.receive(dequeue())
}
case object UnconnectedSingleOutputEdge extends SingleOutputEdge {
  override def flush(): Unit = sys.error("can't flush queue, edge is not connected.")
  override def put(payload: Payload): Unit = sys.error("can't send payload to downstream, edge is not connected.")
  def connectedTo(targetNode: InputNode): ConnectedSingleOutputEdge = ConnectedSingleOutputEdge(targetNode)
}
trait MultipleOutputEdge extends OutputEdge {
  def apply(index: Int): SingleOutputEdge
}
case class OpenMultipleOutputEdge(edges: Seq[SingleOutputEdge]) extends MultipleOutputEdge {
  override def flush(): Unit = ???
  override def apply(index: Int): SingleOutputEdge = sys.error("can't select output edge, this contains unconnected edge.")
}
case class ClosedMultipleOutputEdge(edges: Seq[ConnectedSingleOutputEdge]) extends MultipleOutputEdge {
  override def flush(): Unit = ???
  override def apply(index: Int): SingleOutputEdge = edges(index)
}

