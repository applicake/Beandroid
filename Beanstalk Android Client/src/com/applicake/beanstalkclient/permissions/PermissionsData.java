package com.applicake.beanstalkclient.permissions;

import java.io.Serializable;
import java.util.ArrayList;

import com.applicake.beanstalkclient.Permission;
import com.applicake.beanstalkclient.User;

public class PermissionsData implements Serializable {

  private static final long serialVersionUID = 4319159820968322604L;

  private User user;
  private ArrayList<Permission> permissions;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public ArrayList<Permission> getPermissions() {
    return permissions;
  }

  public void setPermissions(ArrayList<Permission> permissions) {
    this.permissions = permissions;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

}
