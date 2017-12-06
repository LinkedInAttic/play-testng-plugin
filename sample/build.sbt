import com.linkedin.plugin.NGPlugin

lazy val root = (project in file("."))
  .settings(
    name := "Sample",
    version := "2.6.0-SNAPSHOT",
    scalaVersion := "2.12.4",
    crossScalaVersions := Seq("2.11.11", "2.12.4"),
    libraryDependencies ++= Seq(
      "com.linkedin.play-testng-plugin" %% "play-testng-helpers" % "2.6.0"
    )
  )
  .enablePlugins(play.sbt.PlayJava)
  .enablePlugins(NGPlugin)
