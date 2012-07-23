import sbt._
import sbt.Defaults._
import sbt.Keys._

object NGPluginBuild extends Build {
  
  // TODO: hard coded path...
  val localrepo = Resolver.file("Play local repo",  new File(Path.userHome.absolutePath + "/Documents/jto_Play20/repository/local"))(Resolver.ivyStylePatterns)

  lazy val root = Project("root", file("."),
    settings = commonSettings ++ Seq(
      name := "play-testng"
    )) aggregate(NGHelpers, NGPlugin)

  lazy val NGHelpers = Project(
    id = "play-testng-helpers",
    base = file("helpers"),
    settings = commonSettings ++ Seq(
      version := "1.0-SNAPSHOT",
      crossScalaVersions := Seq("2.9.1"),
      libraryDependencies ++= Seq(
        "org.testng" % "testng" % "6.4" /*% "provided"*/,
        "play" %% "play-test" % "2.1-SNAPSHOT" //% "provided"
      )))

  lazy val NGPlugin = Project(
    id = "play-plugins-testng",
    base = file("plugin"),
    settings = commonSettings ++ Seq(
      sbtPlugin := true,
      version := "1.0-SNAPSHOT",
      crossScalaVersions := Seq("2.9.1"),
      libraryDependencies <++= (scalaVersion, sbtVersion) { 
        case (scalaVersion, sbtVersion) => Seq(
          sbtPluginExtra("de.johoop" % "sbt-testng-plugin" % "2.0.2", sbtVersion, scalaVersion),
          "de.johoop" %% "sbt-testng-interface" % "2.0.2"
        )
      }))

  lazy val commonSettings: Seq[Setting[_]] = Project.defaultSettings ++ publishSettings ++ Seq(
    organization := "com.linkedin",
    scalaVersion := "2.9.1",
    resolvers += localrepo)

  lazy val publishSettings: Seq[Setting[_]] = Seq(publishTo := Some(
    localrepo),
    publishMavenStyle := false)
}
