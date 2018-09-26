package com.yuiwai.vstream

import utest._

object BoardTest extends TestSuite {
  val tests = Tests {
    "register" - {
      val (board, _) = Board().register(SilentNode)
      assert(board.size == 1)
    }
    "send" - {
      Board().send(NodeId(1), 1)
    }
  }
}
