package vstream

import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{CanvasRenderingContext2D, MouseEvent}
import vstream.core._
import vstream.generator.RotationGenerator
import vstream.graph.GraphUtil
import vstream.node._
import vstream.view.Renderer

import scala.scalajs.js.JSApp

object Main extends JSApp with Renderer with GraphUtil {
  override def main(): Unit = {
    val canvas: Canvas = dom.document.getElementById("canvas").asInstanceOf[Canvas]
    loop(initialize(canvas))
  }
  def initialize(canvas: Canvas): Context = {
    val graph = ManualSourceNode("ManualSource", RotationGenerator(Seq(SamplePayload1, SamplePayload2, SamplePayload3))) -->
      Broadcast("BroadCast") -->
      Seq(
        ThroughNode("Through1") --> TraceSinkNode("Sink1"),
        ThroughNode("Through2") --> SlowSink("SlowSink1"),
        FilterNode("Green Filter", _.isInstanceOf[SamplePayload2.type]) --> TraceSinkNode("Sink3")
      )

    // Input Handling
    // TODO coordinate position. println(canvas.offsetLeft, canvas.offsetTop)
    canvas.onmouseenter = (_: MouseEvent) => {}
    canvas.onmouseleave = (_: MouseEvent) => {}
    canvas.onmousedown = (_: MouseEvent) => {
      graph.entryPoint.asInstanceOf[ManualSourceNode].onAction()
    }
    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    Context(ctx, graph, CycleClock(20, 0), 0)
  }
  def update(context: Context): Context = {
    val newClock = context.clock.update()
    if (newClock.cleared) context.graph.endPoint.onAction(context.sequence)
    context.copy(clock = newClock, sequence = context.sequence + 1)
  }
  def loop(context: Context): Unit = {
    val newContext = update(context)
    render(newContext)
    dom.window.setTimeout(() => loop(newContext), 50)
  }
}
