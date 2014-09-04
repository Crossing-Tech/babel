import sbt._
import Keys._

object Build extends Build {

  val artifactVersion = "0.6.0-SNAPSHOT"

  lazy val root = Project(id = "babel",
    base = file("."),
    settings = Defaults.defaultSettings ++ Seq(version := artifactVersion)
  ) aggregate(babelfish, babelcamelcore, babelcamelmock)

  lazy val babelfish = Project(id = "babel-fish",
    base = file("babel-fish"),
    settings = Defaults.defaultSettings ++ Seq(
      version := artifactVersion,
      libraryDependencies ++= Dependencies.test
    )
  )

  lazy val camelVersion = SettingKey[String]("x-camel-version")

  lazy val babelcamelcore = Project(id = "babel-camel-core",
    base = file("babel-camel/babel-camel-core"),
    settings = Defaults.defaultSettings ++ Seq(
      camelVersion := "2.12.4",
      version <<= camelVersion { dv => "camel-" + dv + "-" + artifactVersion },
      libraryDependencies <++= (camelVersion) { (dv) =>
        Dependencies.test ++ Dependencies.camel(dv) ++ Seq(Dependencies.cglib, Dependencies.h2, Dependencies.slf4j, Dependencies.commoncsv)
      }
    )) dependsOn (babelfish)

  lazy val babelcamelmock = Project(id = "babel-camel-mock",
    base = file("babel-camel/babel-camel-mock"),
    settings = Defaults.defaultSettings ++ Seq(
      camelVersion := "2.12.4",
      version <<= camelVersion { dv => "camel-" + dv + "-" + artifactVersion },
      libraryDependencies <++= (camelVersion) { (dv) =>
        Dependencies.test ++ Dependencies.camel(dv) ++ Seq(Dependencies.commoncsv)
      }
    ),
    dependencies = Seq(babelcamelcore % "compile->compile;test->test")
  ) dependsOn(babelfish)

}

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
