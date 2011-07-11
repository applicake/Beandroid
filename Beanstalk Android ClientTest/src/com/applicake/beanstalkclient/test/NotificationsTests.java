/* poor tests checking if the service is running in the background
 * 
 */

package com.applicake.beanstalkclient.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.SyncService;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ServiceTestCase;
import android.test.mock.MockContext;


public class NotificationsTests extends ServiceTestCase<SyncService> {

  public static final String PREFIX = "test.";
  private SharedPreferences prefs;
  private CountDownLatch mSignal;
  private MyMockContext context;

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
  
  private static class MyMockContext extends MockContext {

    private Context mBaseContext;

    public MyMockContext(Context context) {
        mBaseContext = context;
    }
    
    @Override
    public String getPackageName() {
      return mBaseContext.getPackageName();
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return mBaseContext.getSharedPreferences(PREFIX + name, mode);
    }
    
    @Override
    public ContentResolver getContentResolver() {
      // TODO Auto-generated method stub
     return mBaseContext.getContentResolver();
    }
    
    @Override
    public Object getSystemService(String name) {
      // TODO Auto-generated method stub
     return mBaseContext.getSystemService(name);
    }
    
}


  @Override
  protected void setUp() throws Exception {
   
    MyMockContext mockContext = new MyMockContext(getContext());

    context = mockContext;
    mSignal = new CountDownLatch(1);
    // overriding SyncSerivces to enable communication between the service and
    // the test case
    testedService = new SyncService() {
      @Override
      public void onStart(Intent intent, int startId) {
        
        super.onStart(intent, startId);
        Tester.increment();
        mSignal.countDown();
        
      }
    };

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
    mSignal.await(5, TimeUnit.SECONDS);
    // checking if the service is scheduled
    assertTrue(SyncService.isServiceRunning(context));

    // stopping service
    SyncService.stopService(context);
    // checking if the service is not scheduled any more
    assertFalse(SyncService.isServiceRunning(context));

  }
  
  public void testServiceInitializationAndStopCredsNotStoredNotificationsEnabled() {
    
    notificationPreferencesSetter(prefs, true, "1", false);
    
    // starting service
    SyncService.initializeService(context);
    // checking if the service is scheduled
    assertTrue(SyncService.isServiceRunning(context));
    
    // stopping service
    SyncService.stopService(context);
    // checking if the service is not scheduled any more
    assertFalse(SyncService.isServiceRunning(context));
    
  }
  
  public void testServiceInitializationAndStopCredsNotStoredNotificationsNotEnabled() {
    
    notificationPreferencesSetter(prefs, true, "1", true);
    
    // starting service
    SyncService.initializeService(context);
    // checking if the service is scheduled
    assertTrue(SyncService.isServiceRunning(context));
    
    // stopping service
    SyncService.stopService(context);
    // checking if the service is not scheduled any more
    assertFalse(SyncService.isServiceRunning(context));
    
  }
  
  public static void notificationPreferencesSetter(SharedPreferences prefs, boolean isOn, String delay, boolean credentailsStored){
    prefs.edit().putBoolean(Constants.CREDENTIALS_STORED, credentailsStored).commit();
    prefs.edit().putBoolean(Constants.AUTO_UPDATE_NOTIFICATION_SERVICE, isOn).commit();
    prefs.edit().putString(Constants.AUTO_UPDATE_NOTIFICATION_SERVICE_DELAY, delay);
    
    // setup credentials
    prefs.edit().putString(Constants.USER_ACCOUNT_DOMAIN, SecretData.USER_ACCOUNT_DOMAIN);
    prefs.edit().putString(Constants.USER_LOGIN, SecretData.USER_LOGIN);
    prefs.edit().putString(Constants.USER_PASSWORD, SecretData.PASSWORD);
  }

}
