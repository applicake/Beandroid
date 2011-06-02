package com.applicake.beanstalkclient.adapters;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import com.applicake.beanstalkclient.Changeset;
import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.utils.GravatarDowloader;
import com.applicake.beanstalkclient.utils.RailsTimezones;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChangesetAdapter extends ArrayAdapter<Changeset> {

	private LayoutInflater mInflater;
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
		mInflater = LayoutInflater.from(context);
		this.changesetArray = changesetArray;
		SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		String timezoneString = prefs.getString(Constants.USER_TIMEZONE, "");
		TimeZone currentUserTimeZone;
		if (timezoneString == "") {
			currentUserTimeZone = TimeZone.getDefault(); 
		} else {
			currentUserTimeZone = RailsTimezones.getJavaTz(timezoneString);
		}

		dateFormatter.setTimeZone(currentUserTimeZone);
		timeFormatter.setTimeZone(currentUserTimeZone);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			view = mInflater.inflate(R.layout.dashboard_entry, parent, false);
		}

		Changeset changeset = changesetArray.get(position);
		Repository repository = repoMap.get(changeset.getRepositoryId());

		if (changeset != null) {
			ImageView userGravatar = (ImageView) view.findViewById(R.id.userGravatar);
			GravatarDowloader.getInstance().download(changeset.getEmail(), userGravatar);

			TextView repositoryNameTextView = (TextView) view
					.findViewById(R.id.reposiotryName);
			repositoryNameTextView.setText(repository.getTitle());

			TextView dateTextView = (TextView) view.findViewById(R.id.date);
			dateTextView.setText(dateFormatter.format(changeset.getTime()));

			TextView userNameTextView = (TextView) view.findViewById(R.id.hash);
			userNameTextView.setText(changeset.getAuthor());

			TextView timeTextView = (TextView) view.findViewById(R.id.time);
			timeTextView.setText(timeFormatter.format(changeset.getTime()));

			TextView messageTextView = (TextView) view.findViewById(R.id.commitMessage);
			messageTextView.setText(changeset.getMessage());

			View colorLabel = (View) view.findViewById(R.id.colorLabel);
			colorLabel.getBackground().setLevel(repository.getColorLabelNo());

		}

		return view;
	}

}
