package com.linkedin.plugin.j;

import play.Application;
import play.Environment;
import play.GlobalSettings;
import play.Mode;
import play.api.inject.Binding;
import play.api.inject.package$;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceBuilder;
import play.test.FakeApplication;
import play.test.Helpers;

/**
 * Default implementation for building an Application. If a builder isn't specified and no overrides are present the Play
 * FakeApplication is used. Otherwise a builder is used. Using @WithPlugins and a builder or overrides isn't supported.
 */
public class FakeApplicationFactoryImpl implements FakeApplicationFactory {
  @Override
  public Application buildApplication(FakeApplicationFactoryArgs args) {
    return shouldUseBuilder(args) ? buildFromBuilder(args) : buildFromFakeApp(args);
  }

  protected boolean shouldUseBuilder(FakeApplicationFactoryArgs args) {
    return args.getBuilderClass().isPresent() || !args.getOverrides().isEmpty();
  }

  protected Application buildFromFakeApp(FakeApplicationFactoryArgs args) {
    return new FakeApplication(args.getPath(), Helpers.class.getClassLoader(), args.getConfig(), args.getPlugins(), args.getGlobal().orElse(null));
  }

  protected Application buildFromBuilder(FakeApplicationFactoryArgs args) {
    Class<? extends GuiceBuilder> builderClass = args.getBuilderClass().orElse(GuiceApplicationBuilder.class);
    GuiceBuilder builder;
    try {
      builder = builderClass.newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Unable to instantiate application builder " + builderClass, e);
    }
    if (!args.getPlugins().isEmpty()) {
      throw new RuntimeException("Using plugins isn't supported when using binding overrides or a GuiceBuilder.");
    }

    builder = (GuiceBuilder) builder.in(new Environment(args.getPath(), Helpers.class.getClassLoader(), Mode.TEST));
    builder = (GuiceBuilder) builder.configure(args.getConfig());
    if (args.getGlobal().isPresent()) {
      GlobalSettings global = args.getGlobal().get();
      if (builder instanceof GuiceApplicationBuilder) {
        builder = ((GuiceApplicationBuilder) builder).global(global);
      } else {
        play.api.GlobalSettings scalaGlobal = new play.core.j.JavaGlobalSettingsAdapter(global);
        builder = (GuiceBuilder) builder.bindings(package$.MODULE$.bind(play.api.GlobalSettings.class).toInstance(scalaGlobal));
      }
    }
    builder = (GuiceBuilder) builder.overrides(args.getOverrides().toArray(new Binding[args.getOverrides().size()]));
    return builder.injector().instanceOf(Application.class);
  }
}
