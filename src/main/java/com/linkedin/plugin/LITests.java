package com.linkedin.plugin;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.testng.*;
import play.test.*;
import static play.test.Helpers.*;

public class LITests implements IHookable{
  
  private Method testMethod(ITestResult testResult){
    return testResult.getMethod().getConstructorOrMethod().getMethod();
  }
  
  private Class testClass(ITestResult testResult){
    return testResult.getTestClass().getRealClass();
  }
  
  private Boolean hasFakeApp(ITestResult testResult){
    Class clazz = testClass(testResult);
    Method m = testMethod(testResult);
    
    WithFakeApplication classFakeApp = (WithFakeApplication)clazz.getAnnotation(WithFakeApplication.class);
    WithFakeApplication a = m.getAnnotation(WithFakeApplication.class);

    return classFakeApp != null || a != null;
  }
  
  private Map<String, String> getConf(ITestResult testResult){
    Map<String, String> conf = new HashMap<String, String>();
    
    if(!hasFakeApp(testResult))
      return conf;
    
    Class clazz = testClass(testResult);
    Method m = testMethod(testResult);
    
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
  
  private List<String> getPlugins(ITestResult testResult){
    Class clazz = testClass(testResult);
    Method m = testMethod(testResult);

    WithPlugins classPlugins = (WithPlugins)clazz.getAnnotation(WithPlugins.class);
    WithPlugins methodPlugins = m.getAnnotation(WithPlugins.class);

    List<String> plugins = new ArrayList<String>();
    if(classPlugins != null)
      plugins.addAll(Arrays.asList(classPlugins.value()));
    if(methodPlugins != null)
    plugins.addAll(Arrays.asList(methodPlugins.value()));

    return plugins;
  }

  public void run(final IHookCallBack icb, ITestResult testResult) {
    if(hasFakeApp(testResult)){
      FakeApplication app = fakeApplication(getConf(testResult), getPlugins(testResult));
      start(app);
      icb.runTestMethod(testResult);
      stop(app);
    }
    else{
      icb.runTestMethod(testResult);
    }
   }
}
