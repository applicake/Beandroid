/* poor tests checking if the service is running in the background
 * 
 */

package com.applicake.beanstalkclient.test;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ServiceTestCase;
import android.test.mock.MockApplication;
import android.util.Log;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.SyncService;


public class NotificationsTests extends ServiceTestCase<SyncService> {

  
  private SharedPreferences prefs;
  private Context context;

  public NotificationsTests() {
    super(SyncService.class);
    // TODO Auto-generated constructor stub
  }
  

  public static class Tester {

    private static int numberOfRuns = 0;

    public static int getNumebrOfRuns() {
      return numberOfRuns;
    }

    public static void reset() {
      numberOfRuns = 0;
    }

    public static void increment() {
      numberOfRuns++;
    }

  }
  

  @Override
  protected void setUp() throws Exception {
   
    final SharedPreferencesMockContext mockContext = new SharedPreferencesMockContext(getContext());
    MockApplication mockApplication = new MockApplication(){
      @Override
      public Context getApplicationContext() {
        Log.d("tests", "Im here");
        return mockContext;
      }
    };
    

    context = mockContext;
    setApplication(mockApplication);
    prefs = PreferenceManager.getDefaultSharedPreferences(context);
    prefs.edit().clear().commit();

    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    // clearing preferences
    prefs.edit().clear().commit();
    super.tearDown();
  }

  public void testServiceInitializationAndStopCredsStoredNotificationsEnabled() throws InterruptedException {
    
    notificationPreferencesSetter(prefs, true, "1", true);
    
    // starting service
    SyncService.initializeService(context);
    // checking if the service is scheduled
    Thread.sleep(1000);
    assertTrue(SyncService.isServiceRunning(context));

    // stopping service
    SyncService.stopService(context);
    Thread.sleep(1000);
    // checking if the service is not scheduled any more
    assertFalse(SyncService.isServiceRunning(context));

  }
  
  public void testServiceInitializationAndStopCredsNotStoredNotificationsEnabled() throws InterruptedException {
    
    notificationPreferencesSetter(prefs, true, "1", false);
    
    // starting service
    SyncService.initializeService(context);
    Thread.sleep(1000);
    // checking if the service is scheduled
    assertFalse(SyncService.isServiceRunning(context));
    
    // stopping service
    SyncService.stopService(context);
    Thread.sleep(1000);
    // checking if the service is not scheduled any more
    assertFalse(SyncService.isServiceRunning(context));
    
  }
  
  public void testServiceInitializationAndStopCredsNotStoredNotificationsNotEnabled() throws InterruptedException {
    
    notificationPreferencesSetter(prefs, false, "1", false);
    
    // starting service
    SyncService.initializeService(context);
    Thread.sleep(1000);
    // checking if the service is scheduled
    assertFalse(SyncService.isServiceRunning(context));
    
    // stopping service
    SyncService.stopService(context);
    Thread.sleep(1000);
    // checking if the service is not scheduled any more
    assertFalse(SyncService.isServiceRunning(context));
    
  }
  
  public static void notificationPreferencesSetter(SharedPreferences prefs, boolean isOn, String delay, boolean credentailsStored){
    SharedPreferences.Editor editor = prefs.edit();
    editor.putBoolean(Constants.CREDENTIALS_STORED, credentailsStored);
    editor.putBoolean(Constants.AUTO_UPDATE_NOTIFICATION_SERVICE, isOn);
    editor.putString(Constants.AUTO_UPDATE_NOTIFICATION_SERVICE_DELAY, delay);
    
    // setup credentials
    editor.putString(Constants.USER_ACCOUNT_DOMAIN, SecretData.USER_ACCOUNT_DOMAIN);
    editor.putString(Constants.USER_LOGIN, SecretData.USER_LOGIN);
    editor.putString(Constants.USER_PASSWORD, SecretData.PASSWORD);
    editor.commit();
    
  }

}
