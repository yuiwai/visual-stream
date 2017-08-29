package vstream.core

import scala.collection.mutable

trait Element {
  protected val queueSize = 1
  protected val _queue: mutable.Queue[Payload] = mutable.Queue.empty
  protected def enqueue(payload: Payload): Unit = _queue += payload
  protected def dequeue(): Payload = _queue.dequeue()
  def queue: Seq[Payload] = _queue
  def hasPayload: Boolean = _queue.nonEmpty
  def hasSpace: Boolean = _queue.size < queueSize
}
