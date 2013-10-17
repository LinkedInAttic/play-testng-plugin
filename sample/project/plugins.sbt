// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2013.10.16-master-56bcfdd")

addSbtPlugin("com.linkedin" % "play-plugins-testng" % "2013.10.16-master-56bcfdd-v1")
