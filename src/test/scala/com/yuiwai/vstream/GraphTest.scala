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
    "add node" - {
      assert(Graph.empty.add(NodeId(1)(SourceNode(SilentOutput()))).size == 1)
      assert(Graph.empty.add(NodeId(1)(FlowNode(SilentInput(), SilentOutput()))).size == 1)
      assert(Graph.empty.add(NodeId(1)(SinkNode(SilentInput()))).size == 1)
    }
    "edge" - {
    }
  }
}
