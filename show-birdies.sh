#!/bin/sh
#
# Runs the boid sample. Expects a JAVA_HOME pointing at a 1.6 JRE.

JAVA=java

# On newer mac, you should set JAVA_HOME to be something like:
# /System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home

if [ -n "$JAVA_HOME" ]
then
  JAVA=$JAVA_HOME/bin/java
fi

$JAVA -Djava.library.path=./lib -classpath ./target/scala-2.9.1/classes:./lib/core.jar:./lib/scala-library.jar:./lib/opengl.jar:./lib/gluegen-rt.jar:./lib/mp3spi1.9.4.jar:./lib/minim.jar:./lib/minim-spi.jar:./lib/jsminim.jar:./lib/jl1.0.jar:./lib/jogl.jar:./lib/tritonus_share.jar bannerdivide.BannerDivide
