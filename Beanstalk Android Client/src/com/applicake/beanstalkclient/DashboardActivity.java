package com.applicake.beanstalkclient;

import java.util.ArrayList;
import java.util.HashMap;

import com.applicake.beanstalkclient.adapters.ChangesetAdapter;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class DashboardActivity extends BeanstalkActivity implements OnItemClickListener,
		OnScrollListener {

	private final static int NUMBER_OF_ENTRIES_PER_PAGE = 15;

	private ChangesetAdapter changesetAdapter;
	private ArrayList<Changeset> changesetArray;
	private Context mContext;

	// never-ending list implementation fields
	private boolean loading = true;
	private boolean listMightHaveMoreItems = false;
	private int lastLoadedPage = 1;
	private View footerView;
	private HashMap<Integer, Repository> repositoryMap;

	private ListView changesetList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dashboard_activity_layout);

		mContext = getApplicationContext();

		changesetList = (ListView) findViewById(R.id.changesetList);
		changesetArray = new ArrayList<Changeset>();
		changesetAdapter = new ChangesetAdapter(this, R.layout.dashboard_entry,
				changesetArray);

		footerView = ((LayoutInflater) getApplicationContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.dashboard_activity_footer, changesetList, false);
		footerView.setEnabled(false);
		changesetList.addFooterView(footerView);

		changesetList.setAdapter(changesetAdapter);
		changesetList.setOnItemClickListener(this);
		changesetList.setOnScrollListener(this);

		new DownloadChangesetListTask().execute();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int itemNumber, long arg3) {
		// implement clicking on all elements except the footer
		if (itemNumber < changesetArray.size()) {
			Intent intent = new Intent(mContext, ChangesetActivity.class);
			Changeset changeset = changesetArray.get(itemNumber);
			intent.putParcelableArrayListExtra(Constants.CHANGEDFILES_ARRAYLIST,
					changeset.getChangedFiles());
			intent.putParcelableArrayListExtra(Constants.CHANGEDDIRS_ARRAYLIST,
					changeset.getChangedDirs());
			Repository repository = repositoryMap.get(changeset.getRepositoryId());
			intent.putExtra(Constants.COMMIT_REPOSIOTRY_NAME, repository.getTitle());
			intent.putExtra(Constants.COMMIT_REPOSIOTRY_LABEL, repository.getColorLabelNo());
			intent.putExtra(Constants.COMMIT_USERNAME, changeset.getAuthor());
			intent.putExtra(Constants.COMMIT_MESSAGE, changeset.getMessage());
			intent.putExtra(Constants.COMMIT_REPOSITORY_ID, changeset.getRepositoryId());
			intent.putExtra(
					Constants.COMMIT_REVISION_ID,
					changeset.getHashId() == "" ? changeset.getRevision() : changeset
							.getHashId());
			startActivityForResult(intent, 0);
		}

	}

	// implementation of never-ending list via OnScrollListener interface

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
		int lastVisibleItem = firstVisibleItem + visibleItemCount;
		if ((!loading) && listMightHaveMoreItems && (lastVisibleItem == totalItemCount)) {
			new DownloadAnotherPageOfChangesetListTask().execute();
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	public class DownloadChangesetListTask extends
			AsyncTask<Void, Void, ArrayList<Changeset>> {

		
		private boolean failed = false;
		private String failMessage;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loading = true;
		}

		@Override
		protected ArrayList<Changeset> doInBackground(Void... params) {

			try {
				String xmlRepoList = HttpRetriever.getRepositoryListXML(prefs);
				String xmlChangesetList = HttpRetriever.getActivityListXML(prefs, 1);
				// parsing repository list
				repositoryMap = XmlParser.parseRepositoryHashMap(xmlRepoList);
				// parsing changeset list
				return XmlParser.parseChangesetList(xmlChangesetList);

			} catch (XMLParserException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (UnsuccessfulServerResponseException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (HttpConnectionErrorException e) {
				failMessage = Strings.networkConnectionErrorMessage;
			}
			failed = true;
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Changeset> changesetParserArray) {
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
						DashboardActivity.this.finish();
					}
				};

				builder.displayDialog();

			} else {

				loading = false;
				if (changesetParserArray.size() < NUMBER_OF_ENTRIES_PER_PAGE) {
					changesetList.removeFooterView(footerView);
					listMightHaveMoreItems = false;
				} else {
					listMightHaveMoreItems = true;
				}
				changesetArray.addAll(changesetParserArray);
				changesetAdapter.setRepoHashMap(repositoryMap);

				changesetAdapter.notifyDataSetChanged();
			}
		}
	}

	public class DownloadAnotherPageOfChangesetListTask extends
			AsyncTask<Void, Void, ArrayList<Changeset>> {

		private boolean failed = false;
		private String failMessage;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loading = true;
		}

		@Override
		protected ArrayList<Changeset> doInBackground(Void... params) {

			try {
				String xmlChangesetList = HttpRetriever.getActivityListXML(prefs,
						lastLoadedPage + 1);
				// parsing changeset list
				return XmlParser.parseChangesetList(xmlChangesetList);
				// TODO better implementation of exception handling

			} catch (XMLParserException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (UnsuccessfulServerResponseException e) {
				failMessage = Strings.internalErrorMessage;
			} catch (HttpConnectionErrorException e) {
				failMessage = Strings.networkConnectionErrorMessage;
			}
			failed = true;
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Changeset> changesetParserArray) {
			if (failed) {
				SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
						failMessage) {

					@Override
					public void retryAction() {
						new DownloadAnotherPageOfChangesetListTask().execute();
					}

					@Override
					public void noRetryAction(DialogInterface dialog) {
						super.noRetryAction(dialog);
						DashboardActivity.this.finish();
					}
				};

				builder.displayDialog();

			} else {

				lastLoadedPage++;
				loading = false;

				if (changesetParserArray.size() < NUMBER_OF_ENTRIES_PER_PAGE) {
					changesetList.removeFooterView(footerView);
					listMightHaveMoreItems = false;
				} else {
					listMightHaveMoreItems = true;
				}

				if (changesetParserArray != null && !changesetParserArray.isEmpty()) {
					for (Changeset ch : changesetParserArray) {
						changesetArray.add(ch);
					}
				}
				changesetAdapter.notifyDataSetChanged();
			}
		}
	}

}
