package com.applicake.beanstalkclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private EditText domainaccountEditText;
	private EditText loginEditText;
	private EditText passwordEditText;

	private ProgressDialog progressDialog;
	private Context mContext;
	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		mContext = getApplicationContext();
		prefs = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);

		Button loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(this);

		domainaccountEditText = (EditText) findViewById(R.id.accountdomain_edittext);
		loginEditText = (EditText) findViewById(R.id.login_edittext);
		passwordEditText = (EditText) findViewById(R.id.password_edittext);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.login_button) {

			// TODO check if login and password fields are filled in correctly
			String login = loginEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			String domain = domainaccountEditText.getText().toString();

			progressDialog = ProgressDialog.show(this, "Checking login credentials",
					"Please wait...");
			new VerifyLoginTask().execute(domain, login, password);

		}

	}

	private class VerifyLoginTask extends AsyncTask<String, Void, Integer> {

		private String domain;
		private String login;
		private String password;

		@Override
		protected Integer doInBackground(String... params) {

			domain = params[0];
			login = params[1];
			password = params[2];
			HttpRetriever httpRetriever = new HttpRetriever();
			int loginAttemptResult = httpRetriever.checkCredentials(domain, login,
					password);

			return loginAttemptResult;
		}

		@Override
		protected void onPostExecute(Integer result) {
			progressDialog.cancel();

			if (result == 200) {
				GUI.displayMonit(mContext, "Access granted");
				Editor editor = prefs.edit();
				editor.putString(Constants.USER_ACCOUNT_DOMAIN, domain);
				editor.putString(Constants.USER_LOGIN, login);
				editor.putString(Constants.USER_PASSWORD, password);
				editor.putBoolean(Constants.CREDENTIALS_STORED, true);
				editor.commit();
			} else
				GUI.displayMonit(mContext, "Access denied: " + result);

			// TODO add various messages

		}

	}

}