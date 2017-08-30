package vstream.node

import vstream.core.Element

trait Node extends Element {
  override val queueSize = 5
}
