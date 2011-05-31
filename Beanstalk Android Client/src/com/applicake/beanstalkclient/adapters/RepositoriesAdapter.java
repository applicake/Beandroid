package com.applicake.beanstalkclient.adapters;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Repository;

public class RepositoriesAdapter extends ArrayAdapter<Repository> {

	private LayoutInflater mInflater;
	private List<Repository> repositoriesArray;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");


	public RepositoriesAdapter(Context context, int textViewResourceId,
			List<Repository> repositoriesArray) {
		super(context, textViewResourceId, repositoriesArray);
		mInflater = LayoutInflater.from(context);
		this.repositoriesArray = repositoriesArray;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			view = mInflater.inflate(R.layout.repositories_entry, null);
		}

		Repository repository = repositoriesArray.get(position);

		if (repository != null) {
	
			TextView repositoryNameTextView = (TextView) view
					.findViewById(R.id.reposiotryName);
			repositoryNameTextView.setText(repository.getTitle());

			TextView dateTextView = (TextView) view.findViewById(R.id.date);
			dateTextView.setText(dateFormatter.format(repository.getCreatedAt()));

			TextView lastCommitTextView = (TextView) view.findViewById(R.id.lastCommit);
			long lastCommitDate = repository.getLastCommitAt();
			if (lastCommitDate == 0){
				lastCommitTextView.setText("last commit: no commits in this repository");
			} else {
				lastCommitTextView.setText("last commit: "+ DateUtils.getRelativeTimeSpanString(lastCommitDate));
			}
			
			
			TextView typeTextView = (TextView) view.findViewById(R.id.type);
			String repoType = repository.getType();
			if (repoType.equals("SubversionRepository")){
				typeTextView.setText("subversion");
			} else if (repoType.equals("GitRepository")){
				typeTextView.setText("GIT");
			}
			
			View colorLabel = (View) view.findViewById(R.id.colorLabel);
			colorLabel.getBackground().setLevel(repository.getColorLabelNo());

		}

		return view;
	}
}
