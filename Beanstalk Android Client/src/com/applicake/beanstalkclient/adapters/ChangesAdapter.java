package com.applicake.beanstalkclient.adapters;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.YamlEntry;

public class ChangesAdapter extends ArrayAdapter<YamlEntry> {

	private Context context;
	private List<YamlEntry> changesArray;

	public ChangesAdapter(Context context, int textViewResourceId,
			List<YamlEntry> changesArray) {
		super(context, textViewResourceId);
		this.context = context;
		this.changesArray = changesArray;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.changeset_list_entry, null);
		}

		YamlEntry changeEntry = changesArray.get(position);

		if (changeEntry != null) {
			TextView valueTextView = (TextView) view.findViewById(R.id.value);
			valueTextView.setText(changeEntry.getValue());

			TextView changesetTag = (TextView) view.findViewById(R.id.state);
			String property = changeEntry.getProperty();
			Log.w("Change property", property);
			if (property.equals("add")) {
				changesetTag.getBackground().setLevel(0);
				changesetTag.setText("added");
			} else if (property.equals("edit")) {
				changesetTag.getBackground().setLevel(1);
				changesetTag.setText("modified");

			} else if (property.equals("delete")) {
				changesetTag.getBackground().setLevel(2);
				changesetTag.setText("removed");
			}

		}

		return view;
	}

}
