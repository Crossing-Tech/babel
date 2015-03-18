/*
 *
 *  Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *  All rights reserved.
 *
 * ==================================================================================
 */

import com.typesafe.sbt.osgi.OsgiKeys
import sbt.Keys._
import sbt._

object Dependencies {

  lazy val babelFish = Seq(libraryDependencies ++= test, OsgiKeys.exportPackage := Seq("io.xtech.babel.fish.*"))

  lazy val babelCamelMock = camelDependencies ++ Seq(
    OsgiKeys.exportPackage := Seq("io.xtech.babel.camel.mock")
  )

  lazy val babelCamelCore = camelDependencies ++ camelTestsDependencies ++ Seq(
    OsgiKeys.exportPackage := Seq("io.xtech.babel.camel.*")
  )

  private[this] val fixedCamelVersion = "2.13.2"

  private[this] lazy val camelDependencies = Seq(
    Build.camelVersion := fixedCamelVersion,
    version  <<= (version, Build.camelVersion) { parseCamelVersion },
    //libraryDependencies ++= Seq("com.novocode" % "junit-interface" % "0.8" % "test->default"),
    libraryDependencies <++= (Build.camelVersion) { (dv) =>
     test ++ camel(dv) ++ Seq(commoncsv)
    }
  )

  private[this] def parseCamelVersion(babel: String, camel: String): String = {
    val camelVersion = if (camel != fixedCamelVersion) {
      "-camel-" + camel // todo for next version : .split("\\.").take(2).mkString(".")
    }else{
    ""
    }
    babel.replace("-SNAPSHOT","") + camelVersion + {if (babel.endsWith("-SNAPSHOT")) {"-SNAPSHOT"} else {""}}
  }

  private[this] lazy val camelTestsDependencies = Seq(
    libraryDependencies <++= (Build.camelVersion) { (dv) =>
      Seq(cglib, h2, slf4j)
    }
  )

  private[this] val camelCore = (camelVersion: String) => "org.apache.camel" % "camel-core" % camelVersion
  private[this] val camelXmlJson = (camelVersion: String) => "org.apache.camel" % "camel-xmljson" % camelVersion % "test"
  private[this] val camelCsv = (camelVersion: String) => "org.apache.camel" % "camel-csv" % camelVersion % "test"
  private[this] val camelSql = (camelVersion: String) => "org.apache.camel" % "camel-sql" % camelVersion % "test"
  private[this] val camelSpring = (camelVersion: String) => "org.apache.camel" % "camel-spring" % camelVersion % "optional"
  private[this] val camelScala = (camelVersion: String) => "org.apache.camel" % "camel-scala" % camelVersion % "optional"
  private[this] val camelTest = (camelVersion: String) => "org.apache.camel" % "camel-test" % camelVersion % "test"

  private[this] def camel(camelVersion: String) = Seq(camelCore, camelXmlJson, camelCsv, camelSql, camelSpring, camelScala, camelTest).map(x => (x(camelVersion)))

  private[this] val commoncsv = "org.apache.servicemix.bundles" % "org.apache.servicemix.bundles.commons-csv" % "1.0-r706900_3" % "test"



  private[this] val cglib = "cglib" % "cglib-nodep" % "2.2" % "test"
  private[this] val h2 = "com.h2database" % "h2" % "1.3.174" % "test"


  private[this] val specs2 = "org.specs2" %% "specs2" % "2.4.1" % "test"
  private[this] val junit = "junit" % "junit" % "4.11" % "test"

  private[this] val test = Seq(junit, specs2)

  private[this] val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.7.3"


}
