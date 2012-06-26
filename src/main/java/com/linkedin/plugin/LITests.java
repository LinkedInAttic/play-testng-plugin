package com.linkedin.plugin;

import java.util.*;

import java.lang.reflect.*;
import java.lang.annotation.*;
import org.testng.*;

import play.test.*;
import static play.test.Helpers.*;

public class LITests implements IHookable{
  public void run(final IHookCallBack icb, ITestResult testResult) {
    Method m = testResult.getMethod().getConstructorOrMethod().getMethod();
    WithFakeApplication a = m.getAnnotation(WithFakeApplication.class);
      
    if(a != null){
      Map<String, String> conf = new HashMap<String, String>();
      for(Conf c : a.configurations())
        conf.put(c.key(), c.value());
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