/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

import com.typesafe.sbt.osgi.OsgiKeys
import sbt._
import sbt.Keys._

object Dependencies {

  lazy val babelCamelMock = camelDependencies ++ Seq(
    OsgiKeys.exportPackage := Seq("io.xtech.babel.camel.mock")
  )

  lazy val babelCamelCore = camelDependencies ++ camelTestsDependencies ++ Seq(
    OsgiKeys.exportPackage := Seq("io.xtech.babel.camel.*")
  )

  private val fixedCamelVersion = "2.13.2"

  private lazy val camelDependencies = Seq(
    Build.camelVersion := fixedCamelVersion,
    version  <<= (version, Build.camelVersion) { parseCamelVersion },
    //libraryDependencies ++= Seq("com.novocode" % "junit-interface" % "0.8" % "test->default"),
    libraryDependencies <++= (Build.camelVersion) { (dv) =>
      Dependencies.test ++ Dependencies.camel(dv) ++ Seq(Dependencies.commoncsv)
    }
  )

  private def parseCamelVersion(babel: String, camel: String): String = {
    val camelVersion = if (camel != fixedCamelVersion) {
      "-camel-" + camel // todo for next version : .split("\\.").take(2).mkString(".")
    }else{
    ""
    }
    babel.replace("-SNAPSHOT","") + camelVersion + {if (babel.endsWith("-SNAPSHOT")) {"-SNAPSHOT"} else {""}}
  }

  private lazy val camelTestsDependencies = Seq(
    libraryDependencies <++= (Build.camelVersion) { (dv) =>
      Seq(Dependencies.cglib, Dependencies.h2, Dependencies.slf4j)
    }
  )

  private val camelCore = (camelVersion: String) => "org.apache.camel" % "camel-core" % camelVersion
  private val camelXmlJson = (camelVersion: String) => "org.apache.camel" % "camel-xmljson" % camelVersion % "test"
  private val camelCsv = (camelVersion: String) => "org.apache.camel" % "camel-csv" % camelVersion % "test"
  private val camelSql = (camelVersion: String) => "org.apache.camel" % "camel-sql" % camelVersion % "test"
  private val camelSpring = (camelVersion: String) => "org.apache.camel" % "camel-spring" % camelVersion % "optional"
  private val camelScala = (camelVersion: String) => "org.apache.camel" % "camel-scala" % camelVersion % "optional"
  private val camelTest = (camelVersion: String) => "org.apache.camel" % "camel-test" % camelVersion % "test"

  private def camel(camelVersion: String) = Seq(camelCore, camelXmlJson, camelCsv, camelSql, camelSpring, camelScala, camelTest).map(x => (x(camelVersion)))

  private val commoncsv = "org.apache.servicemix.bundles" % "org.apache.servicemix.bundles.commons-csv" % "1.0-r706900_3" % "test"



  private val cglib = "cglib" % "cglib-nodep" % "2.2" % "test"
  private val h2 = "com.h2database" % "h2" % "1.3.174" % "test"


  private val specs2 = "org.specs2" %% "specs2" % "2.4.1" % "test"
  private val junit = "junit" % "junit" % "4.11" % "test"

  val test = Seq(junit, specs2)

  private val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.7.3"


}
