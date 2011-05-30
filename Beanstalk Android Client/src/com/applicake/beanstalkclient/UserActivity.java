package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.applicake.beanstalkclient.adapters.UserAdapter;
import com.applicake.beanstalkclient.enums.Plans;
import com.applicake.beanstalkclient.utils.GUI;
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
import android.widget.TextView;

public class UserActivity extends BeanstalkActivity implements OnItemClickListener,
		OnClickListener {

	private UserAdapter userAdapter;
	private ArrayList<User> userArray;
	private ProgressDialog progressDialog;
	private Context mContext;
	private TextView userLeftCounter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list_layout);

		mContext = this;
		userArray = new ArrayList<User>();
		userAdapter = new UserAdapter(this, R.layout.user_list_entry, userArray);

		ListView userList = (ListView) findViewById(R.id.usersList);

		View footerView = ((LayoutInflater) getApplicationContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.add_user_footer, null,
				false);
		footerView.setOnClickListener(this);
		
		userList.addFooterView(footerView);
		userList.setAdapter(userAdapter);
		userList.setOnItemClickListener(this);

		userLeftCounter = (TextView) footerView.findViewById(R.id.userCounter);
		new DownloadUsersListTask().execute();

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Constants.REFRESH_ACTIVITY){
			new DownloadUsersListTask().execute();
		}
	}

	@Override
	public void onClick(View v) {
		startActivityForResult(new Intent(mContext, UserCreateNewActivity.class), 0);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int itemNumber, long arg3) {
		Intent intent = new Intent(mContext, UserDetailsActivity.class);
		User user = userArray.get(itemNumber);
		intent.putExtra(Constants.USER, user);
		startActivityForResult(intent, 0);

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
			progressDialog.cancel();
			userArray = parsedArray;

			if (userArray != null && !userArray.isEmpty()) {
				userAdapter.notifyDataSetChanged();
				userAdapter.clear();

				for (int i = 0; i < userArray.size(); i++) {
					userAdapter.add(userArray.get(i));
				}
			}
			int usersInPlan = Plans.getPlanById(prefs.getInt(Constants.ACCOUNT_PLAN, 0)).getNumberOfUsers();
			int numberLeft = usersInPlan - userArray.size();
			userLeftCounter.setText("available users: "+ String.valueOf(numberLeft)+"/"+String.valueOf(usersInPlan));

//			userAdapter.notifyDataSetChanged();


		}

	}

}
