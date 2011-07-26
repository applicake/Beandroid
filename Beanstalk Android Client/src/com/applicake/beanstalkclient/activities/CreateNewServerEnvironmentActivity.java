package com.applicake.beanstalkclient.activities;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.ServerEnvironment;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderServerErrorException;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

public class CreateNewServerEnvironmentActivity extends BeanstalkActivity {

  private EditText nameEditText;
  private CheckBox automaticCheckBox;
  private EditText branchName;
  private int repoId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.create_new_server_environment_layout);

    repoId = getIntent().getIntExtra(Constants.REPOSITORY_ID, 0);

    nameEditText = (EditText) findViewById(R.id.nameEditText);
    automaticCheckBox = (CheckBox) findViewById(R.id.is_automatic_checkbox);
    branchName = (EditText) findViewById(R.id.branch_name_edittext);

    findViewById(R.id.createButton).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        new SendNewServerEnvironment(CreateNewServerEnvironmentActivity.this).execute();

      }
    });
  }

  class SendNewServerEnvironment extends AsyncTask<Void, Void, Integer> {

    ProgressDialog progressDialog;
    String errorMessage;

    private AsyncTask<Void, Void, Integer> thisTask;
    private Context mContext;
    private String failMessage;
    private boolean failed;

    public SendNewServerEnvironment(Context context) {
      mContext = context;
      thisTask = this;
    }

    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(mContext, "Please wait...",
          "creating repository");

      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          GUI.displayMonit(mContext, "Repository creating task was cancelled");
        }
      });
    }

    protected Integer doInBackground(Void... params) {

      XmlCreator xmlCreator = new XmlCreator();

      try {

        ServerEnvironment serverEnvironment = new ServerEnvironment();
        serverEnvironment.setName(nameEditText.getText().toString());
        serverEnvironment.setAutomatic(automaticCheckBox.isChecked());
        serverEnvironment.setBranchName(branchName.getText().toString());

        String newServerEnvironmentXml = xmlCreator
            .createNewServerEnvironmentXML(serverEnvironment);

        return HttpSender.sendCreateNewServerEnvironmentXML(prefs,
            newServerEnvironmentXml, repoId);

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
            new SendNewServerEnvironment(mContext).execute();
          }
        };

        builder.displayDialog();
      } else {

        if (result == 201) {
          GUI.displayMonit(mContext, "Reposiotry was created successfully!");
          setResult(Constants.REFRESH_ACTIVITY);
          finish();
        }
        if ((result == 0) && (errorMessage != null))
          GUI.displayMonit(mContext, errorMessage);

      }
    }

  }

}
