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

  lazy val babelCamelMock = camelDependencies 

  lazy val babelCamelCore = camelDependencies ++ camelTestsDependencies

  private lazy val camelDependencies = Seq(
    Build.camelVersion := "2.12.4",
    version <<= (version, Build.camelVersion) { (babel,camel) => babel.replace("-SNAPSHOT","") +"-camel-" + camel  + {if (babel.endsWith("-SNAPSHOT")) "-SNAPSHOT" else ""}},
    libraryDependencies <++= (Build.camelVersion) { (dv) =>
      Dependencies.test ++ Dependencies.camel(dv) ++ Seq(Dependencies.commoncsv)
    }
  )

  private lazy val camelTestsDependencies = Seq(
    libraryDependencies <++= (Build.camelVersion) { (dv) =>
      Seq(Dependencies.cglib, Dependencies.h2, Dependencies.slf4j)
    }
  )

  private def camelCore(camelVersion: String) = "org.apache.camel" % "camel-core" % camelVersion
  private def camelXmlJson(camelVersion: String) = "org.apache.camel" % "camel-xmljson" % camelVersion % "test"
  private def camelCsv(camelVersion: String) = "org.apache.camel" % "camel-csv" % camelVersion % "test"
  private def camelSql(camelVersion: String) = "org.apache.camel" % "camel-sql" % camelVersion % "test"
  private def camelSpring(camelVersion: String) = "org.apache.camel" % "camel-spring" % camelVersion % "optional"
  private def camelScala(camelVersion: String) = "org.apache.camel" % "camel-scala" % camelVersion % "test"

  private def camel(camelVersion: String) = Seq(camelCore(camelVersion), camelXmlJson(camelVersion), camelCsv(camelVersion), camelSql(camelVersion), camelSpring(camelVersion), camelScala(camelVersion))

  private val commoncsv = "org.apache.servicemix.bundles" % "org.apache.servicemix.bundles.commons-csv" % "1.0-r706900_3" % "test"



  private val cglib = "cglib" % "cglib-nodep" % "2.2" % "test"
  private val h2 = "com.h2database" % "h2" % "1.3.174" % "test"


  private val specs2 = "org.specs2" %% "specs2" % "2.4.1" % "test"
  private val junit = "junit" % "junit" % "4.11" % "test"

  val test = Seq(junit, specs2)

  private val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.7.3"


}
