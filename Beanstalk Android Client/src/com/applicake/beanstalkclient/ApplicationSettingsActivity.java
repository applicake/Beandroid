package com.applicake.beanstalkclient;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class ApplicationSettingsActivity extends PreferenceActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.application_preferences);
    

  }
  @Override
  public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
      Preference preference) {
    // TODO change preferences to R.strings
    if (preference.getKey().equals("auto_update_notification_service")){
      SyncService.initializeService(getApplicationContext(), 1);
    }
    return super.onPreferenceTreeClick(preferenceScreen, preference);
  }

}
