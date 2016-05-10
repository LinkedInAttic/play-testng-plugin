import sbt._
import sbt.Defaults._
import sbt.Keys._

object NGPluginBuild extends Build {

  lazy val root = Project("root", file("."),
    settings = commonSettings ++ Seq(
      name := "play-testng"
    )) aggregate(NGHelpers, NGPlugin)

  lazy val NGHelpers = Project(
    id = "play-testng-helpers",
    base = file("helpers"),
    settings = commonSettings ++ Seq(
      libraryDependencies ++= Seq(
        "org.testng" % "testng" % "6.8.8", // % "provided"
        "com.typesafe.play" %% "play-test" % "2.4.0", //% "provided"
        "com.typesafe.play" %% "play-java" % "2.4.0" //% "provided"
      )))

  lazy val NGPlugin = Project(
    id = "play-plugins-testng",
    base = file("plugin"),
    settings = commonSettings ++ Seq(
      sbtPlugin := true,
      libraryDependencies <++= (scalaBinaryVersion in update, sbtBinaryVersion in update) {
        case (scalaBinaryVersion, sbtBinaryVersion) => Seq(
          // If changing this, be sure to change in NGPlugin.scala also.
          sbtPluginExtra("de.johoop" % "sbt-testng-plugin" % "3.0.2", sbtBinaryVersion, scalaBinaryVersion),
          "de.johoop" %% "sbt-testng-interface" % "3.0.2"
        )
      }))

  lazy val commonSettings: Seq[Setting[_]] = Project.defaultSettings ++ Seq(
    organization := "com.linkedin.play-testng-plugin",
    scalaVersion := "2.10.4",
    version := "2.4.3"
  )
}
