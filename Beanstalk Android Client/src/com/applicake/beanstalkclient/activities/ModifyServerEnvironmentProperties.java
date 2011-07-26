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

public class ModifyServerEnvironmentProperties extends BeanstalkActivity implements OnClickListener {

  private EditText nameEditText;
  private CheckBox automaticCheckBox;
  private EditText branchNameEditText;
  private ServerEnvironment serverEnvironment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.modify_server_environment_layout);

    nameEditText = (EditText) findViewById(R.id.nameEditText);
    automaticCheckBox = (CheckBox) findViewById(R.id.is_automatic_checkbox);

    // TODO investigate how branch name can be retrieved
    branchNameEditText = (EditText) findViewById(R.id.branch_name_edittext);

    serverEnvironment = getIntent().getParcelableExtra(Constants.SERVER_ENVIRONMENT);

    // set current serve environment values
    nameEditText.setText(serverEnvironment.getName());
    automaticCheckBox.setChecked(serverEnvironment.isAutomatic());
    branchNameEditText.setText(serverEnvironment.getBranchName() == null ? ""
        : serverEnvironment.getBranchName());
    
    findViewById(R.id.apply_button).setOnClickListener(this);

  }
  
  @Override
  public void onClick(View v) {
    new SendServerEnvironmentModificationTask(this).execute();
    
  }


  public class SendServerEnvironmentModificationTask extends
      AsyncTask<Void, Void, Integer> {

    ProgressDialog progressDialog;
    String errorMessage;

    private AsyncTask<Void, Void, Integer> thisTask;
    private String failMessage;
    private boolean failed;
    private Context mContext;

    public SendServerEnvironmentModificationTask(Context context) {
      mContext = context;
      thisTask = this;
    }

    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(mContext, "Please wait...",
          "modifying server environment properties");

      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          GUI.displayMonit(mContext, "Server environment modification task was cancelled");
        }
      });
      super.onPreExecute();
    }

    protected Integer doInBackground(Void... params) {

      XmlCreator xmlCreator = new XmlCreator();
      try {
        serverEnvironment.setName(nameEditText.getText().toString().trim());
        serverEnvironment.setAutomatic(automaticCheckBox.isChecked());
        serverEnvironment.setBranchName(branchNameEditText.getText().toString().trim());

        String serverEnvironmentModificationXml = xmlCreator
            .createModifyServerEnvironmentXML(serverEnvironment);
        return HttpSender.sendModifyServerEnvironmentXML(prefs,
            serverEnvironmentModificationXml, serverEnvironment.getRepositoryId(),
            serverEnvironment.getId());

      } catch (XMLParserException e) {
        e.printStackTrace();
        failMessage = Strings.internalErrorMessage;
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        failMessage = Strings.internalErrorMessage;
      } catch (IllegalStateException e) {
        e.printStackTrace();
        failMessage = Strings.internalErrorMessage;
      } catch (IOException e) {
        e.printStackTrace();
        failMessage = Strings.internalErrorMessage;
      } catch (HttpSenderException e) {
        e.printStackTrace();
        failMessage = Strings.networkConnectionErrorMessage;
      } catch (HttpSenderServerErrorException e) {
        e.printStackTrace();
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
            new SendServerEnvironmentModificationTask(mContext).execute();
          }
        };

        builder.displayDialog();
      }

      if (result == 200) {
        GUI.displayMonit(mContext, "Server environment properties were modified!");
        setResult(Constants.REFRESH_ACTIVITY);
        finish();
      } else if ((result == 0) && (errorMessage != null)) {
        GUI.displayMonit(mContext, "Server error: " + errorMessage);
      } else {
        GUI.displayMonit(mContext, "Unexpected error, please try again later");
      }

    }

  }


}
