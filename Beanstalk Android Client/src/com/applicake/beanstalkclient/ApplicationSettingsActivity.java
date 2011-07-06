package com.applicake.beanstalkclient;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class ApplicationSettingsActivity extends PreferenceActivity {

  protected static final String TAG = ApplicationSettingsActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.application_preferences);
    CheckBoxPreference credentialsStoredCheckboxPreference = (CheckBoxPreference) findPreference("remember_me_preference");

    final CheckBoxPreference notificationsCheckboxPreference = (CheckBoxPreference) findPreference("auto_update_notification_service");
    final ListPreference notificationsDelayPreference = (ListPreference) findPreference("auto_update_notification_service_delay");

    if (credentialsStoredCheckboxPreference.isChecked()) {
      notificationsCheckboxPreference.setEnabled(true);
      notificationsDelayPreference.setEnabled(true);
    } else {
      notificationsCheckboxPreference.setEnabled(false);
      notificationsDelayPreference.setEnabled(false);
    }

    credentialsStoredCheckboxPreference
        .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

          @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            Boolean newBooleanValue = (Boolean) newValue;
            if (newBooleanValue) {
              notificationsCheckboxPreference.setEnabled(true);
              notificationsDelayPreference.setEnabled(true);
              if (notificationsCheckboxPreference.isChecked()){
                SyncService.initializeService(getApplicationContext(),
                    Integer.parseInt(notificationsDelayPreference.getValue()));
                
              }
            } else {
              notificationsCheckboxPreference.setEnabled(false);
              notificationsDelayPreference.setEnabled(false);
              if (notificationsCheckboxPreference.isChecked()){
                SyncService.initializeService(getApplicationContext(),
                    Integer.parseInt(notificationsDelayPreference.getValue()));
                notificationsCheckboxPreference.setChecked(false);
                
              }
            }
            return true;
          }
        });

    notificationsDelayPreference
        .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

          @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            // if the service is enabled, update it
            if (notificationsCheckboxPreference.isChecked()) {
              Integer newIntegerValue = (Integer.parseInt((String) newValue));
              SyncService.initializeService(getApplicationContext(), newIntegerValue);
            }
            Log.d(TAG, "the new value equals " + newValue.toString());
            return true;
          }
        });

    notificationsCheckboxPreference
        .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
          @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            Boolean newBooleanValue = (Boolean) newValue;

            if (newBooleanValue.equals(true))
              SyncService.initializeService(getApplicationContext(),
                  Integer.parseInt(notificationsDelayPreference.getValue()));
            else
              SyncService.stopService(getApplicationContext());
            return true;
          }
        });

  }

}
