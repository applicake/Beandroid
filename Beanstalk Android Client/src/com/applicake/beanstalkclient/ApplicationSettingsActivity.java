package com.applicake.beanstalkclient;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.applicake.beanstalkclient.utils.GUI;

public class ApplicationSettingsActivity extends PreferenceActivity {

  protected static final String TAG = ApplicationSettingsActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    Log.d(TAG, "on create");
    addPreferencesFromResource(R.xml.application_preferences);
    final CheckBoxPreference credentialsStoredCheckboxPreference = (CheckBoxPreference) findPreference("remember_me_preference");

    final CheckBoxPreference notificationsCheckboxPreference = (CheckBoxPreference) findPreference(Constants.AUTO_UPDATE_NOTIFICATION_SERVICE);
    final ListPreference notificationsDelayPreference = (ListPreference) findPreference(Constants.AUTO_UPDATE_NOTIFICATION_SERVICE_DELAY);
    // final CheckBoxPreference notificationsCustomLEDCheckboxPreference =
    // (CheckBoxPreference)
    // findPreference(Constants.AUTO_UPDATE_NOTIFICATION_SERVICE_CUSTOM_LED);
    //
    // final PreferenceCategory notificationPreferenceCategory =
    // (PreferenceCategory)
    // findPreference("auto_update_notification_preferences");
    //
    credentialsStoredCheckboxPreference
        .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

          @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            Boolean newBooleanValue = (Boolean) newValue;
            if (newBooleanValue && notificationsCheckboxPreference.isChecked()) {
              SyncService.initializeService(getApplicationContext());

            } else if (!newBooleanValue && notificationsCheckboxPreference.isChecked()) {
              SyncService.stopService(getApplicationContext());
              notificationsCheckboxPreference.setChecked(false);

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
              SyncService.updateServiceReloadTime(getApplicationContext(), newIntegerValue);
            }
            Log.d(TAG, "the new value equals " + newValue.toString());
            return true;
          }
        });

    notificationsCheckboxPreference
        .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
          @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (!credentialsStoredCheckboxPreference.isChecked()) {
              GUI.displayMonit(getApplicationContext(),
                  "Notifications require \"Store credentails\" preference to be enabled");
              return false;
            } else {
              Boolean newBooleanValue = (Boolean) newValue;

              if (newBooleanValue.equals(true))
                SyncService.initializeService(getApplicationContext());
              else
                SyncService.stopService(getApplicationContext());
              return true;
            }
          }
        });

  }
}
