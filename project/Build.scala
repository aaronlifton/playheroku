import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "playheroku"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "org.scalatest" %% "scalatest" % "1.9.1" % "test",
    "org.squeryl" %% "squeryl" % "0.9.5-6",
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    // "mysql" % "mysql-connector-java" % "5.1.18",
    "com.fasterxml.jackson.core" % "jackson-core" % "2.1.1",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.1",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.1.3"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    testOptions in Test := Nil    
  )

}
