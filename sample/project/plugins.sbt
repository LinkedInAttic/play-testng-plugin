// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2012.08.15.c4c3576")

addSbtPlugin("com.linkedin" % "play-plugins-testng" % "2012.08.15.c4c3576.v5")