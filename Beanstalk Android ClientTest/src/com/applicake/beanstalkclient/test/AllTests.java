package com.applicake.beanstalkclient.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.test.suitebuilder.TestSuiteBuilder;

/**
 * <p>A test suite containing all tests.</p>
 * 
 * <p>To run all suites found in this apk:<br/>
 * <pre>$ adb shell am instrument -w \
 *   com.applicake.beanstalkclient.test/android.test.InstrumentationTestRunner</pre></p>
 *
 * <p>To run just this suite from the command line:<br/>
 * <pre>$ adb shell am instrument -w \
 *   -e class com.applicake.beanstalkclient.test.AllTests \
 *   com.applicake.beanstalkclient.test/android.test.InstrumentationTestRunner</pre></p>
 *
 * <p>To run an individual test case, e.g. {@link com.applicake.beanstalkclient.test.YamlTesting}:<br/>
 * <pre>$ adb shell am instrument -w \
 *   -e class com.applicake.beanstalkclient.test.YamlTesting \
 *   com.applicake.beanstalkclient.test/android.test.InstrumentationTestRunner</pre></p>
 *
 * <p>To run an individual test, e.g. {@link com.applicake.beanstalkclient.test.YamlTesting#testCustomYamlParser()}:<br/>
 * <pre>$ adb shell am instrument -w \
 *   -e class com.applicake.beanstalkclient.test.YamlTesting#testCustomYamlParser \
 *   com.applicake.beanstalkclient.test/android.test.InstrumentationTestRunner</pre></p>
 */
public class AllTests extends TestSuite {
  public static Test suite() {
    return new TestSuiteBuilder(AllTests.class).includeAllPackagesUnderHere().build();
  }
}
