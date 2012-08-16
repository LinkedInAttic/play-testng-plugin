import sbt._
import Keys._
import PlayProject._

import com.linkedin.plugin.NGPlugin
import com.linkedin.plugin.NGPlugin._

object ApplicationBuild extends Build {

    val appName         = "Sample"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "com.linkedin" %% "play-testng-helpers" % "2012.08.15.c4c3576"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
    )
    //.configs(NGTest)
    //.settings(NGPlugin.ngSettings: _*)

}
