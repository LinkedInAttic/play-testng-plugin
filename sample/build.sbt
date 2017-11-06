import com.linkedin.plugin.NGPlugin
import de.johoop.testngplugin.TestNGPlugin

lazy val root = (project in file("."))
  .settings(
    name := "Sample",
    version := "2.5.0-SNAPSHOT",
    scalaVersion := "2.11.11",
    crossScalaVersions := Seq("2.10.6", "2.11.11", "2.12.4"),
    libraryDependencies ++= Seq(
      "com.linkedin.play-testng-plugin" %% "play-testng-helpers" % "2.5.0"
    )
  )
  .enablePlugins(play.sbt.PlayJava)
  .enablePlugins(NGPlugin)