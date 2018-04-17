#!/bin/sh
#
# Runs the boid sample. Expects a JAVA_HOME pointing at a 1.8 JRE.

JAVA=java

# On newer mac, you should set JAVA_HOME to be something like:
# /System/Library/Frameworks/JavaVM.framework/Versions/1.8/Home

if [ -n "$JAVA_HOME" ]
then
  JAVA=$JAVA_HOME/bin/java
fi

$JAVA -Djava.library.path=./lib -classpath ./target/scala-2.10/classes:./target/scala-2.10/classes/scalaboids_2.10-1.0.jar:./lib/core.jar:./lib/scala-library.jar:./lib/opengl.jar:./lib/gluegen-rt.jar:./lib/mp3spi1.9.4.jar:./lib/minim.jar:./lib/minim-spi.jar:./lib/jsminim.jar:./lib/jl1.0.jar:./lib/jogl-all.jar:./lib/tritonus_share.jar bannerdivide.BannerDivide
