package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.applicake.beanstalkclient.HttpRetriever.HttpRetreiverException;
import com.applicake.beanstalkclient.adapters.ChangesetAdapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
		
		String accountDomain = this.prefs.getString(Constants.USER_ACCOUNT_DOMAIN, "");
		String login = this.prefs.getString(Constants.USER_LOGIN, "");
		String password = this.prefs.getString(Constants.USER_PASSWORD, "");
	
		new DownloadChangesetListTask().execute(accountDomain, login, password);
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(mContext, ChangesetActivity.class);
		intent.putExtra("changeset", changesetArray.get(arg2));
		startActivityForResult(intent, 0);
		
	}

	
	
	
	public class DownloadChangesetListTask extends AsyncTask<String, Void, ArrayList<Changeset>> {

		private String domain;
		private String login;
		private String password;
		private HashMap<Integer, Repository> repositoryMap;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(mContext, "Loading Activity list",
			"Please wait...");
		}

		@Override
		protected ArrayList<Changeset> doInBackground(String... params) {

			domain = params[0];
			login = params[1];
			password = params[2];
			
			try {
				HttpRetriever httpRetriever = new HttpRetriever();
				String xmlRepoList = httpRetriever.getRepositoryListXML(domain, login, password);
				String xmlChangesetList = httpRetriever.getActivityListXML(domain, login, password);
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
