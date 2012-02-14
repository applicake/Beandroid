package com.applicake.beanstalkclient.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Release;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.ServerEnvironment;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.adapters.ReleasesAdapter;
import com.applicake.beanstalkclient.adapters.ServersAdapter;
import com.applicake.beanstalkclient.tasks.BeanstalkAsyncTask;
import com.applicake.beanstalkclient.tasks.ResponseHandler;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

public class RepositoryDeploymentsActivity extends BeanstalkActivity implements
    OnClickListener, OnItemClickListener {
  
  private static final int REPOSITORY_ADD_RELEASE = 0x999;
  private static final int REPOSITORY_ADD_ENVIROMENT = 0x123;
  
  boolean serverListLoaded = false;
  boolean releaseListLoaded = false;

  private ListView mReleasesList;
  private ExpandableListView mServersList;
  private ReleasesAdapter mReleasesAdapter;
  private ServersAdapter mServersAdapter;

  private ArrayList<Release> mReleaseArray = new ArrayList<Release>();
  private ArrayList<ServerEnvironment> mServersArray = new ArrayList<ServerEnvironment>();
  private Repository repository;
  private List<Integer> listOfRepositoriesIds;

  private Button releasesButton;
  private Button serversButton;
  private View releasesLoadingFooterView;
  private View serversLoadingFooterView;
  private View serversAddNewFooterView;

  private boolean activityStartedForSpecificRepository;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.repository_deployments_layout);

    repository = getIntent().getParcelableExtra(Constants.REPOSITORY);
    if (repository == null) {
      activityStartedForSpecificRepository = false;
    } else {
      listOfRepositoriesIds = new ArrayList<Integer>();
      listOfRepositoriesIds.add(repository.getId());
      activityStartedForSpecificRepository = true;
      ((TextView) findViewById(R.id.repoName)).setText(repository.getTitle());
      findViewById(R.id.colorLabel).getBackground().setLevel(repository.getColorLabelNo());
    }

    // set repository title and label color based on intent

    // create releases list view and tab switching button, attach loading and
    // footer and set button listeners
    releasesButton = (Button) findViewById(R.id.releases_button);
    releasesButton.setOnClickListener(this);
    releasesButton.setSelected(true);

    mReleasesList = (ListView) findViewById(R.id.releases_list);
    releasesLoadingFooterView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
        .inflate(R.layout.releases_loading_footer, null, false);
    View releasesAddNewFooterView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
        .inflate(R.layout.add_new_release_footer, null, false);
    releasesAddNewFooterView.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        if(activityStartedForSpecificRepository) {
          startAddNewRelease(repository);
        } else {
          startRepositoriesListForResult(REPOSITORY_ADD_RELEASE);
        }
      }
    });

    mReleasesList.addFooterView(releasesLoadingFooterView, null, false);
    mReleasesList.addFooterView(releasesAddNewFooterView, null, true);

    // releases list adapter
    mReleasesAdapter = new ReleasesAdapter(this, R.layout.releases_entry, mReleaseArray);
    mReleasesList.setAdapter(mReleasesAdapter);

    // listener for release details activity
    mReleasesList.setOnItemClickListener(this);

    // create servers list view and tab switching button, attach "loading" and
    // "add new" footer and set button listeners
    mServersList = (ExpandableListView) findViewById(R.id.servers_list);
    serversLoadingFooterView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
        .inflate(R.layout.environments_server_environments_loading_field, null, false);
    serversAddNewFooterView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
        .inflate(R.layout.add_server_environment_footer, null, false);
    mServersList.addFooterView(serversLoadingFooterView, null, false);
    mServersList.addFooterView(serversAddNewFooterView);
    serversButton = (Button) findViewById(R.id.servers_button);
    serversButton.setOnClickListener(this);
    serversButton.setSelected(false);
    // servers list adapter
    mServersAdapter = new ServersAdapter(this, R.layout.environments_list_entry,
        mServersArray);
    mServersList.setAdapter(mServersAdapter);

    serversAddNewFooterView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if(activityStartedForSpecificRepository) {
          startAddNewServer(repository);
        } else {
          startRepositoriesListForResult(REPOSITORY_ADD_ENVIROMENT);
        }
      }
    });

    new DownloadReleaseListTask(this).execute();
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, final int position,
      long id) {
    if (adapterView == mReleasesList) {
      if (!activityStartedForSpecificRepository) {
        final Release release = mReleaseArray.get(position);
        ResponseHandler<Repository> handler = new ResponseHandler<Repository>() {
          @Override
          public void onResult(Repository result) {
            startReleaseDetails(release, result);
          }
        };
        new DownloadRepositoryDetailsTask(release.getRepositoryId(), this, handler).execute();
      } else {
        startReleaseDetails(mReleaseArray.get(position), repository);
      }
    }
  }

  private void startRepositoriesListForResult(int requestCode) {
    Intent intent = new Intent(this, RepositoriesActivity.class);
    intent.putExtra(Constants.RETURN_RESULT_WHEN_CLICK, true);
    startActivityForResult(intent, requestCode);
  }
  
  private void startAddNewServer(Repository repository) {
    Intent intent = new Intent(this, CreateNewServerActivity.class);
    intent.putExtra(Constants.REPOSITORY_ID, repository.getId());
    startActivity(intent);
  }
  
  private void startAddNewRelease(Repository repository) {
    Intent intent = new Intent(this, CreateNewReleaseActivity.class);
    intent.putExtra(Constants.REPOSITORY_ID, String.valueOf(repository.getId()));
    intent.putExtra(Constants.REPOSITORY_TITLE, repository.getTitle());
    intent.putExtra(Constants.REPOSITORY_COLOR_NO, repository.getColorLabelNo());
    startActivityForResult(intent, 0);
  }
  
  private void startReleaseDetails(Release release, Repository repository) {
    Intent intent = new Intent(RepositoryDeploymentsActivity.this, ReleaseDetailsActivity.class);
    intent.putExtra(Constants.REPOSITORY_COLOR_NO, repository.getColorLabelNo());
    intent.putExtra(Constants.REPOSITORY_TITLE, repository.getTitle());
    intent.putExtra(Constants.RELEASE, release);
    startActivity(intent);
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
      if (!activityStartedForSpecificRepository && !serverListLoaded) {
        new DownloadRepositoriesIdsTask().execute();
      } else if (!serverListLoaded) {
        downloadServerEnvironments();
      }
      mReleasesList.setVisibility(View.GONE);
      mServersList.setVisibility(View.VISIBLE);
      releasesButton.setSelected(false);
      serversButton.setSelected(true);
      break;
    }
  }

  private void downloadServerEnvironments() {
    new DownloadServerEnvironmentsListTask(this, listOfRepositoriesIds).execute();
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(resultCode == RepositoriesActivity.RESULT_REPOSITORY) {
      Repository repository = (Repository)data.getParcelableExtra(Constants.REPOSITORY);
      if(requestCode == REPOSITORY_ADD_ENVIROMENT) {
        startAddNewServer(repository);
      } else if(requestCode == REPOSITORY_ADD_RELEASE) {
        startAddNewRelease(repository);
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  public class DownloadRepositoryDetailsTask extends
      BeanstalkAsyncTask<Void, Void, Repository> {

    private int repositoryId;
    private ResponseHandler<Repository> responseHandler;

    public DownloadRepositoryDetailsTask(int repositoryId, Activity activity) {
      super(activity);
      this.repositoryId = repositoryId;
    }

    public DownloadRepositoryDetailsTask(int repositoryId, Activity activity,
        ResponseHandler<Repository> responseHandler) {
      this(repositoryId, activity);
      this.responseHandler = responseHandler;
    }

    @Override
    protected Repository trueDoInBackground(Void[] params) throws Throwable {
      String repositoryXML = HttpRetriever.getRepositoryXML(prefs, repositoryId);
      Repository repository = XmlParser.parseRepository(repositoryXML);
      return repository;
    }

    @Override
    protected void trueOnPostExceute(Repository result) {
      if (responseHandler != null) {
        responseHandler.onResult(result);
      }
    }

    @Override
    protected void performTaskAgain() {
      new DownloadRepositoryDetailsTask(repositoryId, RepositoryDeploymentsActivity.this,
          responseHandler).execute();
    }
  }

  // TODO change info dialog to list header
  public class DownloadReleaseListTask extends AsyncTask<String, Void, List<Release>> {

    private Context context;

    private String errorMessage;
    private String failMessage;
    private boolean failed = false;

    public DownloadReleaseListTask(Context context) {
      this.context = context;
    }

    @Override
    protected List<Release> doInBackground(String... params) {

      try {
        String releasesXml = null;
        if (activityStartedForSpecificRepository) {
          releasesXml = HttpRetriever.getReleasesListForRepositoryXML(prefs,
              repository.getId());
        } else {
          releasesXml = HttpRetriever.getReleasesListForAllRepositories(prefs);
        }
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
      mReleasesList.removeFooterView(releasesLoadingFooterView);

      if (failed) {
        Log.d("xxx", failMessage);
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

  public class DownloadRepositoriesIdsTask extends AsyncTask<Void, Void, List<Integer>> {

    @Override
    protected List<Integer> doInBackground(Void... arg) {
      try {
        String repositoriesXml = HttpRetriever.getRepositoryListXML(prefs);
        return XmlParser.parseRepositoryIdsList(repositoriesXml);
      } catch (Exception e) {

      }
      return null;
    }

    @Override
    protected void onPostExecute(List<Integer> result) {
      listOfRepositoriesIds = result;
      downloadServerEnvironments();
    }
  }

  public class DownloadServerEnvironmentsListTask extends
      AsyncTask<String, Void, List<ServerEnvironment>> {

    private Context context;

    private String failMessage;
    private boolean failed = false;

    private List<Integer> listOfRepositoriesIdsToDownload;

    public DownloadServerEnvironmentsListTask(Context context, int singleRepoId) {
      this.context = context;
      listOfRepositoriesIdsToDownload = new ArrayList<Integer>();
      listOfRepositoriesIdsToDownload.add(singleRepoId);
    }

    public DownloadServerEnvironmentsListTask(Context context,
        List<Integer> repositoriesIds) {
      this.context = context;
      this.listOfRepositoriesIdsToDownload = repositoriesIds;
    }

    @Override
    protected List<ServerEnvironment> doInBackground(String... params) {

      try {
        List<ServerEnvironment> enviroments = new ArrayList<ServerEnvironment>();
        for (Integer singleRepoId : listOfRepositoriesIdsToDownload) {
          String serverEnvironmentsXml = HttpRetriever
              .getServerEnvironmentListForRepositoryXML(prefs, singleRepoId.intValue());
          Log.d("xxx", serverEnvironmentsXml);
          enviroments
              .addAll(XmlParser.parseServerEnvironmentsList(serverEnvironmentsXml));
        }
        return enviroments;

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
      Log.d("xxx", String.valueOf(result != null ? result.size() : 0));
      mServersList.removeFooterView(serversLoadingFooterView);
      if (failed) {
        Log.d("xxx", failMessage);
        SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(context,
            failMessage) {

          @Override
          public void retryAction() {
            new DownloadServerEnvironmentsListTask(context,
                listOfRepositoriesIdsToDownload).execute();
          }

          @Override
          public void noRetryAction(DialogInterface dialog) {
            super.noRetryAction(dialog);
            finish();

          }

        };

        builder.displayDialog();
      } else {
        serverListLoaded = true;
        mServersArray.clear();
        if (result != null) {
          mServersArray.addAll(result);
          mServersAdapter.notifyDataSetChanged();
        }

        if (failMessage != null)
          GUI.displayMonit(context, failMessage);

      }
    }
  }

}
