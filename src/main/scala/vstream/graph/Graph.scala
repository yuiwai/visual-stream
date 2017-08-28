package vstream.graph

import vstream.node.{SinkNode, SourceNode}

trait Graph {
  val entryPoint: SourceNode
  val endPoint: SinkNode
}
object Graph {
  def apply(sourceNode: SourceNode, sinkNode: SinkNode): Graph = new Graph {
    override val entryPoint: SourceNode = sourceNode
    override val endPoint: SinkNode = sinkNode
  }
}