import sbt._
import Keys._
import play._

import com.linkedin.plugin.NGPlugin
import com.linkedin.plugin.NGPlugin._

object ApplicationBuild extends Build {

    val appName         = "Sample"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "com.linkedin" %% "play-testng-helpers" % "2.4.0"
    )

    val main = Project(appName, file("."))
      .enablePlugins(PlayJava)
      .settings(version := appVersion)
      .settings(libraryDependencies ++= appDependencies)
      //.configs(NGTest)
      .settings(NGPlugin.ngSettings: _*)
}
