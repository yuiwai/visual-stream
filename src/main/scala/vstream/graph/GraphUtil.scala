package vstream.graph

import vstream.core.Payload
import vstream.edge.{ConnectedSingleOutputEdge, SingleOutputEdge}
import vstream.node._

trait GraphUtil {
  def connect[FROM <: OutputNode, TO <: InputNode](from: FROM, to: TO): (FROM, TO) = (from, to) match {
    case (f: SingleOutputNode, t: SingleInputNode) =>
      val newFromNode = f.connectTo(t)
      (newFromNode.asInstanceOf[FROM], t.connectFrom(newFromNode).asInstanceOf[TO])
    case (f: MultipleOutputNode, t: SingleInputNode) =>
      val newFromNode = f.connectTo(t)
      (newFromNode.asInstanceOf[FROM], t.connectFrom(newFromNode).asInstanceOf[TO])
  }
  case class WeightedEdge(connectedSingleOutputEdge: ConnectedSingleOutputEdge, weight: Int) extends SingleOutputEdge {
    override def flush(): Unit = ???
    override def put(payload: Payload): Unit = connectedSingleOutputEdge.put(payload)
  }
  implicit class WeightedEdgeWrapper(connectedSingleOutputEdge: ConnectedSingleOutputEdge) {
    def withWeight(weight: Int): WeightedEdge = WeightedEdge(connectedSingleOutputEdge, weight)
  }
  trait SourceShapeLike {
    val sourceNode: SourceNode
    val outputNode: OutputNode
  }
  trait SourceShape extends SourceShapeLike {
    def -->(sinkNode: SinkNode): Graph = {
      val (_, _) = connect(outputNode, sinkNode)
      Graph(sourceNode, sinkNode)
    }
    def -->(sinkShape: SinkShape): Graph = -->(sinkShape.sinkNode)
    def -->(flowNode: FlowNode): SourceShape = {
      val (_, to) = connect(outputNode, flowNode)
      SourceShape(sourceNode, to)
    }
  }
  object SourceShape {
    def apply(src: SourceNode, out: OutputNode): SourceShape = new SourceShape {
      val sourceNode: SourceNode = src
      val outputNode: OutputNode = out
    }
  }
  trait SinkShapeLike {
    val sinkNode: SinkNode
    val inputNode: InputNode
  }
  trait SinkShape extends SinkShapeLike
  implicit class SourceNodeWrapper(val sourceNode: SourceNode) extends SourceShape {
    val outputNode: OutputNode = sourceNode
  }
}
