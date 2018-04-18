package bannerdivide

import ddf.minim.Minim
import ddf.minim.AudioPlayer
import processing.core.{PVector, PImage}
import processing.core.PConstants._
import gfx.core.{ScalaSketch, Camera}
import gfx.core.FloatMath._
import gfx.core.PVectorOpsNonThreadSafe._
import gfx.geometry.BirdMesh
import gfx.fx.Boid

class BannerDivideSketch extends ScalaSketch {
  val numBirds = 350
  val fps = 40
  val birds = new Array[(BirdMesh, Boid, Boolean)](numBirds)

  // Eye position and the thing we are looking at.
  var cam: Camera = null
  var gazeTarget = new PVector()
  var birdCam: Boid = null
  var textPosition: PVector = null

  // Dimensions of box the Boids live in.
  val worldWidth = 500
  val worldHeight = 500
  val worldDepth = 400

  var canvasWidth = 0
  var canvasHeight = 0

  // Audio stuff
  val minim = new Minim(this)
  var song: AudioPlayer = null

  // Time to switch camera positions
  var toggleBirdCamTime: Int = -1

  // The banner
  var banner: PImage = null
  var halfWidth: Int = 0
  var halfHeight: Int = 0
  var quadWidth: Int = 0
  var quadHeight: Int = 0

  val columns = 20
  var divide = false
  var columnsDisplayed = 0
  val birdsPerCol = numBirds / columns

  override def setup() {
    if (!BannerDivide.FULLSCREEN) {
      frame.setResizable(true)
    }

    lights()
    frameRate(fps)
    smooth()
    noStroke()
    banner = loadImage("banner.jpg")
    banner.loadPixels()
    halfWidth = banner.width / 2
    halfHeight = banner.height / 2
    quadWidth = banner.width / columns
    quadHeight = banner.height / birdsPerCol
    song = minim.loadFile("bird-song.mp3")
    reset()
  }

  override def settings() {
    fullScreen(P3D)
    canvasHeight = displayHeight
    canvasWidth = displayWidth
  }

  def reset() {
    // Setup camera
    cam = new Camera(this, ((60 * math.Pi) / 180).toFloat, 1.6f, 1, 1000)
    cam.init()
    setBirdCam(false)
    toggleBirdCamTime = -1

    // Set text to be in bottom left of box at z=0 in 3D space as visible with
    // our perspective frustum.
    val h = 700 / (2 * math.tan(30 * math.Pi / 180))
    textPosition = new PVector(-(1.6 * h / 2).toFloat, (h / 2).toFloat, 0)

    // Reset cues for telling the banner to split in to birds
    divide = false
    columnsDisplayed = columns
    for (i <- 0 until numBirds) {
      birds(i) = makeBird()
    }
    // start music
    song.loop()
    sendBird()
  }

  /**
    * Send a single bird to start the show.
    **/
  def sendBird() {
    // Send a single boid to say hello.
    val birdTup = birds(0)
    val boid = birdTup._2
    boid.position.set(1075, 1050, 1550)
    boid.setGoal(new PVector(halfWidth - 30, 0, -20), () => {
      divide = true
      addToggleTime()
      boid.setGoal(null)
    })
    boid.velocity.set(-1, -1, -1)
    birdTup._1.material(0).fillColor = 0
    birds(0) = (birdTup._1, boid, true)
  }

  def makeBird(): (BirdMesh, Boid, Boolean) = {
    val boid = new Boid()
    boid.setAvoidWalls(true)
    boid.setWorldSize(worldHeight, worldWidth, worldDepth)

    val bird: BirdMesh = BirdMesh.makeABird(this)
    bird.phase = math.floor(math.random * 62.83)
    bird.position = boid.position
    // boolean in the tuple indicates whether or not the bird has been spawned
    (bird, boid, false)
  }

  override def draw() {
    // Clear the frame.
    background(109, 170, 255)
    drawGrid()
    drawBanner()

    if (birdCam != null) {
      cam.position = birdCam.position
      gazeTarget = birdCam.position + birdCam.velocity
    }

    cam.lookAt(gazeTarget.x, gazeTarget.y, gazeTarget.z)
    if (divide) maybeDivideBanner()
    renderBirds()
    maybeToggleBirdCam()
    displayKeyboardShortcuts()
  }

  override def stop() {
    song.close()
    minim.stop()
    super.stop()
  }

  def addToggleTime() {
    toggleBirdCamTime = millis() + (math.random * 5000).toInt + 5000
  }

  def maybeToggleBirdCam() {
    if (toggleBirdCamTime > 0 && toggleBirdCamTime < millis()) {
      setBirdCam(birdCam == null)
      addToggleTime()
    }
  }

  def setBirdCam(followBird: Boolean) {
    if (followBird) {
      // Follow a random bird with our gaze.
      val visibleBirds = getVisibleBirds()
      birdCam = visibleBirds((math.random * (visibleBirds.length - 1)).toInt)._2
    } else {
      // Reset camera to look at (0,0,0) from (0,0,600)
      cam.position = new PVector(0, 0, 600)
      gazeTarget = new PVector()
      birdCam = null
    }
  }

  def getVisibleBirds(): Array[(BirdMesh, Boid, Boolean)] = {
    birds.filter(tup => tup._3)
  }

  override def keyPressed() {
    // Bird cam mode.
    if (key == 'l') {
      if (birdCam != null) {
        setBirdCam(false)
      } else {
        setBirdCam(true)
      }
    } else if (key == 'r') {
      reset()
    }
  }

  def displayKeyboardShortcuts() {
    fill(0, 102, 153)
    text("r : reset", textPosition.x, textPosition.y, textPosition.z)
    text("l : toggle bird cam", textPosition.x, textPosition.y + 20, textPosition.z)
  }

  override def mouseMoved() {
    val v = new PVector(mouseX - (canvasWidth / 2),
      mouseY - (canvasHeight / 2), 0)
    getVisibleBirds().foreach { tup =>
      val b = tup._2
      v.z = b.position.z
      b.repulse(v)
    }
  }

  def renderBirds() {
    val visibleBirds = getVisibleBirds()
    val daBoids = for (tup <- visibleBirds) yield tup._2
    getVisibleBirds.foreach { tup =>
      val boid = tup._2
      boid.run(daBoids)

      val bird = tup._1
      bird.rotation.y = atan2(-boid.velocity.z, boid.velocity.x)
      bird.rotation.z = asin(boid.velocity.y / boid.velocity.mag())

      bird.phase = (bird.phase + math.max(0f, -bird.rotation.z) + 0.1) % 62.83

      pushMatrix()
      bird.updateMatrix()
      applyMatrix(bird.matrix)
      // Add depth fog. We are looking down the negative z axis.
      // All birds up to depth 150 are visible as black.
      // We then have fog up until -1000, where birds are invisible.
      val distance = cam.position.dist(bird.position)
      val depth = if (distance < 600) 0 else math.min(1000, distance - 200)
      fill(bird.material(0).fillColor, (255 * (1 - (depth / 1000))).asInstanceOf[Int])
      bird.applyGeometry()
      popMatrix()
    }
  }

  def drawGrid() {
    val cellSize = 40
    var cols = width * 4 / cellSize
    var rows = height * 4 / cellSize

    pushMatrix()

    translate(0, height / 4)
    rotateX(HALF_PI)

    for (i <- 0 until cols) {
      for (j <- 0 until rows) {
        val x = (i - cols / 2) * cellSize
        val y = (j - rows / 2) * cellSize
        noFill()
        stroke(255)
        rect(x, y, cellSize, cellSize)
      }
    }

    popMatrix()
  }

  /**
    * SUBDIVISION OF IMAGE BANNER.
    **/

  def getBirdColor(col: Int, row: Int): Int = {
    val start = ((row * quadHeight) * banner.width) + (col * quadWidth)
    banner.pixels(start)
  }

  def maybeDivideBanner() {
    if (columnsDisplayed > 0) {
      def getVelocity() = ((math.random * 2) - 1).toFloat
      // Spawn some new boids
      for (row <- 0 until birdsPerCol) {
        val i = (birdsPerCol * (columns - columnsDisplayed)) + row
        val tup = birds(i)
        val boid = tup._2
        val color = getBirdColor(columnsDisplayed, row)
        boid.position.set((columnsDisplayed * quadWidth) - halfWidth, (row * quadHeight) - halfHeight, 0)
        boid.velocity.set(getVelocity(), getVelocity(), getVelocity())
        tup._1.material(0).fillColor = color
        birds(i) = (tup._1, boid, true)
      }
      columnsDisplayed -= 1
    }
  }

  def drawBanner() {
    noStroke()
    textureMode(NORMAL)
    beginShape(QUADS)
    texture(banner)

    for (
      c <- 0 until columnsDisplayed;
      r <- 0 until birdsPerCol
    ) {
      drawBannerPiece(c, r)
    }

    endShape()
  }

  def drawBannerPiece(c: Float, r: Float) {
    val tl = (c, r)
    val tr = (c + 1, r)
    val bl = (c, r + 1)
    val br = (c + 1, r + 1)
    vertex((tl._1 * quadWidth) - halfWidth, (tl._2 * quadHeight) - halfHeight, 0, tl._1 / columns, tl._2 / birdsPerCol)
    vertex((bl._1 * quadWidth) - halfWidth, (bl._2 * quadHeight) - halfHeight, 0, bl._1 / columns, bl._2 / birdsPerCol)
    vertex((br._1 * quadWidth) - halfWidth, (br._2 * quadHeight) - halfHeight, 0, br._1 / columns, br._2 / birdsPerCol)
    vertex((tr._1 * quadWidth) - halfWidth, (tr._2 * quadHeight) - halfHeight, 0, tr._1 / columns, tr._2 / birdsPerCol)
  }
}
