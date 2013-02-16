package bannerdivide

import processing.core.PApplet
import java.awt.Frame
import java.awt.BorderLayout

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
