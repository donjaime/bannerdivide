package bannerdivide

import processing.core.PApplet

object BannerDivide {
  val FULLSCREEN = true

  def main(args: Array[String]) = {
    val params = FULLSCREEN match {
      case true => Array[String]("--present", "bannerdivide.BannerDivideSketch")
      case false => Array[String]("bannerdivide.BannerDivideSketch")
    }
    PApplet.main(params)
  }
}
