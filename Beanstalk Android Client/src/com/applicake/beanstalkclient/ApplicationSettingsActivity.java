package com.applicake.beanstalkclient;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.CheckBox;

public class ApplicationSettingsActivity extends PreferenceActivity {

  private CheckBoxPreference notificationsPreference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.application_preferences);
    notificationsPreference = (CheckBoxPreference) findPreference("auto_update_notification_service");

  }

  @Override
  public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
      Preference preference) {
    // TODO change preferences to R.strings
    if (preference.getKey().equals("auto_update_notification_service")) {
      if (notificationsPreference.isChecked())
        SyncService.initializeService(getApplicationContext(), 1);
      else
        SyncService.stopService(getApplicationContext());
    }
    return super.onPreferenceTreeClick(preferenceScreen, preference);
  }
}
