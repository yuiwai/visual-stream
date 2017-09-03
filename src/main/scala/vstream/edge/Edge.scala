package vstream.edge

import vstream.core.{Element, Payload}
import vstream.node.{InputNode, OutputNode}

trait Edge extends Element
trait InputEdge extends Edge {
  def demand(sequence: Int, demandant: InputNode): Unit
}
trait SingleInputEdge extends InputEdge
case class ConnectedSingleInputEdge(fromNode: OutputNode) extends SingleInputEdge {
  override def demand(sequence: Int, demandant: InputNode): Unit = fromNode.onDemand(sequence, demandant)
}
case object UnconnectedSingleInputEdge extends SingleInputEdge {
  def connectedFrom(fromNode: OutputNode): ConnectedSingleInputEdge = ConnectedSingleInputEdge(fromNode)
  override def demand(sequence: Int, demandant: InputNode): Unit = sys.error("can't demand payload, edge is not connected.")
}
trait MultipleInputEdge extends InputEdge
case class OpenMultipleInputEdge() extends MultipleInputEdge {
  override def demand(sequence: Int, demandant: InputNode): Unit = sys.error("can't demand payload, this contains unconnected edge.")
}
case class ClosedMultipleInputEdge() extends MultipleInputEdge {
  override def demand(sequence: Int, demandant: InputNode): Unit = () // TODO demand to upstream
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
  val edges: Map[InputNode, SingleOutputEdge]
  def apply(inputNode: InputNode): SingleOutputEdge = edges(inputNode)
  def connectTo(inputNode: InputNode): MultipleOutputEdge
}
case class OpenMultipleOutputEdge(edges: Map[InputNode, SingleOutputEdge]) extends MultipleOutputEdge {
  override def flush(): Unit = sys.error("can't flush, this contains unconnected edge.")
  override def apply(inputNode: InputNode): SingleOutputEdge = sys.error("can't select output edge, this contains unconnected edge.")
  override def connectTo(inputNode: InputNode): MultipleOutputEdge =
    if (edges.nonEmpty) copy(edges + (inputNode -> ConnectedSingleOutputEdge(inputNode)))
    else ClosedMultipleOutputEdge(Map(inputNode -> ConnectedSingleOutputEdge(inputNode)))
}
case class ClosedMultipleOutputEdge(edges: Map[InputNode, ConnectedSingleOutputEdge]) extends MultipleOutputEdge {
  override def flush(): Unit = edges.values.foreach(_.flush())
  // override def apply(index: Int): SingleOutputEdge = edges(index)
  override def connectTo(inputNode: InputNode): MultipleOutputEdge = copy(edges + (inputNode -> ConnectedSingleOutputEdge(inputNode)))
}

