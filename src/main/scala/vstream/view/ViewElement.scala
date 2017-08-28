package vstream.view

trait ViewElement
trait CompositeViewElement extends ViewElement
case class RootViewElement() extends CompositeViewElement

