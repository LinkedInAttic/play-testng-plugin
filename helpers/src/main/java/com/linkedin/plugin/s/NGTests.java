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
package com.linkedin.plugin.s;

import com.linkedin.plugin.NGTestsBase;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;
import play.api.Environment;
import play.api.Mode;
import play.api.Application;
import play.api.GlobalSettings;
import play.api.inject.Binding;
import play.api.inject.guice.GuiceApplicationBuilder;
import play.api.inject.guice.GuiceBuilder;
import play.api.inject.package$;
import play.api.mvc.Handler;
import play.api.test.FakeApplication;
import play.api.test.Helpers;
import play.api.test.TestBrowser;
import play.api.test.TestServer;
import play.libs.Scala;
import scala.PartialFunction$;
import scala.Tuple2;
import scala.runtime.AbstractFunction0;
import scala.runtime.AbstractFunction1;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static play.test.Helpers.HTMLUNIT;

/**
 * Scala API to be extended by TestNG classes to use custom @WithFakeApplication/@WithTestServer annotations.
 */
public class NGTests extends NGTestsBase implements IHookable {

  private class AnnotationsReader extends NGTestsBase.AnnotationsReader<WithFakeApplication, WithTestServer> {

    public AnnotationsReader(ITestResult testResult) {
      super(testResult, WithFakeApplication.class, WithTestServer.class);
    }

    private Application buildFakeApplication(WithFakeApplication fa) {
      if (fa == null) {
        return null;
      }
      String path = fa.path();
      GlobalSettings globalSettings = null;
      if (isDefined(fa.withGlobal())) {
        try {
          globalSettings = (GlobalSettings) fa.withGlobal().newInstance();
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
      }

      List<Binding<?>> overrides = getOverrides();
      if (isDefined(fa.guiceBuilder()) || !overrides.isEmpty()) {
        Class builderClass = Object.class.equals(fa.guiceBuilder()) ? GuiceApplicationBuilder.class : fa.guiceBuilder();
        return buildFromBuilder(new File(path), globalSettings, builderClass, overrides);
      }

      // adapted from play.test.FakeApplication
      return new FakeApplication(
        new File(path),
        Helpers.class.getClassLoader(),
        Scala.toSeq(getPlugins()),
        Scala.toSeq(Collections.<String>emptyList()),
        Scala.asScala(getConf()),
        scala.Option.apply(globalSettings),
        PartialFunction$.MODULE$.<Tuple2<String, String>, Handler>empty()
      );
    }

    private Application buildFromBuilder(File path, GlobalSettings globalSettings, Class builderClass, List<Binding<?>> overrides) {
      GuiceBuilder builder;
      try {
        builder = (GuiceBuilder) builderClass.newInstance();
      } catch (Exception e) {
        throw new RuntimeException("Unable to instantiate application builder " + builderClass, e);
      }
      if (!getPlugins().isEmpty()) {
        throw new RuntimeException("Using plugins isn't supported when using binding overrides or a GuiceBuilder.");
      }
      builder = (GuiceBuilder) builder.in(new Environment(path, play.test.Helpers.class.getClassLoader(), Mode.Test()));
      builder = (GuiceBuilder) builder.configure(Scala.asScala(getConf()));
      if (globalSettings != null) {
        if (builder instanceof GuiceApplicationBuilder) {
          builder = ((GuiceApplicationBuilder) builder).global(globalSettings);
        } else {
          builder = (GuiceBuilder) builder.bindings(Scala.varargs(package$.MODULE$.bind(play.api.GlobalSettings.class).toInstance(globalSettings)));
        }
      }
      builder = (GuiceBuilder) builder.overrides(Scala.asScala(overrides));
      return builder.injector().instanceOf(Application.class);
    }

    private TestServer buildTestServer(WithTestServer ts) {
      Application fake = buildFakeApplication(ts.fakeApplication());
      return new TestServer(ts.port(), fake, scala.Option.apply(null), scala.Option.apply(null));
    }
  }

  // XXX: Evil hack, may lead to race conditions...
  private TestBrowser _testBrowser = null;

  /**
   * Scala API: gets a TestBrowser for Scala test classes. (Can only be used with @WithTestServer)
   */
  protected TestBrowser browser() {
    if (_testBrowser == null)
      throw new RuntimeException("No TestBrowser available, test class or method must be annotated with @WithTestServer");
    return _testBrowser;
  }

  public void run(final IHookCallBack icb, final ITestResult testResult) {

    AnnotationsReader reader = new AnnotationsReader(testResult);

    WithFakeApplication fa = reader.getFakeAppAnnotation();
    WithTestServer ts = reader.getTestServerAnnotation();

    if (fa != null)
    {
      Application app = reader.buildFakeApplication(fa);
      Helpers.running(app, new AbstractFunction0() {
        @Override
        public Object apply() {
          icb.runTestMethod(testResult);
          return null;
        }
      });
    }
    else if (ts != null)
    {
      TestServer server = reader.buildTestServer(ts);
      // TODO: parameterize WebDriver
      Helpers.running(server, HTMLUNIT, new AbstractFunction1<TestBrowser, Object>() {
        @Override
        public Object apply(final TestBrowser browser) {
          _testBrowser = browser;
          icb.runTestMethod(testResult);
          return null;
        }
      });
    }
    else
    {
      icb.runTestMethod(testResult);
    }
  }
}
