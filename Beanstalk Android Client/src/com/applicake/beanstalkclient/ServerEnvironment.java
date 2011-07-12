/*
 *   <server-environment>
    <account-id type="integer">4</account-id>
    <automatic type="boolean">true</automatic>
    <branch-name>production</branch-name>
    <created-at type="datetime">2010-07-01T13:58:55+08:00</created-at>
    <current-version>319dded3c14a2f901686b98fc58d5026f102ba65</current-version>
    <id type="integer">6</id>
    <name>Mascarpone</name>
    <repository-id type="integer">58</repository-id>
    <updated-at type="datetime">2010-08-09T11:32:53+08:00</updated-at>
  </server-environment>
 */


package com.applicake.beanstalkclient;

public class ServerEnvironment {
  
  private int accountId;
  private boolean automatic;
  private String branchName;
  private long createdAt;
  private String currentVersion;
  private int id;
  private String name;
  private int repositoryId;
  private long updatedAt;
  
  
  public int getAccountId() {
    return accountId;
  }
  public void setAccountId(int accountId) {
    this.accountId = accountId;
  }
  public boolean isAutomatic() {
    return automatic;
  }
  public void setAutomatic(boolean automatic) {
    this.automatic = automatic;
  }
  public String getBranchName() {
    return branchName;
  }
  public void setBranchName(String branchName) {
    this.branchName = branchName;
  }
  public long getCreatedAt() {
    return createdAt;
  }
  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }
  public String getCurrentVersion() {
    return currentVersion;
  }
  public void setCurrentVersion(String currentVersion) {
    this.currentVersion = currentVersion;
  }
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public int getRepositoryId() {
    return repositoryId;
  }
  public void setRepositoryId(int repositoryId) {
    this.repositoryId = repositoryId;
  }
  public long getUpdatedAt() {
    return updatedAt;
  }
  public void setUpdatedAt(long updatedAt) {
    this.updatedAt = updatedAt;
  }
  
  

}
