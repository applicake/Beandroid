package com.applicake.beanstalkclient;

import com.applicake.beanstalkclient.enums.UserType;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class HomeActivity extends BeanstalkActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start2);
		
		// hide home button
		findViewById(R.id.ActionBarHomeIcon).setVisibility(View.GONE);
		
		// manage flows for various user types
		
		Log.w("usertype", currentUser);
		
		if (currentUser != UserType.OWNER.name()){
			findViewById(R.id.account_settings_button).setVisibility(View.GONE);
		}
		if (currentUser == UserType.USER.name()){
			findViewById(R.id.users_button).setVisibility(View.GONE);
		}
		
		// set title 
		TextView title = (TextView) findViewById(R.id.ActionBarTitle);
		title.setText("Beansdroid");

	}

	// button handlers
	
	public void onHomeDashboardClick(View v){
		Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
		startActivityForResult(intent, 0);
		
	}

	public void onHomeRepositoriesClick(View v){
		Intent intent = new Intent(getApplicationContext(), RepositoriesActivity.class);
		startActivityForResult(intent, 0);
	}
	
	public void onHomeUsersClick(View v){
		Intent intent = new Intent(getApplicationContext(), UserActivity.class);
		startActivityForResult(intent, 0);
	}
	
	public void onHomeDeploymentClick(View v){
		Toast.makeText(getApplicationContext(), "Deploymnet clicked", Toast.LENGTH_SHORT).show();
	}
	
	public void onHomeSettingsClick(View v){
		Intent intent = new Intent(getApplicationContext(), AccountSettingsActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != Constants.CLEAR_STACK_UP_TO_HOME) super.onActivityResult(requestCode, resultCode, data);
	}

}
