package com.yuiwai.vstream

import java.util.concurrent.atomic.AtomicInteger

case class Board[T] private(
  private val nodes: Map[NodeId, Node[_]],
  private val callback: PartialFunction[BoardEvent, Unit]) {
  private val i: AtomicInteger = new AtomicInteger()
  def size: Int = nodes.size
  def register(node: => Node[T]): (Board[T], NodeId) = NodeId(i.incrementAndGet()).pipe { id =>
    copy[T](nodes + (id -> node)) -> id
  }
  def send(nodeId: NodeId, message: T): Board[T] = this
}

object Board {
  private val noCallback: PartialFunction[BoardEvent, Unit] = {
    case _ => ()
  }
  def apply[T](): Board[T] = Board(Map.empty, noCallback)
}

sealed trait BoardEvent