package com.applicake.beanstalkclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Release;

public class ReleaseDetailsActivity extends BeanstalkActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.release_details_layout);

    Intent intent = getIntent();

    String repositoryTitle = intent.getStringExtra(Constants.REPOSITORY_TITLE);
    int labelColor = intent.getIntExtra(Constants.REPOSITORY_COLOR_NO, 0);

    findViewById(R.id.colorLabel).getBackground().setLevel(labelColor);
    ((TextView) findViewById(R.id.repoName)).setText(repositoryTitle);

    Release release = intent.getParcelableExtra(Constants.RELEASE);
    ((TextView) findViewById(R.id.created_at)).setText(release.getCreatedAt()
        .toLocaleString());
    ((TextView) findViewById(R.id.author)).setText(release.getAuthor());
    ((TextView) findViewById(R.id.deployed_at)).setText(release.getDeployedAt()
        .toLocaleString());
    ((TextView) findViewById(R.id.comment)).setText(release.getComment());
    ((TextView) findViewById(R.id.revision)).setText(release.getRevision());
    ((TextView) findViewById(R.id.server)).setText(release.getEnvironmentName());
  }
}
