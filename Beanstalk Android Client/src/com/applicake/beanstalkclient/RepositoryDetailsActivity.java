package com.applicake.beanstalkclient;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.applicake.beanstalkclient.R;

public class RepositoryDetailsActivity extends BeanstalkActivity implements OnClickListener{

	private Repository repository;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.repository_details_layout);
		repository = getIntent().getParcelableExtra(Constants.REPOSITORY);
		String dateFormat = new String("yyyy-MM-dd");

		findViewById(R.id.colorLabel).getBackground().setLevel(
				repository.getColorLabelNo());
		((TextView) findViewById(R.id.repoName)).setText(repository.getName());
		((TextView) findViewById(R.id.repoType)).setText(repository.getType().equals(
				"SubversionRepository") ? "SVN" : "Git");
		((TextView) findViewById(R.id.repoTitle)).setText(repository.getTitle());
		((TextView) findViewById(R.id.repoCreatedAt)).setText("created at: "
				+ DateFormat.format(dateFormat, repository.getCreatedAt()));
		long lastCommit = repository.getLastCommitAt();
		((TextView) findViewById(R.id.repoLastCommit))
				.setText(lastCommit == 0 ? "last commit: no commits in this repository"
						: "last commit: "
								+ DateUtils.getRelativeTimeSpanString(lastCommit));
		((TextView) findViewById(R.id.repoRevision)).setText(String.valueOf("revision: " + repository.getRevision()));
		((TextView) findViewById(R.id.repoStorageUsed)).setText("storage used: "+ String.valueOf(repository.getStorageUsedBytes()));
		((TextView) findViewById(R.id.repoUpdatedAt)).setText("updated at: "+ DateFormat.format(dateFormat, repository.getUpdatedAt()));
		
		// add button listeners 
		Button viewCommitsButton = (Button) findViewById(R.id.buttonViewCommits);
		Button usersPermissionsButton = (Button) findViewById(R.id.buttonUsersPermissions);
		Button deploymentButton = (Button) findViewById(R.id.buttonDeployment);
		Button modifyPropertiesButton = (Button) findViewById(R.id.buttonModifyProperties);
		Button deleteRepositoryButton = (Button) findViewById(R.id.buttonDeleteRepository);
		
		viewCommitsButton.setOnClickListener(this);
		usersPermissionsButton.setOnClickListener(this);
		deploymentButton.setOnClickListener(this);
		modifyPropertiesButton.setOnClickListener(this);
		deleteRepositoryButton.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		
		if (v.getId() == R.id.buttonViewCommits){
			Intent intent = new Intent(getApplicationContext(), RepositoryCommitsActivity.class);
			intent.putExtra(Constants.REPOSITORY_ID, String.valueOf(repository.getId()));
			intent.putExtra(Constants.REPOSITORY_TITLE, repository.getTitle());
			intent.putExtra(Constants.REPOSITORY_COLOR_NO, repository.getColorLabelNo());
			startActivityForResult(intent, 0);
			
		}
		
		if (v.getId() == R.id.buttonUsersPermissions){
			Intent intent = new Intent(getApplicationContext(), RepositoryUserPermissionsActivity.class);
			startActivityForResult(intent, 0);
		}
		
		if (v.getId() == R.id.buttonDeployment){
			GUI.displayMonit(getApplicationContext(), "to be implemented");
		}
		
		if (v.getId() == R.id.buttonModifyProperties){
			Intent intent = new Intent(getApplicationContext(), RepositoryModifyPropertiesActivity.class);
			startActivityForResult(intent, 0);
		}
		
		if (v.getId() == R.id.buttonDeleteRepository){
			GUI.displayMonit(getApplicationContext(), "r u sure?");
		}
		
	}

}
