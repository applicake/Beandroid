package com.applicake.beanstalkclient.activities;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.adapters.RepositoriesAdapter;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

public class RepositoriesActivity extends BeanstalkActivity implements OnItemClickListener, OnClickListener {

  public static final int RESULT_REPOSITORY = 91;

  private Context mContext;
  private boolean returnAfterClick = false;
  public ArrayList<Repository> repositoriesArray;
  public RepositoriesAdapter repositoriesAdapter;
  public ListView repositoriesList;
  public TextView repositoriesLeftCounter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.repositories_layout);

    mContext = this;

    // TODO refactor 

    if(getIntent().getAction() != null) {
      returnAfterClick = getIntent().getAction().equals(Intent.ACTION_PICK);
    }

    repositoriesList = (ListView) findViewById(R.id.repositoriesList);
    View footerView = ((LayoutInflater) getApplicationContext().getSystemService(
        Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.add_new_repository_footer,
        null, false);
    if (currentUser != UserType.USER.name() && !returnAfterClick) {
      repositoriesList.addFooterView(footerView);
      footerView.setOnClickListener(this);
      repositoriesLeftCounter = (TextView) footerView.findViewById(R.id.repositoryCounter);
    }

    repositoriesArray = new ArrayList<Repository>();
    repositoriesAdapter = new RepositoriesAdapter(getApplicationContext(),
        R.layout.repositories_entry, repositoriesArray);
    repositoriesList.setAdapter(repositoriesAdapter);
    repositoriesList.setOnItemClickListener(this);


    new DownloadRepositoriesTask(this).execute();

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Constants.REFRESH_ACTIVITY) {
      new DownloadRepositoriesTask(this).execute();
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onClick(View v) {
    Intent intent = new Intent(mContext, CreateNewRepositoryActivity.class);
    startActivityForResult(intent, 0);
  }

  @Override
  public void onItemClick(AdapterView<?> arg0, View arg1, int itemNumber, long arg3) {
    if (itemNumber < repositoriesArray.size()) {
      if (!returnAfterClick) {
        Intent intent = new Intent(mContext, RepositoryDetailsActivity.class);
        intent.putExtra(Constants.REPOSITORY, repositoriesArray.get(itemNumber));
        startActivityForResult(intent, 0);
      } else {
        Intent intent = new Intent();
        intent.putExtra(Constants.REPOSITORY, repositoriesArray.get(itemNumber));
        setResult(RESULT_REPOSITORY, intent);
        finish();
      }
    }

  }

  public class DownloadRepositoriesTask extends
      AsyncTask<String, Void, ArrayList<Repository>> {

    private Context context;
    private ProgressDialog progressDialog;

    @SuppressWarnings("rawtypes")
    private AsyncTask thisTask;
    private String errorMessage;
    private String failMessage;
    private boolean failed = false;

    public DownloadRepositoriesTask(Context context) {
      this.context = context;
      thisTask = this;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progressDialog = ProgressDialog.show(context, "Loading Repository list",
          "Please wait...");
      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          finish();
        }
      });

    }

    @Override
    protected ArrayList<Repository> doInBackground(String... params) {

      try {
        String repositoriesXml = HttpRetriever.getRepositoryListXML(prefs);
        return XmlParser.parseRepositoryList(repositoriesXml);

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
    protected void onPostExecute(ArrayList<Repository> result) {
      progressDialog.dismiss();
      if (failed) {
        SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
            failMessage) {

          @Override
          public void retryAction() {
            new DownloadRepositoriesTask(context).execute();
          }

          @Override
          public void noRetryAction(DialogInterface dialog) {
            super.noRetryAction(dialog);
            finish();
          }

        };

        builder.displayDialog();
      } else {
        repositoriesArray.clear();
        if (result != null) {
          repositoriesArray.addAll(result);

          repositoriesAdapter.notifyDataSetChanged();

          if (currentUser.equals(UserType.OWNER.name())) {

            int repositoriesInPlan = prefs.getInt(Constants.NUMBER_OF_REPOS_AVAILABLE, 0);
            int numberLeft = repositoriesInPlan - repositoriesArray.size();
            if(repositoriesLeftCounter != null) {
              repositoriesLeftCounter.setText("available repositories: "
                  + String.valueOf(numberLeft) + "/" + String.valueOf(repositoriesInPlan));
            }
          }
        }

        if (errorMessage != null)
          GUI.displayMonit(context, errorMessage);

      }
    }

  }

}
