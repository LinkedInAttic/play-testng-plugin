package com.linkedin.plugin.s;

import play.api.Application;

/**
 * Knows how to build applications using the data that is available from annotations.
 */
public interface FakeApplicationFactory {
  Application buildScalaApplication(FakeApplicationFactoryArgs args);
}
