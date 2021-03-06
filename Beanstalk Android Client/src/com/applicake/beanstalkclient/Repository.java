package com.applicake.beanstalkclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.os.Parcel;
import android.os.Parcelable;

import com.applicake.beanstalkclient.enums.ColorLabels;

public class Repository implements Parcelable {
  static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
  static SimpleDateFormat FORMATTER_NOTIMEZONE = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss'Z'");

  private int accountId;
  private boolean anonymous;
  private String colorLabel;
  private long createdAt;
  private int id;
  private long lastCommitAt;
  private String name;
  private int revision;
  private int storageUsedBytes;
  private String title;
  private String type;
  private long updatedAt;
  private String vcs;
  private String defaultBranch;

  public static Repository generateFakeRepositoryForOverall() {
    Repository repo = new Repository();
    repo.setTitle("Overall repositories");
    repo.setColorLabel("label-white");
    
    return repo;
  }
  
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(accountId);
    dest.writeString(String.valueOf(anonymous));
    dest.writeString(colorLabel);
    dest.writeLong(createdAt);
    dest.writeInt(id);
    dest.writeLong(lastCommitAt);
    dest.writeString(name);
    dest.writeInt(revision);
    dest.writeInt(storageUsedBytes);
    dest.writeString(title);
    dest.writeString(type);
    dest.writeLong(updatedAt);
    dest.writeString(vcs);
    dest.writeString(defaultBranch);
  }

  public Repository() {
    // TODO Auto-generated constructor stub
  }

  public Repository(Parcel in) {
    this.accountId = in.readInt();
    this.anonymous = Boolean.getBoolean(in.readString());
    this.colorLabel = in.readString();
    this.createdAt = in.readLong();
    this.id = in.readInt();
    this.lastCommitAt = in.readLong();
    this.name = in.readString();
    this.revision = in.readInt();
    this.storageUsedBytes = in.readInt();
    this.title = in.readString();
    this.type = in.readString();
    this.updatedAt = in.readLong();
    this.vcs = in.readString();
    this.defaultBranch = in.readString();
  }

  @Override
  public int describeContents() {
    // TODO Auto-generated method stub
    return 0;
  }

  public static final Parcelable.Creator<Repository> CREATOR = new Parcelable.Creator<Repository>() {
    public Repository createFromParcel(Parcel in) {
      return new Repository(in);
    }

    public Repository[] newArray(int size) {
      return new Repository[size];
    }
  };

  public int getAccountId() {
    return accountId;
  }

  public void setAccountId(int accountId) {
    this.accountId = accountId;
  }

  public boolean isAnonymous() {
    return anonymous;
  }

  public void setAnonymous(boolean anonymous) {
    this.anonymous = anonymous;
  }

  public String getColorLabel() {
    return colorLabel;
  }

  public void setColorLabel(String colorLabel) {
    this.colorLabel = colorLabel;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String date) throws ParseException {
    this.createdAt = FORMATTER.parse(date.trim()).getTime();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public long getLastCommitAt() {
    return lastCommitAt;
  }

  public void setLastCommitAt(String date) throws ParseException {
    this.lastCommitAt = FORMATTER_NOTIMEZONE.parse(date.trim()).getTime();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getRevision() {
    return revision;
  }

  public void setRevision(int revision) {
    this.revision = revision;
  }

  public int getStorageUsedBytes() {
    return storageUsedBytes;
  }

  public void setStorageUsedBytes(int storageUsedBytes) {
    this.storageUsedBytes = storageUsedBytes;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String date) throws ParseException {
    this.updatedAt = FORMATTER.parse(date.trim()).getTime();
  }

  public String getVcs() {
    return vcs;
  }

  public void setVcs(String vcs) {
    this.vcs = vcs;
  }

  public String getDefaultBranch() {
    return defaultBranch;
  }

  public void setDefaultBranch(String defaultBranch) {
    this.defaultBranch = defaultBranch;
  }

  public int getColorLabelNo() {
    return ColorLabels.getNumberFromLabel(colorLabel);

  }

}
