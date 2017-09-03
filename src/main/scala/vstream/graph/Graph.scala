package vstream.graph

import vstream.node.{SinkNodeLike, SourceNode}

trait Graph {
  val entryPoint: SourceNode
  val endPoint: SinkNodeLike
}
object Graph {
  def apply(sourceNode: SourceNode, sinkNodeLike: SinkNodeLike): Graph = new Graph {
    override val entryPoint: SourceNode = sourceNode
    override val endPoint: SinkNodeLike = sinkNodeLike
  }
}
