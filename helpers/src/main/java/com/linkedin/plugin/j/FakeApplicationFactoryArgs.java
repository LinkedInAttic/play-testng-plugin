package com.linkedin.plugin.j;

import javax.annotation.Nullable;
import play.api.inject.Binding;
import play.inject.guice.GuiceBuilder;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeApplicationFactoryArgs {
  private final File _path;
  private final Class<? extends GuiceBuilder> _builderClass;
  private final List<Binding<?>> _overrides;
  private final Map<String, Object> _config;

  public FakeApplicationFactoryArgs(File path,
                                    @Nullable Class<? extends GuiceBuilder> builderClass,
                                    List<Binding<?>> overrides,
                                    Map<String, Object> config) {
    _path = path;
    _builderClass = builderClass;
    _overrides = overrides;
    _config = config;
  }

  public File getPath() {
    return _path;
  }

  public Optional<Class<? extends GuiceBuilder>> getBuilderClass() {
    return Optional.ofNullable(_builderClass);
  }

  public List<Binding<?>> getOverrides() {
    return _overrides;
  }

  public Map<String, Object> getConfig() {
    return _config;
  }
}
