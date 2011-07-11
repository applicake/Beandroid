package com.applicake.beanstalkclient.activities;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.Permission;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.adapters.RepositoryPermissionsAdapter;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

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
    View headerView = ((LayoutInflater) getApplicationContext().getSystemService(
        Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.repo_name_header, null, false);

    // setup list header
    headerView.findViewById(R.id.colorLabel).getBackground()
        .setLevel(repository.getColorLabelNo());
    TextView repoNameTextView = (TextView) headerView.findViewById(R.id.repoName);
    repoNameTextView.setText(repository.getTitle());

    usersList.addHeaderView(headerView, null, false);

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
        Permission permission = usersAdapter.getUserIdToPermissionMap().get(user.getId());
        Intent intent = new Intent(mContext, PermissionModifyActivity.class);
        intent.putExtra(Constants.REPOSITORY, repository);
        intent.putExtra(Constants.USER, user);

        if (permission != null) {
          intent.putExtra(Constants.PERMISSION, permission);
        }
        startActivityForResult(intent, 0);
      } else if (user.getAdmin() != UserType.USER) {
        GUI.displayMonit(mContext,
            "Owner and Admins have full access to all repositories");
      } else {
        GUI.displayMonit(mContext, "Loading...");
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
    private String errorMessage;
    private String failMessage;
    private boolean failed = false;

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
          finish();
        }
      });
    }

    @Override
    protected ArrayList<User> doInBackground(Void... params) {

      try {
        String xmlUserList = HttpRetriever.getUserListXML(prefs);
        // parsing users list
        return XmlParser.parseUserList(xmlUserList);

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
        if (parsedArray != null) {
          usersArray.clear();
          usersArray.addAll(parsedArray);

          usersAdapter.notifyDataSetChanged();
        } else if (errorMessage != null) {
          GUI.displayMonit(mContext, errorMessage);
        } else
          GUI.displayMonit(mContext, "Unexpected error, please try again later");

      }
    }

  }

}
