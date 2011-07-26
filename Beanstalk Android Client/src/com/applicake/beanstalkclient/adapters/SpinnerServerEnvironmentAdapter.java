package com.applicake.beanstalkclient.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.applicake.beanstalkclient.ServerEnvironment;

public class SpinnerServerEnvironmentAdapter extends ArrayAdapter<ServerEnvironment>
    implements SpinnerAdapter {

  private LayoutInflater mInflater;
  private int textViewResourceId;
  private List<ServerEnvironment> environmentsArray;

  public SpinnerServerEnvironmentAdapter(Context context, int textViewResourceId,
      List<ServerEnvironment> environmentsArray) {
    super(context, textViewResourceId, environmentsArray);
    mInflater = LayoutInflater.from(context);
    this.textViewResourceId = textViewResourceId;
    this.environmentsArray = environmentsArray;

  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    TextView view;
    if (convertView == null)
      view = (TextView) mInflater.inflate(textViewResourceId, null);
    else
      view = (TextView) convertView;
    view.setText(environmentsArray.get(position).getName());
    return view;
  }

  @Override
  public int getCount() {
    return environmentsArray.size();
  }

  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    TextView view;
    if (convertView == null)
      view = (TextView) mInflater.inflate(textViewResourceId, null);
    else
      view = (TextView) convertView;
    view.setText(environmentsArray.get(position).getName());
    return view;
  }

}
