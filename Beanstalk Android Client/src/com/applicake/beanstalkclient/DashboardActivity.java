package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.applicake.beanstalkclient.adapters.ChangesetAdapter;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpRetreiverException;

import android.content.Context;
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

public class DashboardActivity extends BeanstalkActivity implements OnItemClickListener, OnScrollListener {
	
	private final static int NUMBER_OF_ENTRIES_PER_PAGE = 15;
	
	private ChangesetAdapter changesetAdapter;
	private ArrayList<Changeset> changesetArray;
	private Context mContext;
	
	// never-ending list implementation fields
	private boolean loading = true;
	private boolean listMightHaveMoreItems = false;
	private int lastLoadedPage = 1;
	private View footerView;

	private ListView changesetList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.dashboard_activity_layout);
		
		mContext = this;
		
		changesetList = (ListView) findViewById(R.id.changesetList);
		changesetArray = new ArrayList<Changeset>();
		changesetAdapter = new ChangesetAdapter(this, R.layout.dashboard_entry, changesetArray);
	
		footerView = ((LayoutInflater) getApplicationContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dashboard_activity_footer,
				changesetList, false);
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
		if (itemNumber < changesetArray.size()){
			Intent intent = new Intent(mContext, ChangesetActivity.class);
			Changeset changeset = changesetArray.get(itemNumber);
			intent.putParcelableArrayListExtra(Constants.CHANGEDFILES_ARRAYLIST, changeset.getChangedFiles());
			intent.putParcelableArrayListExtra(Constants.CHANGEDDIRS_ARRAYLIST, changeset.getChangedDirs());
			intent.putExtra(Constants.COMMIT_USERNAME, changeset.getAuthor());
			intent.putExtra(Constants.COMMIT_MESSAGE, changeset.getMessage());
			intent.putExtra(Constants.COMMIT_REPOSITORY_ID, changeset.getRepositoryId());
			intent.putExtra(Constants.COMMIT_REVISION_ID, changeset.getHashId() == "" ? changeset.getRevision(): changeset.getHashId());
			startActivityForResult(intent, 0);
		}
		
	}
	
	//implementation of never-ending list via OnScrollListener interface
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
		int lastVisibleItem = firstVisibleItem + visibleItemCount;
		if ((!loading) && listMightHaveMoreItems && (lastVisibleItem == totalItemCount)){
			new DownloadAnotherPageOfChangesetListTask().execute();
		}
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
	
	public class DownloadChangesetListTask extends AsyncTask<Void, Void, ArrayList<Changeset>> {

		private HashMap<Integer, Repository> repositoryMap;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loading = true;
//			progressDialog = ProgressDialog.show(mContext, "Please wait...",
//			"Loading activity list...");
		}

		@Override
		protected ArrayList<Changeset> doInBackground(Void... params) {
			
			try {
				HttpRetriever httpRetriever = new HttpRetriever();
				String xmlRepoList = httpRetriever.getRepositoryListXML(prefs);
				String xmlChangesetList = httpRetriever.getActivityListXML(prefs, 1);
				// parsing repository list
				repositoryMap = XmlParser.parseRepositoryHashMap(xmlRepoList);
				//parsing changeset list
				return XmlParser.parseChangesetList(xmlChangesetList);
				//TODO better implementation of exception handling
			} catch (ParserConfigurationException e) {
				GUI.displayMonit(mContext, "An error occured while parsing Changeset list");
				e.printStackTrace();
			} catch (SAXException e) {
				GUI.displayMonit(mContext, "An error occured while parsing Changeset list");
				e.printStackTrace();
			} catch (IOException e) {
				GUI.displayMonit(mContext, "An error occured while parsing Changeset list");
				e.printStackTrace();
			} catch (HttpRetreiverException e) {
				GUI.displayMonit(mContext, "An error occured while parsing Changeset list");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Changeset> changesetParserArray) {
			loading = false;
//			lastLoadedPage = 1;
			if (changesetParserArray.size() < NUMBER_OF_ENTRIES_PER_PAGE){
				changesetList.removeFooterView(footerView);
//				footerView.setVisibility(View.GONE);
				listMightHaveMoreItems = false;
			} else {
				listMightHaveMoreItems = true;
			}
			changesetArray.addAll(changesetParserArray);
			changesetAdapter.setRepoHashMap(repositoryMap);
//			
//			if (changesetArray != null && !changesetArray.isEmpty()) {
//				for (int i = 0; i < changesetArray.size(); i++) {
//					changesetAdapter.add(changesetArray.get(i));
//				}
//			}
			changesetAdapter.notifyDataSetChanged();
//			progressDialog.cancel();
		}
	}
	
	public class DownloadAnotherPageOfChangesetListTask extends AsyncTask<Void, Void, ArrayList<Changeset>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loading = true;
//			progressDialog = ProgressDialog.show(mContext, "Please wait...",
//			"Loading activity list...");
		}

		@Override
		protected ArrayList<Changeset> doInBackground(Void... params) {
			
			try {
				HttpRetriever httpRetriever = new HttpRetriever();
				String xmlChangesetList = httpRetriever.getActivityListXML(prefs, lastLoadedPage + 1);
				//parsing changeset list
				return XmlParser.parseChangesetList(xmlChangesetList);
				//TODO better implementation of exception handling
			} catch (ParserConfigurationException e) {
				GUI.displayMonit(mContext, "An error occured while parsing Changeset list");
				e.printStackTrace();
			} catch (SAXException e) {
				GUI.displayMonit(mContext, "An error occured while parsing Changeset list");
				e.printStackTrace();
			} catch (IOException e) {
				GUI.displayMonit(mContext, "An error occured while parsing Changeset list");
				e.printStackTrace();
			} catch (HttpRetreiverException e) {
				GUI.displayMonit(mContext, "An error occured while parsing Changeset list");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Changeset> changesetParserArray) {	
			lastLoadedPage++;
			loading = false;
			
			
			if (changesetParserArray.size() < NUMBER_OF_ENTRIES_PER_PAGE){
				footerView.setVisibility(View.GONE);
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
