package com.applicake.beanstalkclient.permissions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;

import com.applicake.beanstalkclient.Permission;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.ServerEnvironment;
import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.enums.UserType;

public class ServerEnviromentsPermissions extends PermissionsInterpreter {
    
  private Map<Integer, Integer> parsedEnviromentPermissions;
  private Set<Integer> fullDeploymentsAccessRepositoriesIds;
  private User loggedUser;
  
  public ServerEnviromentsPermissions(Context context) {
    super(context);
  }
    
  protected void initialize() {
    parsedEnviromentPermissions = new HashMap<Integer, Integer>();
    fullDeploymentsAccessRepositoriesIds = new HashSet<Integer>();
  }
  
  public void setPermissionsData(PermissionsData data) {
    loggedUser = data.getUser();
    for(Permission permission : data.getPermissions()) {
      if(permission.isFullDeploymentAccess()) {
        fullDeploymentsAccessRepositoriesIds.add(permission.getRepositoryId());
      } else if(permission.getServerEnvironmentId() != Permission.NO_ID_SET) {
        parsedEnviromentPermissions.put(permission.getRepositoryId(), permission.getServerEnvironmentId());
      }
    }
  }
  
  public boolean hasDeploymentAccessForRepository(Repository repository) {
    return hasFullDeploymentsAccessForRepository(repository.getId()) || parsedEnviromentPermissions.containsKey(repository.getId());
  }
  
  public boolean hasFullDeploymentsAccessForRepository(Repository repository) {
    return hasFullDeploymentsAccessForRepository(repository.getId());
  }
  
  private boolean isUserAdminAtLeast() {
    return loggedUser != null && (loggedUser.getUserType() == UserType.OWNER 
        || loggedUser.getUserType() == UserType.ADMIN);
  }
  
  public boolean hasFullDeploymentsAccessForRepository(int repositoryId) {
    return  isUserAdminAtLeast() || fullDeploymentsAccessRepositoriesIds.contains(repositoryId);
  }
  
  public boolean hasAccessForThisEnvironment(ServerEnvironment environment) {
    if(hasFullDeploymentsAccessForRepository(environment.getRepositoryId())) {
      return true;
    }
    Integer environmentId = parsedEnviromentPermissions.get(environment.getRepositoryId());
    return environmentId != null && environmentId.intValue() == environment.getId();
  }
}
