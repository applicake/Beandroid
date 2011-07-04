package com.applicake.beanstalkclient.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.LoginActivity;
import com.applicake.beanstalkclient.enums.UserType;

import android.app.Service;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class LoginActivityUserVerificationTests extends
    ActivityInstrumentationTestCase2<LoginActivity> {

  public LoginActivityUserVerificationTests() {
    super("com.applicake.beanstalkclient", LoginActivity.class);
    // TODO Auto-generated constructor stub
  }

  String[] ownerCorrect = { "bartosz-filipowicz", "bartoszfilipowicz",
      SecretData.PASSWORD }; // OWNER
  String[] adminCorrect = { "bartosz-filipowicz", "login", "password" }; // ADMIN
  String[] userCorrect = { "bartosz-filipowicz", "user", "test" }; // USER
  String[] ownerIncorrect = { "bartosz-filipowicz", "bartoszfilipowicz",
      SecretData.PASSWORD + "xx" }; // OWNER INCORRECT
  String[] adminIncorrect = { "bartosz-filipowicz", "login", "password" }; // ADMIN
  // INCORRECT
  String[] userIncorrect = { "bartosz-filipowicz", "user", "test" }; // USER
  // INCORRECT
  private LoginActivity baseActivity;
  private SharedPreferences prefs;

  @Override
  protected void setUp() throws Exception {
    Log.d("tests", "setup");
    baseActivity = getActivity();
    prefs = PreferenceManager.getDefaultSharedPreferences(baseActivity);
    prefs.edit().clear().commit();
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Log.d("tests", "teardown");
    super.tearDown();
  }

  public void testOwnerUserTypeRecogintion() throws Throwable {
    RunnableTaskCouple testCouple = new RunnableTaskCouple(ownerCorrect);
    runTestOnUiThread(testCouple.mRunnable);
    testCouple.mSignal.await(30, TimeUnit.SECONDS);
    String userType = prefs.getString(Constants.USER_TYPE, "");
    assertEquals(UserType.OWNER.name(), userType);

  }

  public void testAdminUserTypeRecogintion() throws Throwable {
    RunnableTaskCouple testCouple = new RunnableTaskCouple(adminCorrect);
    runTestOnUiThread(testCouple.mRunnable);
    String userType = prefs.getString(Constants.USER_TYPE, "");
    assertEquals(UserType.ADMIN.name(), userType);

  }

  public void testUserUserTypeRecogintion() throws Throwable {
    RunnableTaskCouple testCouple = new RunnableTaskCouple(userCorrect);
    runTestOnUiThread(testCouple.mRunnable);
    String userType = prefs.getString(Constants.USER_TYPE, "");
    assertEquals(UserType.USER.name(), userType);

  }

  // helper class coupling a Runnable object with an AsyncTask for test
  // purposes
  // the constructor creates a Runnable task which executes the Task

  private class RunnableTaskCouple {
    public Runnable mRunnable;
    public LoginActivity.VerifyLoginTask mTestVerifyLoginTask;
    final CountDownLatch mSignal = new CountDownLatch(1);

    public RunnableTaskCouple(final String[] params) {
      mTestVerifyLoginTask = baseActivity.new VerifyLoginTask(){
        @Override
        protected void onPostExecute(Integer result) {
          super.onPostExecute(result);
          mSignal.countDown();
        }
      };
      mRunnable = new Runnable() {
        @Override
        public void run() {
          mTestVerifyLoginTask.execute(params);
        }
      };
    }
  }
}
