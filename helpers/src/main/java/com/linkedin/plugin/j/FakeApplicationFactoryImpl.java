package com.linkedin.plugin.j;

import play.Application;
import play.Environment;
import play.GlobalSettings;
import play.Mode;
import play.api.inject.Binding;
import play.api.inject.package$;
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

  /**
   * @deprecated We now always use a {@link GuiceApplicationBuilder}.
   */
  @SuppressWarnings("unused")
  @Deprecated
  protected boolean shouldUseBuilder(FakeApplicationFactoryArgs args) {
    return true;
  }

  /**
   * @deprecated Play's FakeApplication is deprecated-- this will use a default GuiceApplicationBuilder instead,
   * as though {@link FakeApplicationFactoryImpl#buildFromBuilder(FakeApplicationFactoryArgs)} was called.
   */
  @Deprecated
  protected Application buildFromFakeApp(FakeApplicationFactoryArgs args) {
    return buildFromBuilder(args);
  }

  @SuppressWarnings({"unchecked", "deprecated"})
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
