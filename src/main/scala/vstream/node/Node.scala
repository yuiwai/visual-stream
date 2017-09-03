package vstream.node

import vstream.core.Element

trait Node extends Element {
  val nodeName: String
  override val queueSize = 5
}
