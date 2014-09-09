import sbt._
import Keys._

/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */


object Build extends Build {

  val artifactVersion = "0.6.0-SNAPSHOT"


  lazy val root = Project(id = "babel",
    base = file("."),
    settings = Defaults.defaultSettings ++ Publish.settings ++Seq(version := artifactVersion)
  ) aggregate(babelfish, babelcamelcore, babelcamelmock)

  lazy val babelfish = Project(id = "babel-fish",
    base = file("babel-fish"),
    settings = Defaults.defaultSettings ++ Publish.settings ++ Seq(
      version := artifactVersion,
      libraryDependencies ++= Dependencies.test
    )
  )

  //camelVersion allows you to use ``sbt "set camelVersion=2.10.4" test`` in order to test a specific version of camel
  lazy val camelVersion = SettingKey[String]("x-camel-version")

  lazy val babelcamelcore = Project(id = "babel-camel-core",
    base = file("babel-camel/babel-camel-core"),
    settings = Defaults.defaultSettings ++ Publish.settings ++ Seq(
      camelVersion := "2.12.4",
      version <<= camelVersion { dv => "camel-" + dv + "-" + artifactVersion },
      libraryDependencies <++= (camelVersion) { (dv) =>
        Dependencies.test ++ Dependencies.camel(dv) ++ Seq(Dependencies.cglib, Dependencies.h2, Dependencies.slf4j, Dependencies.commoncsv)
      }
    )) dependsOn (babelfish)

  lazy val babelcamelmock = Project(id = "babel-camel-mock",
    base = file("babel-camel/babel-camel-mock"),
    settings = Defaults.defaultSettings ++ Publish.settings ++ Seq(
      camelVersion := "2.12.4",
      version <<= camelVersion { dv => "camel-" + dv + "-" + artifactVersion },
      libraryDependencies <++= (camelVersion) { (dv) =>
        Dependencies.test ++ Dependencies.camel(dv) ++ Seq(Dependencies.commoncsv)
      }
    ),
    dependencies = Seq(babelcamelcore % "compile->compile;test->test")
  ) dependsOn(babelfish)

}


