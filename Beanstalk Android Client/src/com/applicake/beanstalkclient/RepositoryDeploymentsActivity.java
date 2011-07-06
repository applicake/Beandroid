package com.applicake.beanstalkclient;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class RepositoryDeploymentsActivity extends BeanstalkActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.repository_deployments_layout);

    Intent intent = getIntent();

    ((TextView) findViewById(R.id.repoTitle)).setText(intent
        .getStringExtra(Constants.REPOSITORY_TITLE));
    findViewById(R.id.colorLabel).getBackground().setLevel(
        intent.getIntExtra(Constants.REPOSITORY_COLOR_NO, 0));
  }

}
