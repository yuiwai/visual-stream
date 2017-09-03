package vstream.core

case class CycleClock(cycle: Int, current: Int, cleared: Boolean = false) {
  lazy val progress: Float = current.toFloat / cycle
  def update(d: Int): CycleClock = {
    val newCurrent = current + d
    if (newCurrent >= cycle) copy(current = newCurrent % cycle, cleared = true)
    else copy(current = newCurrent, cleared = false)
  }
  def update(): CycleClock = update(1)
}
