package com.applicake.beanstalkclient.activities;

import java.util.List;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.XmlParser;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews.ActionException;

public class OverallRepoServerEnviromentsListFragment extends SpecificRepoServerEnviromentsFragment {
  
  private static final int REPOSITORY_ADD_ENVIROMENT = 0x123;
  
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    new DownloadRepositoriesIdsTask().execute();
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
      new DownloadServerEnvironmentsListTask(getActivity(), result).execute();
    }
  }
  
  @Override
  public void onClick(View view) {
    Intent intent = new Intent(getActivity(), RepositoriesActivity.class);
    //Intent intent = new Intent(Intent.ACTION_PICK);
    intent.putExtra(Constants.RETURN_RESULT_WHEN_CLICK, true);
    //intent.setDataAndType(Uri.EMPTY, "application/repos");
    //intent.setType("application/repos");
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