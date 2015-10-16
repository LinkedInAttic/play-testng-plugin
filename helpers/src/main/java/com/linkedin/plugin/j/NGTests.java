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
package com.linkedin.plugin.j;

import com.linkedin.plugin.NGTestsBase;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;
import play.Application;
import play.Environment;
import play.GlobalSettings;
import play.Mode;
import play.api.inject.Binding;
import play.api.inject.package$;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceBuilder;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestBrowser;
import play.test.TestServer;

import java.io.File;
import java.util.List;

import static play.test.Helpers.HTMLUNIT;

/**
 * Java API to be extended by TestNG classes to use custom @WithFakeApplication/@WithTestServer annotations.
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

      return new FakeApplication(new File(path), Helpers.class.getClassLoader(), getConf(), getPlugins(), globalSettings);
    }

    /**
     * Build from a 2.4 Builder instead of using FakeApplication
     */
    private Application buildFromBuilder(File path, GlobalSettings globalSettings, Class<?> builderClass, List<Binding<?>> overrides) {
      GuiceBuilder builder;
      try {
        builder = (GuiceBuilder) builderClass.newInstance();
      } catch (Exception e) {
        throw new RuntimeException("Unable to instantiate application builder " + builderClass, e);
      }
      if (!getPlugins().isEmpty()) {
        throw new RuntimeException("Using plugins isn't supported when using binding overrides or a GuiceBuilder.");
      }

      builder = (GuiceBuilder) builder.in(new Environment(path, Helpers.class.getClassLoader(), Mode.TEST));
      builder = (GuiceBuilder) builder.configure(getConf());
      if (globalSettings != null) {
        if (builder instanceof GuiceApplicationBuilder) {
          builder = ((GuiceApplicationBuilder) builder).global(globalSettings);
        } else {
          play.api.GlobalSettings scalaGlobal = new play.core.j.JavaGlobalSettingsAdapter(globalSettings);
          builder = (GuiceBuilder) builder.bindings(package$.MODULE$.bind(play.api.GlobalSettings.class).toInstance(scalaGlobal));
        }
      }
      builder = (GuiceBuilder) builder.overrides(overrides.toArray(new Binding[overrides.size()]));
      return builder.injector().instanceOf(Application.class);
    }

    private TestServer buildTestServer(WithTestServer ts) {
      Application fake = buildFakeApplication(ts.fakeApplication());
      return Helpers.testServer(ts.port(), fake);
    }
  }

  // XXX: Evil hack, may lead to race conditions...
  private TestBrowser _testBrowser = null;

  /**
   * Java API: gets a TestBrowser for Java test classes. (Can only be used with @WithTestServer)
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
      Helpers.running(app, () -> {
        icb.runTestMethod(testResult);
      });

    }
    else if (ts != null)
    {
      TestServer server = reader.buildTestServer(ts);
      // TODO: parameterize WebDriver
      Helpers.running(server, HTMLUNIT, browser -> {
        _testBrowser = browser;
        icb.runTestMethod(testResult);
      });
    }
    else
    {
      icb.runTestMethod(testResult);
    }
  }
}
