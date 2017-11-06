package com.linkedin.plugin.s;

import play.api.Application;
import play.api.Environment;
import play.api.GlobalSettings;
import play.api.Mode;
import play.api.inject.guice.GuiceApplicationBuilder;
import play.api.inject.guice.GuiceBuilder;
import play.api.inject.package$;
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

  /**
   * @deprecated {@link play.api.test.FakeApplication} is deprecated, we always use Guice builders now.
   */
  @SuppressWarnings("unused")
  @Deprecated
  protected boolean shouldUseBuilder(FakeApplicationFactoryArgs args) {
    return true;
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
    builder = (GuiceBuilder) builder.in(new Environment(args.getPath(), play.test.Helpers.class.getClassLoader(), Mode.Test()));
    builder = (GuiceBuilder) builder.configure(Scala.asScala(args.getConfig()));
    if (args.getGlobal().isPresent()) {
      GlobalSettings global = args.getGlobal().get();
      if (builder instanceof GuiceApplicationBuilder) {
        builder = ((GuiceApplicationBuilder) builder).global(global);
      } else {
        builder = (GuiceBuilder) builder.bindings(Scala.varargs(package$.MODULE$.bind(play.api.GlobalSettings.class).toInstance(global)));
      }
    }
    builder = (GuiceBuilder) builder.overrides(Scala.asScala(args.getOverrides()));
    return builder.injector().instanceOf(Application.class);
  }

  /**
   * @deprecated {@link play.api.test.FakeApplication} is deprecated. This calls through to
   * {@link FakeApplicationFactoryImpl#buildFromBuilder(FakeApplicationFactoryArgs)} now.
   */
  @Deprecated
  protected Application buildFromFakeApp(FakeApplicationFactoryArgs args) {
    return buildFromBuilder(args);

  }
}
