package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.applicake.beanstalkclient.adapters.RepositoriesAdapter;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpRetreiverException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RepositoriesActivity extends BeanstalkActivity implements OnItemClickListener, OnClickListener {

	private Context mContext;
	private ProgressDialog progressDialog;
	public ArrayList<Repository> repositoriesArray;
	public RepositoriesAdapter repositoriesAdapter;
	public ListView repositoriesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.repositories_layout);

		mContext = this;
		repositoriesList = (ListView) findViewById(R.id.repositoriesList);
		View footerView = ((LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.add_new_repository, null, false);
		repositoriesList.addFooterView(footerView);
		footerView.setOnClickListener(this);
		
		repositoriesArray = new ArrayList<Repository>();
		repositoriesAdapter = new RepositoriesAdapter(getApplicationContext(),
				R.layout.repositories_entry, repositoriesArray);
		repositoriesList.setAdapter(repositoriesAdapter);
		repositoriesList.setOnItemClickListener(this);
		


		// changesetList.setOnItemClickListener(this);
		new DownloadChangesetListTask().execute();

	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(mContext, CreateNewRepositoryActivity.class);
		startActivityForResult(intent, 0);
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int itemNumber, long arg3) {
		if (itemNumber < repositoriesArray.size()){
			Intent intent = new Intent(mContext, RepositoryDetailsActivity.class);
			intent.putExtra(Constants.REPOSITORY, repositoriesArray.get(itemNumber));
			startActivityForResult(intent, 0);
		}
		
	}


	public class DownloadChangesetListTask extends
			AsyncTask<String, Void, ArrayList<Repository>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(mContext, "Loading Repository list",
					"Please wait...");
		}

		@Override
		protected ArrayList<Repository> doInBackground(String... params) {

			HttpRetriever httpRetriever = new HttpRetriever();

			try {
				String repositoriesXml = httpRetriever.getRepositoryListXML(prefs);
				XmlParser xmlParser = new XmlParser();
				return xmlParser.parseRepositoryList(repositoriesXml);
			} catch (HttpRetreiverException e) {
				// TODO generate http parsing exception handling
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Repository> result) {
			repositoriesArray = result;
			progressDialog.cancel();

			// commentArray = result;
			if (repositoriesArray != null && !repositoriesArray.isEmpty()) {
				repositoriesAdapter.notifyDataSetChanged();
				repositoriesAdapter.clear();

				for (int i = 0; i < repositoriesArray.size(); i++) {
					repositoriesAdapter.add(repositoriesArray.get(i));
				}

			}

			repositoriesAdapter.notifyDataSetChanged();

		}

	}


}
