// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.0")

addSbtPlugin("de.johoop" % "sbt-testng-plugin" % "3.1.1")

addSbtPlugin("com.linkedin.play-testng-plugin" % "play-plugins-testng" % "2.5.0")
