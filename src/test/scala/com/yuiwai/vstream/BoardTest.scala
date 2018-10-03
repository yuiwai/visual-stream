package com.yuiwai.vstream

import utest._

object BoardTest extends TestSuite {
  val tests = Tests {
    "register" - {
      val (board, _) = Board().register(SilentNode)
      assert(board.size == 1)
    }
    "send" - {
      "failed" - {
        var i = 0
        Board({
          case _: MessageSent => i = i + 1
          case _: MessageDeliveryFailed => i = i + 1
        })
          .send(NodeId(1)(SilentNode), 1)
        assert(i == 2)
      }
      "succeeded" - {
        var i = 0
        val pf: PartialFunction[BoardEvent, Unit] = {
          case _: MessageSent => i = i + 1
          case _: MessageDelivered => i = i + 1
          case _ => ()
        }
        Board[Int](pf)
          .register(ThroughNode[Int]()) match {
          case (board, nodeId) =>
            board.send(nodeId, 1)
        }
        assert(i == 2)
      }
    }
  }
}
