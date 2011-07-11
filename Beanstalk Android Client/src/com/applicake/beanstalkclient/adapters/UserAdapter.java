package com.applicake.beanstalkclient.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.utils.GravatarDowloader;

public class UserAdapter extends ArrayAdapter<User> {

  private List<User> userArray;
  private LayoutInflater mInflater;
  private GravatarDowloader gravatarDownloader;

  public UserAdapter(Context context, int textViewResourceId, List<User> userArray) {
    super(context, textViewResourceId, userArray);
    mInflater = LayoutInflater.from(context);
    this.userArray = userArray;
    gravatarDownloader = GravatarDowloader.getInstance();
  }

  public View getView(int position, View convertView, ViewGroup parent) {

    View view;
    if (convertView != null) {
      view = convertView;
    } else {
      view = mInflater.inflate(R.layout.user_list_entry, null);
    }

    if (userArray.get(position) != null) {
      String email = userArray.get(position).getEmail();
      gravatarDownloader
          .download(email, (ImageView) view.findViewById(R.id.userGravatar));

      ((TextView) view.findViewById(R.id.userName)).setText(userArray.get(position)
          .getFirstName() + " " + userArray.get(position).getLastName());
      UserType userType = userArray.get(position).getAdmin();

      if (userType == UserType.ADMIN) {
        view.findViewById(R.id.adminLabel).setVisibility(View.VISIBLE);
        view.findViewById(R.id.ownerLabel).setVisibility(View.GONE);

      } else if (userType == UserType.OWNER) {
        view.findViewById(R.id.ownerLabel).setVisibility(View.VISIBLE);
        view.findViewById(R.id.adminLabel).setVisibility(View.GONE);
      } else {
        view.findViewById(R.id.ownerLabel).setVisibility(View.GONE);
        view.findViewById(R.id.adminLabel).setVisibility(View.GONE);
      }

      ((TextView) view.findViewById(R.id.userEmail)).setText(email);

    }

    return view;
  }

}
