package com.applicake.beanstalkclient.test.adapters;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;
import android.view.View;

import com.applicake.beanstalkclient.Permission;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.ServerEnvironment;
import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.adapters.ServersAdapter;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.permissions.PermissionsData;
import com.applicake.beanstalkclient.permissions.ServerEnviromentsPermissions;

public class ServersAdapterTestCase extends AndroidTestCase {
	
	private ServersAdapter adapter;
	private List<ServerEnvironment> serversArray = new ArrayList<ServerEnvironment>();
	private ServerEnviromentsPermissions permissions = new ServerEnviromentsPermissions();
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		adapter = new ServersAdapter(getContext(), serversArray, permissions);
		ServerEnvironment exampleEnvironment = new ServerEnvironment();
		exampleEnvironment.setRepositoryId(0);
		exampleEnvironment.setId(0);
		exampleEnvironment.setName("AAA");
		exampleEnvironment.setAutomatic(false);
		exampleEnvironment.setDownloaded(true);
		
		serversArray.add(exampleEnvironment);
	}
	
	private PermissionsData generateData(UserType type) {
		PermissionsData data = new PermissionsData();
		User user = new User();
		user.setUserType(type);
		data.setUser(user);
		data.setPermissions(new ArrayList<Permission>());
		return data;
	}
	
	private PermissionsData generateFullAccessPermissions() {
		return generateData(UserType.OWNER);
	}
	
	private PermissionsData generateNonFullAcessPermissions() {
		return generateData(UserType.USER);
	}
		
	public void testEditButtonIsNotVisibleWhenNoFullAccess() {
		permissions.setPermissionsData(generateNonFullAcessPermissions());
		View itemsView = adapter.getGroupView(0, false, null, null);
		assertEquals(View.GONE, itemsView.findViewById(R.id.edit_server_environment_button).getVisibility());
	}
	
	public void testEditButtonIsVisibleWhenHasFullAccess() {
		permissions.setPermissionsData(generateFullAccessPermissions());
		View itemsView = adapter.getGroupView(0, false, null, null);
		assertEquals(View.VISIBLE, itemsView.findViewById(R.id.edit_server_environment_button).getVisibility());
	}
	
	public void testAddServerButtonIsVisibleWhenHasFullAccess() {
		permissions.setPermissionsData(generateFullAccessPermissions());
		View childViews = adapter.getChildView(0, 0, true, null, null);
		assertEquals(childViews.getId(),  R.id.environments_add_server_footer);
	}
	
	public void testAddServerButtonIsDisabledWhenNoFullAccess() {
		permissions.setPermissionsData(generateNonFullAcessPermissions());
		int childCount = adapter.getChildrenCount(0);
		assertEquals(0, childCount);
	}
}
