package com.applicake.beanstalkclient.test.activites;

import android.os.AsyncTask;
import android.test.SingleLaunchActivityTestCase;
import android.util.Log;

import com.applicake.beanstalkclient.activities.LoginActivity;
import com.applicake.beanstalkclient.test.SecretData;


public class LoginActivityTests extends SingleLaunchActivityTestCase<LoginActivity> {

  public LoginActivityTests() {
    super("com.applicake.beanstalkclient", LoginActivity.class);
    // TODO Auto-generated constructor stub
  }

  // sample test credentials
  // the values in the comments represent the validity of (domain name, login,
  // password) array
  // 1 means the value is valid, 0 means the value is invalid
  // e.g. 101 means that the domain name is valid, login is invalid and the
  // password is valid

  String[] validDomainValidLoginValidPassword = { "bartosz-filipowicz",
      "bartoszfilipowicz", SecretData.PASSWORD }; // 111
  String[] invalidDomainValidLoginValidPassword = { "bartosz-filipowicz12",
      "bartoszfilipowicz", SecretData.PASSWORD }; // 011
  String[] validDomainValidLoginInvalidPassword = { "bartosz-filipowicz",
      "bartoszfilipowicz", SecretData.PASSWORD + "xx" }; // 110
  String[] invalidDomainInvalidLoginInvalidPassword = { "bartosz-filipowicz12",
      "bartoszfilipowicz12", SecretData.PASSWORD + "xx" }; // 000
  String[] validDomainInvalidLoginValidPassword = { "bartosz-filipowicz",
      "bartoszfilipowicz12", SecretData.PASSWORD }; // 101
  String[] invalidDomainInvalidLoginValidPassword = { "bartosz-filipowicz",
      "bartoszfilipowicz12", SecretData.PASSWORD }; // 001
  String[] validDomainInvalidLoginInvalidPassword = { "bartosz-filipowicz",
      "bartoszfilipowicz12", SecretData.PASSWORD }; // 100
  String[] invalidDomainValidLoginInvalidPassword = { "1bartosz-filipowicz",
      "bartoszfilipowicz", SecretData.PASSWORD + "xx" }; // 010

  
  @Override
  protected void setUp() throws Exception {
    
    super.setUp();
    Log.d("tests", "setup");
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Log.d("tests", "teardown");
  }

  // verify login task tests [on-line]

  // the values in the comments represent the validity of (domain name, login,
  // password) array
  // 1 means the value is valid, 0 means the value is invalid
  // e.g. 101 means that the domain name is valid, login is invalid and the
  // password is valid

  // the VerifyLoginTaskResult is an AsyncTask which checks login credentials
  // returning HTTP status code from the request
  // the task also returns 500 if the API is not enabled on Beanstalk website,

  public void testVerifyLoginTaskResult111() throws Throwable {

    // 111
    RunnableTaskCouple testCouple = new RunnableTaskCouple(
        validDomainValidLoginValidPassword);
    runTestOnUiThread(testCouple.mRunnable);
    assertEquals("111", new Integer(200), testCouple.mTestVerifyLoginTask.get());
  }

  public void testVerifyLoginTaskResult110() throws Throwable {
    // 110
    RunnableTaskCouple testCouple = new RunnableTaskCouple(
        validDomainValidLoginInvalidPassword);
    runTestOnUiThread(testCouple.mRunnable);
    assertEquals("110", new Integer(401), testCouple.mTestVerifyLoginTask.get());
  }

  public void testVerifyLoginTaskResult101() throws Throwable {

    // 101
    RunnableTaskCouple testCouple = new RunnableTaskCouple(
        validDomainInvalidLoginValidPassword);
    runTestOnUiThread(testCouple.mRunnable);
    assertEquals("101", new Integer(401), testCouple.mTestVerifyLoginTask.get());
  }

  public void testVerifyLoginTaskResult100() throws Throwable {

    // 100
    RunnableTaskCouple testCouple = new RunnableTaskCouple(
        validDomainInvalidLoginInvalidPassword);
    runTestOnUiThread(testCouple.mRunnable);
    assertEquals("100", new Integer(401), testCouple.mTestVerifyLoginTask.get());
  }

  public void testVerifyLoginTaskResult011() throws Throwable {

    // 011
    RunnableTaskCouple testCouple = new RunnableTaskCouple(
        invalidDomainValidLoginValidPassword);
    runTestOnUiThread(testCouple.mRunnable);
    assertEquals("011", new Integer(302), testCouple.mTestVerifyLoginTask.get());
  }

  public void testVerifyLoginTaskResult001() throws Throwable {
    setUp();
    // 001
    RunnableTaskCouple testCouple = new RunnableTaskCouple(
        invalidDomainInvalidLoginValidPassword);
    runTestOnUiThread(testCouple.mRunnable);
    assertEquals("001", new Integer(401), testCouple.mTestVerifyLoginTask.get());

  }

  public void testVerifyLoginTaskResult010() throws Throwable {

    // 010
    RunnableTaskCouple testCouple = new RunnableTaskCouple(
        invalidDomainValidLoginInvalidPassword);
    runTestOnUiThread(testCouple.mRunnable);
    assertEquals("010", new Integer(302), testCouple.mTestVerifyLoginTask.get());

  }
  
  public void testVerifyLoginTaskResult000() throws Throwable {

    // 000
    RunnableTaskCouple testCouple = new RunnableTaskCouple(
        invalidDomainInvalidLoginInvalidPassword);
    runTestOnUiThread(testCouple.mRunnable);
    assertEquals("110", new Integer(302), testCouple.mTestVerifyLoginTask.get());

  }

  // helper class coupling a Runnable object with an AsyncTask for test
  // purposes
  // the constructor creates a Runnable task which executes the Task

  private class RunnableTaskCouple {
    public Runnable mRunnable;
    public AsyncTask<String, Void, Integer> mTestVerifyLoginTask;
    private LoginActivity activity;
    public RunnableTaskCouple(final String[] params) {
      activity = getActivity();
      Log.d("tests", activity.toString());
      mRunnable = new Runnable() {
      
        @Override
        public void run() {
          mTestVerifyLoginTask = activity.new VerifyLoginTask(){
            @Override
            protected void onPreExecute() {
             // override this method as it has nothing to do with the test subject
            }
            @Override
            protected void onPostExecute(Integer result) {
              // override this method as it has nothing to do with the test subject
            }
          };
          mTestVerifyLoginTask.execute(params);
        }
      };
    }
  }

}
