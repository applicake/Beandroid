package com.applicake.beanstalkclient;

import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpImproperStatusCodeException;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

import com.applicake.beanstalkclient.utils.XmlParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener {

	private EditText domainaccountEditText;
	private EditText loginEditText;
	private EditText passwordEditText;
	private CheckBox remeberMeCheckBox;

	private ProgressDialog progressDialog;
	private Context mContext;
	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		prefs = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
		setContentView(R.layout.main);
		// auto login with previously stored user data
		if (prefs.getBoolean(Constants.REMEBER_ME_CHECKBOX, false)
				&& prefs.getBoolean(Constants.CREDENTIALS_STORED, false)) {
			// check credentials validity

			Log.i("checkbox checked", "inside if");
			String storedDomain = prefs.getString(Constants.USER_ACCOUNT_DOMAIN, "");
			String storedLogin = prefs.getString(Constants.USER_LOGIN, "");
			String storedPassword = prefs.getString(Constants.USER_PASSWORD, "");
			new VerifyLoginTask().execute(storedDomain, storedLogin, storedPassword);

		}

		Button loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(this);

		domainaccountEditText = (EditText) findViewById(R.id.accountdomain_edittext);
		loginEditText = (EditText) findViewById(R.id.login_edittext);
		passwordEditText = (EditText) findViewById(R.id.password_edittext);
		remeberMeCheckBox = (CheckBox) findViewById(R.id.rememberMeCheckBox);

		// custom input filter that allows only alphanumeric characters and "-"
		// character, but not in the beginning or the end of the string 
		//TODO
		// create better implementation

		InputFilter httpAddressFilter = new InputFilter() {

			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {

				for (int i = start; i < end; i++) {
					if (!Character.isLetterOrDigit(source.charAt(i))
							&& !(source.charAt(i) == '-')) {
						return "";
					}
					if (((dstart == 0) || (dend == dest.length()))
							&& (source.charAt(i) == '-')) {
						return "";
					}
				}
				return null;
			}

		};

		domainaccountEditText.setFilters(new InputFilter[] { httpAddressFilter });
	}

	public void onClick(View v) {
		if (v.getId() == R.id.login_button) {

			// TODO check if login and password fields are filled in correctly
			String login = loginEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			String domain = domainaccountEditText.getText().toString();

			Editor editor = prefs.edit();
			editor.putBoolean(Constants.REMEBER_ME_CHECKBOX,
					remeberMeCheckBox.isChecked());
			editor.commit();
			new VerifyLoginTask().execute(domain, login, password);

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case Constants.CLOSE_ALL_ACTIVITIES:
			if (!prefs.getBoolean(Constants.REMEBER_ME_CHECKBOX, false))
				clearCredentials();
			setResult(Constants.CLOSE_ALL_ACTIVITIES);
			finish();
		}

	}

	public void clearCredentials() {
		Editor editor = prefs.edit();
		editor.putBoolean(Constants.CREDENTIALS_STORED, false);
		editor.putString(Constants.USER_ACCOUNT_DOMAIN, "");
		editor.putString(Constants.USER_LOGIN, "");
		editor.putString(Constants.USER_PASSWORD, "");
		editor.putBoolean(Constants.REMEBER_ME_CHECKBOX, false);
		editor.commit();
	}

	public class VerifyLoginTask extends AsyncTask<String, Void, Integer> {

		private String domain;
		private String login;
		private String password;

		private Account account;

		@SuppressWarnings("rawtypes")
		private AsyncTask thisTask = this;
		private boolean failed = false;;
		private String failMessage;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Checking login credentials",
					"Please wait...");
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					thisTask.cancel(true);
					GUI.displayMonit(mContext, "Logging in task was cancelled");
				}
			});
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {

			domain = params[0];
			login = params[1];
			password = params[2];
			String loginAttemptResultxml;
			try {
				loginAttemptResultxml = HttpRetriever.checkCredentials(domain, login,
						password);

				account = XmlParser.parseAccountInfo(loginAttemptResultxml);
				return 200;

			} catch (XMLParserException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (HttpImproperStatusCodeException e) {
				return e.getStatusCode();
			} catch (HttpConnectionErrorException e) {
				failMessage = Strings.networkConnectionErrorMessage;
			}
			failed = true;
			return null;

		}

		@Override
		protected void onPostExecute(Integer result) {
			progressDialog.dismiss();

			if (failed) {
				SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
						failMessage) {

					@Override
					public void retryAction() {
						new VerifyLoginTask().execute(domain, login, password);
					}
				};
				builder.displayDialog();

			} else {

				if ((result == 200) && (account != null)) {

					GUI.displayMonit(mContext, "Access granted");
					Editor editor = prefs.edit();
					editor.putString(Constants.USER_ACCOUNT_DOMAIN, domain);
					editor.putString(Constants.USER_LOGIN, login);
					editor.putString(Constants.USER_PASSWORD, password);
					editor.putInt(Constants.ACCOUNT_PLAN, account.getPlanId());
					editor.putString(Constants.USER_TIMEZONE, account.getTimeZone());
					editor.putBoolean(Constants.CREDENTIALS_STORED, true);
					editor.commit();

					Intent intent = new Intent(getApplicationContext(),
							HomeActivity.class);
					startActivityForResult(intent, 0);

				} else if (result == 302) {
					GUI.displayMonit(mContext, "Invalid account domain");
				} else if (result == 401) {
					GUI.displayMonit(mContext, "Invalid username or password");
				} else if (result == 500) {
					GUI.displayMonit(mContext,
							"You must have Developer API enabled in your Beanstalk account settings");
				} else {
					GUI.displayMonit(mContext, "Server error: " + result);
				}

			}

		}

	}

}