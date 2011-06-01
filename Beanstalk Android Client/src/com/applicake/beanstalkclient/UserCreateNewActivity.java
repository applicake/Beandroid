package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;

import com.applicake.beanstalkclient.adapters.SpinnerTimezoneAdapter;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.RailsTimezones;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class UserCreateNewActivity extends BeanstalkActivity implements OnClickListener {
	private Button createButton;
	private Context mContext;

	private EditText loginEditText;
	private EditText nameEditText;
	private EditText lastNameEditText;
	private EditText emailEditText;
	private Spinner timezoneSpinner;
	private CheckBox adminCheckBox;
	private EditText passwordEditText;
	private ArrayList<String> popupValuesList;
	private ArrayList<String> spinnerValuesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_create_new_layout);
		mContext = this;

		loginEditText = (EditText) findViewById(R.id.loginEditText);
		nameEditText = (EditText) findViewById(R.id.nameEditText);
		lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
		emailEditText = (EditText) findViewById(R.id.emailEditText);
		popupValuesList = RailsTimezones.getDetailedRailsTimezonesArrayList();
		spinnerValuesList = RailsTimezones.listOfRailsTimezones();

		timezoneSpinner
				.setAdapter(new SpinnerTimezoneAdapter(this,
						android.R.layout.simple_spinner_item, popupValuesList,
						spinnerValuesList));
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
		String errorMessage;
		@SuppressWarnings("rawtypes")
		private AsyncTask thisTask = this;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait...",
					"creating user");

			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					thisTask.cancel(true);
					GUI.displayMonit(mContext, "Data sending task was cancelled");
				}
			});

			super.onPreExecute();
		}

		protected Integer doInBackground(Void... params) {

			XmlCreator xmlCreator = new XmlCreator();
			HttpSender httpSender = new HttpSender();
			try {
				String userCreateXml = xmlCreator.createNewUserXML(loginEditText
						.getText().toString().trim(), nameEditText.getText().toString()
						.trim(), lastNameEditText.getText().toString().trim(),
						emailEditText.getText().toString().trim(),
						spinnerValuesList.get(timezoneSpinner.getSelectedItemPosition()),
						adminCheckBox.isChecked(), passwordEditText.getText().toString()
								.trim());
				return httpSender.sendCreateUserXML(prefs, userCreateXml);

			} catch (IllegalArgumentException e) {
				errorMessage = "error";
				e.printStackTrace();
			} catch (IllegalStateException e) {
				errorMessage = "error";
				e.printStackTrace();
			} catch (IOException e) {
				errorMessage = "Unexpected IO error";
				e.printStackTrace();
			} catch (HttpSenderException e) {
				errorMessage = e.getMessage();
				e.printStackTrace();
			}
			return 0;

		}

		@Override
		protected void onPostExecute(Integer result) {
			progressDialog.dismiss();
			if (result == 201) {
				GUI.displayMonit(mContext, "user was created!");
				setResult(Constants.REFRESH_ACTIVITY);
				finish();

			} else if (result == 0) {
				GUI.displayMonit(mContext, errorMessage);
			}

			super.onPostExecute(result);
		}

	}

}
