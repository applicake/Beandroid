package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.applicake.beanstalkclient.adapters.RepositoryPermissionsAdapter;
import com.applicake.beanstalkclient.adapters.UserPermissionsAdapter;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpRetreiverException;

public class RepositoryUsersPermissionsActivity extends BeanstalkActivity implements
		OnItemClickListener {

	private Context mContext;
	private ProgressDialog progressDialog;
	private ArrayList<User> usersArray;
	private RepositoryPermissionsAdapter usersAdapter;
	private ListView usersList;
	private Repository repository;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.repository_permissions_layout);
		repository = getIntent().getParcelableExtra(Constants.REPOSITORY);

		mContext = this;
		usersList = (ListView) findViewById(R.id.userPermissionsList);

		usersArray = new ArrayList<User>();
		usersAdapter = new RepositoryPermissionsAdapter(mContext, prefs, repository,
				R.layout.repository_permissions_entry, usersArray);
		usersList.setAdapter(usersAdapter);
		usersList.setOnItemClickListener(this);

		new DownloadUsersListTask().execute();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int itemNumber, long arg3) {
		// if (itemNumber < repositoriesArray.size()) {
		// Intent intent = new Intent(mContext, PermissionModifyActivity.class);
		// Repository repository = repositoriesArray.get(itemNumber);
		// intent.putExtra(Constants.REPOSITORY, repository);
		// intent.putExtra(Constants.USER, user);
		// if (repoIdToPermission.containsKey(repository.getId())){
		// intent.putExtra(Constants.PERMISSION,
		// repoIdToPermission.get(repository.getId()));
		// }
		// startActivityForResult(intent, 0);
		// }

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Constants.REFRESH_ACTIVITY) {
			// refresh one user entry instead
			// new DownloadUsersListTask().execute();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class DownloadUsersListTask extends AsyncTask<Void, Void, ArrayList<User>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(mContext, "Loading user list",
					"Please wait...");
		}

		@Override
		protected ArrayList<User> doInBackground(Void... params) {

			try {
				HttpRetriever httpRetriever = new HttpRetriever();
				String xmlUserList = httpRetriever.getUserListXML(prefs);
				XmlParser xmlParser = new XmlParser();
				// parsing users list
				return xmlParser.parseUserList(xmlUserList);
				// TODO better implementation of exception handling
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
				GUI.displayMonit(mContext,
						"An error occured while parsing Changeset list");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<User> parsedArray) {
			usersArray = parsedArray;

			if (usersArray != null && !usersArray.isEmpty()) {
				usersAdapter.notifyDataSetChanged();
				usersAdapter.clear();

				for (int i = 0; i < usersArray.size(); i++) {
					usersAdapter.add(usersArray.get(i));
					
				}
			}

			usersAdapter.notifyDataSetChanged();
			progressDialog.cancel();
			
			for (int i = 0; i< usersArray.size(); i++){
//				new DownloadPermissionsTask().execute(i);
				
			}
			
			

		}

	}

	public class DownloadPermissionsTask extends
			AsyncTask<Integer, Void, HashMap<Integer, Permission>> {

		
		private ProgressBar loadingBar;
		private TextView repoPermissionLabel;
		private TextView deploymentPermissionLabel;
		private TextView repoPermissionTitle;
		private TextView deploymentPermissionTitle;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected HashMap<Integer, Permission> doInBackground(Integer... params) {
			int i = params[0];
			View view = usersList.getChildAt(i);
			
			loadingBar = (ProgressBar) view.findViewById(R.id.loadingBar);

			repoPermissionLabel = (TextView) view.findViewById(R.id.repositoryLabel);
			deploymentPermissionLabel = (TextView) view
					.findViewById(R.id.deploymentLabel);

			repoPermissionTitle = (TextView) view.findViewById(R.id.repoPermission);
			deploymentPermissionTitle = (TextView) view
					.findViewById(R.id.deploymentPermission);
			
			
			
			String userId = String.valueOf(usersArray.get(i).getId());

			XmlParser xmlParser = new XmlParser();
			

			try {
				String permissionsXml = new HttpRetriever().getPermissionListForUserXML(
						prefs, userId);

				return xmlParser.parseRepoIdToPermissionHashMap(permissionsXml);
			} catch (HttpRetreiverException e) {
				// TODO generate http parsing exception handling
				e.printStackTrace();
				cancel(true);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				cancel(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				cancel(true);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				cancel(true);
			}

			return null;

		}

		@Override
		protected void onPostExecute(HashMap<Integer, Permission> result) {
			loadingBar.setVisibility(View.GONE);
			repoPermissionLabel.setVisibility(View.VISIBLE);
			deploymentPermissionLabel.setVisibility(View.VISIBLE);
			repoPermissionTitle.setVisibility(View.VISIBLE);
			deploymentPermissionTitle.setVisibility(View.VISIBLE);

			if (result.containsKey(repository.getId())) {
				Permission permission = result.get(repository.getId());

				if (permission.isReadAccess()) {
					if (permission.isWriteAccess()) {
						repoPermissionLabel.setText("write");
						repoPermissionLabel.getBackground().setLevel(0);
					} else {
						repoPermissionLabel.setText("read");
						repoPermissionLabel.getBackground().setLevel(1);
					}
				}

				if (permission.isFullDeploymentAccess()) {
					deploymentPermissionLabel.setText("write");
					deploymentPermissionLabel.getBackground().setLevel(0);
				} else {
					deploymentPermissionLabel.setText("read");
					deploymentPermissionLabel.getBackground().setLevel(2);
				}
			} else {
				repoPermissionLabel.setText("no access");
				repoPermissionLabel.getBackground().setLevel(2);

				deploymentPermissionLabel.setText("read");
				deploymentPermissionLabel.getBackground().setLevel(2);
			}

		}

	}

}
