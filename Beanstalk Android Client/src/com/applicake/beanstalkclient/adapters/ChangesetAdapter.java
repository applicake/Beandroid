package com.applicake.beanstalkclient.adapters;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import com.applicake.beanstalkclient.Changeset;
import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Repository;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChangesetAdapter extends ArrayAdapter<Changeset> {

	private Context context;
	private List<Changeset> changesetArray;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm");
	private HashMap<Integer, Repository> repoMap;

	public void setRepoHashMap(HashMap<Integer, Repository> repoList) {
		this.repoMap = repoList;
	}

	public ChangesetAdapter(Context context, int textViewResourceId,
			List<Changeset> changesetArray) {
		super(context, textViewResourceId, changesetArray);
		this.context = context;
		this.changesetArray = changesetArray;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.dashboard_entry, null);
		}

		Changeset changeset = changesetArray.get(position);
		Repository repository = repoMap.get(changeset.getRepositoryId());

		if (changeset != null) {
			TextView repositoryNameTextView = (TextView) view
					.findViewById(R.id.reposiotryName);
			repositoryNameTextView.setText(repository.getTitle());

			TextView dateTextView = (TextView) view.findViewById(R.id.date);
			dateTextView.setText(dateFormatter.format(changeset.getTime()));

			TextView userNameTextView = (TextView) view.findViewById(R.id.hash);
			userNameTextView.setText(changeset.getAuthor());

			// temporary
			TextView timeTextView = (TextView) view.findViewById(R.id.time);
			timeTextView.setText(timeFormatter.format(changeset.getTime()));

			TextView messageTextView = (TextView) view.findViewById(R.id.commitMessage);
			messageTextView.setText(changeset.getMessage());

			View colorLabel = (View) view.findViewById(R.id.colorLabel);
			if (repository.getColorLabel().equals("label-white")){
				colorLabel.setBackgroundResource(R.color.white);
			} else if (repository.getColorLabel().equals("label-red")){
				colorLabel.setBackgroundResource(R.color.red);
			} else if (repository.getColorLabel().equals("label-yellow")){
				colorLabel.setBackgroundResource(R.color.yellow);
			} else if (repository.getColorLabel().equals("label-blue")){
				colorLabel.setBackgroundResource(R.color.blue);
			} else if (repository.getColorLabel().equals("label-orange")){
				colorLabel.setBackgroundResource(R.color.orange);
			} else if (repository.getColorLabel().equals("label-pink")){
				colorLabel.setBackgroundResource(R.color.pink);
			} else if (repository.getColorLabel().equals("label-green")){
				colorLabel.setBackgroundResource(R.color.green);
			} else if (repository.getColorLabel().equals("label-grey")){
				colorLabel.setBackgroundResource(R.color.gray);
			} 
			
		}

		return view;
	}

}
