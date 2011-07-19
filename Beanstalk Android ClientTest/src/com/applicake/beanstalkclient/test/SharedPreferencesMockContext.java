package com.applicake.beanstalkclient.test;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.mock.MockContext;

public class SharedPreferencesMockContext extends MockContext {

  private static final String PREFIX = "test.";

  private Context mBaseContext;

  public SharedPreferencesMockContext(Context context) {
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