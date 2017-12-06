package com.linkedin.plugin.j;

import play.Application;
import play.Environment;
import play.Mode;
import play.api.inject.Binding;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceBuilder;
import play.test.Helpers;

/**
 * Default implementation for building an Application. If a builder isn't specified, a default GuiceApplicationBuilder
 * is used.
 */
public class FakeApplicationFactoryImpl implements FakeApplicationFactory {
  @Override
  public Application buildApplication(FakeApplicationFactoryArgs args) {
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

    builder = (GuiceBuilder) builder.in(new Environment(args.getPath(), Helpers.class.getClassLoader(), Mode.TEST));
    builder = (GuiceBuilder) builder.configure(args.getConfig());
    builder = (GuiceBuilder) builder.overrides(args.getOverrides().toArray(new Binding[args.getOverrides().size()]));
    return builder.injector().instanceOf(Application.class);
  }
}
