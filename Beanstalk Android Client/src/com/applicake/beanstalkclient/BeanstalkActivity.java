package com.applicake.beanstalkclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public abstract class BeanstalkActivity extends Activity {
  // on create

  protected SharedPreferences prefs;
  protected String currentUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    currentUser = prefs.getString(Constants.USER_TYPE, "");

  }

  // inflate menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.options_menu, menu);
    return true;
  }

  // options menu handling
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
    case R.id.settings:
      startActivity(new Intent(this, ApplicationSettingsActivity.class));
      return true;
    case R.id.logout:
      logout();
      return true;
    case R.id.exit:
      exitApplication();
      return true;
    default:
      return true;
    }
  }

  public void clearCredentials() {
    Editor editor = prefs.edit();
    editor.putBoolean(Constants.CREDENTIALS_STORED, false);
    editor.putString(Constants.USER_ACCOUNT_DOMAIN, "");
    editor.putString(Constants.USER_LOGIN, "");
    editor.putString(Constants.USER_PASSWORD, "");
    editor.putBoolean(Constants.REMEBER_ME_CHECKBOX, false);
    editor.commit();
  }

  public void logout() {
    clearCredentials();
    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
    setResult(Constants.CLOSE_ALL_BUT_LOGOUT);
    finish();
  }

  // closing all activities mechanism
  public void exitApplication() {
    if (!prefs.getBoolean(Constants.CREDENTIALS_STORED, false)) {
      clearCredentials();
    }
    setResult(Constants.CLOSE_ALL_ACTIVITIES);
    finish();
  }

  // synchronizing killing all activities with exit action
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (resultCode) {
    case Constants.CLOSE_ALL_ACTIVITIES:
      if (!prefs.getBoolean(Constants.REMEBER_ME_CHECKBOX, false))
        clearCredentials();
      setResult(Constants.CLOSE_ALL_ACTIVITIES);
      finish();
      break;
    case Constants.CLOSE_ALL_BUT_LOGOUT:
      setResult(Constants.CLOSE_ALL_BUT_LOGOUT);
      finish();
      break;
    case Constants.CLEAR_STACK_UP_TO_HOME:
      finish();
    }

    // super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onDestroy() {
    cancelAllDownloadTasks();
    super.onDestroy();

  }

  protected void cancelAllDownloadTasks() {

  }

  // action bar handling
  public void onRepositoriesButtonClick(View v) {
    Intent intent = new Intent(getApplicationContext(), RepositoriesActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    setResult(Constants.CLEAR_STACK_UP_TO_HOME);
    startActivityForResult(intent, 0);
    if (!(this instanceof HomeActivity))
      finish();
  }

  public void onUsersButtonClick(View v) {
    Intent intent = new Intent(getApplicationContext(), UserActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    setResult(Constants.CLEAR_STACK_UP_TO_HOME);
    startActivityForResult(intent, 0);
    if (!(this instanceof HomeActivity))
      finish();
  }

  public void onDeploymentButtonClick(View v) {

    Toast.makeText(getApplicationContext(), "Deployment button clicked",
        Toast.LENGTH_SHORT).show();
  }

  public void onHomeButtonClick(View v) {
    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    setResult(Constants.CLEAR_STACK_UP_TO_HOME);
    startActivityForResult(intent, 0);
    if (!(this instanceof HomeActivity))
      finish();
  }

}
