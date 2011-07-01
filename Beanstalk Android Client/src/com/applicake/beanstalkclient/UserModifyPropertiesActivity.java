package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;

import com.applicake.beanstalkclient.adapters.SpinnerTimezoneAdapter;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.GravatarDowloader;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.RailsTimezones;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderServerErrorException;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
	private Spinner timezoneSpinner;
	private CheckBox adminCheckBox;
	private EditText newPasswordEditText;
	private EditText retypedPasswordEditText;
	private ArrayList<String> popupValuesList;
	private ArrayList<String> spinnerValuesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_modify_properties_layout);
		mContext = this;
		user = getIntent().getParcelableExtra(Constants.USER);
		
		GravatarDowloader.getInstance().download(user.getEmail(), (ImageView) findViewById(R.id.userGravatar));
		((TextView) findViewById(R.id.userName)).setText(user.getFirstName() + " "
				+ user.getLastName());
		((TextView) findViewById(R.id.userEmail)).setText(user.getEmail());

		nameEditText = (EditText) findViewById(R.id.nameEditText);
		lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
		emailEditText = (EditText) findViewById(R.id.emailEditText);
		timezoneSpinner = (Spinner) findViewById(R.id.timezoneSpinner);
		adminCheckBox = (CheckBox) findViewById(R.id.adminCheckBox);

		passwordChangeButton = (Button) findViewById(R.id.passwordChangeButton);
		passwordChangeButton.setOnClickListener(this);

		applyChangesButton = (Button) findViewById(R.id.saveChangesButton);
		applyChangesButton.setOnClickListener(this);
		popupValuesList = RailsTimezones.getDetailedRailsTimezonesArrayList();
		spinnerValuesList = RailsTimezones.listOfRailsTimezones();

		timezoneSpinner
				.setAdapter(new SpinnerTimezoneAdapter(this,
						android.R.layout.simple_spinner_item, popupValuesList,
						spinnerValuesList));
		loadUserInfo();

	}

	private void loadUserInfo() {
		if (user != null) {
			nameEditText.setText(user.getFirstName());
			lastNameEditText.setText(user.getLastName());
			emailEditText.setText(user.getEmail());
			timezoneSpinner.setSelection(spinnerValuesList.indexOf(user.getTimezone()));
			// timezoneSpinner.setSelection();
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
		// temporary
		String errorMessage = "";

		@SuppressWarnings("rawtypes")
		private AsyncTask thisTask = this;
		private String failMessage;
		private boolean failed = false;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait...",
					"changing user properties");

			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					thisTask.cancel(true);
					GUI.displayMonit(mContext, "Data sending task was cancelled");
				}
			});
		}

		protected Integer doInBackground(Void... params) {

			XmlCreator xmlCreator = new XmlCreator();
			try {
				String userModificationXml = xmlCreator.createUserPropertiesChangeXML(
						nameEditText.getText().toString().trim(), lastNameEditText
								.getText().toString().trim(), emailEditText.getText()
								.toString().trim(),
						spinnerValuesList.get(timezoneSpinner.getSelectedItemPosition()),
						adminCheckBox.isChecked());
				return HttpSender.sendUpdateUserXML(prefs, userModificationXml,
						String.valueOf(user.getId()));

			} catch (XMLParserException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (IllegalArgumentException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (IllegalStateException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (IOException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (HttpSenderException e) {
				failMessage = Strings.networkConnectionErrorMessage;
			} catch (HttpSenderServerErrorException e) {
				errorMessage = e.getMessage();
				return 0;
			}
			failed = true;
			return 0;

		}

		@Override
		protected void onPostExecute(Integer result) {
			progressDialog.dismiss();
			if (failed) {
				SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
						failMessage) {
					@Override
					public void retryAction() {
						new SendUserPasswordTask().execute();
					}
				};

				builder.displayDialog();
			} else {

				if (result == 200) {
					GUI.displayMonit(mContext, "User properties were modified!");
					setResult(Constants.REFRESH_ACTIVITY);
					finish();

				} else if (result == 0 && errorMessage != null) {
					GUI.displayServerErrorMonit(mContext, errorMessage);
				} else
					GUI.displayUnexpectedErrorMonit(mContext);

			}

		}

	}

	public class SendUserPasswordTask extends AsyncTask<String, Void, Integer> {

		ProgressDialog progressDialog;
		@SuppressWarnings("rawtypes")
		private AsyncTask thisTask = this;
		private String failMessage;
		private String errorMessage;
		private boolean failed;

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait...",
					"changing user password");

			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					thisTask.cancel(true);
					GUI.displayMonit(mContext, "Data sending task was cancelled");
				}
			});

		}

		protected Integer doInBackground(String... params) {

			XmlCreator xmlCreator = new XmlCreator();
			try {
				String userPasswordModificationXml = xmlCreator
						.createPasswordChangeXML(params[0]);
				return HttpSender.sendUpdateUserXML(prefs, userPasswordModificationXml,
						String.valueOf(user.getId()));

			} catch (XMLParserException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (IllegalArgumentException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (IllegalStateException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (IOException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (HttpSenderException e) {
				failMessage = Strings.networkConnectionErrorMessage;
			} catch (HttpSenderServerErrorException e) {
				errorMessage = e.getMessage();
				return 0;
			}
			failed = true;
			return 0;

		}

		@Override
		protected void onPostExecute(Integer result) {
			progressDialog.dismiss();

			if (failed) {
				SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
						failMessage) {
					@Override
					public void retryAction() {
						new SendUserPasswordTask().execute();
					}
				};

				builder.displayDialog();
			} else {

				if (result == 200) {
					GUI.displayMonit(mContext, "User password was modified!");

				} else if (result == 0 && errorMessage != null) {
					GUI.displayServerErrorMonit(mContext, errorMessage);
				} else
					GUI.displayUnexpectedErrorMonit(mContext);

			}

		}

	}

}
