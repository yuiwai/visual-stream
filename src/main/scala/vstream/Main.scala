package vstream

import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{CanvasRenderingContext2D, MouseEvent}
import vstream.core._
import vstream.generator.RotationGenerator
import vstream.graph.GraphUtil
import vstream.node.{FilterNode, ManualSourceNode, ThroughNode, TraceSinkNode}
import vstream.view.Renderer

import scala.scalajs.js.JSApp

object Main extends JSApp with Renderer with GraphUtil {
  override def main(): Unit = {
    val canvas: Canvas = dom.document.getElementById("canvas").asInstanceOf[Canvas]
    loop(initialize(canvas))
  }
  def initialize(canvas: Canvas): Context = {
    val graph = (ManualSourceNode(RotationGenerator(Seq(SamplePayload1, SamplePayload2, SamplePayload3))) -->
      ThroughNode()) -->
      FilterNode(_.isInstanceOf[SamplePayload2.type]) -->
      TraceSinkNode()

    // Input Handling
    // TODO coordinate position. println(canvas.offsetLeft, canvas.offsetTop)
    canvas.onmouseenter = (_: MouseEvent) => {}
    canvas.onmouseleave = (_: MouseEvent) => {}
    canvas.onmousedown = (_: MouseEvent) => {
      graph.entryPoint.asInstanceOf[ManualSourceNode].onAction()
    }
    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    dom.window.setInterval(() => {
      graph.endPoint.onAction()
    }, 1000)

    Context(ctx, graph)
  }
  def update(context: Context): Context = context
  def loop(context: Context): Unit = {
    update(context)
    render(context)
    dom.window.setTimeout(() => loop(context), 100)
    // dom.window.requestAnimationFrame((_:Double) => loop(context))
  }
}


