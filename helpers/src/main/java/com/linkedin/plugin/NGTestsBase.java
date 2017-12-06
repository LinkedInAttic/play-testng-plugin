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
import play.api.inject.Binding;
import play.api.inject.BindingKey;
import play.api.inject.package$;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;

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
      Class<A> clazz = testClass();
      Method m = testMethod();

      A classAnn = clazz.getAnnotation(c);
      A a = m.getAnnotation(c);

      if(a != null)
        return a;
      else
        return classAnn;
    }

    protected Map<String, Object> getConf() {
      Map<String, Object> conf = new HashMap<>();

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

    protected boolean isDefined(Class clz) {
      return clz != null && !Object.class.equals(clz);
    }

    protected <U> U instantiate(Class<U> clazz) {
      try {
        return clazz.newInstance();
      } catch (Exception e) {
        throw new RuntimeException("Unable to instantiate class " + clazz);
      }
    }

    private Stream<? extends Binding<?>> toStream(WithOverrides overrides) {
      return Optional.ofNullable(overrides).map(o -> Arrays.stream(o.value()).map(this::toBinding)).orElse(Stream.empty());
    }

    private Stream<? extends Binding<?>> toStream(BindingOverride override) {
      return Optional.ofNullable(override).map(o -> Stream.<Binding<?>>of(toBinding(o))).orElse(Stream.empty());
    }

    @SuppressWarnings("unchecked")
    private Binding<?> toBinding(BindingOverride override) {
      BindingKey bindingKey = package$.MODULE$.bind(override.target());
      if (! Object.class.equals(override.annotationQualifier())) {
        bindingKey = bindingKey.qualifiedWith(override.annotationQualifier());
      }
      if (!"".equals(override.stringQualifier())) {
        bindingKey = bindingKey.qualifiedWith(override.stringQualifier());
      }
      return bindingKey.to(override.implementation());
    }

    protected List<Binding<?>> getOverrides() {
      if (getFakeAppAnnotation() == null) {
        return Collections.emptyList();
      }

      Class clazz = testClass();
      Method m = testMethod();

      Stream<Binding<?>> bindings = concat(
          concat(
              concat(toStream((WithOverrides) clazz.getAnnotation(WithOverrides.class)),
                     toStream((BindingOverride) clazz.getAnnotation(BindingOverride.class))),
                     toStream(m.getAnnotation(WithOverrides.class))),
                     toStream(m.getAnnotation(BindingOverride.class))
      );

      return bindings.collect(Collectors.toList());
    }

    private Method testMethod() {
      return itr.getMethod().getConstructorOrMethod().getMethod();
    }

    @SuppressWarnings("unchecked")
    private <A> Class<A> testClass() {
      return (Class<A>) itr.getTestClass().getRealClass();
    }


  }
}
