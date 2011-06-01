package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.applicake.beanstalkclient.adapters.RepositoriesAdapter;
import com.applicake.beanstalkclient.enums.Plans;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpRetreiverException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class RepositoriesActivity extends BeanstalkActivity implements
		OnItemClickListener, OnClickListener {

	private Context mContext;
	private ProgressDialog progressDialog;
	public ArrayList<Repository> repositoriesArray;
	public RepositoriesAdapter repositoriesAdapter;
	public ListView repositoriesList;
	public TextView repositoriesLeftCounter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.repositories_layout);

		mContext = this;
		repositoriesList = (ListView) findViewById(R.id.repositoriesList);
		View footerView = ((LayoutInflater) getApplicationContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.add_new_repository,
				null, false);
		repositoriesList.addFooterView(footerView);
		footerView.setOnClickListener(this);

		repositoriesArray = new ArrayList<Repository>();
		repositoriesAdapter = new RepositoriesAdapter(getApplicationContext(),
				R.layout.repositories_entry, repositoriesArray);
		repositoriesList.setAdapter(repositoriesAdapter);
		repositoriesList.setOnItemClickListener(this);

		repositoriesLeftCounter = (TextView) footerView
				.findViewById(R.id.repositoryCounter);

		// changesetList.setOnItemClickListener(this);
		new DownloadChangesetListTask().execute();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Constants.REFRESH_ACTIVITY)
			new DownloadChangesetListTask().execute();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(mContext, CreateNewRepositoryActivity.class);
		startActivityForResult(intent, 0);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int itemNumber, long arg3) {
		if (itemNumber < repositoriesArray.size()) {
			Intent intent = new Intent(mContext, RepositoryDetailsActivity.class);
			intent.putExtra(Constants.REPOSITORY, repositoriesArray.get(itemNumber));
			startActivityForResult(intent, 0);
		}

	}

	public class DownloadChangesetListTask extends
			AsyncTask<String, Void, ArrayList<Repository>> {

		@SuppressWarnings("rawtypes")
		private AsyncTask thisTask = this;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(mContext, "Loading Repository list",
					"Please wait...");
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					thisTask.cancel(true);
				}
			});

		}

		@Override
		protected ArrayList<Repository> doInBackground(String... params) {

			HttpRetriever httpRetriever = new HttpRetriever();

			try {
				String repositoriesXml = httpRetriever.getRepositoryListXML(prefs);
				return XmlParser.parseRepositoryList(repositoriesXml);
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
			repositoriesArray.clear();
			repositoriesArray.addAll(result);
			progressDialog.dismiss();

			repositoriesAdapter.notifyDataSetChanged();

			int repositoriesInPlan = Plans.getPlanById(
					prefs.getInt(Constants.ACCOUNT_PLAN, 0)).getNumberOfRepos();
			int numberLeft = repositoriesInPlan - repositoriesArray.size();
			repositoriesLeftCounter.setText("available repositories: "
					+ String.valueOf(numberLeft) + "/"
					+ String.valueOf(repositoriesInPlan));

		}

	}

}
