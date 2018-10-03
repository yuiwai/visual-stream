package com.yuiwai.vstream

import java.util.concurrent.atomic.AtomicInteger

import com.yuiwai.vstream.Board.Callback

case class Board[T] private(
  private val nodes: Map[NodeId[Node[T]], Node[T]],
  private val callback: Callback) {
  private val i: AtomicInteger = new AtomicInteger()
  def size: Int = nodes.size
  def register[N <: Node[T]](node: => N): (Board[T], NodeId[N]) = NodeId(i.incrementAndGet())(node).pipe { id =>
    callback(NodeAdded())
    copy[T](nodes + (id -> node)) -> id
  }
  def send[N <: Node[T]](nodeId: NodeId[N], message: T): Board[T] = {
    callback(MessageSent())
    nodes.get(nodeId) match {
      case Some(_) =>
        callback(MessageDelivered())
        this
      case None =>
        callback(MessageDeliveryFailed())
        this
    }
  }
}

object Board {
  type Callback = PartialFunction[BoardEvent, Unit]
  private val noCallback: Callback = {
    case _ => ()
  }
  def apply[T](): Board[T] = Board(Map.empty, noCallback)
  def apply[T](callback: Callback): Board[T] = Board(Map.empty, callback)
}

sealed trait BoardEvent
final case class NodeAdded() extends BoardEvent
final case class MessageSent() extends BoardEvent
final case class MessageDelivered() extends BoardEvent
final case class MessageDeliveryFailed() extends BoardEvent