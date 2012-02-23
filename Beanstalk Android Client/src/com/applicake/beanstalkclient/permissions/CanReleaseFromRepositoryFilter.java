package com.applicake.beanstalkclient.permissions;

import android.content.Context;

import com.applicake.beanstalkclient.Repository;

public class CanReleaseFromRepositoryFilter extends ObjectsFilter<Repository> {
  
  private ServerEnviromentsPermissions permissions;
  
  public CanReleaseFromRepositoryFilter(Context context) {
    super(context);
    permissions = new ServerEnviromentsPermissions(context);
  }
  
  @Override
  public boolean fits(Repository repository) {
    return permissions.hasDeploymentAccessForRepository(repository);
  }
  
  
}
