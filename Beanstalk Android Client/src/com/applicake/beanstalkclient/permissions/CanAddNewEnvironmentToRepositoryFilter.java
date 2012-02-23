package com.applicake.beanstalkclient.permissions;

import android.content.Context;

import com.applicake.beanstalkclient.Repository;

public class CanAddNewEnvironmentToRepositoryFilter extends ObjectsFilter<Repository> {
  
  private ServerEnviromentsPermissions permissions;
  
  public CanAddNewEnvironmentToRepositoryFilter(Context context) {
    super(context);
    permissions = new ServerEnviromentsPermissions(context);
  }
  
  @Override
  public boolean fits(Repository repository) {
    return permissions.hasFullDeploymentsAccessForRepository(repository);
  }

}
