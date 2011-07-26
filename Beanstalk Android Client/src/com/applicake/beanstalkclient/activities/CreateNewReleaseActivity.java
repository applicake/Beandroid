package com.applicake.beanstalkclient.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.ServerEnvironment;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.adapters.SpinnerServerEnvironmentAdapter;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderServerErrorException;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

public class CreateNewReleaseActivity extends BeanstalkActivity {

  private EditText revision;
  private List<ServerEnvironment> serverEnvironmentList;
  private SpinnerServerEnvironmentAdapter serverEnvironmentSpinnerAdapter;
  private String repoId;
  private EditText comment;
  private CheckBox deployFromScratch;
  private Spinner serverEnvironmentSpinner;
  private Context mContext;

  // consider saving storing data in a Bundle
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.create_new_release_layout);
    mContext = this;

    repoId = getIntent().getStringExtra(Constants.REPOSITORY_ID);

    revision = (EditText) findViewById(R.id.revision_edit_text);
    comment = (EditText) findViewById(R.id.comment_edit_text);
    comment.requestFocus();
    deployFromScratch = (CheckBox) findViewById(R.id.from_scratch_checkbox);
    serverEnvironmentSpinner = (Spinner) findViewById(R.id.environment_spinner);
    Button createButton = (Button) findViewById(R.id.create_button);
    createButton.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        new SendNewRelease(mContext).execute();
      }
    });

    revision.setOnFocusChangeListener(new OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        // ignore first focus gain
        if (hasFocus) {
          final Intent intent = getIntent();
          intent
              .setClass(getApplicationContext(), RepositoryRevisionPickerActivity.class);
          startActivityForResult(intent, 0);

        }
      }
    });

    serverEnvironmentList = new ArrayList<ServerEnvironment>();
    serverEnvironmentSpinnerAdapter = new SpinnerServerEnvironmentAdapter(this,
        android.R.layout.simple_spinner_item, serverEnvironmentList);
    serverEnvironmentSpinner.setAdapter(serverEnvironmentSpinnerAdapter);

    new DownloadServerEnvironmentsListTask(this).execute();
    // set spinner adapter

    // download spinner list

    // set create button action

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      revision.setText(data.getStringExtra(Constants.COMMIT_REVISION_ID));
    }
    super.onActivityResult(requestCode, resultCode, data);

  }

  public class DownloadServerEnvironmentsListTask extends
      AsyncTask<String, Void, List<ServerEnvironment>> {

    private Context context;
    private String failMessage;
    private boolean failed = false;

    public DownloadServerEnvironmentsListTask(Context context) {
      this.context = context;
    }

    // show dialog

    @Override
    protected List<ServerEnvironment> doInBackground(String... params) {

      try {
        String serverEnvironmentsXml = HttpRetriever
            .getServerEnvironmentListForRepositoryXML(prefs, repoId);
        Log.d("xxx", serverEnvironmentsXml);
        return XmlParser.parseServerEnvironmentsList(serverEnvironmentsXml);

      } catch (UnsuccessfulServerResponseException e) {
        failMessage = e.getMessage();
        return null;
      } catch (HttpConnectionErrorException e) {
        failMessage = Strings.networkConnectionErrorMessage;
      } catch (XMLParserException e) {
        failMessage = Strings.internalErrorMessage;
      }
      failed = true;
      return null;
    }

    @Override
    protected void onPostExecute(List<ServerEnvironment> result) {
      // hide dialog
      if (failed) {
        Log.d("xxx", failMessage);
        SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(context,
            failMessage) {

          @Override
          public void retryAction() {
            new DownloadServerEnvironmentsListTask(context).execute();
          }

          @Override
          public void noRetryAction(DialogInterface dialog) {
            super.noRetryAction(dialog);
            finish();
          }

        };

        builder.displayDialog();
      } else {
        serverEnvironmentList.clear();
        if (result != null) {
          serverEnvironmentList.addAll(result);
          serverEnvironmentSpinnerAdapter.notifyDataSetChanged();
        }

        if (failMessage != null)
          GUI.displayMonit(context, failMessage);

      }
    }
  }

  class SendNewRelease extends AsyncTask<Void, Void, Integer> {

    ProgressDialog progressDialog;
    String errorMessage;

    private AsyncTask<Void, Void, Integer> thisTask;
    private Context mContext;
    private String failMessage;
    private boolean failed;

    public SendNewRelease(Context context) {
      mContext = context;
      thisTask = this;
    }

    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(mContext, "Please wait...",
          "creating new release");

      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);

          // TODO consider implementing actual cancellation of HTTP request
          GUI.displayMonit(mContext, "Release creation task was cancelled");
        }
      });
    }

    protected Integer doInBackground(Void... params) {

      XmlCreator xmlCreator = new XmlCreator();

      try {
        
        int environmentId = serverEnvironmentList.get(serverEnvironmentSpinner.getSelectedItemPosition())
            .getId();

        String newServerEnvironmentXml = xmlCreator.createNewReleaseXML(revision
            .getText().toString(), comment.getText().toString(), deployFromScratch.isChecked(),
            environmentId);

        return HttpSender.sendCreateReleaseXML(prefs, newServerEnvironmentXml, repoId, environmentId);

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
            new SendNewRelease(mContext).execute();
          }
        };

        builder.displayDialog();
      } else {

        if (result == 201) {
          GUI.displayMonit(mContext, "Release was created successfully!");
          setResult(Constants.REFRESH_ACTIVITY);
          finish();
        }
        if ((result == 0) && (errorMessage != null))
          GUI.displayMonit(mContext, errorMessage);

      }
    }

  }

}
