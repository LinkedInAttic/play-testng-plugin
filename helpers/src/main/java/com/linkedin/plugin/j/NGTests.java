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
import play.GlobalSettings;
import play.inject.guice.GuiceBuilder;
import play.test.Helpers;
import play.test.TestBrowser;
import play.test.TestServer;

import java.io.File;
import java.util.Optional;

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
      FakeApplicationFactory appFactory = instantiate(fa.appFactory());

      return appFactory.buildApplication(new FakeApplicationFactoryArgs(
          new File(fa.path()),
          isDefined(fa.guiceBuilder()) ? Optional.of(fa.guiceBuilder()) : Optional.<Class<? extends GuiceBuilder>>empty(),
          isDefined(fa.withGlobal()) ? Optional.of(instantiate(fa.withGlobal())) : Optional.<GlobalSettings>empty(),
          getOverrides(),
          getConf(),
          getPlugins()
      ));
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
