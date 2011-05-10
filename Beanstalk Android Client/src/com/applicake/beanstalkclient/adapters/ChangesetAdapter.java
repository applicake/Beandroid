package com.applicake.beanstalkclient.adapters;

import java.util.List;

import com.applicake.beanstalkclient.Changeset;
import com.applicake.beanstalkclient.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChangesetAdapter extends ArrayAdapter<Changeset> {
	
	private Context context;
	private List<Changeset> changesetArray;

	public ChangesetAdapter(Context context, int textViewResourceId,
			List<Changeset> changesetArray) {
		super(context, textViewResourceId, changesetArray);
		this.context = context;
		this.changesetArray = changesetArray;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if (view == null){
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.dashboard_entry, null);
		}
		
		Changeset changeset = changesetArray.get(position);
		
		if (changeset != null){
			TextView repositoryNameTextView = (TextView) view.findViewById(R.id.reposiotryName);
//			repositoryNameTextView.setText(changeset.getRepositoryId());
			repositoryNameTextView.setText("123");
			
			TextView dateTextView = (TextView) view.findViewById(R.id.date);
			dateTextView.setText(changeset.getTime().toGMTString());
			
			TextView hashTextView = (TextView) view.findViewById(R.id.hash);
			hashTextView.setText(changeset.getHashId());
			
			//temporary		
			TextView timeTextView = (TextView) view.findViewById(R.id.time);
			timeTextView.setText("xxx");
	
			TextView messageTextView = (TextView) view.findViewById(R.id.commitMessage);
			messageTextView.setText(changeset.getMessage());
						
		}

		return view;
	}

}
