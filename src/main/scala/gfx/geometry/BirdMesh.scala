package gfx.geometry

import processing.core.PConstants._
import processing.core.PStyle
import gfx.core.Mesh
import gfx.core.FloatMath._
import gfx.core.ScalaSketch

class BirdMesh(applyGeometry: () => Unit, material: PStyle*)
  extends Mesh(applyGeometry, material: _*) {
  var phase = math.random * 62.83
}

object BirdMesh {
  def getMaterial(): PStyle = {
    val style = new PStyle()
    style.fillColor = (0xffffffff)
    style
  }

  def getGeometry(birdPhase: () => Double,
    sketch: ScalaSketch): () => Unit = {
    () => {
      sketch.beginShape(TRIANGLES)
      sketch.vertex(5, 0, 0)
      sketch.vertex(-5, 0, 0)
      sketch.vertex(-5, -2, 1)

      sketch.vertex(0, sin(birdPhase()) * 5, -6)
      sketch.vertex(-3, 0, 0)
      sketch.vertex(2, 0, 0)

      sketch.vertex(0, sin(birdPhase()) * 5, 6)
      sketch.vertex(2, 0, 0)
      sketch.vertex(-3, 0, 0)
      sketch.endShape()
    }
  }

  def makeABird(sketch: ScalaSketch): BirdMesh = {
    lazy val bird: BirdMesh = new BirdMesh(
      getGeometry(() => bird.phase, sketch), getMaterial())
    bird
  }
}
