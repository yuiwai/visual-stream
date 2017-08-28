package vstream.node

import vstream.core.{Element, Payload}

import scala.collection.mutable

trait Node extends Element {
  protected val _queue: mutable.Queue[Payload] = mutable.Queue.empty
  protected def enqueue(payload: Payload): Unit = _queue += payload
  protected def dequeue(): Payload = _queue.dequeue()
  def queue: Seq[Payload] = _queue
}

