import sbt._
import Keys._

import com.linkedin.plugin.NGPlugin
import com.linkedin.plugin.NGPlugin._

object ApplicationBuild extends Build {

    val appName         = "Sample"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "com.linkedin" %% "play-testng-helpers" % "2012.09.20.1886ca6-v5"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here
    )
    //.configs(NGTest)
    .settings(NGPlugin.ngSettings: _*)

}
