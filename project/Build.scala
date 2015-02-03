import sbt._
import sbt.Defaults._
import sbt.Keys._

object NGPluginBuild extends Build {

  object Repos {
    val typeSafeReleases = "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/"
    val scalazReleases = "Scalaz Repository" at "http://dl.bintray.com/scalaz/releases"
  }

  lazy val root = Project("root", file("."),
    settings = commonSettings ++ Seq(
      name := "play-testng"
    )) aggregate(NGHelpers, NGPlugin)

  lazy val NGHelpers = Project(
    id = "play-testng-helpers",
    base = file("helpers"),
    settings = commonSettings ++ Seq(
      libraryDependencies ++= Seq(
        "org.testng" % "testng" % "6.8.5", // % "provided"
        "com.typesafe.play" %% "play-test" % "2.4.0-RC1" //% "provided"
      )))

  lazy val NGPlugin = Project(
    id = "play-plugins-testng",
    base = file("plugin"),
    settings = commonSettings ++ Seq(
      sbtPlugin := true,
      libraryDependencies <++= (scalaVersion, sbtVersion) {
        case (scalaVersion, sbtVersion) => Seq(
          // If changing this, be sure to change in NGPlugin.scala also.
          sbtPluginExtra("de.johoop" % "sbt-testng-plugin" % "3.0.2", "0.13", "2.10"),
          "de.johoop" %% "sbt-testng-interface" % "3.0.2"
        )
      }))

  lazy val commonSettings: Seq[Setting[_]] = Project.defaultSettings ++ Seq(
    organization := "com.linkedin",
    scalaVersion := "2.10.4",
    version := "2.4.0",
    resolvers ++= Seq(Repos.typeSafeReleases, Repos.scalazReleases)

  )
}
