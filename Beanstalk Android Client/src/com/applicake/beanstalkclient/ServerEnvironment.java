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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ServerEnvironment implements Parcelable {

  static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

  // basic server environment data
  private int accountId;
  private boolean automatic;
  private String branchName;
  private long createdAt;
  private String currentVersion;
  private int id;
  private String name;
  private int repositoryId;
  private long updatedAt;

  // handling server lists
  private List<Server> serverList = null;
  private boolean downloading = false;
  private boolean downloaded = false;

  // pratial implementation of Parcelable interface
  // this enables basic version of server environment to be passed between
  // intents
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(automatic ? 1 : 0);
    dest.writeString(branchName);
    dest.writeInt(id);
    dest.writeString(name);
    dest.writeInt(repositoryId);
  }

  public ServerEnvironment(Parcel in) {
    this.automatic = in.readInt() == 0 ? false : true;
    this.branchName = in.readString();
    this.id = in.readInt();
    this.name = in.readString();
    this.repositoryId = in.readInt();
  }

  public ServerEnvironment() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public int describeContents() {
    // TODO Auto-generated method stub
    return 0;
  }

  public static final Parcelable.Creator<ServerEnvironment> CREATOR = new Parcelable.Creator<ServerEnvironment>() {
    public ServerEnvironment createFromParcel(Parcel in) {
      return new ServerEnvironment(in);
    }

    public ServerEnvironment[] newArray(int size) {
      return new ServerEnvironment[size];
    }
  };

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

  public void setCreatedAt(String date) throws ParseException {
    this.createdAt = FORMATTER.parse(date.trim()).getTime();
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

  public void setUpdatedAt(String date) throws ParseException {
    this.updatedAt = FORMATTER.parse(date.trim()).getTime();
  }

  public void setDownloading(boolean downloading) {
    this.downloading = downloading;
  }

  public boolean isDownloading() {
    return downloading;
  }

  public void setServerList(List<Server> serverList) {
    this.serverList = serverList;
  }

  public List<Server> getServerList() {
    return serverList;
  }

  public void setDownloaded(boolean downloaded) {
    this.downloaded = downloaded;
  }

  public boolean isDownloaded() {
    return downloaded;
  }

}
