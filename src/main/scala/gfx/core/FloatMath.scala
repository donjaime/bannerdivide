package gfx.core

object FloatMath {
  def radians(angle: Double): Float =
    math.toRadians(angle).toFloat

  def cos(angleInRadians: Double): Float =
    math.cos(angleInRadians).toFloat

  def sin(angleInRadians: Double): Float =
    math.sin(angleInRadians).toFloat

  def atan2(y: Double, x: Double): Float =
    math.atan2(y, x).toFloat

  def asin(angleInRadians: Double): Float =
    math.asin(angleInRadians).toFloat
}
