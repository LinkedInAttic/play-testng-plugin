import sbt.Defaults._

sbtPlugin := true

name := "play-plugins-testng"

version := "1.0-SNAPSHOT"

organization := "com.linkedin"

publishTo := Some(Resolver.file("Local repo",  new File(Path.userHome.absolutePath + "/Documents/linkedin/play2/repository/local"))(Resolver.ivyStylePatterns))

publishMavenStyle := false

libraryDependencies <++= (scalaVersion, sbtVersion) { 
	case (scalaVersion, sbtVersion) => Seq(
		sbtPluginExtra("de.johoop" % "sbt-testng-plugin" % "2.0.2", sbtVersion, scalaVersion)
	)
}
