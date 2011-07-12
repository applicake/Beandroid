package com.applicake.beanstalkclient.adapters;

import java.util.List;

import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Server;
import com.applicake.beanstalkclient.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ServersAdapter extends BaseAdapter {

  private List<Server> mServersArray;
  private LayoutInflater mInflater;
  
  
  public ServersAdapter(Context context, int i, List<Server> mServersArray) {
    this.mServersArray = mServersArray;
    mInflater = LayoutInflater.from(context); 
  }

  @Override
  public int getCount() {
    return mServersArray.size();
  }

  @Override
  public Object getItem(int position) {
    return mServersArray.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view;
    if (convertView == null)
      view = mInflater.inflate(R.layout.servers_list_entry, null);
    else 
      view = convertView;
    
    // TODO implement servers adapter
    
    
    return null;
  }

}
