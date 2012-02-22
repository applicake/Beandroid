package com.applicake.beanstalkclient.permissions;

import com.applicake.beanstalkclient.Permission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerEnviromentsPermissions {
  
  private Map<Integer, Integer> parsedEnviromentPermissions = new HashMap<Integer, Integer>();
  private Map<Integer, Boolean> fullDeploymentsAccessForRepositoryMap = new HashMap<Integer, Boolean>();
  
  public void setPermissions(List<Permission> permissions) {
    for(Permission permission : permissions) {
    }
  }
}
