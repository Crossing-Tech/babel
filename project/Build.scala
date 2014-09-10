/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

import sbt._
import Keys._
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.osgi.{OsgiKeys, SbtOsgi}
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scoverage.ScoverageSbtPlugin.instrumentSettings
import org.scoverage.coveralls.CoverallsPlugin.coverallsSettings

object Build extends Build {

  val artifactVersion = "0.6.0-SNAPSHOT"

  lazy val basicSettings = Defaults.defaultSettings ++ Publish.settings ++ instrumentSettings ++ coverallsSettings   ++ Seq(
    version := artifactVersion
  )
  lazy val defaultSettings = basicSettings ++  formatSettings ++ SbtOsgi.osgiSettings

  lazy val root = Project(id = "babel",
    base = file("."),
    settings = basicSettings ++ Seq(
      publishArtifact in(Compile, packageBin) := false,
      publishArtifact in(Compile, packageSrc) := false,
      publishArtifact in(Compile, packageDoc) := false
    )
  ) aggregate(babelfish, babelcamelcore, babelcamelmock)

  lazy val babelfish = Project(id = "babel-fish",
    base = file("babel-fish"),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Dependencies.test,
      OsgiKeys.exportPackage := Seq("io.xtech.babel.fish.*")
    )
  )

  //allows you to define a camelVersion by prompting "set camelVersion=2.10.4" for example.
  lazy val camelVersion = SettingKey[String]("x-camel-version")

  lazy val babelcamelcore = Project(id = "babel-camel-core",
    base = file("babel-camel/babel-camel-core"),
    settings = defaultSettings ++ Dependencies.babelCamelCore++ Seq(
      publishArtifact in(Test, packageBin) := true
    )
  ).dependsOn(babelfish)


  lazy val babelcamelmock = Project(id = "babel-camel-mock",
    base = file("babel-camel/babel-camel-mock"),
    settings = defaultSettings ++ Dependencies.babelCamelMock,
    dependencies = Seq(babelcamelcore % "compile->compile;test->test")
  ).dependsOn(babelfish)



  //reformatting enforced for compile phase
  lazy val formatSettings = SbtScalariform.scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile := Formatting.formattingPreferences,
    ScalariformKeys.preferences in Test := Formatting.formattingPreferences
  )


}


