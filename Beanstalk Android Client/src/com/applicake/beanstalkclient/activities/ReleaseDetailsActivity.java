package com.applicake.beanstalkclient.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.applicake.beanstalkclient.Changeset;
import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Release;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

public class ReleaseDetailsActivity extends BeanstalkActivity implements OnClickListener {
  public ProgressDialog mProgressDialog;
  private Release mRelease;
  private String mRepositoryTitle;
  private int mLabelColor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.release_details_layout);

    Intent intent = getIntent();

    mRepositoryTitle = intent.getStringExtra(Constants.REPOSITORY_TITLE);
    mLabelColor = intent.getIntExtra(Constants.REPOSITORY_COLOR_NO, 0);

    findViewById(R.id.colorLabel).getBackground().setLevel(mLabelColor);
    ((TextView) findViewById(R.id.repoName)).setText(mRepositoryTitle);

    mRelease = intent.getParcelableExtra(Constants.RELEASE);
    ((TextView) findViewById(R.id.environment_name)).setText(mRelease
        .getEnvironmentName());
    ((TextView) findViewById(R.id.created_at)).setText(mRelease.getCreatedAt()
        .toLocaleString());
    ((TextView) findViewById(R.id.author)).setText(mRelease.getAuthor());
    ((TextView) findViewById(R.id.deployed_at)).setText(mRelease.getDeployedAt()
        .toLocaleString());
    ((TextView) findViewById(R.id.comment)).setText(mRelease.getComment());
    ((TextView) findViewById(R.id.revision)).setText(mRelease.getRevision());

    findViewById(R.id.buttonViewChanges).setOnClickListener(this);
  }

  public class DownloadChangesetTask extends AsyncTask<Void, Void, Changeset> {

    @SuppressWarnings("rawtypes")
    private AsyncTask thisTask = this;
    private String errorMessage;
    private String failMessage;
    private boolean failed = false;

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mProgressDialog = ProgressDialog.show(ReleaseDetailsActivity.this,
          "Loading changes", "Please wait...");
      mProgressDialog.setCancelable(true);
      mProgressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          GUI.displayMonit(ReleaseDetailsActivity.this,
              "Data download task was cancelled");
        }
      });
      super.onPreExecute();
    }

    @Override
    protected Changeset doInBackground(Void... params) {

      try {
        String xmlChangesetList = HttpRetriever.getSingleChangesetXml(prefs,
            mRelease.getRevision(), mRelease.getRepositoryId());
        // parsing changeset list
        return XmlParser.parseChangeset(xmlChangesetList);
        // TODO better implementation of exception handling

      } catch (UnsuccessfulServerResponseException e) {
        errorMessage = e.getMessage();
        e.printStackTrace();
        return null;
      } catch (HttpConnectionErrorException e) {
        e.printStackTrace();
        failMessage = Strings.networkConnectionErrorMessage;
      } catch (XMLParserException e) {
        failMessage = Strings.internalErrorMessage;
        e.printStackTrace();
      }
      failed = true;
      return null;
    }

    @Override
    protected void onPostExecute(Changeset changeset) {
      mProgressDialog.dismiss();
      if (failed) {
        SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(
            ReleaseDetailsActivity.this, failMessage) {

          @Override
          public void retryAction() {
            new DownloadChangesetTask().execute();
          }

          @Override
          public void noRetryAction(DialogInterface dialog) {
            super.noRetryAction(dialog);
            finish();
          }

        };

        builder.displayDialog();
      } else {

        if (changeset != null) {
          try {
            Intent intent = new Intent(ReleaseDetailsActivity.this,
                ChangesetActivity.class);
            intent.putParcelableArrayListExtra(Constants.CHANGEDFILES_ARRAYLIST,
                changeset.getChangedFiles());
            intent.putParcelableArrayListExtra(Constants.CHANGEDDIRS_ARRAYLIST,
                changeset.getChangedDirs());
            intent.putExtra(Constants.COMMIT_REPOSIOTRY_NAME, mRepositoryTitle);
            intent.putExtra(Constants.COMMIT_REPOSIOTRY_LABEL, mLabelColor);
            intent.putExtra(Constants.COMMIT_USERNAME, changeset.getAuthor());
            intent.putExtra(Constants.COMMIT_MESSAGE, changeset.getMessage());
            intent.putExtra(Constants.COMMIT_REPOSITORY_ID, changeset.getRepositoryId());
            intent.putExtra(
                Constants.COMMIT_REVISION_ID,
                changeset.getHashId() == "" ? changeset.getRevision() : changeset
                    .getHashId());
            startActivity(intent);
          } catch (Throwable e) {
            Log.e("error", e.getMessage());
          }

        } else if (errorMessage != null) {
          GUI.displayMonit(ReleaseDetailsActivity.this, "Server error: " + errorMessage);
        }

      }
    }

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.buttonViewChanges:
      new DownloadChangesetTask().execute();
      break;
    }
  }
}
