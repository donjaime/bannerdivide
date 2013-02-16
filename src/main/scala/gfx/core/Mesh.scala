package gfx.core

import processing.core.PStyle

class Mesh(val applyGeometry: () => Unit, val material: PStyle*)
  extends Object3D {
  var flipSided = false;
  var doubleSided = false;
  var overdraw = false;
}
