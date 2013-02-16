# Flocking Boid Simulation in Scala

This is an experiment in flocking boid simulations using (Processing)[https://code.google.com/p/processing/], OpenGL, and (Scala)[http://www.scala-lang.org/].

The code is inspired (and partially derived from) similar simulations written in Javascript and published by (Mr. Doob)[http://mrdoob.com/].

Also included are a set of implicit type convertions to make the Processing vector library work with regular mathematical operators, keeing correct operator precedence.

## Running The Prebuilt Binaries

To make this work cross platform, I bundled a build of JOGL, Processing, and an audio library called Minim. To setup these bundled dependencies, run the included `setup.sh` or manually extract `deps.tar` to the `lib` folder.

*Note: Unfortunately the snapshot of Processing I build requires Java 1.6 to run.* 

Since building scala with SBT is a PITA, and Oracle and Apple have made it near impossible to get a 1.6 JDK on OS X, I have included a simple pre-build of the app that gets setup when you run `setup.sh`.

### TLDR
Do this:
`setup.sh`

`show-birdies.sh`


## Building From Source

To build from source, you will need to have either (SBT)[http://www.scala-sbt.org/] installed, or Eclipse + the (Scala plugin for eclipse)[http://scala-ide.org/].

With SBT (and java 1.6!) you can compile and run the demo via `sbt run`. For Eclipse users I have bundled a sample `.project` and `.classpath` that you can load in the IDE.
