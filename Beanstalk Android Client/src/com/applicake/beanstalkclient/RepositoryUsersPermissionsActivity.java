package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.applicake.beanstalkclient.adapters.RepositoryPermissionsAdapter;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.utils.GUI;
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
	public void onItemClick(AdapterView<?> arg0, View view, int itemNumber, long arg3) {
		if (itemNumber < usersArray.size()) {
			User user = usersArray.get(itemNumber);
			if (user.getAdmin() == UserType.USER && (Boolean) view.getTag()) {
				Permission permission = usersAdapter.getUserIdToPermissionMap().get(
						user.getId());
				Intent intent = new Intent(mContext, PermissionModifyActivity.class);
				intent.putExtra(Constants.REPOSITORY, repository);
				intent.putExtra(Constants.USER, user);

				if (permission != null) {
					intent.putExtra(Constants.PERMISSION, permission);
				}
				startActivityForResult(intent, 0);
			} else {
				GUI.displayMonit(mContext,
						"Owner and Admins have full access to all repositories");
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Constants.REFRESH_ACTIVITY) {

			new DownloadUsersListTask().execute();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class DownloadUsersListTask extends AsyncTask<Void, Void, ArrayList<User>> {

		@SuppressWarnings("rawtypes")
		private AsyncTask thisTask = this;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(mContext, "Loading user list",
					"Please wait...");

			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					thisTask.cancel(true);
					GUI.displayMonit(mContext, "Download task was cancelled");
				}
			});
		}

		@Override
		protected ArrayList<User> doInBackground(Void... params) {

			try {
				String xmlUserList = HttpRetriever.getUserListXML(prefs);
				// parsing users list
				return XmlParser.parseUserList(xmlUserList);
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
			usersArray.clear();
			usersArray.addAll(parsedArray);

			usersAdapter.notifyDataSetChanged();
			progressDialog.dismiss();

		}

	}

}
