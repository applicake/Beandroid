package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.applicake.beanstalkclient.adapters.UserPermissionsAdapter;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpRetreiverException;

public class UserPermissionsActivity extends BeanstalkActivity implements
		OnItemClickListener {

	private String userId;
	private Context mContext;
	private ProgressDialog progressDialog;
	private ArrayList<Repository> repositoriesArray;
	// private ArrayList<Permission> permissionsArray;
	private Map<Integer, Permission> repoIdToPermission;
	private UserPermissionsAdapter repositoriesAdapter;
	private ListView repositoriesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_permissions_layout);
		userId = getIntent().getStringExtra(Constants.USER_ID);

		mContext = this;
		repositoriesList = (ListView) findViewById(R.id.userPermissionsList);

		repositoriesArray = new ArrayList<Repository>();
		repositoriesAdapter = new UserPermissionsAdapter(getApplicationContext(),
				R.layout.user_permissions_entry, repositoriesArray, repoIdToPermission);
		repositoriesList.setAdapter(repositoriesAdapter);
		repositoriesList.setOnItemClickListener(this);

		new DownloadRepositoryListTask().execute();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int itemNumber, long arg3) {
		if (itemNumber < repositoriesArray.size()) {
			Intent intent = new Intent(mContext, RepositoryDetailsActivity.class);
			intent.putExtra(Constants.REPOSITORY, repositoriesArray.get(itemNumber));
			startActivityForResult(intent, 0);
		}

	}

	public class DownloadRepositoryListTask extends AsyncTask<String, Void, Integer> {

		private static final int SUCCESSFUL_PARSING = 1;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(mContext, "Loading Repository list",
					"Please wait...");
		}

		@Override
		protected Integer doInBackground(String... params) {

			HttpRetriever httpRetriever = new HttpRetriever();

			try {
				String repositoriesXml = httpRetriever.getRepositoryListXML(prefs);
				XmlParser xmlParser = new XmlParser();
				repositoriesArray = xmlParser.parseRepositoryList(repositoriesXml);
				

				String permissionsXml = new HttpRetriever().getPermissionListForUserXML(prefs,
						userId);
				Log.w("permission xml", permissionsXml);
				repoIdToPermission = xmlParser.parsePermissionHashMap(permissionsXml);
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

			return SUCCESSFUL_PARSING;
		}

		@Override
		protected void onPostExecute(Integer result) {
			progressDialog.cancel();

			if (result == 1) {
				repositoriesAdapter.setRepoIdToPermissionMap(repoIdToPermission);
				if (repositoriesArray != null && !repositoriesArray.isEmpty()) {
					repositoriesAdapter.notifyDataSetChanged();
					repositoriesAdapter.clear();

					for (int i = 0; i < repositoriesArray.size(); i++) {
						repositoriesAdapter.add(repositoriesArray.get(i));
					}

				}

			}

			repositoriesAdapter.notifyDataSetChanged();

		}

	}

}
