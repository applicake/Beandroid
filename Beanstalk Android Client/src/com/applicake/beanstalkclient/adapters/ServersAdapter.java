package com.applicake.beanstalkclient.adapters;

import java.util.List;

import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.ServerEnvironment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

public class ServersAdapter extends BaseExpandableListAdapter {

  private List<ServerEnvironment> mServersArray;
  private LayoutInflater mInflater;
  
  
  public ServersAdapter(Context context, int i, List<ServerEnvironment> serversArray) {
    this.mServersArray = serversArray;
    mInflater = LayoutInflater.from(context); 
  }



//  @Override
  public long getItemId(int position) {
    return position;
  }


  @Override
  public int getGroupCount() {
    return mServersArray.size();
  }

  @Override
  public int getChildrenCount(int groupPosition) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Object getGroup(int groupPosition) {
    return mServersArray.get(groupPosition);
  }

  @Override
  public Object getChild(int groupPosition, int childPosition) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long getGroupId(int groupPosition) {
    // TODO Auto-generated method stub
    return 0;
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
  public void downloadChildList(int groupId){
    
    
  }

  @Override
  public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
      ViewGroup parent) {
    View view;
    if (convertView == null)
      view = mInflater.inflate(R.layout.environments_list_entry, null);
    else 
      view = convertView;
    
    // TODO implement servers adapter
    
    
    return view;
  }

  @Override
  public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
      View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    // TODO Auto-generated method stub
    return false;
  }
  
  

}
