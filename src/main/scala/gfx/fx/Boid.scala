package gfx.fx

import processing.core.PVector;
import gfx.core.PVectorOpsNonThreadSafe._

/**
 * Boid simulation.
 * 
 * This is a Scala port of Mr. Doob's Javascript implementation of flocking
 * birds from the three.js project.
 * 
 * See:
 * https://github.com/mrdoob/three.js/blob/master/examples/canvas_geometry_birds.html
 */
class Boid {
  // Constants
  val reachAmount = 0.05f
  val accelScale = 5
  val flockingThresh = 0.5f
  val neighborThreshold = 0.6f
  val repulseDistance = 50

  // Publicly mutable State
  var position = new PVector()
  var velocity = new PVector()
  var acceleration = new PVector()

  // Internal State
  private var goal: PVector = null
  private var width = 500
  private var height = 500
  private var depth = 200
  private var neighborhoodRadius = 100
  private var maxSpeed = 4
  private var maxSteerForce = 0.1f
  private var avoidWalls = false

  private var goalReached: () => Unit = null

  // Setters for internal state
  def setGoal(target: PVector) = goal = target
  def setGoal(target: PVector, callback: () => Unit) = { goal = target; goalReached = callback }
  def setAvoidWalls(avoid: Boolean) = avoidWalls = avoid
  def setWorldSize(w: Int, h: Int, d: Int) = { width = w; height = h; depth = d; }

  // Temporary vector used for calculation without needing to
  // re-allocate a new vector each time.
  private var vector = new PVector()

  def run(boids: Array[Boid]) = {
    if (avoidWalls) {
      dodgeWall(-width, position.y, position.z)
      dodgeWall(width, position.y, position.z)
      dodgeWall(position.x, -height, position.z)
      dodgeWall(position.x, height, position.z)
      dodgeWall(position.x, position.y, -depth)
      dodgeWall(position.x, position.y, depth)
    }

    if (math.random > flockingThresh) {
      flock(boids)
    }

    if (goal != null) {
      acceleration.add(reach(goal, reachAmount))
      if (goalReached != null && position.dist(goal) < 20) goalReached()
    }

    move()
  }

  def dodgeWall(x: Float, y: Float, z: Float) = {
    vector.set(x, y, z)
    vector.set(avoid(vector))
    vector.mult(accelScale)
    acceleration.add(vector)
  }

  def avoid(target: PVector): PVector = {
    var steer = position - target
    steer.mult(1f / PVector.dot(steer, steer))
    steer
  }

  def flock(boids: Array[Boid]) = {
    val neighbors = gatherNeighboringBoids(boids)
    acceleration.add(alignment(neighbors))
    acceleration.add(cohesion(neighbors))
    acceleration.add(separation(neighbors))
  }

  def reach(target: PVector, amount: Float): PVector =
    (target - position) * amount

  def move() = {
    velocity.add(acceleration)
    val l = velocity.mag()
    if (l > maxSpeed) velocity.div(l / maxSpeed)
    position.add(velocity)
    acceleration.set(0, 0, 0)
  }

  def repulse(target: PVector) = {
    val distance = position.dist(target)
    if (distance < repulseDistance) {
      acceleration.add((position - target) * (0.5f / distance))
    }
  }

  def alignment(boids: Array[Boid]): PVector = {
    gatherBoidSum(boids, _.velocity, (count, posSum) => {
      normBoidSum(count, posSum, x => x)
    })
  }

  def cohesion(boids: Array[Boid]): PVector = {
    gatherBoidSum(boids, _.position, (count, posSum) => {
      normBoidSum(count, posSum, _ - position)
    })
  }

  def separation(boids: Array[Boid]): PVector = {
    gatherBoidSum(boids, (b: Boid) => {
      val repulse = position - b.position
      repulse.normalize()
      repulse / b.position.dist(position)
    }, (x, y) => y)
  }

  /**
   * Gather random neighboring boids within neighborThreshold.
   */
  def gatherNeighboringBoids(boids: Array[Boid]): Array[Boid] = {
    def isNeighbor(b: Boid, position: PVector) = {
      val d = b.position.dist(position);
      d > 0 && d <= neighborhoodRadius
    }

    boids.filter(math.random <= neighborThreshold && isNeighbor(_, position))
  }

  /**
   * Accumulate a summation of a specified PVector property on a Boid, and
   * adjust the result.
   */
  def gatherBoidSum(neighborBoids: Array[Boid], prop: Boid => PVector,
    adjust: (Int, PVector) => PVector): PVector = {
    val vectSum = new PVector()
    neighborBoids.foreach(b => vectSum.add(prop(b)))
    adjust(neighborBoids.length, vectSum)
  }

  /**
   * Given an accumulation over 'count' Boids, we normalize by the count and
   * then apply an adjustment function. Finally, we divide by the ratio of
   * the length of the vector to maxSteerForce if the length happens to be
   * larger than maxSteerForce.
   */
  def normBoidSum(count: Int, vectSum: PVector,
    adjust: PVector => PVector): PVector = {
    if (count > 0) vectSum.div(count)

    val adjusted = adjust(vectSum)
    val l = adjusted.mag()
    if (l > maxSteerForce) adjusted.div(l / maxSteerForce)
    adjusted
  }
}
