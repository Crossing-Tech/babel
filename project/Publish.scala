/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

import sbt.Keys._
import sbt._

object Publish {

  lazy val settings = Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := (<inceptionYear>2014</inceptionYear>
      <url>http://www.crossing-tech.com</url>
        <licenses>
          <license>
            <name>Apache 2</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>https://github.com/Crossing-Tech/babel.git</url>
          <connection>scm:git:git@github.com:Crossing-Tech/babel.git</connection>
        </scm>
        <developers>
          <developer>
            <id>chpache</id>
            <name>Christophe Pache</name>
            <url>https://twitter.com/chpache</url>
          </developer>
          <developer>
            <id>leifh</id>
            <name>Leif Hallgren</name>
            <url>https://twitter.com/lhallgren</url>
          </developer>
        </developers>)
  )
}