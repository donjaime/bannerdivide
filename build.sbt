name := "scalaboids"

version := "1.0"

scalaVersion := "2.10.2"

fork in run := true

javaOptions in run += "-Djava.library.path=./lib"