package com.yuiwai.vstream

import utest._

object BoardTest extends TestSuite {
  val tests = Tests {
    "register" - {
      val (board, _) = Board().register(SilentNode)
      assert(board.size == 1)
    }
    "send" - {
      var i = 0
      Board({ case _: BoardEvent => i = i + 1 }).send(NodeId(1), 1)
      assert(i == 1)
    }
  }
}
