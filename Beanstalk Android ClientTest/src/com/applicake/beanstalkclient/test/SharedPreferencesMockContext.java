package com.applicake.beanstalkclient.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.RenamingDelegatingContext;
import android.test.mock.MockContext;



public class SharedPreferencesMockContext extends MockContext {
  private static final String TAG = "MyMockContext";

  private static final String MOCK_PREFIX = "test.";

  
  
  
  @Override
  public SharedPreferences getSharedPreferences(String name, int mode) {
    // TODO Auto-generated method stub
    return super.getSharedPreferences(name, mode);
  }


}