package com.applicake.beanstalkclient;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpRetreiverException;

public class RepositoryDetailsActivity extends BeanstalkActivity implements
		OnClickListener {

	private Context mContext;

	private Repository repository;
	private View colorLabel;
	private TextView repoName;
	private String dateFormat = new String("yyyy-MM-dd");
	private TextView repoType;
	private TextView repoTitle;
	private TextView repoCreatedAt;
	private TextView repoLastCommit;
	private TextView repoRevision;
	private TextView repoStorageUsed;
	private TextView repoUpdatedAt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.repository_details_layout);
		repository = getIntent().getParcelableExtra(Constants.REPOSITORY);

		mContext = this;

		colorLabel = findViewById(R.id.colorLabel);
		repoName = (TextView) findViewById(R.id.repoName);

		repoType = (TextView) findViewById(R.id.repoType);
		repoTitle = (TextView) findViewById(R.id.repoTitle);
		repoCreatedAt = (TextView) findViewById(R.id.repoCreatedAt);
		repoLastCommit = (TextView) findViewById(R.id.repoLastCommit);
		repoRevision = (TextView) findViewById(R.id.repoRevision);
		repoStorageUsed = (TextView) findViewById(R.id.repoStorageUsed);
		repoUpdatedAt = (TextView) findViewById(R.id.repoUpdatedAt);

		loadRepositoryData();

		// add button listeners
		Button viewCommitsButton = (Button) findViewById(R.id.buttonViewCommits);
		Button usersPermissionsButton = (Button) findViewById(R.id.buttonUsersPermissions);
		Button deploymentButton = (Button) findViewById(R.id.buttonDeployment);
		Button modifyPropertiesButton = (Button) findViewById(R.id.buttonModifyProperties);

		viewCommitsButton.setOnClickListener(this);
		usersPermissionsButton.setOnClickListener(this);
		deploymentButton.setOnClickListener(this);
		modifyPropertiesButton.setOnClickListener(this);

	}

	public void loadRepositoryData() {

		colorLabel.getBackground().setLevel(repository.getColorLabelNo());
		repoName.setText(repository.getName());
		repoType.setText(repository.getType().equals("SubversionRepository") ? "SVN"
				: "Git");
		repoTitle.setText(repository.getTitle());
		repoCreatedAt.setText("created at: "
				+ DateFormat.format(dateFormat, repository.getCreatedAt()));
		long lastCommit = repository.getLastCommitAt();
		repoLastCommit
				.setText(lastCommit == 0 ? "last commit: no commits in this repository"
						: "last commit: "
								+ DateUtils.getRelativeTimeSpanString(lastCommit));
		repoRevision.setText(String.valueOf("revision: " + repository.getRevision()));
		repoStorageUsed.setText("storage used: "
				+ String.valueOf(repository.getStorageUsedBytes()));
		repoUpdatedAt.setText("updated at: "
				+ DateFormat.format(dateFormat, repository.getUpdatedAt()));

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Constants.REFRESH_ACTIVITY) {
			setResult(resultCode);
			new DownloadRepositoryInfo().execute();

		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.buttonViewCommits) {
			Intent intent = new Intent(getApplicationContext(),
					RepositoryCommitsActivity.class);
			intent.putExtra(Constants.REPOSITORY_ID, String.valueOf(repository.getId()));
			intent.putExtra(Constants.REPOSITORY_TITLE, repository.getTitle());
			intent.putExtra(Constants.REPOSITORY_COLOR_NO, repository.getColorLabelNo());
			startActivityForResult(intent, 0);

		}

		if (v.getId() == R.id.buttonUsersPermissions) {
			Intent intent = new Intent(getApplicationContext(),
					RepositoryUsersPermissionsActivity.class);
			intent.putExtra(Constants.REPOSITORY, repository);
			startActivityForResult(intent, 0);
		}

		if (v.getId() == R.id.buttonDeployment) {
			GUI.displayMonit(getApplicationContext(), "to be implemented");
		}

		if (v.getId() == R.id.buttonModifyProperties) {
			Intent intent = new Intent(getApplicationContext(),
					RepositoryModifyPropertiesActivity.class);
			intent.putExtra(Constants.REPOSITORY_ID, String.valueOf(repository.getId()));
			intent.putExtra(Constants.REPOSITORY_TITLE, repository.getTitle());
			intent.putExtra(Constants.REPOSITORY_COLOR_NO, repository.getColorLabelNo());
			startActivityForResult(intent, 0);
		}

	}

	public class DownloadRepositoryInfo extends AsyncTask<Void, Void, Boolean> {

		Repository refreshedRepository;

		@SuppressWarnings("rawtypes")
		private AsyncTask thisTask = this;
		ProgressDialog progressDialog;
		
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(mContext, "Please wait",
					"Repository data is being refreshed");

			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					thisTask.cancel(true);
					GUI.displayMonit(mContext, "Download task was cancelled");
				}
			});
		};

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String repositoryXml = new HttpRetriever().getRepositoryXML(prefs,
						repository.getId());
				repository = XmlParser.parseRepository(repositoryXml);
			} catch (HttpRetreiverException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
				cancel(true);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
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
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (result) {
				loadRepositoryData();

			}
		}

	}

}
