package com.applicake.beanstalkclient.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.applicake.beanstalkclient.Permission;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.utils.GravatarDowloader;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

public class RepositoryPermissionsAdapter extends ArrayAdapter<User> {

  private Repository repository;
  private List<User> userArray;
  private Map<Integer, Permission> userIdToPermissionMap = new HashMap<Integer, Permission>();
  private SharedPreferences prefs;
  private LayoutInflater mInflater;

  public RepositoryPermissionsAdapter(Context context, SharedPreferences prefs,
      Repository repository, int textViewResourceId, List<User> userArray) {
    super(context, textViewResourceId, userArray);
    this.repository = repository;
    mInflater = LayoutInflater.from(context);
    this.prefs = prefs;
    this.userArray = userArray;
  }

  public void setRepoIdToPermissionMap(Map<Integer, Permission> repoIdToPermissionMap) {
    this.userIdToPermissionMap = repoIdToPermissionMap;
  }

  public View getView(int position, View convertView, ViewGroup parent) {

    View view = mInflater.inflate(R.layout.repository_permissions_entry, parent, false);

    User user = userArray.get(position);

    if (user != null) {

      // setting view clickable
      view.setTag(true);

      String email = user.getEmail();
      ImageView userGravatar = (ImageView) view.findViewById(R.id.userGravatar);
      GravatarDowloader.getInstance().download(email, userGravatar);

      TextView userNameTextView = (TextView) view.findViewById(R.id.userName);
      userNameTextView.setText(user.getFirstName() + " " + user.getLastName());

      TextView userEmailTextView = (TextView) view.findViewById(R.id.userEmail);
      userEmailTextView.setText(email);

      if (user.getUserType() == UserType.USER) {
        // making view unclickable while the data is being downloaded
        view.setTag(false);

        new DownloadPermissionsTask().execute(String.valueOf(user.getId()), view);
      } else if (user.getUserType() == UserType.ADMIN) {

        ((ProgressBar) view.findViewById(R.id.loadingBar)).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.adminLabel)).setVisibility(View.VISIBLE);

      } else if (user.getUserType() == UserType.OWNER) {

        ((ProgressBar) view.findViewById(R.id.loadingBar)).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.ownerLabel)).setVisibility(View.VISIBLE);
      }

    }

    return view;
  }

  public Map<Integer, Permission> getUserIdToPermissionMap() {
    return userIdToPermissionMap;
  }

  public class DownloadPermissionsTask extends
      AsyncTask<Object, Void, HashMap<Integer, Permission>> {

    private ProgressBar loadingBar;
    private TextView repoPermissionLabel;
    private TextView deploymentPermissionLabel;
    private TextView repoPermissionTitle;
    private TextView deploymentPermissionTitle;
    private String userId;
    private View view;
    private boolean failed;
    private Button refreshButton;

    @Override
    protected HashMap<Integer, Permission> doInBackground(Object... params) {
      userId = (String) params[0];
      view = (View) params[1];

      loadingBar = (ProgressBar) view.findViewById(R.id.loadingBar);
      loadingBar.setVisibility(View.VISIBLE);

      repoPermissionLabel = (TextView) view.findViewById(R.id.repositoryLabel);
      deploymentPermissionLabel = (TextView) view.findViewById(R.id.deploymentLabel);

      refreshButton = (Button) view.findViewById(R.id.refresh_button);

      repoPermissionTitle = (TextView) view.findViewById(R.id.repoPermission);
      deploymentPermissionTitle = (TextView) view.findViewById(R.id.deploymentPermission);

      Log.w("user ID", userId);

      try {
        String permissionsXml = HttpRetriever.getPermissionListForUserXML(prefs, userId);
        Log.w("user permission xml", permissionsXml);

        return XmlParser.parseRepoIdToPermissionHashMap(permissionsXml);

        // all those exceptions are handled the same way
      } catch (HttpConnectionErrorException e) {

      } catch (UnsuccessfulServerResponseException e) {

      } catch (XMLParserException e) {

      }
      failed = true;
      return null;

    }

    @Override
    protected void onPostExecute(HashMap<Integer, Permission> result) {
      // setting view clickable again

      loadingBar.setVisibility(View.GONE);

      if (failed) {
        // if one of downloads fail a refresh button will be displayed
        refreshButton.setOnClickListener(new OnClickListener() {

          @Override
          public void onClick(View v) {
            new DownloadPermissionsTask().execute(userId, view);
            refreshButton.setVisibility(View.GONE);

          }
        });
        refreshButton.setVisibility(View.VISIBLE);

      } else {

        if (result != null) {
          view.setTag(true);

          repoPermissionLabel.setVisibility(View.VISIBLE);
          deploymentPermissionLabel.setVisibility(View.VISIBLE);
          repoPermissionTitle.setVisibility(View.VISIBLE);
          deploymentPermissionTitle.setVisibility(View.VISIBLE);

          if (result.containsKey(repository.getId())) {
            Permission permission = result.get(repository.getId());
            // add permission to userid -> permission hashmap
            userIdToPermissionMap.put(Integer.valueOf(userId), permission);

            if (permission.isReadAccess()) {
              if (permission.isWriteAccess()) {
                repoPermissionLabel.setText("write");
                repoPermissionLabel.getBackground().setLevel(0);
              } else {
                repoPermissionLabel.setText("read");
                repoPermissionLabel.getBackground().setLevel(1);
              }
            }

            if (permission.isFullDeploymentAccess()) {
              deploymentPermissionLabel.setText("write");
              deploymentPermissionLabel.getBackground().setLevel(0);
            } else {
              deploymentPermissionLabel.setText("read");
              deploymentPermissionLabel.getBackground().setLevel(2);
            }
          } else {
            repoPermissionLabel.setText("no access");
            repoPermissionLabel.getBackground().setLevel(2);

            deploymentPermissionLabel.setText("read");
            deploymentPermissionLabel.getBackground().setLevel(2);
          }
        } else {
          view.findViewById(R.id.parsingFailed).setVisibility(View.VISIBLE);
        }
      }

    }

  }

}
