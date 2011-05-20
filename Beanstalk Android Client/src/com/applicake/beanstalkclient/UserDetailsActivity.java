package com.applicake.beanstalkclient;

import java.io.UnsupportedEncodingException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.utils.GravatarDowloader;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;

public class UserDetailsActivity extends BeanstalkActivity implements OnClickListener{

	private User user;
	private Context mContext;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_details_layout);
		mContext = this;
		
		user = getIntent().getParcelableExtra(Constants.USER);
		UserType userType = user.getAdmin();
		ImageView userGravatar = (ImageView) findViewById(R.id.userGravatar);
		
		GravatarDowloader.getInstance().download(user.getEmail(), userGravatar);
		
		if (userType == UserType.ADMIN){
			findViewById(R.id.adminLabel).setVisibility(View.VISIBLE);
			
		} else if (userType == UserType.OWNER){
			findViewById(R.id.ownerLabel).setVisibility(View.VISIBLE);
			findViewById(R.id.adminLabel).setVisibility(View.GONE);
		}
		
		((TextView) findViewById(R.id.userName)).setText(user.getFirstName() + " " + user.getLastName());
		((TextView) findViewById(R.id.userLogin)).setText(user.getLogin());
		((TextView) findViewById(R.id.userEmail)).setText(user.getEmail());
		
		
		// add button listeners 
		Button userPermissionsButton = (Button) findViewById(R.id.buttonUserPermissions);
		Button modifyPropertiesButton = (Button) findViewById(R.id.buttonModifyProperties);
		Button deleteUserButton = (Button) findViewById(R.id.buttonDeleteUser);
		
		userPermissionsButton.setOnClickListener(this);
		modifyPropertiesButton.setOnClickListener(this);
		deleteUserButton.setOnClickListener(this);
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to delete this user?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                new sendDeleteUserRequest().execute();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		
		return alert;
	}

	@Override
	public void onClick(View v) {
		
		if (v.getId() == R.id.buttonUserPermissions){
			Intent intent = new Intent(getApplicationContext(), UserPermissionsActivity.class);
			intent.putExtra(Constants.USER_ID, String.valueOf(user.getId()));
			startActivityForResult(intent, 0);
	
		}
		
		if (v.getId() == R.id.buttonModifyProperties){
			Intent intent = new Intent(getApplicationContext(), UserModifyPropertiesActivity.class);
			intent.putExtra(Constants.USER, user);
			startActivityForResult(intent, 0);
		}
		
		if (v.getId() == R.id.buttonDeleteUser){
			showDialog(0);
	
		}
		
	}
	
	public class sendDeleteUserRequest extends AsyncTask<Void, Void, Integer>{
		
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait...", "deleting user");
			super.onPreExecute();
		}
		

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				return new HttpSender().sendDeleteUserRequest(prefs, String.valueOf(user.getId()));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			} catch (HttpSenderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
			
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			
			progressDialog.cancel();
			if (result == 200){
				GUI.displayMonit(mContext, "The user was deleted!");
				finish();
			} 
			

			super.onPostExecute(result);
		}
		
	}

}
