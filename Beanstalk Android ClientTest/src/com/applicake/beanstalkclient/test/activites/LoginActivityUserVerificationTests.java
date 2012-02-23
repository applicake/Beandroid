package com.applicake.beanstalkclient.test.activites;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.test.AndroidTestCase;
import android.util.Log;

import com.applicake.beanstalkclient.activities.LoginActivity;
import com.applicake.beanstalkclient.activities.LoginActivity.AuthorizationData;
import com.applicake.beanstalkclient.test.SecretData;
import com.applicake.beanstalkclient.enums.UserType;

public class LoginActivityUserVerificationTests extends
    AndroidTestCase {

  public LoginActivityUserVerificationTests() {
    super();
    // TODO Auto-generated constructor stub
  }

  String[] ownerCorrect = { "bartosz-filipowicz", "bartoszfilipowicz",
      SecretData.PASSWORD }; // OWNER
  String[] adminCorrect = { "bartosz-filipowicz", "login", "password" }; // ADMIN
  String[] userCorrect = { "bartosz-filipowicz", "user", "test" }; // USER
  String[] ownerIncorrect = { "bartosz-filipowicz", "bartoszfilipowicz",
      SecretData.PASSWORD + "xx" }; // OWNER INCORRECT
  String[] adminIncorrect = { "bartosz-filipowicz", "login", "password"+"xx" }; // ADMIN
  // INCORRECT
  String[] userIncorrect = { "bartosz-filipowicz", "user", "test"+"xx" }; // USER
  // INCORRECT
  private LoginActivity baseActivity;

  @Override
  protected void setUp() throws Exception {
    Log.d("tests", "setup");
    baseActivity = new LoginActivity();
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Log.d("tests", "teardown");
    super.tearDown();
  }
  

  // if this test fails it means the device doesn't have network connection
  public void testDeviceInternetConnection(){
    String strUrl = "http://stackoverflow.com/about";

    try {
      URL url = new URL(strUrl);
      HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
      urlConn.connect();

      assertEquals(HttpURLConnection.HTTP_OK, urlConn.getResponseCode());
    } catch (IOException e) {
      fail("Error creating HTTP connection");
    }

  }

  public void testOwnerUserTypeRecogintion() throws Throwable {

    AuthorizationData authData;
    authData = baseActivity.authenticateAndCheckUserType(ownerCorrect[0],
        ownerCorrect[1], ownerCorrect[2]);
    assertEquals(UserType.OWNER, authData.getUser().getUserType());
    
    try {
       baseActivity.authenticateAndCheckUserType(ownerIncorrect[0],
          ownerIncorrect[1], ownerIncorrect[2]);
      fail();
    } catch (Exception e) {
    }

  }
  
  public void testAdminUserTypeRecogintion() throws Throwable {
    
    AuthorizationData authData;
    authData = baseActivity.authenticateAndCheckUserType(adminCorrect[0],
        adminCorrect[1], adminCorrect[2]);
    assertEquals(UserType.ADMIN, authData.getUser().getUserType());
    
    try {
      baseActivity.authenticateAndCheckUserType(adminIncorrect[0],
          adminIncorrect[1], adminIncorrect[2]);
      fail();
    } catch (Exception e) {
    }
    
  }
  public void testUserUserTypeRecogintion() throws Throwable {
    
    AuthorizationData authData;
    authData = baseActivity.authenticateAndCheckUserType(userCorrect[0],
        userCorrect[1], userCorrect[2]);
    assertEquals(UserType.USER, authData.getUser().getUserType());
    
    try {
      baseActivity.authenticateAndCheckUserType(userIncorrect[0],
          userIncorrect[1], userIncorrect[2]);
      fail();
    } catch (Exception e) {
    }
    
  }

}
