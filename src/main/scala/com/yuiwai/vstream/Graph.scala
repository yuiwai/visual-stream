package com.yuiwai.vstream

final case class Graph private(
  sources: Seq[SourceNode[_]],
  flows: Seq[FlowNode[_]],
  sinks: Seq[SinkNode[_]],
  edges: Seq[Edge[_]]
) {
  def size: Int = sources.size + flows.size + sinks.size
  def run(callback: PartialFunction[GraphEvent, Unit]): Graph = this
}
object Graph {
  def empty: Graph = Graph(Seq.empty, Seq.empty, Seq.empty, Seq.empty)
}

trait GraphEvent
