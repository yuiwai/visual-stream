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

    Context(ctx, graph, CycleClock(20, 0))
  }
  def update(context: Context): Context = {
    val newClock = context.clock.update()
    if (newClock.cleared) context.graph.endPoint.onAction()
    context.copy(clock = newClock)
  }
  def loop(context: Context): Unit = {
    val newContext = update(context)
    render(newContext)
    dom.window.setTimeout(() => loop(newContext), 50)
    // dom.window.requestAnimationFrame((_:Double) => loop(context))
  }
}


