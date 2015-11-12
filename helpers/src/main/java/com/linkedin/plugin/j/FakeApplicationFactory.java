package com.linkedin.plugin.j;

import play.Application;

/**
 * Knows how to build applications using the data that is available from annotations.
 */
public interface FakeApplicationFactory {

  Application buildApplication(FakeApplicationFactoryArgs args);

}
