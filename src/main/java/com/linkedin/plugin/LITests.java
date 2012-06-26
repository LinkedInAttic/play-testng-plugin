package com.linkedin.plugin;

import java.util.*;

import java.lang.reflect.*;
import java.lang.annotation.*;
import org.testng.*;

import play.test.*;
import static play.test.Helpers.*;

public class LITests implements IHookable{
  public void run(final IHookCallBack icb, ITestResult testResult) {

    Class clazz = testResult.getTestClass().getRealClass();
    Method m = testResult.getMethod().getConstructorOrMethod().getMethod();

    WithFakeApplication classFakeApp = (WithFakeApplication)clazz.getAnnotation(WithFakeApplication.class);
    WithFakeApplication a = m.getAnnotation(WithFakeApplication.class);

    Confs classConfs = (Confs)clazz.getAnnotation(Confs.class);
    Conf classConf = (Conf)clazz.getAnnotation(Conf.class);
    Confs methodConfs = m.getAnnotation(Confs.class);
    Conf methodConf = m.getAnnotation(Conf.class);

    if(classFakeApp != null || a != null){
      Map<String, String> conf = new HashMap<String, String>();

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

      FakeApplication app = fakeApplication(conf);
      start(app);
      icb.runTestMethod(testResult);
      stop(app);
    }
    else{
      icb.runTestMethod(testResult);
    }
   }
}