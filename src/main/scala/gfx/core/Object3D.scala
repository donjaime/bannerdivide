package gfx.core

import processing.core.PVector
import processing.core.PMatrix3D

class Object3D {
  var position: PVector = new PVector()
  val rotation: PVector = new PVector()
  val scale: PVector = new PVector(1, 1, 1)
  val screen: PVector = new PVector()
  val matrix: PMatrix3D = new PMatrix3D()
  var autoUpdateMatrix = true

  def updateMatrix() = {
    matrix.reset()
    matrix.translate(position.x, position.y, position.z)
    matrix.rotateX(rotation.x)
    matrix.rotateY(rotation.y)
    matrix.rotateZ(rotation.z)
    matrix.scale(scale.x, scale.y, scale.z)
  }
}
