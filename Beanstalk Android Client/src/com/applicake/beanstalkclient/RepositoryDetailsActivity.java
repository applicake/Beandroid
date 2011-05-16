package com.applicake.beanstalkclient;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.widget.TextView;

import com.applicake.beanstalkclient.R;

public class RepositoryDetailsActivity extends BeanstalkActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.repository_details_layout);
		Repository repository = getIntent().getParcelableExtra(Constants.REPOSITORY);
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
		

	}

}
