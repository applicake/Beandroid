package com.applicake.beanstalkclient.adapters;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.applicake.beanstalkclient.Permission;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Repository;

public class UserPermissionsAdapter extends ArrayAdapter<Repository> {

	private List<Repository> repositoriesArray;
	private Map<Integer, Permission> repoIdToPermissionMap;
	private LayoutInflater mInflater;

	public void setRepoIdToPermissionMap(Map<Integer, Permission> repoIdToPermissionMap) {
		this.repoIdToPermissionMap = repoIdToPermissionMap;
	}

	public UserPermissionsAdapter(Context context, int textViewResourceId,
			List<Repository> repositoriesArray,
			Map<Integer, Permission> repoIdToPermissionMap) {
		super(context, textViewResourceId, repositoriesArray);
		mInflater = LayoutInflater.from(context);
		this.repositoriesArray = repositoriesArray;
		this.repoIdToPermissionMap = repoIdToPermissionMap;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.user_permissions_entry, null);

		Repository repository = repositoriesArray.get(position);
		Permission permission;

		if (repository != null) {

			TextView repoTitleTextView = (TextView) view
					.findViewById(R.id.reposiotryTitle);
			repoTitleTextView.setText(repository.getName());

			TextView repositoryNameTextView = (TextView) view
					.findViewById(R.id.repositoryName);
			repositoryNameTextView.setText(repository.getTitle());

			View colorLabel = (View) view.findViewById(R.id.colorLabel);
			colorLabel.getBackground().setLevel(repository.getColorLabelNo());

			TextView repoPermissionLabel = (TextView) view
					.findViewById(R.id.repositoryLabel);
			TextView deploymentPermissionLabel = (TextView) view
					.findViewById(R.id.deploymentLabel);

			if (repoIdToPermissionMap.containsKey(repository.getId())) {
				permission = repoIdToPermissionMap.get(repository.getId());

				if (permission.isReadAccess()) {
					if (permission.isWriteAccess()) {
						repoPermissionLabel.setText("write");
						repoPermissionLabel.getBackground().setLevel(0);
					} else {
						repoPermissionLabel.setText("read");
						repoPermissionLabel.getBackground().setLevel(1);
					}
				}

				if (permission.isFullDeploymentAccess()) {
					deploymentPermissionLabel.setText("write");
					deploymentPermissionLabel.getBackground().setLevel(0);
				} else {
					deploymentPermissionLabel.setText("read");
					deploymentPermissionLabel.getBackground().setLevel(2);
				}
			} else {
				repoPermissionLabel.setText("no access");
				repoPermissionLabel.getBackground().setLevel(2);

				deploymentPermissionLabel.setText("read");
				deploymentPermissionLabel.getBackground().setLevel(2);
			}

		}

		return view;
	}
}
