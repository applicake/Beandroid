package com.applicake.beanstalkclient.test.activites;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.View;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.activities.HomeActivity;
import com.applicake.beanstalkclient.enums.UserType;

public class HomeActivityTestCase extends ActivityUnitTestCase<HomeActivity> {
	
	public HomeActivityTestCase() {
		super(HomeActivity.class);
	}
	
	public void testUsersAndSettingsButtonNotVisibleWhenUserTypeIsUser() {
		Intent intent = new Intent();
		intent.putExtra(Constants.USER_TYPE, UserType.USER.name());
		startActivity(intent, null, null);
		View button = getActivity().findViewById(R.id.users_button);
		assertEquals(View.GONE, button.getVisibility());
		button = getActivity().findViewById(R.id.account_settings_button);
		assertEquals(View.GONE, button.getVisibility());
	}

	
	public void testSettingsButtonNotVisibleWhenUserTypeIsNotOwner() {
		Intent intent = new Intent();
		intent.putExtra(Constants.USER_TYPE, UserType.ADMIN.name());
		startActivity(intent, null, null);
		View button = getActivity().findViewById(R.id.account_settings_button);
		assertEquals(View.GONE, button.getVisibility());
	}
	
}