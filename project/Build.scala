import sbt._
import sbt.Defaults._
import sbt.Keys._

object NGPluginBuild extends Build {

  object Repos {
    val LinkedInPatterns = Patterns(
      Seq("[organization]/[module]/[revision]/[module]-[revision].ivy"),
      Seq("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]"),
      isMavenCompatible = true)

    val LocalRepoName = "~/local-repo"
    val LocalRepoPath = file(System.getProperty("user.home") + "/local-repo")
    val localRepo = Resolver.file(LocalRepoName, LocalRepoPath)(LinkedInPatterns)

    val ArtifactoryBaseUrl = "http://artifactory.corp.linkedin.com:8081/artifactory/"
    val sandbox = Resolver.url("Artifactory sandbox",
                               url(ArtifactoryBaseUrl + "ext-sandbox"))(LinkedInPatterns)
    val core = Resolver.url("Artifactory CORE",
                            url(ArtifactoryBaseUrl + "CORE"))(LinkedInPatterns)
    
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
    version := "2012.09.20.1886ca6-v5",
    resolvers ++= Seq(Repos.localRepo, Repos.sandbox, Repos.typeSafeReleases))

  lazy val publishSettings: Seq[Setting[_]] = Seq(
    // publishTo <<= version { (v: String) =>
    //  if (v.trim.endsWith("SNAPSHOT")) 
    //    Some(Repos.snapshots) 
    // else
    //   Some(Repos.releases)
    //},
    // publishLocalConfiguration <<= (packagedArtifacts, publishMavenStyle, deliverLocal, ivyLoggingLevel) map {
    //   (artifacts, mavenStyle, ivyFile, loggingLevel) =>
    //   Classpaths.publishConfig(artifacts, if (mavenStyle) None else Some(ivyFile), Seq("sha1", "md5"),
    //                            Repos.LocalRepoName, loggingLevel)
    // },
    publishTo := Some(Repos.core),
    credentials ++= Seq(
      Credentials(Path.userHome / ".sbt" / ".licredentials")
    ),
    publishMavenStyle := false)
}
