package com.linkedin.plugin.s;

import play.api.Application;
import play.api.Environment;
import play.api.inject.guice.GuiceApplicationBuilder;
import play.api.inject.guice.GuiceBuilder;
import play.libs.Scala;

/**
 * Default implementation for building an Application. If a builder isn't specified, a default GuiceApplicationBuilder
 * is used.
 */
public class FakeApplicationFactoryImpl implements FakeApplicationFactory {
  @Override
  public Application buildScalaApplication(FakeApplicationFactoryArgs args) {
    return buildFromBuilder(args);
  }

  @SuppressWarnings("unchecked")
  protected Application buildFromBuilder(FakeApplicationFactoryArgs args) {
    Class<? extends GuiceBuilder> builderClass = args.getBuilderClass().orElse(GuiceApplicationBuilder.class);
    GuiceBuilder builder;
    try {
      builder = builderClass.newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Unable to instantiate application builder " + builderClass, e);
    }
    builder = (GuiceBuilder) builder.in(new Environment(args.getPath(), play.test.Helpers.class.getClassLoader(), play.Mode.TEST.asScala()));
    builder = (GuiceBuilder) builder.configure(Scala.asScala(args.getConfig()));
    builder = (GuiceBuilder) builder.overrides(Scala.asScala(args.getOverrides()));
    return builder.injector().instanceOf(Application.class);
  }
}
