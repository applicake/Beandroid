package com.applicake.beanstalkclient;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.utils.GravatarDowloader;

public class UserDetailsActivity extends BeanstalkActivity implements OnClickListener{

	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_details_layout);
		
		user = getIntent().getParcelableExtra(Constants.USER);
		UserType userType = user.getAdmin();
		ImageView userGravatar = (ImageView) findViewById(R.id.userGravatar);
		
		GravatarDowloader.getInstance().download(user.getEmail(), userGravatar);
		
		if (userType == UserType.ADMIN){
			findViewById(R.id.adminLabel).setVisibility(View.VISIBLE);
			
		} else if (userType == UserType.OWNER){
			findViewById(R.id.ownerLabel).setVisibility(View.VISIBLE);
			findViewById(R.id.adminLabel).setVisibility(View.GONE);
		}
		
		((TextView) findViewById(R.id.userName)).setText(user.getFirstName() + " " + user.getLastName());
		((TextView) findViewById(R.id.userLogin)).setText(user.getLogin());
		((TextView) findViewById(R.id.userEmail)).setText(user.getEmail());
		
		
		// add button listeners 
		Button userPermissionsButton = (Button) findViewById(R.id.buttonUserPermissions);
		Button modifyPropertiesButton = (Button) findViewById(R.id.buttonModifyProperties);
		Button deleteUserButton = (Button) findViewById(R.id.buttonDeleteUser);
		
		userPermissionsButton.setOnClickListener(this);
		modifyPropertiesButton.setOnClickListener(this);
		deleteUserButton.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		
		if (v.getId() == R.id.buttonUserPermissions){
	
		}
		
		if (v.getId() == R.id.buttonModifyProperties){
		
		}
		
		if (v.getId() == R.id.buttonDeleteUser){
	
		}
		
	}

}
