package vstream.node

import vstream.core.Element

trait Node extends Element {
  val nodeId: Int
  override val queueSize = 5
}
