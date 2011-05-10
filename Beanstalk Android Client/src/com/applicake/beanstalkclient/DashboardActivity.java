package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.applicake.beanstalkclient.adapters.ChangesetAdapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class DashboardActivity extends BeanstalkActivity {
	
	private ChangesetAdapter changesetAdapter;
	private ArrayList<Changeset> changesetArray;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard_activity_layout);
		
		changesetArray = new ArrayList<Changeset>();
		
		changesetAdapter = new ChangesetAdapter(this, R.layout.dashboard_entry, changesetArray);
		
		ListView changesetList = (ListView) findViewById(R.id.changesetList);
		changesetList.setAdapter(changesetAdapter);
		
		String accountDomain = this.prefs.getString(Constants.USER_ACCOUNT_DOMAIN, "");
		String login = this.prefs.getString(Constants.USER_LOGIN, "");
		String password = this.prefs.getString(Constants.USER_PASSWORD, "");
	
		new DownloadChangesetListTask().execute(accountDomain, login, password);
		
	}
	
	public class DownloadChangesetListTask extends AsyncTask<String, Void, ArrayList<Changeset>> {

		private String domain;
		private String login;
		private String password;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			progressDialog = ProgressDialog.show(getApplicationContext(), "Loading Activity list",
//			"Please wait...");
		}

		@Override
		protected ArrayList<Changeset> doInBackground(String... params) {

			domain = params[0];
			login = params[1];
			password = params[2];
			HttpRetriever httpRetriever = new HttpRetriever();
			String xml = httpRetriever.getActivityListXML(domain, login, password);
			XmlParser xmlParser = new XmlParser();

			try {
				return xmlParser.parseChangesetList(xml);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Changeset> changesetParserArray) {
			
			
			changesetArray = changesetParserArray;
			
			if (changesetArray != null && !changesetArray.isEmpty()) {
				changesetAdapter.notifyDataSetChanged();
				changesetAdapter.clear();
				Log.i("entryList size", String.valueOf(changesetArray.size()));

				for (int i = 0; i < changesetArray.size(); i++) {
					changesetAdapter.add(changesetArray.get(i));
				}
			}
			
			changesetAdapter.notifyDataSetChanged();
//			progressDialog.cancel();

		}

	}

}
