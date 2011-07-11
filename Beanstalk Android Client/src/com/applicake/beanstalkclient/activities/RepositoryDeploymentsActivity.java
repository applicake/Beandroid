package com.applicake.beanstalkclient.activities;

import java.util.ArrayList;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Release;
import com.applicake.beanstalkclient.Server;
import com.applicake.beanstalkclient.ServersAdapter;
import com.applicake.beanstalkclient.adapters.ReleasesAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class RepositoryDeploymentsActivity extends BeanstalkActivity implements
    OnClickListener {

  boolean serverListLoaded = false;
  boolean releaseListLoaded = false;
  
  private ListView mReleasesList;
  private ListView mServersList;
  private ReleasesAdapter mReleasesAdapter;
  private ServersAdapter mServersAdapter;
  
  private ArrayList<Release> mReleaseArray = new ArrayList<Release>();
  private ArrayList<Server> mServersArray = new ArrayList<Server>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.repository_deployments_layout);

    Intent intent = getIntent();

    // set repository title and label color based on intent
    ((TextView) findViewById(R.id.repoTitle)).setText(intent
        .getStringExtra(Constants.REPOSITORY_TITLE));
    findViewById(R.id.colorLabel).getBackground().setLevel(
        intent.getIntExtra(Constants.REPOSITORY_COLOR_NO, 0));

    // handle tab switching
    mReleasesList = (ListView) findViewById(R.id.releases_list);
    mServersList = (ListView) findViewById(R.id.servers_list);
    findViewById(R.id.releases_button).setOnClickListener(this);
    findViewById(R.id.servers_button).setOnClickListener(this);

    // releases list
    mReleasesAdapter = new ReleasesAdapter(this, 0, mReleaseArray);
    mReleasesList.setAdapter(mReleasesAdapter);
    
    // servers list
    mServersAdapter = new ServersAdapter(this, 0, mServersArray );
    mServersList.setAdapter(mServersAdapter);
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    switch (id) {
    case R.id.releases_button:
      
    case R.id.servers_button:
      // switch tabs
      mReleasesList.setVisibility(id == R.id.releases_button ? View.VISIBLE : View.GONE);
      mServersList.setVisibility(id == R.id.servers_button ? View.VISIBLE : View.GONE);
      break;
    
    }
  }
  
  

}
