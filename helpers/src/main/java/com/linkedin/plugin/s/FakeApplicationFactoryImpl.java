package com.linkedin.plugin.s;

import play.api.Application;
import play.api.Environment;
import play.api.GlobalSettings;
import play.api.Mode;
import play.api.inject.guice.GuiceApplicationBuilder;
import play.api.inject.guice.GuiceBuilder;
import play.api.inject.package$;
import play.api.mvc.Handler;
import play.api.test.FakeApplication;
import play.api.test.Helpers;
import play.libs.Scala;
import scala.PartialFunction$;
import scala.Tuple2;

import java.util.Collections;

/**
 * Default implementation for building an Application. If a builder isn't specified and no overrides are present the Play
 * FakeApplication is used. Otherwise a builder is used. Using @WithPlugins and a builder or overrides isn't supported.
 */
public class FakeApplicationFactoryImpl implements FakeApplicationFactory {
  @Override
  public Application buildScalaApplication(FakeApplicationFactoryArgs args) {
    return shouldUseBuilder(args) ? buildFromBuilder(args) : buildFromFakeApp(args);
  }

  protected boolean shouldUseBuilder(FakeApplicationFactoryArgs args) {
    return args.getBuilderClass().isPresent() || !args.getOverrides().isEmpty();
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

  protected Application buildFromFakeApp(FakeApplicationFactoryArgs args) {
    return new FakeApplication(
        args.getPath(),
        Helpers.class.getClassLoader(),
        Scala.toSeq(args.getPlugins()),
        Scala.toSeq(Collections.<String>emptyList()),
        Scala.asScala(args.getConfig()),
        scala.Option.apply(args.getGlobal().orElse(null)),
        PartialFunction$.MODULE$.<Tuple2<String, String>, Handler>empty()
    );

  }
}
