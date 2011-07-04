package com.applicake.beanstalkclient;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ApplicationSettingsActivity extends PreferenceActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.application_preferences);

  }

}
