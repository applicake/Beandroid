package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.applicake.beanstalkclient.adapters.ChangesetAdapter;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpRetreiverException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class DashboardActivity extends BeanstalkActivity implements OnItemClickListener {
	
	private ChangesetAdapter changesetAdapter;
	private ArrayList<Changeset> changesetArray;
	private ProgressDialog progressDialog;
	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.dashboard_activity_layout);
		
		mContext = this;
		changesetArray = new ArrayList<Changeset>();
		changesetAdapter = new ChangesetAdapter(this, R.layout.dashboard_entry, changesetArray);
	
		ListView changesetList = (ListView) findViewById(R.id.changesetList);
		changesetList.setAdapter(changesetAdapter);
		changesetList.setOnItemClickListener(this);
		
		new DownloadChangesetListTask().execute();
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int itemNumber, long arg3) {
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

	public class DownloadChangesetListTask extends AsyncTask<Void, Void, ArrayList<Changeset>> {

		private HashMap<Integer, Repository> repositoryMap;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(mContext, "Loading Activity list",
			"Please wait...");
		}

		@Override
		protected ArrayList<Changeset> doInBackground(Void... params) {
			
			try {
				HttpRetriever httpRetriever = new HttpRetriever();
				String xmlRepoList = httpRetriever.getRepositoryListXML(prefs);
				String xmlChangesetList = httpRetriever.getActivityListXML(prefs);
				XmlParser xmlParser = new XmlParser();
				// parsing repository list
				repositoryMap = xmlParser.parseRepositoryHashMap(xmlRepoList);
				//parsing changeset list
				return xmlParser.parseChangesetList(xmlChangesetList);
				//TODO better implementation of exception handling
			} catch (ParserConfigurationException e) {
				GUI.displayMonit(mContext, "An error occured while paring Changeset list");
				e.printStackTrace();
			} catch (SAXException e) {
				GUI.displayMonit(mContext, "An error occured while paring Changeset list");
				e.printStackTrace();
			} catch (IOException e) {
				GUI.displayMonit(mContext, "An error occured while paring Changeset list");
				e.printStackTrace();
			} catch (HttpRetreiverException e) {
				GUI.displayMonit(mContext, "An error occured while parsing Changeset list");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Changeset> changesetParserArray) {
			changesetArray = changesetParserArray;
			changesetAdapter.setRepoHashMap(repositoryMap);
			
			if (changesetArray != null && !changesetArray.isEmpty()) {
				changesetAdapter.notifyDataSetChanged();
				changesetAdapter.clear();
				
				for (int i = 0; i < changesetArray.size(); i++) {
					changesetAdapter.add(changesetArray.get(i));
				}
			}
			
			changesetAdapter.notifyDataSetChanged();
			progressDialog.cancel();

		}

	}




}
