package vstream.view

import vstream.core.{Context, Payload}
import vstream.edge.ConnectedSingleOutputEdge
import vstream.graph.Graph
import vstream.node.{MultipleOutputNode, Node, SingleOutputNode, SinkNode}

trait Renderer {
  val (w, h) = (100, 50)
  def render(implicit context: Context): Unit = {
    import context.ctx
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height)
    renderGraph(context.graph)
  }
  def renderGraph(graph: Graph)(implicit context: Context): Unit = {
    renderNode(graph.entryPoint, Offset(0, 0))
  }
  def renderNode(node: Node, offset: Offset)(implicit context: Context): Unit = {
    renderRect(offset)
    renderQueue(node.queue, offset)
    node match {
      case singleOutputNode: SingleOutputNode =>
        val outputEdge = singleOutputNode.outputEdge.asInstanceOf[ConnectedSingleOutputEdge]
        val targetNode = outputEdge.targetNode
        renderEdge(outputEdge, offset.move(100, 25))
        renderNode(targetNode, offset.move(150, 0))
      case multipleOutputNode: MultipleOutputNode =>
        val outputEdges = multipleOutputNode.outputEdge.edges.values.map(_.asInstanceOf[ConnectedSingleOutputEdge])
        outputEdges.zipWithIndex foreach { case (edge, i) =>
          val targetNode = edge.targetNode
          renderEdge(edge, offset.move(100, 25), (50, 75 * i))
          renderNode(targetNode, offset.move(150, 75 * i))
        }
      case _: SinkNode =>
    }
  }
  def renderEdge(outputEdge: ConnectedSingleOutputEdge, offset: Offset, to: (Int, Int) = (50, 0))(implicit context: Context): Unit = {
    import context.ctx
    import context.clock
    import offset._
    ctx.beginPath()
    ctx.moveTo(x, y)
    ctx.lineTo(x + to._1, y + to._2)
    ctx.closePath()
    ctx.stroke()
    if (outputEdge.hasPayload) {
      ctx.beginPath()
      ctx.fillStyle = outputEdge.queue.head.color
      ctx.arc(x + 50 * (clock.current.toFloat / clock.cycle), y + to._2 * (clock.current.toFloat / clock.cycle), 5, 0, Math.PI * 2)
      ctx.closePath()
      ctx.fill()
      ctx.stroke()
    }
  }
  def renderRect(offset: Offset)(implicit context: Context): Unit = {
    import context.ctx
    import offset._
    ctx.beginPath()
    ctx.moveTo(x, y)
    ctx.lineTo(x + w, y)
    ctx.lineTo(x + w, y + h)
    ctx.lineTo(x, y + h)
    ctx.closePath()
    ctx.stroke()
  }
  def renderQueue(queue: Seq[Payload], offset: Offset)(implicit context: Context): Unit = {
    import context.ctx
    import offset._
    queue.zipWithIndex.foreach { case (payload, i) =>
      ctx.beginPath()
      ctx.fillStyle = payload.color
      ctx.arc(x + (10 * (i + 1)), y + 35, 5, 0, Math.PI * 2)
      ctx.closePath()
      ctx.stroke()
      ctx.fill()
    }
  }
  case class Offset(x: Int, y: Int) {
    def move(xd: Int, yd: Int): Offset = copy(x + xd, y + yd)
  }
}
