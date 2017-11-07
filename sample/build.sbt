import com.linkedin.plugin.NGPlugin

lazy val root = (project in file("."))
  .settings(
    name := "Sample",
    version := "2.5.0-SNAPSHOT",
    scalaVersion := "2.11.11",
    crossScalaVersions := Seq("2.11.11"),
    libraryDependencies ++= Seq(
      "com.linkedin.play-testng-plugin" %% "play-testng-helpers" % "2.5.0"
    )
  )
  .enablePlugins(play.sbt.PlayJava)
  .enablePlugins(NGPlugin)
