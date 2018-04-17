package gfx.core

import processing.core.PVector

/**
 * DSL for static arithmetic methods on PVector.
 */
class PVectorOps(var v1: PVector) {
  def +(v2: PVector): PVector = PVector.add(v1, v2)

  def -(v2: PVector): PVector = PVector.sub(v1, v2)

  //def *(v2: PVector): PVector = PVector.mult(v1, v2)

  def *(x: Float): PVector = PVector.mult(v1, x)

  def /(x: Float): PVector = PVector.div(v1, x)

  def rebind(v: PVector): PVectorOps = { v1 = v; this }
}

/**
 * CAVEAT: Results in extra 2 object allocations for each operand. Should be
 * fixed via escape analysis in newer 1.6 JVMs.
 */
object PVectorOps {
  implicit def PVectorToPVectorOps(vector: PVector): PVectorOps = {
    new PVectorOps(vector)
  }
}

/**
 * CAVEAT: Uses a pre-allocated object to avoid extra object allocations each
 * time we use the DSL. But, this is not thread safe!!!
 */
object PVectorOpsNonThreadSafe {
  private val v = new PVectorOps(new PVector())

  implicit def NonTheadSafePVectorToPVectorOps(vector: PVector): PVectorOps = {
    v.rebind(vector)
  }
}
