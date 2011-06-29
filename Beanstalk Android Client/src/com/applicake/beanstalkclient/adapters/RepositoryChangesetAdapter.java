package com.applicake.beanstalkclient.adapters;

import java.text.SimpleDateFormat;
import java.util.List;

import com.applicake.beanstalkclient.Changeset;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.utils.GravatarDowloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RepositoryChangesetAdapter extends ArrayAdapter<Changeset> {

	private LayoutInflater mInflater;
	private List<Changeset> changesetArray;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm");

	public RepositoryChangesetAdapter(Context context, int textViewResourceId,
			List<Changeset> changesetArray) {
		super(context, textViewResourceId, changesetArray);
		mInflater = LayoutInflater.from(context);
		this.changesetArray = changesetArray;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			view = mInflater.inflate(R.layout.repository_changeset_entry, null);
		}

		Changeset changeset = changesetArray.get(position);

		if (changeset != null) {
			ImageView userGravatar = (ImageView) view.findViewById(R.id.userGravatar);
			GravatarDowloader.getInstance().download(changeset.getEmail(), userGravatar);

			TextView hashTextView = (TextView) view.findViewById(R.id.revisionTextView);
			hashTextView.setText(changeset.getRevision());

			TextView dateTextView = (TextView) view.findViewById(R.id.date);
			dateTextView.setText(dateFormatter.format(changeset.getTime()));

			TextView userNameTextView = (TextView) view.findViewById(R.id.author);
			userNameTextView.setText(changeset.getAuthor());

			// temporary
			TextView timeTextView = (TextView) view.findViewById(R.id.time);
			timeTextView.setText(timeFormatter.format(changeset.getTime()));

			TextView messageTextView = (TextView) view.findViewById(R.id.commitMessage);
			messageTextView.setText(changeset.getMessage());

		}

		return view;
	}

}
