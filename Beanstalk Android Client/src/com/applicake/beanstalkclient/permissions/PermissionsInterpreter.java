package com.applicake.beanstalkclient.permissions;

import java.io.IOException;

import android.content.Context;

import com.applicake.beanstalkclient.utils.GUI;

public abstract class PermissionsInterpreter {
  
  private Context mContext;
  
  public PermissionsInterpreter() {
    initialize();
  }
  
  public PermissionsInterpreter(Context context) {
    this();
    this.mContext = context;
    
    PermissionsPersistenceUtil util = new PermissionsPersistenceUtil(context);
    
    try {
      setPermissionsData(util.readStoredPermissionsData());
    } catch(IOException e) {
      e.printStackTrace();
      GUI.displayMonit(context, "Can't read permissions data. Assuming you have none.");
    }
  }
  
  public void setPermissionsData(PermissionsData data) {
    if(isPermissionsDataValid(data)) {
      parsePermissionsData(data);
    } else {
      throw new IllegalStateException("user or permissions list in PermissionData object passed to this method are null");
    }
  }
  
  protected boolean isPermissionsDataValid(PermissionsData data) {
    return data != null && data.getUser() != null && data.getPermissions() != null;
  }
  
  protected abstract void parsePermissionsData(PermissionsData data);
  
  protected void initialize() { }
    
  protected Context getContext() {
    return mContext;
  }
}
