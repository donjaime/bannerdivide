name := "scalaboids"

version := "1.0"

scalaVersion := "2.9.1"

fork in run := true

javaOptions in run += "-Djava.library.path=./lib"