package com.yuiwai.vstream
import utest._

object GraphTest extends TestSuite {
  val tests = Tests {
    "empty" - {
      assert(Graph.empty.size == 0)
    }
    "run" - {
      Graph.empty.run(Map.empty)
    }
    "edge" - {

    }
  }
}
