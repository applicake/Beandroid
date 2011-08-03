package com.applicake.beanstalkclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.enums.UserType;

public class HomeActivity extends BeanstalkActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

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
      hideUsersActionBarButton();
      findViewById(R.id.users_button).setVisibility(View.GONE);
    }

    // set title
    // TextView title = (TextView) findViewById(R.id.ActionBarTitle);
    // title.setText("Beansdroid");

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
	  startActivityForResult(RepositoryDeploymentsActivity.generateIntentForOverallRepositories(this), 0);
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
  
  protected void hideUsersActionBarButton() {
    hideViews(R.id.ActionBarUsersButton, R.id.actionBarUsersSeparator);
  }
  
  private void hideViews(int... resIds) {
    for(int resId : resIds) {
      findViewById(resId).setVisibility(View.GONE);
    }
  }

}
