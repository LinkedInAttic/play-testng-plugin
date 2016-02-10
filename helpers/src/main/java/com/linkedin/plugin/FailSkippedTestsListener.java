package com.linkedin.plugin;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * Created by rli on 2/9/16.
 *
 * This is a workaround for a bug in sbt testng plugin that skipped tests won't fail the entire test suite.
 */
public class FailSkippedTestsListener extends TestListenerAdapter {

  @Override
  public void onTestSkipped(ITestResult testResult) {
    testResult.setStatus(ITestResult.FAILURE);
  }
}
