package com.applicake.beanstalkclient.adapters;

import java.util.ArrayList;
import java.util.List;

import com.applicake.beanstalkclient.utils.RailsTimezones;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinnerTimezoneAdapter extends ArrayAdapter<String> {

	private LayoutInflater mInflater;
	private List<String> displayedValues;

	public SpinnerTimezoneAdapter(Context context, int textViewResourceId,
			List<String> popupValuesList, List<String> spinnerValues) {
		super(context, textViewResourceId, popupValuesList);
		mInflater = LayoutInflater.from(context);
		displayedValues = spinnerValues;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) convertView;
		
		if (convertView == null){
			view = (TextView) mInflater.inflate(android.R.layout.simple_spinner_item, parent, false);
			
		}
		
		view.setText(displayedValues.get(position));
		return view;
		
	}

}
