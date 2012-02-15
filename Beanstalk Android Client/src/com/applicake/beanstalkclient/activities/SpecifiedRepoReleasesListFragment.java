package com.applicake.beanstalkclient.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Release;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.adapters.ReleasesAdapter;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

public class SpecifiedRepoReleasesListFragment extends Fragment implements OnItemClickListener, OnClickListener {
  
  private ReleasesAdapter mReleasesAdapter;
  private Repository repository;
  protected List<Release> mReleaseArray = new ArrayList<Release>();
  protected SharedPreferences prefs;
  protected ListView mReleasesList;
  
  private View releasesLoadingFooterView;
  private View releasesAddNewFooterView;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        
    mReleasesAdapter = new ReleasesAdapter(getActivity(), R.layout.releases_entry, mReleaseArray);
    repository = getActivity().getIntent().getParcelableExtra(Constants.REPOSITORY);
    prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()); // can't we do just getActivity()? TODO
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mReleasesList = (ListView)LayoutInflater.from(getActivity()).inflate(R.layout.releases_list_fragment, null);
    mReleasesList.setOnItemClickListener(this);
    
    releasesLoadingFooterView = inflater.inflate(R.layout.releases_loading_footer, null, false);
    releasesAddNewFooterView = inflater.inflate(R.layout.add_new_release_footer, null, false);
    releasesAddNewFooterView.setOnClickListener(this);

    mReleasesList.addFooterView(releasesLoadingFooterView, null, false);
    mReleasesList.addFooterView(releasesAddNewFooterView, null, true);
    
    mReleasesList.setAdapter(mReleasesAdapter);

    
    return mReleasesList;
  }
  
  @Override
  public void onResume() {
    super.onResume();
    downloadReleases();
  }
  
  protected void downloadReleases() {
    new DownloadReleaseListTask(getActivity()).execute();
  }
  
  @Override
  public void onItemClick(AdapterView<?> listView, View itemView, int position, long id) {
    Release release = mReleaseArray.get(position);
    startReleaseDetails(release, repository);
  }
  
  @Override
  public void onClick(View v) {
    startAddNewRelease(repository);
  }
  
  protected void startAddNewRelease(Repository repository) {
    Intent intent = new Intent(getActivity(), CreateNewReleaseActivity.class);
    intent.putExtra(Constants.REPOSITORY_ID, String.valueOf(repository.getId()));
    intent.putExtra(Constants.REPOSITORY_TITLE, repository.getTitle());
    intent.putExtra(Constants.REPOSITORY_COLOR_NO, repository.getColorLabelNo());
    startActivityForResult(intent, 0);
  }
  
  protected void startReleaseDetails(Release release, Repository repository) {
    Intent intent = new Intent(getActivity(), ReleaseDetailsActivity.class);
    intent.putExtra(Constants.REPOSITORY_COLOR_NO, repository.getColorLabelNo());
    intent.putExtra(Constants.REPOSITORY_TITLE, repository.getTitle());
    intent.putExtra(Constants.RELEASE, release);
    startActivity(intent);
  }
  
  protected String getReleasesListXML() throws UnsuccessfulServerResponseException, HttpConnectionErrorException {
    return HttpRetriever.getReleasesListForRepositoryXML(prefs, repository.getId());
  }
  
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
        String releasesXml = getReleasesListXML();
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
            getActivity().finish();
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
