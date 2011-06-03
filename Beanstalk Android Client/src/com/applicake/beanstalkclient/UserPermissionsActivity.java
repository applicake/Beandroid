package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.applicake.beanstalkclient.adapters.UserPermissionsAdapter;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpRetreiverException;

public class UserPermissionsActivity extends BeanstalkActivity implements
		OnItemClickListener {

	private User user;
	private Context mContext;
	private ProgressDialog progressDialog;
	private ArrayList<Repository> repositoriesArray;
	// private ArrayList<Permission> permissionsArray;
	private Map<Integer, Permission> repoIdToPermission;
	private UserPermissionsAdapter repositoriesAdapter;
	private ListView repositoriesList;
	private DownloadRepositoryListTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_permissions_layout);
		user = getIntent().getParcelableExtra(Constants.USER);

		mContext = this;
		repositoriesList = (ListView) findViewById(R.id.userPermissionsList);

		repositoriesArray = new ArrayList<Repository>();
		repositoriesAdapter = new UserPermissionsAdapter(getApplicationContext(),
				R.layout.user_permissions_entry, repositoriesArray, repoIdToPermission);
		repositoriesList.setAdapter(repositoriesAdapter);
		repositoriesList.setOnItemClickListener(this);

		task = new DownloadRepositoryListTask();
		task.execute();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int itemNumber, long arg3) {
		if (itemNumber < repositoriesArray.size()) {
			Intent intent = new Intent(mContext, PermissionModifyActivity.class);
			Repository repository = repositoriesArray.get(itemNumber);
			intent.putExtra(Constants.REPOSITORY, repository);
			intent.putExtra(Constants.USER, user);
			if (repoIdToPermission.containsKey(repository.getId())) {
				intent.putExtra(Constants.PERMISSION,
						repoIdToPermission.get(repository.getId()));
			}
			startActivityForResult(intent, 0);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Constants.REFRESH_ACTIVITY) {
			task = new DownloadRepositoryListTask();
			task.execute();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void cancelAllDownloadTasks() {
		if (task != null)
			task.cancel(true);
		super.cancelAllDownloadTasks();
	}

	public class DownloadRepositoryListTask extends AsyncTask<String, Void, Integer> {

		private static final int SUCCESSFUL_PARSING = 1;
		
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
					GUI.displayMonit(mContext, "Data sending task was cancelled");
				}
			});
		}

		@Override
		protected Integer doInBackground(String... params) {


			try {
				String repositoriesXml = HttpRetriever.getRepositoryListXML(prefs);
				repositoriesArray.clear();
				repositoriesArray.addAll(XmlParser.parseRepositoryList(repositoriesXml));

				String permissionsXml = HttpRetriever.getPermissionListForUserXML(
						prefs, String.valueOf(user.getId()));
				Log.w("permission xml", permissionsXml);
				repoIdToPermission = XmlParser
						.parseRepoIdToPermissionHashMap(permissionsXml);
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
			} catch (HttpConnectionErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsuccessfulServerResponseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return SUCCESSFUL_PARSING;
		}

		@Override
		protected void onPostExecute(Integer result) {
			progressDialog.dismiss();

			if (result == 1) {
				repositoriesAdapter.setRepoIdToPermissionMap(repoIdToPermission);
			}

			repositoriesAdapter.notifyDataSetChanged();

		}

	}

}
