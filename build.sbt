
organization := "io.xtech.babel"

name := "babel"

version := "0.6.0-SNAPSHOT"

scalaVersion := "2.10.4"

resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "0.99.7.1")

addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "0.99.0")

crossScalaVersions := Seq("2.10.4", "2.11.2")
