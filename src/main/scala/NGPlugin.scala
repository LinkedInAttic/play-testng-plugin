package com.linkedin.plugin

import sbt._
import Keys._

import de.johoop.testngplugin.TestNGPlugin._

object NGPlugin extends Plugin {

  override def settings: Seq[Setting[_]] = super.settings ++ Seq(
     scalaSource in Test <<= baseDirectory / "junit",
     javaSource in Test <<= baseDirectory / "junit"
   ) ++ inConfig(NGTest)(Defaults.testSettings ++ testNGSettings) ++ Seq(
       scalaSource in NGTest <<= baseDirectory / "test",
       javaSource in NGTest <<= baseDirectory / "test"
  )

  lazy val NGTest = config("ng") extend(Test)
}