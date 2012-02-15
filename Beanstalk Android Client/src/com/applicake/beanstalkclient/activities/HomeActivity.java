package com.applicake.beanstalkclient.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.enums.UserType;

public class HomeActivity extends BeanstalkActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
     
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
      setContentView(R.layout.home_screen_activity_landscape);

    else
      setContentView(R.layout.home_screen_activity);

    // hide home button
    // findViewById(R.id.ActionBarHomeIcon).setVisibility(View.GONE);
    findViewById(R.id.ActionBarHomeIcon).setClickable(false);

    // manage flows for various user types

    Log.w("usertype", currentUser);

    if (!currentUser.equals(UserType.OWNER.name())) {
      findViewById(R.id.account_settings_button).setVisibility(View.GONE);
    }
    if (currentUser.equals(UserType.USER.name())) {
      findViewById(R.id.users_button).setVisibility(View.GONE);
    }

    // set title
    // TextView title = (TextView) findViewById(R.id.ActionBarTitle);
    // title.setText("Beansdroid");

  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {

    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      setContentView(R.layout.home_screen_activity_landscape);

    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
      setContentView(R.layout.home_screen_activity);
    }

    super.onConfigurationChanged(newConfig);
  }

  // button handlers

  public void onHomeDashboardClick(View v) {
    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
    startActivityForResult(intent, 0);

  }

  public void onHomeRepositoriesClick(View v) {
    Intent intent = new Intent(getApplicationContext(), RepositoriesActivity.class);
    startActivityForResult(intent, 0);
  }

  public void onHomeUsersClick(View v) {
    Intent intent = new Intent(getApplicationContext(), UserActivity.class);
    startActivityForResult(intent, 0);
  }

  public void onHomeDeploymentClick(View v) {
	  Intent intent = new Intent(this, NewRepositoryDeploymentsActivity.class);
	  intent.putExtra(Constants.REPOSITORY, Repository.generateFakeRepositoryForOverall());
	  intent.putExtra(Constants.OVERALL_REPOS, true);
	  startActivityForResult(intent, 0);
  }

  public void onHomeSettingsClick(View v) {
    Intent intent = new Intent(getApplicationContext(), AccountSettingsActivity.class);
    startActivityForResult(intent, 0);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Constants.CLEAR_STACK_UP_TO_HOME)
      super.onActivityResult(requestCode, resultCode, data);
  }

}
