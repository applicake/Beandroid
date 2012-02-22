package com.applicake.beanstalkclient;

import java.util.List;

public class SessionHolder {
  
  private static SessionHolder instance;
  private Account account;
  private List<Permission> permissions;
  private Plan plan;
  
  public static synchronized SessionHolder getInstance() {
    if(instance == null) {
      instance = new SessionHolder();
    }
    return instance;
  }
  
  private SessionHolder() { }
  
  public void setAccountData(Account account) {
    this.account = account;
  }
  
  public void setPermissions(List<Permission> permissions) {
    
    this.permissions = permissions;
  }
  
  public void setPlan(Plan plan) {
    this.plan = plan;
  }
  
  public Account getAccountData() {
    return account;
  }
  
  public List<Permission> getPermissions() {
    return permissions;
  }
  
  public Plan getPlan() {
    return plan;
  }
  
}
