package com.applicake.beanstalkclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public abstract class BeanstalkActivity extends Activity {
	//on create
	
	protected SharedPreferences prefs;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
//		setContentView(R.layout.main_blank);
		
	}

	//inflate menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu, menu);
	    return true;
	}
	
	//options menu handling 
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.settings:
	        Toast.makeText(getApplicationContext(), "settings", Toast.LENGTH_SHORT).show();
	        return true;
	    case R.id.logout:
	    	Toast.makeText(getApplicationContext(), "logout", Toast.LENGTH_SHORT).show();
	    	logout();
	        return true;
	    case R.id.exit:
	    	Toast.makeText(getApplicationContext(), "exit", Toast.LENGTH_SHORT).show();
	    	exitApplication();
	    	return true;
	    default:
	        return true;
	    }
	}
	
	public void clearCredentials(){
		Editor editor = prefs.edit();
		editor.putBoolean(Constants.CREDENTIALS_STORED, false);
		editor.putString(Constants.USER_ACCOUNT_DOMAIN, "");
		editor.putString(Constants.USER_LOGIN, "");
		editor.putString(Constants.USER_PASSWORD, "");
		editor.putBoolean(Constants.REMEBER_ME_CHECKBOX, false);
		editor.commit();
	}
	
	public void logout(){
		clearCredentials();
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(intent);
		setResult(Constants.CLOSE_ALL_BUT_LOGOUT);
		finish();
	}
	//closing all activities mechanism 
	public void exitApplication(){
		setResult(Constants.CLOSE_ALL_ACTIVITIES);
		finish();
	}
	
	//synchronizing killing all activities with exit action
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch(resultCode)
	    {
	    case Constants.CLOSE_ALL_ACTIVITIES:
	    	if (!prefs.getBoolean(Constants.REMEBER_ME_CHECKBOX, false)) clearCredentials();
	        setResult(Constants.CLOSE_ALL_ACTIVITIES);
	        finish();
	        break;
	    case Constants.CLOSE_ALL_BUT_LOGOUT:
	    	setResult(Constants.CLOSE_ALL_BUT_LOGOUT);
	    	finish();
	    }
	   
	    	

//		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onDestroy() {
		cancelAllDownloadTasks();
		super.onDestroy();
		
	}
	
	protected void cancelAllDownloadTasks(){
		
	}


	//action bar handling
	public void onRepositoriesButtonClick(View v) {
		Intent intent = new Intent(getApplicationContext(), RepositoriesActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, 0);
	}
	
	public void onUsersButtonClick(View v) {
		Intent intent = new Intent(getApplicationContext(), UserActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, 0);
	}
	
	public void onDeploymentButtonClick(View v) {

		Toast.makeText(getApplicationContext(), "Deployment button clicked", Toast.LENGTH_SHORT).show();
	}
	
	public void onHomeButtonClick(View v) {
		Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, 0);
	}
	
}
