/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

import sbt._
import Keys._

object Build extends Build {

  val artifactVersion = "0.6.0-SNAPSHOT"

  val defaultSettings = Defaults.defaultSettings ++ Publish.settings ++ Seq(
    version := artifactVersion
  )

  lazy val root = Project(id = "babel",
    base = file("."),
    settings = defaultSettings
  ) aggregate(babelfish, babelcamelcore, babelcamelmock)

  lazy val babelfish = Project(id = "babel-fish",
    base = file("babel-fish"),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Dependencies.test
    )
  )

  lazy val camelVersion = SettingKey[String]("x-camel-version")

  lazy val babelcamelcore = Project(id = "babel-camel-core",
    base = file("babel-camel/babel-camel-core"),
    settings = defaultSettings ++ Dependencies.camelSettings ++ Dependencies.camelTestsSettings
  ) dependsOn (babelfish)

  lazy val babelcamelmock = Project(id = "babel-camel-mock",
    base = file("babel-camel/babel-camel-mock"),
    settings = defaultSettings ++ Dependencies.camelSettings,
    dependencies = Seq(babelcamelcore % "compile->compile;test->test")
  ) dependsOn(babelfish)

}


