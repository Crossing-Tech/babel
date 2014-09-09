/**
 * Created by babel on 9/9/14.
 */

import sbt._
import sbt.Keys._
object Dependencies {

  def camelCore(camelVersion: String) = "org.apache.camel" % "camel-core" % camelVersion
  def camelXmlJson(camelVersion: String) = "org.apache.camel" % "camel-xmljson" % camelVersion % "test"
  def camelCsv(camelVersion: String) = "org.apache.camel" % "camel-csv" % camelVersion % "test"
  def camelSql(camelVersion: String) = "org.apache.camel" % "camel-sql" % camelVersion % "test"
  def camelSpring(camelVersion: String) = "org.apache.camel" % "camel-spring" % camelVersion % "optional"
  def camelScala(camelVersion: String) = "org.apache.camel" % "camel-scala" % camelVersion % "test"

  def camel(camelVersion: String) = Seq(camelCore(camelVersion), camelXmlJson(camelVersion), camelCsv(camelVersion), camelSql(camelVersion), camelSpring(camelVersion), camelScala(camelVersion))

  val commoncsv = "org.apache.servicemix.bundles" % "org.apache.servicemix.bundles.commons-csv" % "1.0-r706900_3" % "test"



  val cglib = "cglib" % "cglib-nodep" % "2.2" % "test"
  val h2 = "com.h2database" % "h2" % "1.3.174" % "test"


  val specs2 = "org.specs2" %% "specs2" % "2.4.1" % "test"
  val junit = "junit" % "junit" % "4.11" % "test"

  val test = Seq(junit, specs2)

  val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.7.3"


}
