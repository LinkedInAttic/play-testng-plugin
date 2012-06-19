import sbt._
import sbt.Defaults._
import sbt.Keys._

object NGPluginBuild extends Build {

  lazy val root = Project(
    id = "play-testng-helpers",
    base = file("."),
    settings = Project.defaultSettings ++ commonSettings ++ Seq(
      version := "1.0-SNAPSHOT",
      crossScalaVersions := Seq("2.9.1"),
      libraryDependencies ++= Seq()))

  lazy val NGPlugin = Project(
    id = "play-plugins-testng",
    base = file("plugin"),
    settings = Project.defaultSettings ++ commonSettings ++ Seq(
      sbtPlugin := true,
      version := "1.0-SNAPSHOT",
      crossScalaVersions := Seq("2.9.1"),
      libraryDependencies <++= (scalaVersion, sbtVersion) { 
        case (scalaVersion, sbtVersion) => Seq(
          sbtPluginExtra("de.johoop" % "sbt-testng-plugin" % "2.0.2", sbtVersion, scalaVersion),
          "de.johoop" %% "sbt-testng-interface" % "2.0.2"
        )
      }))

  lazy val commonSettings: Seq[Setting[_]] = publishSettings ++ Seq(
    organization := "com.linkedin",
    scalaVersion := "2.9.1")

  lazy val publishSettings: Seq[Setting[_]] = Seq(publishTo := Some(
    Resolver.file("Local repo",  new File(Path.userHome.absolutePath + "/Documents/linkedin/play2/repository/local"))(Resolver.ivyStylePatterns)),
    publishMavenStyle := false)
}
