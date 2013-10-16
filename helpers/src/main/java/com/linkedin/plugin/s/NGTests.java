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
import java.util.HashMap;
import java.util.Map;

import static play.test.Helpers.HTMLUNIT;

public class NGTests extends NGTestsBase implements IHookable {

  private class AnnotationsReader extends NGTestsBase.AnnotationsReader {

    public AnnotationsReader(ITestResult testResult){
      super(testResult);
    }

    protected Map<String, Object> getConf() {
      if (getFakeApp() == null)
        return new HashMap<String, Object>();
      return super.getConf();
    }

    private WithFakeApplication getFakeApp() {
      return getAnnotationFromMethodOrClass(WithFakeApplication.class);
    }

    private WithTestServer getTestServer() {
      return getAnnotationFromMethodOrClass(WithTestServer.class);
    }

    private FakeApplication buildFakeApplication(WithFakeApplication fa) {
      if (fa != null) {
        String path = fa.path();

        // adapted from play.test.FakeApplication
        return new FakeApplication(
          new File(path),
          Helpers.class.getClassLoader(),
          Scala.toSeq(getPlugins()),
          Scala.toSeq(Collections.<String>emptyList()),
          Scala.asScala(getConf()),
          scala.Option.apply((play.api.GlobalSettings) null),
          PartialFunction$.MODULE$.<Tuple2<String, String>, Handler>empty()
        );
      }
      return null;
    }

    private TestServer buildTestServer(WithTestServer ts) {
      FakeApplication fake = buildFakeApplication(ts.fakeApplication());
      return new TestServer(ts.port(), fake, scala.Option.apply(null));
    }
  }

  // XXX: Evil hack, may lead to race conditions...
  private TestBrowser _testBrowser = null;
  protected TestBrowser browser() {
    if (_testBrowser == null)
      throw new RuntimeException("No TestBrowser available, test class or method must be annotated with @WithTestServer");
    return _testBrowser;
  }

  public void run(final IHookCallBack icb, final ITestResult testResult) {

    AnnotationsReader reader = new AnnotationsReader(testResult);

    WithFakeApplication fa = reader.getFakeApp();
    WithTestServer ts = reader.getTestServer();

    if (fa != null)
    {
      FakeApplication app = reader.buildFakeApplication(fa);
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
