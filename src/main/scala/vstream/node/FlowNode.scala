package vstream.node

import vstream.core.Payload

import scala.collection.mutable

trait FlowNode extends AnyRef with InputNode with OutputNode {
  override def onDemand(sequence: Int, demandant: InputNode): Unit = {
    outputEdge.flush()
    if (hasPayload && outputEdge.hasSpace) emit(dequeue())
    if (hasSpace) inputEdge.demand(sequence, this)
  }
}
case class ThroughNode(nodeId: Int) extends FlowNode with SingleInputNode with SingleOutputNode
case class FilterNode(nodeId: Int, filter: Payload => Boolean) extends FlowNode with SingleInputNode with SingleOutputNode {
  override def receive(payload: Payload): Unit = if (filter(payload)) enqueue(payload)
}
case class Broadcast(nodeId: Int) extends FlowNode with SingleInputNode with MultipleOutputNode {
  private var _queues: Map[InputNode, mutable.Queue[Payload]] = Map.empty
  private var _lastSequence: Int = 0
  def emitTo(payload: Payload, demandant: InputNode): Unit = outputEdge.edges(demandant).put(payload)
  def dequeueFrom(demandant: InputNode): Payload = _queues(demandant).dequeue()
  def hasPayloadIn(demandant: InputNode): Boolean = _queues.get(demandant).exists(_.nonEmpty)
  override def receive(payload: Payload): Unit =
    for ((_, _) <- _queues) enqueue(payload)
  override def onDemand(sequence: Int, demandant: InputNode): Unit = {
    flushEdge(demandant)
    if (hasPayloadIn(demandant)) emitTo(dequeueFrom(demandant), demandant)
    // TODO check outputEdge is having space.
    if (_lastSequence < sequence) {
      // if (hasPayload && outputEdge.hasSpace) emit(dequeue())
      if (hasSpace) inputEdge.demand(sequence, this)
      _lastSequence = sequence
    }
    if (hasPayload) _queues(demandant).enqueue(dequeue())
  }
  override def emit(payload: Payload): Unit = for (singleOutputEdge <- outputEdge.edges.values) {
    singleOutputEdge.put(payload)
  }
  override def connectTo(inputNode: SingleInputNode): MultipleOutputNode = {
    _queues = _queues + (inputNode -> mutable.Queue.empty)
    super.connectTo(inputNode)
  }
  override def queue: Seq[Payload] = _queues.values.head
}