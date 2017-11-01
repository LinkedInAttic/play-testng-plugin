import Defaults._

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .enablePlugins(CrossPerProjectPlugin)
  .aggregate(NGHelpers, NGPlugin)

lazy val NGHelpers = (project in file("helpers"))
  .settings(commonSettings: _*)
  .settings(
    name := "play-testng-helpers",
    scalaVersion := "2.11.11",
    libraryDependencies ++= Seq(
      "org.testng" % "testng" % "6.8.8", // % "provided"
      "com.typesafe.play" %% "play-test" % "2.5.18", //% "provided"
      "com.typesafe.play" %% "play-java" % "2.5.18" //% "provided"
    ))

lazy val NGPlugin = (project in file("plugin"))
  .settings(commonSettings: _*)
  .settings(
    name := "play-plugins-testng",
    sbtPlugin := true,
    scalaVersion := "2.10.6",
    crossScalaVersions := Seq("2.10.6"),
    libraryDependencies ++= Seq(
        // If changing this, be sure to change in NGPlugin.scala also.
        sbtPluginExtra("de.johoop" % "sbt-testng-plugin" % "3.0.2", (sbtBinaryVersion in update).value, (scalaBinaryVersion in update).value),
        "de.johoop" %% "sbt-testng-interface" % "3.0.2"
    )
  )

def commonSettings: Seq[Def.Setting[_]] = Seq(
  organization := "com.linkedin.play-testng-plugin",
  version := "2.5.0"
)
