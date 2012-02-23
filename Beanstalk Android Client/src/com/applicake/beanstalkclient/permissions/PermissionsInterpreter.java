package com.applicake.beanstalkclient.permissions;

import java.io.IOException;

import com.applicake.beanstalkclient.utils.GUI;

import android.content.Context;

public abstract class PermissionsInterpreter {
  
  private Context mContext;
  
  public PermissionsInterpreter(Context context) {
    this.mContext = context;
    initialize();
    PermissionsPersistenceUtil util = new PermissionsPersistenceUtil(context);
    
    try {
      setPermissionsData(util.readStoredPermissionsData());
    } catch(IOException e) {
      e.printStackTrace();
      GUI.displayMonit(context, "Can't read permissions data. Assuming you have none.");
    }
  }
  
  
  protected abstract void setPermissionsData(PermissionsData data);
  protected void initialize() { }
    
  protected Context getContext() {
    return mContext;
  }
}
