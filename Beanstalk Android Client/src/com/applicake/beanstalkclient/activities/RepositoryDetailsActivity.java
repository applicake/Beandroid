package com.applicake.beanstalkclient.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.permissions.ServerEnviromentsPermissions;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;
import com.applicake.beanstalkclient.widgets.DetailsView;

public class RepositoryDetailsActivity extends BeanstalkActivity implements
    OnClickListener {

  private Context mContext;

  private Repository repository;
  private View colorLabel;
  private TextView repoName;
  private String dateFormat = new String("yyyy-MM-dd");
  private TextView repoType;
  private TextView repoTitle;
  private DetailsView detailsView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.repository_details_layout);
    repository = getIntent().getParcelableExtra(Constants.REPOSITORY);

    mContext = this;

    colorLabel = findViewById(R.id.colorLabel);
    repoName = (TextView) findViewById(R.id.repoName);

    repoType = (TextView) findViewById(R.id.repoType);
    repoTitle = (TextView) findViewById(R.id.repoTitle);
    detailsView = (DetailsView) findViewById(R.id.details_view);

    // add button listeners
    Button viewCommitsButton = (Button) findViewById(R.id.buttonViewCommits);
    Button usersPermissionsButton = (Button) findViewById(R.id.buttonUsersPermissions);
    Button deploymentButton = (Button) findViewById(R.id.buttonDeployment);
    Button modifyPropertiesButton = (Button) findViewById(R.id.buttonModifyProperties);

    if (currentUser == UserType.USER.name()) {
      usersPermissionsButton.setVisibility(View.GONE);
      modifyPropertiesButton.setVisibility(View.GONE);
    }
    
    ServerEnviromentsPermissions permissions = new ServerEnviromentsPermissions(this);
    
    if(!permissions.hasDeploymentAccessForRepository(repository)) {
      deploymentButton.setVisibility(View.GONE);
    }
    
    viewCommitsButton.setOnClickListener(this);
    usersPermissionsButton.setOnClickListener(this);
    deploymentButton.setOnClickListener(this);
    modifyPropertiesButton.setOnClickListener(this);

    loadRepositoryData();

  }

  public void loadRepositoryData() {

    Log.w("color change",
        String.valueOf(colorLabel.getBackground().setLevel(repository.getColorLabelNo())));

    repoName.setText(repository.getName());
    repoType.setText(repository.getType().equals("SubversionRepository") ? "SVN" : "Git");
    repoTitle.setText(repository.getTitle());

    detailsView.addProperty("Created at", DateFormat.format(dateFormat, repository.getCreatedAt()));
    long lastCommit = repository.getLastCommitAt();
    detailsView.addProperty("Last commit", lastCommit == 0 ? "no commits" : DateUtils
        .getRelativeTimeSpanString(lastCommit));
    detailsView.addProperty("Revision", String.valueOf(repository.getRevision()));
    detailsView.addProperty("Storage used", String.valueOf(repository.getStorageUsedBytes()));
    detailsView.addProperty("Updated at", DateFormat.format(dateFormat, repository.getUpdatedAt()));


  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Constants.REFRESH_ACTIVITY) {
      setResult(resultCode);
      new DownloadRepositoryInfo().execute();
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onClick(View v) {
    
    // FIXME is getApplicationContext() really needed here?

    if (v.getId() == R.id.buttonViewCommits) {
      Intent intent = new Intent(getApplicationContext(), RepositoryCommitsActivity.class);
      intent.putExtra(Constants.REPOSITORY_ID, String.valueOf(repository.getId()));
      intent.putExtra(Constants.REPOSITORY_TITLE, repository.getTitle());
      intent.putExtra(Constants.REPOSITORY_COLOR_NO, repository.getColorLabelNo());
      startActivityForResult(intent, 0);

    }

    if (v.getId() == R.id.buttonUsersPermissions) {
      Intent intent = new Intent(getApplicationContext(),
          RepositoryUsersPermissionsActivity.class);
      intent.putExtra(Constants.REPOSITORY, repository);
      startActivityForResult(intent, 0);
    }

    if (v.getId() == R.id.buttonDeployment) {
      startActivityForResult(RepositoryDeploymentsActivity.generateIntentForSpecificRepo(this, repository), 0);      
    }

    if (v.getId() == R.id.buttonModifyProperties) {
      Intent intent = new Intent(getApplicationContext(),
          RepositoryModifyPropertiesActivity.class);
      intent.putExtra(Constants.REPOSITORY_ID, String.valueOf(repository.getId()));
      intent.putExtra(Constants.REPOSITORY_TITLE, repository.getTitle());
      intent.putExtra(Constants.REPOSITORY_COLOR_NO, repository.getColorLabelNo());
      startActivityForResult(intent, 0);
    }

  }

  public class DownloadRepositoryInfo extends AsyncTask<Void, Void, Boolean> {

    @SuppressWarnings("rawtypes")
    private AsyncTask thisTask = this;
    ProgressDialog progressDialog;

    private boolean failed;

    private String failMessage;

    private String errorMessage;

    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(mContext, "Please wait",
          "Repository data is being refreshed");

      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          GUI.displayMonit(mContext, "Download task was cancelled");
        }
      });
    };

    @Override
    protected Boolean doInBackground(Void... params) {
      try {
        String repositoryXml = HttpRetriever.getRepositoryXML(prefs, repository.getId());
        repository = XmlParser.parseRepository(repositoryXml);
        return true;

      } catch (UnsuccessfulServerResponseException e) {
        errorMessage = e.getMessage();
        return false;
      } catch (HttpConnectionErrorException e) {
        failMessage = Strings.networkConnectionErrorMessage;
      } catch (XMLParserException e) {
        failMessage = Strings.internalErrorMessage;
      }
      failed = true;
      return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
      progressDialog.dismiss();

      if (failed) {
        SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
            failMessage) {

          @Override
          public void retryAction() {
            new DownloadRepositoryInfo().execute();
          }

          @Override
          public void noRetryAction(DialogInterface dialog) {
            super.noRetryAction(dialog);
            finish();
          }

        };

        builder.displayDialog();
      } else {
        if (result) {
          loadRepositoryData();

        } else {
          GUI.displayMonit(mContext, "Server error: " + errorMessage);
        }
      }
    }

  }

}
