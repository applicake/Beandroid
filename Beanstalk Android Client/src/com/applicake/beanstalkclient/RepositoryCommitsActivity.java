package com.applicake.beanstalkclient;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.applicake.beanstalkclient.adapters.RepositoryChangesetAdapter;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

public class RepositoryCommitsActivity extends BeanstalkActivity implements
    OnItemClickListener {

  private RepositoryChangesetAdapter changesetAdapter;
  private ArrayList<Changeset> changesetArray;
  private ProgressDialog progressDialog;
  private Context mContext;
  private String repoId;
  private String repoTitle;
  private int repoColorLabelNo;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.repository_changesets_activity_layout);

    Intent currentIntent = getIntent();
    repoId = currentIntent.getStringExtra(Constants.REPOSITORY_ID);
    repoTitle = currentIntent.getStringExtra(Constants.REPOSITORY_TITLE);
    repoColorLabelNo = currentIntent.getIntExtra(Constants.REPOSITORY_COLOR_NO, 0);

    mContext = this;
    changesetArray = new ArrayList<Changeset>();
    changesetAdapter = new RepositoryChangesetAdapter(this,
        R.layout.repository_changeset_entry, changesetArray);

    ListView changesetList = (ListView) findViewById(R.id.changesetList);
    View headerView = ((LayoutInflater) getApplicationContext().getSystemService(
        Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.repo_name_header, null, false);

    // setup list header
    headerView.findViewById(R.id.colorLabel).getBackground().setLevel(repoColorLabelNo);
    TextView repoNameTextView = (TextView) headerView.findViewById(R.id.repoName);
    repoNameTextView.setText(repoTitle);

    changesetList.addHeaderView(headerView, null, false);
    changesetList.setAdapter(changesetAdapter);
    changesetList.setOnItemClickListener(this);

    new DownloadChangesetListTask().execute();

  }

  @Override
  public void onItemClick(AdapterView<?> arg0, View arg1, int itemNumber, long arg3) {
    if ((itemNumber > 0) && (itemNumber <= changesetArray.size())) {
      Intent intent = new Intent(mContext, ChangesetActivity.class);
      Changeset changeset = changesetArray.get(itemNumber - 1);
      intent.putParcelableArrayListExtra(Constants.CHANGEDFILES_ARRAYLIST,
          changeset.getChangedFiles());
      intent.putParcelableArrayListExtra(Constants.CHANGEDDIRS_ARRAYLIST,
          changeset.getChangedDirs());
      intent.putExtra(Constants.COMMIT_REPOSIOTRY_NAME, repoTitle);
      intent.putExtra(Constants.COMMIT_REPOSIOTRY_LABEL, repoColorLabelNo);
      intent.putExtra(Constants.COMMIT_USERNAME, changeset.getAuthor());
      intent.putExtra(Constants.COMMIT_MESSAGE, changeset.getMessage());
      intent.putExtra(Constants.COMMIT_REPOSITORY_ID, changeset.getRepositoryId());
      intent.putExtra(Constants.COMMIT_REVISION_ID,
          changeset.getHashId() == "" ? changeset.getRevision() : changeset.getHashId());
      startActivityForResult(intent, 0);

    }

  }

  public class DownloadChangesetListTask extends
      AsyncTask<Void, Void, ArrayList<Changeset>> {

    @SuppressWarnings("rawtypes")
    private AsyncTask thisTask = this;
    private String errorMessage;
    private String failMessage;
    private boolean failed = false;

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progressDialog = ProgressDialog.show(mContext, "Loading Activity list",
          "Please wait...");
      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          GUI.displayMonit(mContext, "Data download task was cancelled");
        }
      });
      super.onPreExecute();
    }

    @Override
    protected ArrayList<Changeset> doInBackground(Void... params) {

      try {
        String xmlChangesetList = HttpRetriever.getChangesetForReposiotoryXML(prefs,
            repoId);
        // parsing changeset list
        return XmlParser.parseChangesetList(xmlChangesetList);
        // TODO better implementation of exception handling

      } catch (UnsuccessfulServerResponseException e) {
        errorMessage = e.getMessage();
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
    protected void onPostExecute(ArrayList<Changeset> changesetParserArray) {
      progressDialog.dismiss();
      if (failed) {
        SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
            failMessage) {

          @Override
          public void retryAction() {
            new DownloadChangesetListTask().execute();
          }

          @Override
          public void noRetryAction(DialogInterface dialog) {
            super.noRetryAction(dialog);
            finish();
          }

        };

        builder.displayDialog();
      } else {

        if (changesetParserArray != null) {

          changesetArray.addAll(changesetParserArray);
          changesetAdapter.notifyDataSetChanged();

        } else if (errorMessage != null) {
          GUI.displayMonit(mContext, "Server error: " + errorMessage);
        }

      }
    }

  }

}
