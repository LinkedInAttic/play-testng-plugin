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

import org.testng.IHookable;
import org.testng.ITestResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public abstract class NGTestsBase implements IHookable {

  protected abstract class AnnotationsReader<F extends Annotation, T extends Annotation> {

    protected ITestResult itr;
    protected Class<F> fakeAppAnnotationClass;
    protected Class<T> testServerAnnotationClass;

    public AnnotationsReader(ITestResult testResult, Class<F> fakeAppAnnotationClass, Class<T> testServerAnnotationClass) {
      itr = testResult;
      this.fakeAppAnnotationClass = fakeAppAnnotationClass;
      this.testServerAnnotationClass = testServerAnnotationClass;
    }

    public F getFakeAppAnnotation() {
      return getAnnotationFromMethodOrClass(fakeAppAnnotationClass);
    }

    public T getTestServerAnnotation() {
      return getAnnotationFromMethodOrClass(testServerAnnotationClass);
    }

    protected <A extends Annotation> A getAnnotationFromMethodOrClass(Class<A> c) {
      Class clazz = testClass();
      Method m = testMethod();

      A classAnn = (A) clazz.getAnnotation(c);
      A a = m.getAnnotation(c);

      if(a != null)
        return a;
      else
        return classAnn;
    }

    protected Map<String, Object> getConf() {
      Map<String, Object> conf = new HashMap<String, Object>();

      if (getFakeAppAnnotation() == null)
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

    protected List<String> getPlugins(){
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

    private Method testMethod() {
      return itr.getMethod().getConstructorOrMethod().getMethod();
    }

    private Class testClass() {
      return itr.getTestClass().getRealClass();
    }

  }
}
