package com.linkedin.plugin

import sbt._
import Keys._

import de.johoop.testngplugin.TestNGPlugin._

object NGPlugin extends Plugin {

  override def settings: Seq[Setting[_]] = ngSettings

  def ngSettings: Seq[Setting[_]] = super.settings ++ Seq(
    testOptions in Test := Seq(),
    testOptions in Test += Tests.Setup { loader =>
      loader.loadClass("play.api.Logger").getMethod("init", classOf[java.io.File]).invoke(null, new java.io.File("."))
    },
    testOptions in Test += Tests.Cleanup { loader =>
      loader.loadClass("play.api.Logger").getMethod("shutdown").invoke(null)
    },
    //testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "sequential", "true"),
    testOptions in Test += Tests.Argument(TestFrameworks.JUnit,"junitxml", "console")
   ) ++ 
   inConfig(Test)(testNGSettings) ++
   Seq(
       libraryDependencies <++= (testNGVersion in Test)(v => Seq(
         "org.testng" % "testng" % v % "test->default",
         "de.johoop" %% "sbt-testng-interface" % "2.0.2" % "test"))
  )
}

import org.scalatools.testing.{Fingerprint, SubclassFingerprint, Framework, Logger, EventHandler}
import java.util.concurrent.Semaphore
import de.johoop.testnginterface._

class WrappedTestNGFramework extends Framework {
  val name = "TestNGFakeApp"

  val tests = Array[Fingerprint](Annotated("com.linkedin.plugin.FakeApplication"))

  def testRunner(testClassLoader: ClassLoader, loggers: Array[Logger]) = new WrappedTestNGRunner(testClassLoader, loggers, sharedState)

  private[this] val sharedState = new TestRunState
}

class WrappedTestNGRunner(testClassLoader: ClassLoader, loggers: Array[Logger], state: TestRunState) extends TestNGRunner(testClassLoader: ClassLoader, loggers: Array[Logger], state: TestRunState) {
  override def run(testClassname: String, fingerprint: Fingerprint, eventHandler: EventHandler, testOptions: Array[String]) = {
    super.run(testClassname, fingerprint, eventHandler, testOptions)
  }
}
