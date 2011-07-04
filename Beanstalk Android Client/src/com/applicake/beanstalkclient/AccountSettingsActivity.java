package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;

import com.applicake.beanstalkclient.adapters.SpinnerTimezoneAdapter;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.RailsTimezones;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpImproperStatusCodeException;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderServerErrorException;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AccountSettingsActivity extends BeanstalkActivity implements OnClickListener {

  private EditText accountName;
  private Spinner timezoneSpinner;
  private ArrayList<String> popupValuesList;
  private ArrayList<String> spinnerValuesList;
  private Context mContext;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.account_settings_layout);

    mContext = this;
    accountName = (EditText) findViewById(R.id.account_name_edit_text);
    timezoneSpinner = (Spinner) findViewById(R.id.timezone_spinner);

    popupValuesList = RailsTimezones.getDetailedRailsTimezonesArrayList();
    spinnerValuesList = RailsTimezones.listOfRailsTimezones();

    timezoneSpinner.setAdapter(new SpinnerTimezoneAdapter(this,
        android.R.layout.simple_spinner_item, popupValuesList, spinnerValuesList));

    ((Button) findViewById(R.id.save_changes_button)).setOnClickListener(this);

    new DownloadAccountInfoTask().execute();

  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.save_changes_button) {
      new SendUpdateAccountPropertiesTask().execute();
    }

  }

  public class DownloadAccountInfoTask extends AsyncTask<String, Void, Integer> {

    @SuppressWarnings("rawtypes")
    private AsyncTask thisTask = this;
    private boolean failed = false;;
    private String failMessage;
    private ProgressDialog progressDialog;
    private Account account;

    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(mContext, "Downloading account information",
          "Please wait...");
      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          GUI.displayMonit(mContext, "Downloading account information task was cancelled");
        }
      });
    }

    @Override
    protected Integer doInBackground(String... params) {

      try {
        String accountResultxml = HttpRetriever.getAccountInfo(prefs);

        account = XmlParser.parseAccountInfo(accountResultxml);
        return 200;

      } catch (XMLParserException e) {
        failMessage = Strings.internalErrorMessage;
      } catch (HttpImproperStatusCodeException e) {
        return e.getStatusCode();
      } catch (HttpConnectionErrorException e) {
        failMessage = Strings.networkConnectionErrorMessage;
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
            new DownloadAccountInfoTask().execute();
          }
        };
        builder.displayDialog();

      } else {

        if ((result == 200) && (account != null)) {
          accountName.setText(account.getName());
          timezoneSpinner.setSelection(spinnerValuesList.indexOf(account.getTimeZone()));
        } else if (result == 0) {

          GUI.displayUnexpectedErrorMonit(mContext);
        } else {
          GUI.displayServerErrorMonit(mContext, String.valueOf(result));
        }

      }

    }

  }

  public class SendUpdateAccountPropertiesTask extends AsyncTask<Void, Void, Integer> {

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
          "Changing account properties");

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
        String accountModificationXml = xmlCreator.createAccountPropertiesChangeXML(
            accountName.getText().toString().trim(),
            spinnerValuesList.get(timezoneSpinner.getSelectedItemPosition()));
        Log.w("XML", accountModificationXml);
        return HttpSender.sendUpdateAccountXML(prefs, accountModificationXml);

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
            new SendUpdateAccountPropertiesTask().execute();
          }
        };

        builder.displayDialog();
      } else {

        if (result == 200) {
          GUI.displayMonit(mContext, "Account properties were modified!");
          setResult(Constants.REFRESH_ACTIVITY);
          finish();

        } else if (result == 0 && errorMessage != null) {
          GUI.displayServerErrorMonit(mContext, errorMessage);
        } else
          GUI.displayUnexpectedErrorMonit(mContext);

      }

    }
  }
}
