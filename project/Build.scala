import sbt._
import sbt.Defaults._
import sbt.Keys._

object NGPluginBuild extends Build {

  object Repos {
    val pattern = Patterns(
      Seq("[organisation]/[module]/[revision]/[module]-[revision](-[classifier]).ivy"),
      Seq("[organisation]/[module]/[revision]/[module]-[revision](-[classifier]).[ext]"),
      true
    )

    val artifactory = "http://artifactory.corp.linkedin.com:8081/artifactory/"
    val mavenLocal = Resolver.file("file",  new File(Path.userHome.absolutePath + "/Documents/mvn-repo/snapshots"))
    val sandbox = Resolver.url("Artifactory sandbox", url(artifactory + "ext-sandbox"))(pattern)
    val typeSafeReleases = "TypeSafeRelease" at "http://repo.typesafe.com/typesafe/releases/"
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
        "org.testng" % "testng" % "6.4", // % "provided"
        "play" %% "play-test" % "2012.09.20.1886ca6" //% "provided"
      )))

  lazy val NGPlugin = Project(
    id = "play-plugins-testng",
    base = file("plugin"),
    settings = commonSettings ++ Seq(
      sbtPlugin := true,
      libraryDependencies <++= (scalaVersion, sbtVersion) { 
        case (scalaVersion, sbtVersion) => Seq(
          // If changing this, be sure to change in NGPlugin.scala also.
          sbtPluginExtra("de.johoop" % "sbt-testng-plugin" % "2.0.3", "0.12", scalaVersion),
          "de.johoop" %% "sbt-testng-interface" % "2.0.3"
        )
      }))

  lazy val commonSettings: Seq[Setting[_]] = Project.defaultSettings ++ publishSettings ++ Seq(
    organization := "com.linkedin",
    scalaVersion := "2.9.2",
    version := "2012.09.20.1886ca6",
    resolvers ++= Seq(Repos.sandbox, Repos.typeSafeReleases))

  lazy val publishSettings: Seq[Setting[_]] = Seq(
    // publishTo <<= version { (v: String) =>
    //  if (v.trim.endsWith("SNAPSHOT")) 
    //    Some(Repos.snapshots) 
    //  else
    //    Some(Repos.releases)
    // },
    publishTo := Some(Repos.sandbox),
    credentials ++= Seq(
      Credentials(Path.userHome / ".sbt" / ".licredentials")
    ),
    publishMavenStyle := false)
}
