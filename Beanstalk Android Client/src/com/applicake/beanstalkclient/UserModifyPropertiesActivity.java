package com.applicake.beanstalkclient;

import java.io.IOException;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class UserModifyPropertiesActivity extends BeanstalkActivity implements
		OnClickListener {
	private Button applyChangesButton;
	private Context mContext;

	private User user;

	private Button passwordChangeButton;
	private Dialog dialog;

	private EditText nameEditText;
	private EditText lastNameEditText;
	private EditText emailEditText;
	private EditText timezoneEditText;
	private CheckBox adminCheckBox;
	private EditText newPasswordEditText;
	private EditText retypedPasswordEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_modify_properties_layout);
		mContext = this;

		user = getIntent().getParcelableExtra(Constants.USER);

		nameEditText = (EditText) findViewById(R.id.nameEditText);
		lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
		emailEditText = (EditText) findViewById(R.id.emailEditText);
		timezoneEditText = (EditText) findViewById(R.id.timezoneEditText);
		adminCheckBox = (CheckBox) findViewById(R.id.adminCheckBox);

		passwordChangeButton = (Button) findViewById(R.id.passwordChangeButton);
		passwordChangeButton.setOnClickListener(this);

		applyChangesButton = (Button) findViewById(R.id.saveChangesButton);
		applyChangesButton.setOnClickListener(this);
		loadUserInfo();

	}

	private void loadUserInfo() {
		if (user != null) {
			nameEditText.setText(user.getFirstName());
			lastNameEditText.setText(user.getLastName());
			emailEditText.setText(user.getEmail());
			timezoneEditText.setText(user.getTimezone());
			UserType userType = user.getAdmin();
			if (userType == UserType.ADMIN) {
				adminCheckBox.setChecked(true);
			} else if (userType == UserType.USER) {
				adminCheckBox.setChecked(false);
			} else if (userType == UserType.OWNER) {
				adminCheckBox.setChecked(true);
				adminCheckBox.setEnabled(false);
			}

		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.saveChangesButton) {
			new SendUserPropertiesTask().execute();
		}

		if (v.getId() == R.id.passwordChangeButton) {
			showDialog(0);
			// do something send change password request

		}

	}

	// password change dialog with validation
	// the first field is validated to have at least 4 characters
	// both fields are validated against each other for equality
	@Override
	protected Dialog onCreateDialog(int id) {

		dialog = new Dialog(mContext);
		dialog.setContentView(R.layout.password_change_dialog);
		dialog.setTitle("password change");

		newPasswordEditText = (EditText) dialog.findViewById(R.id.newPasswordEditText);
		retypedPasswordEditText = (EditText) dialog
				.findViewById(R.id.retypePasswordEditText);

		Button saveButton = (Button) dialog.findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (newPasswordEditText.getText().toString().length() >= 4) {
					if (!newPasswordEditText.getText().toString()
							.equals(retypedPasswordEditText.getText().toString())) {
						GUI.displayMonit(dialog.getContext(), "the passwords don't match");
					} else {
						dialog.cancel();
						new SendUserPasswordTask().execute(newPasswordEditText.getText()
								.toString());
					}

				} else
					GUI.displayMonit(dialog.getContext(),
							"The password has to be at least 4 characters long");

			}
		});

		Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}

		});

		return dialog;
	}

	public class SendUserPropertiesTask extends AsyncTask<Void, Void, Integer> {

		ProgressDialog progressDialog;
		//temporary
		String errorMessage = "";

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait...",
					"changing user properties");
			super.onPreExecute();
		}

		protected Integer doInBackground(Void... params) {

			XmlCreator xmlCreator = new XmlCreator();
			HttpSender httpSender = new HttpSender();
			try {
				String userModificationXml = xmlCreator
						.createUserPropertiesChangeXML(nameEditText.getText().toString()
								.trim(), lastNameEditText.getText().toString().trim(),
								emailEditText.getText().toString().trim(),
								adminCheckBox.isChecked());
				return httpSender.sendUpdateUserXML(prefs, userModificationXml,
						String.valueOf(user.getId()));

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
				errorMessage = e.getMessage();
				e.printStackTrace();
			}
			return 0;

		}

		@Override
		protected void onPostExecute(Integer result) {
			progressDialog.cancel();
			if (result == 200) {
				
				GUI.displayMonit(mContext, "user properties were modified!");
				setResult(Constants.REFRESH_ACTIVITY);
				finish();

			} else if (result == 0){
				GUI.displayMonit(mContext, errorMessage);
			}
			

			super.onPostExecute(result);
		}

	}

	public class SendUserPasswordTask extends AsyncTask<String, Void, Integer> {

		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait...",
					"changing user password");
			super.onPreExecute();
		}

		protected Integer doInBackground(String... params) {

			XmlCreator xmlCreator = new XmlCreator();
			HttpSender httpSender = new HttpSender();
			try {
				String userPasswordModificationXml = xmlCreator
						.createPasswordChangeXML(params[0]);
				return httpSender.sendUpdateUserXML(prefs, userPasswordModificationXml,
						String.valueOf(user.getId()));

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
				GUI.displayMonit(mContext, "user password was modified!");

			}

			super.onPostExecute(result);
		}

	}

}
