package com.applicake.beanstalkclient.test.utils;

import java.util.ArrayList;

import android.test.AndroidTestCase;

import com.applicake.beanstalkclient.Permission;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.ServerEnvironment;
import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.permissions.PermissionsData;
import com.applicake.beanstalkclient.permissions.ServerEnviromentsPermissions;

public class ServerPermissionsTestCase extends AndroidTestCase {
	
	private static final int TESTCASE_REPOSITORY_ID = 1;
	private static final int TESTCASE_ENVIRONMENT_ID = 1234;

	private ServerEnviromentsPermissions serverPermissions = new ServerEnviromentsPermissions();	
	private Repository simpleRepository;
	private ServerEnvironment environment;
				
	private User generateUser() {
		User user = new User();
		user.setUserType(UserType.USER);
		return user;
	}
	
	private User generateAdmin() {
		User user = new User();
		user.setUserType(UserType.ADMIN);
		return user;
	}
	
	private User generateOwner() {
		User user = new User();
		user.setUserType(UserType.OWNER);
		return user;
	}
	
	private PermissionsData generateCustomPermissions(User user) {
		PermissionsData permissionData = new PermissionsData();
		permissionData.setUser(user);
		permissionData.setPermissions(new ArrayList<Permission>());
		return permissionData;
	}
	
	private PermissionsData generateWriteablePermissionsForRepository(Repository repository, ServerEnvironment environment) {
		PermissionsData permissionData = new PermissionsData();
		permissionData.setUser(generateUser());
		ArrayList<Permission> permissions = new ArrayList<Permission>();
		Permission permission = new Permission();
		permission.setRepositoryId(repository.getId());
		permission.setServerEnvironmentId(environment.getId());
		permissions.add(permission);
		permissionData.setPermissions(permissions);
		return permissionData;
	}
	
	private PermissionsData generateFullAccessPermissionsForRepository(Repository repository, ServerEnvironment environment) {
		PermissionsData permissionData = new PermissionsData();
		permissionData.setUser(generateUser());
		ArrayList<Permission> permissions = new ArrayList<Permission>();
		Permission permission = new Permission();
		permission.setRepositoryId(repository.getId());
		permission.setFullDeploymentAccess(true);
		permissions.add(permission);
		permissionData.setPermissions(permissions);
		return permissionData;
	}
	
	private PermissionsData generateEmptyPermissions() {
		return new PermissionsData();
	}
	
	private PermissionsData generateAllDenyPermissions() {
		return generateCustomPermissions(generateUser());
	}
	
	private PermissionsData generateOwnerPermissions() {
		return generateCustomPermissions(generateOwner());
	}
	
	private PermissionsData generateAdminPermissions() {
		return generateCustomPermissions(generateAdmin());
	}
	
	private Repository generateSimpleRepository() {
		if(simpleRepository == null) {
			simpleRepository = new Repository();
			simpleRepository.setId(TESTCASE_REPOSITORY_ID);
		}
		return simpleRepository;
	}
	
	private ServerEnvironment generateSimpleEnvironment() {
		if(environment == null) {
			environment = new ServerEnvironment();
			environment.setId(TESTCASE_ENVIRONMENT_ID);
			environment.setRepositoryId(TESTCASE_REPOSITORY_ID);
		}
		return environment;
	}
	
	public void testThrowIllegalStateExceptionWhenPermissionDataIsInvalid() {
		// no JUnit4 features, such as expected field in @Test annotation? pity - have to check it manually using try-catch.
		try {
			serverPermissions.setPermissionsData(generateEmptyPermissions());
			fail();
		} catch(IllegalStateException e) {
			// success
		}
	}
	
	public void testCantDeployToRepositoryWhenHaveAllDenyPermissions() {
		serverPermissions.setPermissionsData(generateAllDenyPermissions());
		assertEquals(false, serverPermissions.hasDeploymentAccessForRepository(generateSimpleRepository()));
	}
	
	public void testDontHaveFullAccessWhenHaveAllDenyPermissions() {
		serverPermissions.setPermissionsData(generateAllDenyPermissions());
		assertEquals(false, serverPermissions.hasFullDeploymentsAccessForRepository(generateSimpleRepository()));
	}
	
	public void testDontHaveFullDeploymentAccessToRepository() {
		serverPermissions.setPermissionsData(generateWriteablePermissionsForRepository(generateSimpleRepository(), generateSimpleEnvironment()));
		assertEquals(false, serverPermissions.hasFullDeploymentsAccessForRepository(generateSimpleRepository()));
	}
	
	public void testCanDeployToRepositoryWhenHaveFullDeploymentAccess() {
		serverPermissions.setPermissionsData(generateFullAccessPermissionsForRepository(generateSimpleRepository(), generateSimpleEnvironment()));
		assertEquals(true, serverPermissions.hasDeploymentAccessForRepository(generateSimpleRepository()));
	}
	
	public void testCanDeployToRepositoryWhenHaveWriteAccess() {
		serverPermissions.setPermissionsData(generateWriteablePermissionsForRepository(generateSimpleRepository(), generateSimpleEnvironment()));
		assertEquals(true, serverPermissions.hasDeploymentAccessForRepository(generateSimpleRepository()));
	}
		
	public void testCanDeployWhenAdmin() {
		serverPermissions.setPermissionsData(generateAdminPermissions());
		assertEquals(true, serverPermissions.hasDeploymentAccessForRepository(generateSimpleRepository()));

	}
	
	public void testCanDeployWhenOwner() {
		serverPermissions.setPermissionsData(generateOwnerPermissions());
		assertEquals(true, serverPermissions.hasDeploymentAccessForRepository(generateSimpleRepository()));

	}
	
	public void testHaveFullAccessWhenAdmin() {
		serverPermissions.setPermissionsData(generateAdminPermissions());
		assertEquals(true, serverPermissions.hasFullDeploymentsAccessForRepository(generateSimpleRepository()));

	}
	
	public void testHaveFullAccessWhenOwner() {
		serverPermissions.setPermissionsData(generateOwnerPermissions());
		assertEquals(true, serverPermissions.hasFullDeploymentsAccessForRepository(generateSimpleRepository()));

	}
}
