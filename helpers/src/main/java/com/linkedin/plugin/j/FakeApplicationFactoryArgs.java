package com.linkedin.plugin.j;

import play.GlobalSettings;
import play.api.inject.Binding;
import play.inject.guice.GuiceBuilder;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeApplicationFactoryArgs {
  private final File _path;
  private final Optional<Class<? extends GuiceBuilder>> _builderClass;
  private final Optional<GlobalSettings> _global;
  private final List<Binding<?>> _overrides;
  private final Map<String, Object> _config;
  private final List<String> _plugins;

  public FakeApplicationFactoryArgs(File path,
                                    Optional<Class<? extends GuiceBuilder>> builderClass,
                                    Optional<GlobalSettings> global,
                                    List<Binding<?>> overrides,
                                    Map<String, Object> config,
                                    List<String> plugins) {
    _path = path;
    _builderClass = builderClass;
    _global = global;
    _overrides = overrides;
    _config = config;
    _plugins = plugins;
  }

  public File getPath() {
    return _path;
  }

  public Optional<Class<? extends GuiceBuilder>> getBuilderClass() {
    return _builderClass;
  }

  public Optional<GlobalSettings> getGlobal() {
    return _global;
  }

  public List<Binding<?>> getOverrides() {
    return _overrides;
  }

  public Map<String, Object> getConfig() {
    return _config;
  }

  public List<String> getPlugins() {
    return _plugins;
  }
}
