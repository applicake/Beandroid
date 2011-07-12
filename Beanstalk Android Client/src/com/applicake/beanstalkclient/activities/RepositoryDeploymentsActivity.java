package com.applicake.beanstalkclient.activities;

import java.util.ArrayList;
import java.util.List;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Release;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.Server;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.adapters.ReleasesAdapter;
import com.applicake.beanstalkclient.adapters.ServersAdapter;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
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
import android.widget.ListView;
import android.widget.TextView;

public class RepositoryDeploymentsActivity extends BeanstalkActivity implements
    OnClickListener {

  boolean serverListLoaded = false;
  boolean releaseListLoaded = false;

  private ListView mReleasesList;
  private ListView mServersList;
  private ReleasesAdapter mReleasesAdapter;
  private ServersAdapter mServersAdapter;

  private ArrayList<Release> mReleaseArray = new ArrayList<Release>();
  private ArrayList<Server> mServersArray = new ArrayList<Server>();
  private Repository repository;
  private Button releasesButton;
  private Button serversButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.repository_deployments_layout);

    repository = getIntent().getParcelableExtra(Constants.REPOSITORY);

    // set repository title and label color based on intent
    ((TextView) findViewById(R.id.repoTitle)).setText(repository.getTitle());
    findViewById(R.id.colorLabel).getBackground().setLevel(repository.getColorLabelNo());

    // handle tab switching
    mReleasesList = (ListView) findViewById(R.id.releases_list);
    mServersList = (ListView) findViewById(R.id.servers_list);

    releasesButton = (Button) findViewById(R.id.releases_button);
    releasesButton.setOnClickListener(this);

    serversButton = (Button) findViewById(R.id.servers_button);
    serversButton.setOnClickListener(this);
    
    releasesButton.setSelected(true);
    serversButton.setSelected(false);
    
    // releases list
    mReleasesAdapter = new ReleasesAdapter(this, R.layout.releases_entry, mReleaseArray);
    mReleasesList.setAdapter(mReleasesAdapter);

    // servers list
    mServersAdapter = new ServersAdapter(this, 0, mServersArray);
    mServersList.setAdapter(mServersAdapter);

    new DownloadReleaseListTask(this).execute();
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    switch (id) {
    case R.id.releases_button:
      mReleasesList.setVisibility(View.VISIBLE);
      mServersList.setVisibility(View.GONE);
      releasesButton.setSelected(true);
      serversButton.setSelected(false);
      break;

    case R.id.servers_button:
      mReleasesList.setVisibility(View.GONE);
      mServersList.setVisibility(View.VISIBLE);
      releasesButton.setSelected(false);
      serversButton.setSelected(true);
      break;
    }
  }

  
  // TODO change info dialog to list header 
  public class DownloadReleaseListTask extends AsyncTask<String, Void, List<Release>> {

    private Context context;
    private ProgressDialog progressDialog;

    @SuppressWarnings("rawtypes")
    private AsyncTask thisTask;
    private String errorMessage;
    private String failMessage;
    private boolean failed = false;

    public DownloadReleaseListTask(Context context) {
      this.context = context;
      thisTask = this;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progressDialog = ProgressDialog.show(context, "Loading release list",
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
    protected List<Release> doInBackground(String... params) {

      try {
        String releasesXml = HttpRetriever.getReleasesListForRepositoryXML(prefs,
            repository.getId());
        return XmlParser.parseReleasesList(releasesXml);

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
    protected void onPostExecute(List<Release> result) {
      progressDialog.dismiss();
      if (failed) {
        SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(context,
            failMessage) {

          @Override
          public void retryAction() {
            new DownloadReleaseListTask(context).execute();
          }

          @Override
          public void noRetryAction(DialogInterface dialog) {
            super.noRetryAction(dialog);
            finish();
          }

        };

        builder.displayDialog();
      } else {
        mReleaseArray.clear();
        if (result != null) {
          mReleaseArray.addAll(result);
          mReleasesAdapter.notifyDataSetChanged();
        }

        if (errorMessage != null)
          GUI.displayMonit(context, errorMessage);

      }
    }

  }

}
