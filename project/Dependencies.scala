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


  private val testScope = "test"
  private val optionalScope = "optional"
  private val camelModulePrefix = s"camel"
  private val camelGroupId = "org.apache.camel"

  lazy val babelFish = Seq(libraryDependencies ++= test, OsgiKeys.exportPackage := Seq("io.xtech.babel.fish.*"))

  lazy val babelCamelMock = camelDependencies ++ Seq(
    OsgiKeys.exportPackage := Seq("io.xtech.babel.camel.mock")
  )

  lazy val babelCamelCore = camelDependencies ++ camelTestsDependencies ++ Seq(
    OsgiKeys.exportPackage := Seq("io.xtech.babel.camel.*")
  )

  private[this] val fixedCamelVersion = "2.15.0"

  private[this] lazy val camelDependencies = Seq(
    Build.camelVersion := fixedCamelVersion,
    version  <<= (version, Build.camelVersion) { parseCamelVersion },
    //libraryDependencies ++= Seq("com.novocode" % "junit-interface" % "0.8" % "test->default"),
    libraryDependencies <++= (Build.camelVersion) { (dv) =>
     test ++ camel(dv) ++ Seq(csvForTest, xomForTest)
    }
  )

  private[this] def parseCamelVersion(babel: String, camel: String): String = {
    val camelVersion = if (camel != fixedCamelVersion) {
      "-camel-" + camel.split("\\.").take(2).mkString(".")
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

  private[this] val camelCore = (camelVersion: String) => camelGroupId% s"$camelModulePrefix-core" % camelVersion
  private[this] val camelXmlJson = (camelVersion: String) => camelGroupId% s"$camelModulePrefix-xmljson" % camelVersion % testScope
  private[this] val camelCsv = (camelVersion: String) => camelGroupId% s"$camelModulePrefix-csv" % camelVersion % testScope
  private[this] val camelSql = (camelVersion: String) => camelGroupId% s"$camelModulePrefix-sql" % camelVersion % testScope
  private[this] val camelSpring = (camelVersion: String) => camelGroupId% s"$camelModulePrefix-spring" % camelVersion % optionalScope
  private[this] val camelScala = (camelVersion: String) => camelGroupId% s"$camelModulePrefix-scala" % camelVersion % optionalScope
  private[this] val camelTest = (camelVersion: String) => camelGroupId% s"$camelModulePrefix-test" % camelVersion % testScope
  private[this] val csvForTest = "org.apache.commons" % "commons-csv" % "1.1" % testScope
  private[this] val xomForTest = "xom" % "xom" % "1.2.5" % testScope

  private[this] def camel(camelVersion: String) = Seq(camelCore, camelXmlJson, camelCsv, camelSql, camelSpring, camelScala, camelTest).map(x => (x(camelVersion)))

  private[this] val commoncsv = "org.apache.servicemix.bundles" % "org.apache.servicemix.bundles.commons-csv" % "1.0-r706900_3" % testScope



  private[this] val cglib = "cglib" % "cglib-nodep" % "2.2" % testScope
  private[this] val h2 = "com.h2database" % "h2" % "1.3.174" % testScope


  private[this] val specs2 = "org.specs2" %% "specs2" % "2.4.1" % testScope
  private[this] val junit = "junit" % "junit" % "4.11" % testScope

  private[this] val test = Seq(junit, specs2)

  private[this] val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.7.3"


}
