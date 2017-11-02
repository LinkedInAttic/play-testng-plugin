package com.linkedin.plugin.j;

import javax.annotation.Nullable;
import play.GlobalSettings;
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

  @Deprecated
  private final GlobalSettings _global;

  @Deprecated @SuppressWarnings("unused")
  public FakeApplicationFactoryArgs(File path,
                                    Optional<Class<? extends GuiceBuilder>> builderClass,
                                    Optional<GlobalSettings> global,
                                    List<Binding<?>> overrides,
                                    Map<String, Object> config,
                                    List<String> plugins) {
    _path = path;
    _builderClass = builderClass.orElse(null);
    _global = global.orElse(null);
    _overrides = overrides;
    _config = config;
  }

  @SuppressWarnings("deprecation") // because we need to nil-out the deprecated `_global` member variable
  public FakeApplicationFactoryArgs(File path,
                                    @Nullable Class<? extends GuiceBuilder> builderClass,
                                    List<Binding<?>> overrides,
                                    Map<String, Object> config) {
    _path = path;
    _builderClass = builderClass;
    _global = null;
    _overrides = overrides;
    _config = config;
  }

  public File getPath() {
    return _path;
  }

  public Optional<Class<? extends GuiceBuilder>> getBuilderClass() {
    return Optional.ofNullable(_builderClass);
  }

  @Deprecated
  public Optional<GlobalSettings> getGlobal() {
    return Optional.ofNullable(_global);
  }

  public List<Binding<?>> getOverrides() {
    return _overrides;
  }

  public Map<String, Object> getConfig() {
    return _config;
  }
}
