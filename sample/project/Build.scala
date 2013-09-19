import sbt._
import Keys._

import com.linkedin.plugin.NGPlugin
import com.linkedin.plugin.NGPlugin._

object ApplicationBuild extends Build {

    val appName         = "Sample"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "com.linkedin" %% "play-testng-helpers" % "2.2.0-v1"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here
    )
    //.configs(NGTest)
    .settings(NGPlugin.ngSettings: _*)

}
