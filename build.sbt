name := """gestor-recetas"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.10"

libraryDependencies += guice

enablePlugins(PlayEbean)
libraryDependencies += evolutions
libraryDependencies += jdbc

libraryDependencies += "com.h2database" % "h2" % "2.1.214"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.5"

libraryDependencies += "com.googlecode.json-simple" % "json-simple" % "1.1.1"

libraryDependencies ++= Seq(evolutions, jdbc)