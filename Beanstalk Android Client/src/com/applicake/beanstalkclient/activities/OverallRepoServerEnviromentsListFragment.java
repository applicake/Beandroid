package com.applicake.beanstalkclient.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.permissions.CanAddNewEnvironmentToRepositoryFilter;
import com.applicake.beanstalkclient.tasks.BeanstalkAsyncTask;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.XmlParser;

public class OverallRepoServerEnviromentsListFragment extends SpecificRepoServerEnviromentsFragment {
  
  private static final int REPOSITORY_ADD_ENVIROMENT = 0x123;
  
  @Override
  protected void downloadEnviroments() {
    new DownloadRepositoriesIdsTask(getActivity()).execute();
  }
  
  public class DownloadRepositoriesIdsTask extends BeanstalkAsyncTask<Void, Void, List<Integer>> {

    public DownloadRepositoriesIdsTask(Activity context) {
      super(context);
    }

    @Override
    protected List<Integer> trueDoInBackground(Void... arg) {
      try {
        String repositoriesXml = HttpRetriever.getRepositoryListXML(prefs);
        return XmlParser.parseRepositoryIdsList(repositoriesXml);
      } catch (Exception e) {
      }
      return null;
    }

    @Override
    protected void onPostExecute(List<Integer> result) {
      new DownloadServerEnvironmentsListTask(getActivity(), result).execute();
    }

    @Override
    protected void performTaskAgain() {
      downloadEnviroments();
    }
  }
  
  @Override
  public void onClick(View view) {
    Intent intent = new Intent(getActivity(), RepositoriesActivity.class);
    intent.setAction(Intent.ACTION_PICK);
    intent.putExtra(Constants.FILTER_CLASS, CanAddNewEnvironmentToRepositoryFilter.class);
    startActivityForResult(intent, REPOSITORY_ADD_ENVIROMENT);
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode == REPOSITORY_ADD_ENVIROMENT && resultCode == RepositoriesActivity.RESULT_REPOSITORY) {
      Repository repo = data.getParcelableExtra(Constants.REPOSITORY);
      startAddNewServer(repo);
    }
  }
  

}
