package com.applicake.beanstalkclient;

import java.io.IOException;

import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.GravatarDowloader;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class PermissionModifyActivity extends BeanstalkActivity implements
		OnClickListener {
	private User user;
	private Repository repository;
	private Permission permission;
	private Context mContext;

	private Spinner repoAccessSpinner;
	private Spinner deploymentAccessSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.permissions_modify_layout);
		mContext = this;

		Intent currnetIntent = getIntent();
		user = currnetIntent.getParcelableExtra(Constants.USER);
		repository = currnetIntent.getParcelableExtra(Constants.REPOSITORY);
		permission = currnetIntent.getParcelableExtra(Constants.PERMISSION);

		// fill with data
		ImageView userGravatar = (ImageView) findViewById(R.id.userGravatar);
		GravatarDowloader.getInstance().download(user.getEmail(), userGravatar);

		TextView userName = (TextView) findViewById(R.id.userName);
		userName.setText(user.getFirstName() + " " + user.getLastName());

		findViewById(R.id.colorLabel).getBackground().setLevel(
				repository.getColorLabelNo());
		((TextView) findViewById(R.id.repositoryTitle)).setText(repository.getTitle());
		((TextView) findViewById(R.id.repositoryName)).setText(repository.getName());

		Button removeButton = (Button) findViewById(R.id.deleteButton);
		removeButton.setOnClickListener(this);

		Button applyButton = (Button) findViewById(R.id.applyButton);
		applyButton.setOnClickListener(this);

		repoAccessSpinner = (Spinner) findViewById(R.id.repoAccessSpinner);
		deploymentAccessSpinner = (Spinner) findViewById(R.id.deploymentAccessSpinner);

		if (permission != null) {
			if (permission.isWriteAccess())
				repoAccessSpinner.setSelection(2);
			else if (permission.isReadAccess())
				repoAccessSpinner.setSelection(1);
		 if (permission.isFullDeploymentAccess()) 
			 deploymentAccessSpinner.setSelection(1);
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.deleteButton) {
			if (permission != null){
				new DeletePermissionsTask().execute(permission.getId());
			} else {
				GUI.displayMonit(mContext, "no changes were made");
				finish();
			}
		}

		if (v.getId() == R.id.applyButton) {
			if ((repoAccessSpinner.getSelectedItemPosition() == 0)
					&& (deploymentAccessSpinner.getSelectedItemPosition() == 0)) {
				if (permission != null){
					new DeletePermissionsTask().execute(permission.getId());
				} else {
					GUI.displayMonit(mContext, "no changes were made");
					finish();
				}
			} else {
				new ModifyPermissionsTask().execute(
						repoAccessSpinner.getSelectedItemPosition(),
						deploymentAccessSpinner.getSelectedItemPosition());

			}

		}

	}

	// this task creates and sends XML with modified permission data
	// the task takes two arguments where the first one represents repository
	// access and the second represents deployment access. Valid argument values
	// are : 0 - no access; 1 - read only; 2 - read and write access for
	// repository access and: 0 - read only; 1 - write access for deployment
	// access

	public class ModifyPermissionsTask extends AsyncTask<Integer, Void, Integer> {

		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait...",
					"changing user properties");
			super.onPreExecute();
		}

		protected Integer doInBackground(Integer... params) {
			int repoAccess = params[0];
			int deploymentAccess = params[1];

			XmlCreator xmlCreator = new XmlCreator();
			HttpSender httpSender = new HttpSender();
			try {
				boolean readAccess = (repoAccess != 0);
				boolean writeAccess = (repoAccess == 2);
				boolean deploymentBooleanAccess = (deploymentAccess == 1);
				String modifyPermissionXml = xmlCreator.createPermissionXML(
						String.valueOf(user.getId()), String.valueOf(repository.getId()),
						readAccess, writeAccess, deploymentBooleanAccess);

				return httpSender.sendPermissionXML(prefs, modifyPermissionXml);

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HttpSenderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Integer result) {
			progressDialog.cancel();
			if (result == 201) {
				GUI.displayMonit(mContext, "user permissions were modified!");
				setResult(Constants.REFRESH_ACTIVITY);
				finish();

			} else {
				GUI.displayMonit(mContext, "error");
			}

			super.onPostExecute(result);
		}

	}
	public class DeletePermissionsTask extends AsyncTask<Integer, Void, Integer> {
		
		ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait...",
			"removing permission");
			super.onPreExecute();
		}
		
		protected Integer doInBackground(Integer... params) {
			Integer permissionId = params[0];
			
			HttpSender httpSender = new HttpSender();
			try {
				
				return httpSender.sendDeletePermissionRequest(prefs, String.valueOf(permissionId));
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HttpSenderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
			
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			progressDialog.cancel();
			if (result == 200) {
				GUI.displayMonit(mContext, "user permission was removed!");
				setResult(Constants.REFRESH_ACTIVITY);
				finish();
				
			} else {
				GUI.displayMonit(mContext, "error " + String.valueOf(result));
			}
			
			super.onPostExecute(result);
		}
		
	}

}
