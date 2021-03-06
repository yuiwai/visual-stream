package vstream.node

import vstream.generator.Generator

trait SourceNode extends OutputNode {
  val generator: Generator
  def onAction(): Unit = if (hasSpace) enqueue(generator.generate())
}
case class ManualSourceNode(nodeName: String, generator: Generator) extends SourceNode with SingleOutputNode

