package com.applicake.beanstalkclient.adapters;

import java.util.List;

import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.utils.GravatarDowloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserAdapter extends ArrayAdapter<User> {

	private Context context;
	private List<User> userArray;
	

	public UserAdapter(Context context, int textViewResourceId,
			List<User> userArray) {
		super(context, textViewResourceId, userArray);
		this.context = context;
		this.userArray = userArray;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.user_list_entry, null);
		}

		User user = userArray.get(position);


		if (user != null) {
			String email = user.getEmail();
			ImageView userGravatar = (ImageView) view.findViewById(R.id.userGravatar);
			GravatarDowloader.getInstance().download(email, userGravatar);
			
			TextView userNameTextView = (TextView) view
					.findViewById(R.id.userName);
			userNameTextView.setText(user.getFirstName() + " " + user.getLastName());
			UserType userType = user.getAdmin();

			if (userType == UserType.ADMIN){
				view.findViewById(R.id.adminLabel).setVisibility(View.VISIBLE);
				
			} else if (userType == UserType.OWNER){
				view.findViewById(R.id.ownerLabel).setVisibility(View.VISIBLE);
				view.findViewById(R.id.adminLabel).setVisibility(View.GONE);
			}
			
			TextView userEmailTextView = (TextView) view.findViewById(R.id.userEmail);
			userEmailTextView.setText(email);
			
		}

		return view;
	}

}
