package com.linkedin.plugin

import sbt._
import Keys._

import de.johoop.testngplugin.TestNGPlugin._

object NGPlugin extends Plugin {

  override def settings: Seq[Setting[_]] = super.settings ++ testNGSettings ++ Seq(
     testListeners in Test := Seq()
   )
}
