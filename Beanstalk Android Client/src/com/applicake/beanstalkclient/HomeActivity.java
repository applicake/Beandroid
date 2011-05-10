package com.applicake.beanstalkclient;

import android.content.Intent;
import android.os.Bundle;
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
		Toast.makeText(getApplicationContext(), "Repositories clicked", Toast.LENGTH_SHORT).show();
	}
	
	public void onHomeUsersClick(View v){
		Toast.makeText(getApplicationContext(), "Users clicked", Toast.LENGTH_SHORT).show();
	}
	
	public void onHomeDeploymentClick(View v){
		Toast.makeText(getApplicationContext(), "Deploymnet clicked", Toast.LENGTH_SHORT).show();
	}
	
	public void onHomeSettingsClick(View v){
		Toast.makeText(getApplicationContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
	}

}
