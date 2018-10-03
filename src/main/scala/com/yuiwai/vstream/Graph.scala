package com.yuiwai.vstream

final case class Graph private(
  sources: Seq[SourceNode[_]],
  flows: Seq[FlowNode[_]],
  sinks: Seq[SinkNode[_]],
  edges: Seq[Edge[_]]
) {
  def size: Int = sources.size + flows.size + sinks.size
  def run(callback: PartialFunction[GraphEvent, Unit]): Graph = this
  def add[N <: Node[_]](nodeId: NodeId[N]): Graph = nodeId.node match {
    case n: SourceNode[_] => add(n)
    case n: FlowNode[_] => add(n)
    case n: SinkNode[_] => add(n)
    case _ => this
  }
  def add(sourceNode: SourceNode[_]): Graph = copy(sources = sources :+ sourceNode)
  def add(flowNode: FlowNode[_]): Graph = copy(flows = flows :+ flowNode)
  def add(sinkNode: SinkNode[_]): Graph = copy(sinks = sinks :+ sinkNode)
}
object Graph {
  def empty: Graph = Graph(Seq.empty, Seq.empty, Seq.empty, Seq.empty)
}

trait GraphEvent
