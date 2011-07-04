package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;

import com.applicake.beanstalkclient.adapters.SpinnerTimezoneAdapter;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.RailsTimezones;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderServerErrorException;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

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
    timezoneSpinner = (Spinner) findViewById(R.id.timezonesSpinner);
    popupValuesList = RailsTimezones.getDetailedRailsTimezonesArrayList();
    spinnerValuesList = RailsTimezones.listOfRailsTimezones();

    timezoneSpinner.setAdapter(new SpinnerTimezoneAdapter(mContext,
        android.R.layout.simple_spinner_item, popupValuesList, spinnerValuesList));
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
    private String failMessage;
    private boolean failed;

    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(mContext, "Please wait...", "creating user");

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
        String userCreateXml = xmlCreator.createNewUserXML(loginEditText.getText()
            .toString().trim(), nameEditText.getText().toString().trim(),
            lastNameEditText.getText().toString().trim(), emailEditText.getText()
                .toString().trim(),
            spinnerValuesList.get(timezoneSpinner.getSelectedItemPosition()),
            adminCheckBox.isChecked(), passwordEditText.getText().toString().trim());
        return HttpSender.sendCreateUserXML(prefs, userCreateXml);

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
            new SendUserCreateTask().execute();
          }
        };

        builder.displayDialog();
      } else {

        if (result == 201) {
          GUI.displayMonit(mContext, "User was created!");
          setResult(Constants.REFRESH_ACTIVITY);
          finish();

        } else if ((result == 0) && (errorMessage != null)) {
          GUI.displayMonit(mContext, errorMessage);
        } else
          GUI.displayUnexpectedErrorMonit(mContext);
      }
    }

  }

}
