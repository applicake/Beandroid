package com.applicake.beanstalkclient;

import android.os.Parcel;
import android.os.Parcelable;

public class Permission implements Parcelable {

  public int describeContents() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(String.valueOf(fullDeploymentAccess));
    dest.writeInt(id);
    dest.writeInt(repositoryId);
    dest.writeInt(serverEnvironmentId);
    dest.writeInt(userId);
    dest.writeString(String.valueOf(readAccess));
    dest.writeString(String.valueOf(writeAccess));

  }

  public Permission() {
    // TODO Auto-generated constructor stub
  }

  public Permission(Parcel in) {
    this.fullDeploymentAccess = Boolean.parseBoolean(in.readString());
    this.id = in.readInt();
    this.repositoryId = in.readInt();
    this.serverEnvironmentId = in.readInt();
    this.userId = in.readInt();
    this.readAccess = Boolean.parseBoolean(in.readString());
    this.writeAccess = Boolean.parseBoolean(in.readString());
  }

  public static final Parcelable.Creator<Permission> CREATOR = new Parcelable.Creator<Permission>() {
    public Permission createFromParcel(Parcel in) {
      return new Permission(in);
    }

    public Permission[] newArray(int size) {
      return new Permission[size];
    }
  };

  private boolean fullDeploymentAccess;
  private int id;
  private int repositoryId;
  private int serverEnvironmentId;
  private int userId;
  private boolean readAccess;
  private boolean writeAccess;

  public boolean isFullDeploymentAccess() {
    return fullDeploymentAccess;
  }

  public void setFullDeploymentAccess(boolean fullDeploymentAccess) {
    this.fullDeploymentAccess = fullDeploymentAccess;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getRepositoryId() {
    return repositoryId;
  }

  public void setRepositoryId(int repositoryId) {
    this.repositoryId = repositoryId;
  }

  public int getServerEnvironmentId() {
    return serverEnvironmentId;
  }

  public void setServerEnvironmentId(int serverEnvironmentId) {
    this.serverEnvironmentId = serverEnvironmentId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public boolean isReadAccess() {
    return readAccess;
  }

  public void setReadAccess(boolean readAccess) {
    this.readAccess = readAccess;
  }

  public boolean isWriteAccess() {
    return writeAccess;
  }

  public void setWriteAccess(boolean writeAccess) {
    this.writeAccess = writeAccess;
  }

}
