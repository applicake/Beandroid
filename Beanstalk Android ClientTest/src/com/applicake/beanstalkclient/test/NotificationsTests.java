package com.applicake.beanstalkclient.test;

import com.applicake.beanstalkclient.HomeActivity;
import com.applicake.beanstalkclient.SyncService;

import android.app.Application;
import android.test.ActivityUnitTestCase;
import android.test.AndroidTestCase;
import android.test.ServiceTestCase;

public class NotificationsTests extends ServiceTestCase<SyncService> {
  
  


  public NotificationsTests() {
    super(SyncService.class);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected void setUp() throws Exception {
    
    super.setUp();
  }
  
  @Override
  protected void tearDown() throws Exception {
    // TODO Auto-generated method stub
    super.tearDown();
  }
 
  public void testServiceInitialization(){
    SyncService.initializeService(getService());
    assertTrue(SyncService.isServiceRunning(getService()));
  }
  
  

}
