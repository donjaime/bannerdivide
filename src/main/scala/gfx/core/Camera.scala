package gfx.core

import processing.core.PVector

/**
 * Perspective camera.
 */
class Camera(sketch: ScalaSketch, var fov: Float, aspect: Float, zNear: Float, zFar: Float) extends Object3D {

  def init() {
    sketch.perspective(fov, aspect, zNear, zFar)
  }

  def lookAt(target: PVector) {
    lookAt(target.x, target.y, target.z)
  }

  def lookAt(x: Float, y: Float, z: Float) {
    // Apply Camera position
    sketch.camera(position.x, position.y, position.z, x, y, z, 0, 1, 0)
  }
}
