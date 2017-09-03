package vstream.core

import org.scalajs.dom.raw.CanvasRenderingContext2D
import vstream.graph.Graph

case class Context(ctx: CanvasRenderingContext2D, graph: Graph, clock: CycleClock, sequence: Int)
