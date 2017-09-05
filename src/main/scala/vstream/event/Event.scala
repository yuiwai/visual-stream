package vstream.event

sealed trait Event
trait NodeEvent extends Event
trait EdgeEvent extends Event
trait QueueEvent extends Event
trait PayloadEvent extends Event
