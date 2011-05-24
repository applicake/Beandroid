package com.applicake.beanstalkclient;

import java.io.IOException;
import com.applicake.beanstalkclient.enums.UserType;
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

public class UserCreateNewActivity extends BeanstalkActivity implements OnClickListener {
	private Button createButton;
	private Context mContext;

	private EditText loginEditText;
	private EditText nameEditText;
	private EditText lastNameEditText;
	private EditText emailEditText;
	private EditText timezoneEditText;
	private CheckBox adminCheckBox;
	private EditText passwordEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_create_new_layout);
		mContext = this;

		loginEditText = (EditText) findViewById(R.id.loginEditText);
		nameEditText = (EditText) findViewById(R.id.nameEditText);
		lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
		emailEditText = (EditText) findViewById(R.id.emailEditText);
		timezoneEditText = (EditText) findViewById(R.id.timezoneEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		adminCheckBox = (CheckBox) findViewById(R.id.adminCheckBox);
		createButton = (Button) findViewById(R.id.createButton);
		createButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.createButton) {
			new SendUserCreateTask().execute();
		}

	}

	public class SendUserCreateTask extends AsyncTask<Void, Void, Integer> {

		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait...",
					"creating user");
			super.onPreExecute();
		}

		protected Integer doInBackground(Void... params) {

			XmlCreator xmlCreator = new XmlCreator();
			HttpSender httpSender = new HttpSender();
			try {
				String userCreateXml = xmlCreator.createNewUserXML(
						loginEditText.getText().toString().trim(), 
						nameEditText.getText().toString().trim(), 
						lastNameEditText.getText().toString().trim(),
						emailEditText.getText().toString().trim(),
						adminCheckBox.isChecked(), 
						passwordEditText.getText().toString().trim());
				return httpSender.sendCreateUserXML(prefs, userCreateXml);

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
				GUI.displayMonit(mContext, "user was created!");
				setResult(Constants.REFRESH_ACTIVITY);
				finish();

			}

			super.onPostExecute(result);
		}

	}

}
