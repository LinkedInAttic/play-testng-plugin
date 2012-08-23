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
package com.linkedin.plugin;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.io.File;

import org.testng.*;
import play.test.*;
import static play.test.Helpers.*;

import play.libs.F.*;

// TODO: refactor
public class NGTests implements IHookable {
  
  private class AnnotationsReader {
    
    private ITestResult itr;
    
    public AnnotationsReader(ITestResult testResult){
      itr = testResult;
    }
    
    private Method testMethod(){
      return itr.getMethod().getConstructorOrMethod().getMethod();
    }

    private Class testClass(){
      return itr.getTestClass().getRealClass();
    }

    private <T extends Annotation> T getAnnotationFromMethodOrClass(Class<T> c){
      Class clazz = testClass();
      Method m = testMethod();

      T classAnn = (T)clazz.getAnnotation(c);
      T a = m.getAnnotation(c);

      if(a != null)
        return a;
      else
        return classAnn;
    }

    private WithFakeApplication getFakeApp(){
      return getAnnotationFromMethodOrClass(WithFakeApplication.class);
    }

    private WithTestServer getTestServer(){
      return getAnnotationFromMethodOrClass(WithTestServer.class);
    }

    private Map<String, String> getConf(){
      Map<String, String> conf = new HashMap<String, String>();

      if(getFakeApp() == null)
        return conf;

      Class clazz = testClass();
      Method m = testMethod();

      Confs classConfs = (Confs)clazz.getAnnotation(Confs.class);
      Conf classConf = (Conf)clazz.getAnnotation(Conf.class);
      Confs methodConfs = m.getAnnotation(Confs.class);
      Conf methodConf = m.getAnnotation(Conf.class);

      if(classConfs != null){
        for(Conf c : classConfs.value())
          conf.put(c.key(), c.value());
      }

      if(classConf != null)
        conf.put(classConf.key(), classConf.value());

      if(methodConfs != null){
        for(Conf c : methodConfs.value())
          conf.put(c.key(), c.value());
      }

      if(methodConf != null)
        conf.put(methodConf.key(), methodConf.value());

      return conf;
    }

    private List<String> getPlugins(){
      Class clazz = testClass();
      Method m = testMethod();

      WithPlugins classPlugins = (WithPlugins)clazz.getAnnotation(WithPlugins.class);
      WithPlugins methodPlugins = m.getAnnotation(WithPlugins.class);

      List<String> plugins = new ArrayList<String>();
      if(classPlugins != null)
        plugins.addAll(Arrays.asList(classPlugins.value()));
      if(methodPlugins != null)
      plugins.addAll(Arrays.asList(methodPlugins.value()));

      return plugins;
    }
    
    private FakeApplication buildFakeApplication(WithFakeApplication fa){
      if(fa != null){
        String path = fa.path();
        return new FakeApplication(new File(path), Helpers.class.getClassLoader(), getConf(), getPlugins());
      }
      return null;
    }

    private TestServer buildTestServer(WithTestServer ts) {
      FakeApplication fake = buildFakeApplication(ts.fakeApplication());
      return testServer(ts.port(), fake);
    }
  }
  
  // XXX: Evil hack, may lead to race conditions...
  private TestBrowser _testBrowser = null;
  protected TestBrowser browser(){
    if(_testBrowser == null)
      throw new RuntimeException("No TestBrowser available, test class or method must be annotated with @WithTestServer");
    return _testBrowser;
  }

  public void run(final IHookCallBack icb, final ITestResult testResult) {
    
    AnnotationsReader reader = new AnnotationsReader(testResult);
    
    WithFakeApplication fa = reader.getFakeApp();
    WithTestServer ts = reader.getTestServer();

    if(fa != null){
      FakeApplication app = reader.buildFakeApplication(fa);
      start(app);
      icb.runTestMethod(testResult);
      stop(app);
    }
    else if(ts != null){
      TestServer server = reader.buildTestServer(ts);
      // TODO: parameterize WebDriver
      running(server, HTMLUNIT, new Callback<TestBrowser>() {
        public void invoke(final TestBrowser browser) {
          _testBrowser = browser;
          icb.runTestMethod(testResult);
        }
      });
    }
    else{
      icb.runTestMethod(testResult);
    }
   }
}
