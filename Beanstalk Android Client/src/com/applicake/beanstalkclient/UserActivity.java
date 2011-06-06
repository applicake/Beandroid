package com.applicake.beanstalkclient;

import java.util.ArrayList;

import com.applicake.beanstalkclient.adapters.UserAdapter;
import com.applicake.beanstalkclient.enums.Plans;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
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
		if (resultCode == Constants.REFRESH_ACTIVITY) {
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
		@SuppressWarnings("rawtypes")
		private AsyncTask thisTask = this;
		private String errorMessage;
		private String failMessage;
		private boolean failed;

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

			} catch (UnsuccessfulServerResponseException e) {
				errorMessage = e.getMessage();
				return null;
			} catch (HttpConnectionErrorException e) {
				failMessage = Strings.networkConnectionErrorMessage;
			} catch (XMLParserException e) {
				failMessage = Strings.internalErrorMessage;
			}
			failed = true;
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<User> parsedArray) {
			progressDialog.dismiss();
			if (failed) {
				SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
						failMessage) {

					@Override
					public void retryAction() {
						new DownloadUsersListTask().execute();
					}

					@Override
					public void noRetryAction(DialogInterface dialog) {
						super.noRetryAction(dialog);
						finish();
					}

				};

				builder.displayDialog();
			} else {
				if (parsedArray != null){

					userArray.clear();
					userArray.addAll(parsedArray);

					userAdapter.notifyDataSetChanged();

					int usersInPlan = Plans.getPlanById(
							prefs.getInt(Constants.ACCOUNT_PLAN, 0)).getNumberOfUsers();
					int numberLeft = usersInPlan - userArray.size();
					userLeftCounter.setText("available users: " + String.valueOf(numberLeft)
							+ "/" + String.valueOf(usersInPlan));
				} else if (errorMessage != null){
					GUI.displayMonit(mContext, "Server error: "+ errorMessage);
				} else GUI.displayMonit(mContext, "Unexpected error, please try again later");


			}
		}

	}

}
