package com.yuiwai.vstream

import java.util.concurrent.atomic.AtomicInteger

import com.yuiwai.vstream.Board.Callback

case class Board[T] private(
  private val nodes: Map[NodeId, Node[_]],
  private val callback: Callback) {
  private val i: AtomicInteger = new AtomicInteger()
  def size: Int = nodes.size
  def register(node: => Node[T]): (Board[T], NodeId) = NodeId(i.incrementAndGet()).pipe { id =>
    callback(NodeAdded())
    copy[T](nodes + (id -> node)) -> id
  }
  def send(nodeId: NodeId, message: T): Board[T] = {
    callback(MessageSent())
    this
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