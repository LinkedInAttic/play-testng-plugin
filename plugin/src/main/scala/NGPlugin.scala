// Copyright 2012 LinkedIn
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.linkedin.plugin

import sbt._
import Keys._

import de.johoop.testngplugin.TestNGPlugin._

object NGPlugin extends Plugin {

  def ngSettings: Seq[Setting[_]] = super.settings ++ Seq(
    testOptions := Seq(),
    testOptions += Tests.Setup { loader =>
      val loggerClass = playLoggerClass(loader)
      if (loggerClass != null) {
        loggerClass.getMethod("init", classOf[java.io.File]).invoke(null, new java.io.File("."))
      }
    },
    testOptions += Tests.Cleanup { loader =>
      val loggerClass = playLoggerClass(loader)
      if (loggerClass != null) {
        loggerClass.getMethod("shutdown").invoke(null)
      }
    },
    //testOptions += Tests.Argument(TestFrameworks.Specs2, "sequential", "true"),
    testOptions += Tests.Argument(TestFrameworks.JUnit,"junitxml", "console")
   ) ++
   testNGSettings ++
   Seq(
       libraryDependencies <++= (testNGVersion)(v => Seq(
         "org.testng" % "testng" % v % "test->default",
         // If changing this, be sure to change in Build.scala also.
         "de.johoop" %% "sbt-testng-interface" % "3.0.2" % "test"))
  )

  private def playLoggerClass(loader: ClassLoader) = {
    try {
      loader.loadClass("play.api.Logger")
    } catch {
      case e: ClassNotFoundException => null
    }
  }
}
