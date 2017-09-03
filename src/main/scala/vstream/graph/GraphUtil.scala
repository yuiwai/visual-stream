package vstream.graph

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
  /*
  case class WeightedEdge(connectedSingleOutputEdge: ConnectedSingleOutputEdge, weight: Int) extends SingleOutputEdge {
    override def flush(): Unit = ???
    override def put(payload: Payload): Unit = connectedSingleOutputEdge.put(payload)
  }
  implicit class WeightedEdgeWrapper(connectedSingleOutputEdge: ConnectedSingleOutputEdge) {
    def withWeight(weight: Int): WeightedEdge = WeightedEdge(connectedSingleOutputEdge, weight)
  }
  */
  trait SourceShapeLike {
    val sourceNode: SourceNode
    val outputNode: OutputNode
  }
  trait SourceShape extends SourceShapeLike {
    def -->(sinkNode: SinkNode): Graph = {
      val (_, _) = connect(outputNode, sinkNode)
      Graph(sourceNode, sinkNode)
    }
    def -->(sinkShape: SinkShape): Graph = {
      connect(outputNode, sinkShape.inputNode)
      Graph(sourceNode, sinkShape.sinkNode)
    }
    def -->(flowNode: FlowNode): SourceShape = {
      val (_, to) = connect(outputNode, flowNode)
      SourceShape(sourceNode, to)
    }
    def -->(sinkShapes: Seq[SinkShape]): Graph = outputNode match {
      case _: MultipleOutputNode =>
        sinkShapes.foreach { sinkShape =>
          connect(outputNode, sinkShape.inputNode)
        }
        Graph(sourceNode, CompositeSinkNode(sinkShapes.map(_.sinkNode)))
      case _ => sys.error("can't connect to multiple targets without MultipleOutputNode")
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
  object SinkShape {
    def apply(sink: SinkNode, in: InputNode): SinkShape = new SinkShape {
      override val sinkNode: SinkNode = sink
      override val inputNode: InputNode = in
    }
  }
  trait FlowShapeLike {
    val inputNode: InputNode
    val outputNode: OutputNode
  }
  trait FlowShape extends FlowShapeLike {
    def -->(sinkNode: SinkNode): SinkShape = {
      connect(outputNode, sinkNode)
      SinkShape(sinkNode, inputNode)
    }
    def -->(flowNode: FlowNode): FlowShape = {
      connect(outputNode, flowNode)
      FlowShape(inputNode, flowNode)
    }
  }
  object FlowShape {
    def apply(in: InputNode, out: OutputNode): FlowShape = new FlowShape {
      override val outputNode: OutputNode = out
      override val inputNode: InputNode = in
    }
  }
  implicit class SourceNodeWrapper(val sourceNode: SourceNode) extends SourceShape {
    val outputNode: OutputNode = sourceNode
  }
  implicit class FlowNodeWrapper(val flowNode: FlowNode) extends FlowShape {
    override val inputNode: InputNode = flowNode
    override val outputNode: OutputNode = flowNode
  }
}
