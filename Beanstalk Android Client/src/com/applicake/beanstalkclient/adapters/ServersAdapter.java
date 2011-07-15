package com.applicake.beanstalkclient.adapters;

import java.util.List;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Server;
import com.applicake.beanstalkclient.ServerEnvironment;
import com.applicake.beanstalkclient.activities.CreateNewServerActivity;
import com.applicake.beanstalkclient.activities.ModifyServerEnvironmentProperties;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ServersAdapter extends BaseExpandableListAdapter {

  private List<ServerEnvironment> mServersArray;
  private LayoutInflater mInflater;
  private SharedPreferences prefs;
  private Context mContext;

  public ServersAdapter(Context context, int i, List<ServerEnvironment> serversArray) {
    this.mServersArray = serversArray;
    mContext = context;
    mInflater = LayoutInflater.from(context);
    prefs = PreferenceManager.getDefaultSharedPreferences(context);
  }

  @Override
  public int getGroupCount() {
    return mServersArray.size();
  }

  // this method checks if a list of servers for a certain server environment
  // has been downloaded or is being dowlnoaded. The list will always display
  // one additional field which is used as a "add new server" button

  @Override
  public int getChildrenCount(int groupPosition) {
    Log.d("xxx", "getting ChildrenCount for " + String.valueOf(groupPosition));

    ServerEnvironment serverEnvironment = mServersArray.get(groupPosition);

    // the list is loaded
    if (serverEnvironment.isDownloaded()) {
      if (mServersArray != null && serverEnvironment.getServerList() != null) {
        return serverEnvironment.getServerList().size() + 1;
      } else
        return 1;

      // the list is being dowlnoaded
      // displays 2 additional cells - loading cell and add new server cell
    } else if (serverEnvironment.isDownloading()) {

      return 2;

      // the list has not been downloaded - start download
      // displays 2 additional cells - loading cell and add new server cell
    } else {
      new DownloadServerListForEnvironmentTask(serverEnvironment).execute();
      return 2;
    }

  }

  @Override
  public Object getGroup(int groupPosition) {
    return mServersArray.get(groupPosition);
  }

  @Override
  public Object getChild(int groupPosition, int childPosition) {
    return mServersArray.get(groupPosition).getServerList().get(childPosition);
  }

  @Override
  public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  @Override
  public long getChildId(int groupPosition, int childPosition) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean hasStableIds() {
    // TODO Auto-generated method stub
    return false;
  }

  // downloads server list for particular environment
  public void downloadChildList(int groupId) {

  }

  @Override
  public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
      ViewGroup parent) {
    View view;
    if (convertView == null)
      view = mInflater.inflate(R.layout.environments_list_entry, null);
    else
      view = convertView;

    final ServerEnvironment serverEnvironment = mServersArray.get(groupPosition);

    if (serverEnvironment != null) {
      TextView environmentName = (TextView) view.findViewById(R.id.environment_name);
      TextView branchName = (TextView) view.findViewById(R.id.branch_name);
      TextView automatic = (TextView) view.findViewById(R.id.automatic);
      TextView editServerEnvironmentButton = (TextView) view.findViewById(R.id.edit_server_environment_button);
      editServerEnvironmentButton.setOnClickListener(new OnClickListener() {
        
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(mContext, ModifyServerEnvironmentProperties.class);
          intent.putExtra(Constants.SERVER_ENVIRONMENT, serverEnvironment);
          mContext.startActivity(intent);
        }
      });

      environmentName.setText(serverEnvironment.getName());
      branchName.setText(serverEnvironment.getBranchName());
      // TODO change to automatic/manual
      automatic.setText(String.valueOf(serverEnvironment.isAutomatic()));
    }

    return view;
  }

  @Override
  public View getChildView(final int groupPosition, int childPosition,
      boolean isLastChild, View convertView, ViewGroup parent) {

    View view;

    if (isLastChild) {
      view = mInflater.inflate(R.layout.environments_add_server_footer, null);
      view.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
          GUI.displayMonit(mContext,
              "create new server for " + mServersArray.get(groupPosition).getName());
          // tests
          Intent intent = new Intent(mContext, CreateNewServerActivity.class);
          intent.putExtra(Constants.SERVER_ENVIRONMENT, mServersArray.get(groupPosition));
          
          mContext.startActivity(new Intent(mContext, CreateNewServerActivity.class));

        }
      });
    } else if (mServersArray.get(groupPosition).isDownloading()) {
      view = mInflater.inflate(R.layout.environments_servers_loading_field, null);
    }

    else {
      view = mInflater.inflate(R.layout.environments_server_list_entry, null);
      // temporary
      Server server = mServersArray.get(groupPosition).getServerList().get(childPosition);

      if (server != null) {
        TextView environmentName = (TextView) view.findViewById(R.id.environment_name);
        TextView branchName = (TextView) view.findViewById(R.id.branch_name);
        TextView automatic = (TextView) view.findViewById(R.id.automatic);

        environmentName.setText(server.getName());
        branchName.setText(String.valueOf(server.getRevision()));
        // TODO change to automatic/manual
        automatic.setText(server.getEnvitonmentName());
        // TODO Auto-generated method stub

      }
    }

    return view;

  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    // TODO Auto-generated method stub

    return false;
  }

  public class DownloadServerListForEnvironmentTask extends
      AsyncTask<Void, Void, List<Server>> {

    private ServerEnvironment mServerEnvironment;

    public DownloadServerListForEnvironmentTask(ServerEnvironment serverEnvironment) {
      mServerEnvironment = serverEnvironment;
    }

    @Override
    protected void onPreExecute() {
      mServerEnvironment.setDownloading(true);
      super.onPreExecute();
    }

    @Override
    protected List<Server> doInBackground(Void... params) {
      try {
        String serverListXml = HttpRetriever.getServerListForEnviromentXML(prefs,
            mServerEnvironment.getRepositoryId(), mServerEnvironment.getId());
        Log.d("xxx", serverListXml);
        return XmlParser.parseServerList(serverListXml);

      } catch (HttpConnectionErrorException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (UnsuccessfulServerResponseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (XMLParserException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      return null;

    }

    @Override
    protected void onPostExecute(List<Server> result) {
      Log.d("xxx", "server list size: " + (result != null ? result.size() : 0));

      // TODO add retry feature
      mServerEnvironment.setServerList(result);
      mServerEnvironment.setDownloaded(true);
      mServerEnvironment.setDownloading(false);

      notifyDataSetChanged();

    }

  }

}
