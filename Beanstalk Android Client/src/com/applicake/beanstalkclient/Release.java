package com.applicake.beanstalkclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Release implements Parcelable {

  private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss'Z'");
  private static final String[] STATES = new String[] { "pending", "waiting", "failed",
      "success", "skipped", };
  private static final Map<String, Integer> STATES_TO_INT = new HashMap<String, Integer>();
  static {
    for (int i = 0; i < STATES.length; ++i) {
      STATES_TO_INT.put(STATES[i], i);
    }
  }

  private int accountId;
  private String author;
  private int userId;
  private String comment;
  private long createdAt;
  private long deployedAt;
  private int id;
  private long lastRetryAt;
  private int repositoryId;
  private String environmentName;
  private int environmentId;
  private int retries;
  private int revision;
  private int state;
  private long updatedAt;

  public Release() {
  }

  public Release(Parcel in) {
    readFromParcel(in);
  }

  public int getAccountId() {
    return accountId;
  }

  public void setAccountId(int accountId) {
    this.accountId = accountId;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Date getCreatedAt() {
    return new Date(createdAt);
  }

  public void setCreatedAt(String createdAt) throws ParseException {
    this.createdAt = FORMATTER.parse(createdAt.trim()).getTime();
  }

  public Date getDeployedAt() {
    return new Date(deployedAt);
  }

  public void setDeployedAt(String deployedAt) throws ParseException {
    this.deployedAt = FORMATTER.parse(deployedAt.trim()).getTime();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Date getLastRetryAt() {
    if (lastRetryAt == -1) {
      return null;
    } else {
      return new Date(lastRetryAt);
    }
  }

  public void setLastRetryAt(String lastRetryAt) throws ParseException {
    if (TextUtils.isEmpty(lastRetryAt)) {
      this.lastRetryAt = -1;
    } else {
      this.lastRetryAt = FORMATTER.parse(lastRetryAt.trim()).getTime();
    }
  }

  public int getRepositoryId() {
    return repositoryId;
  }

  public void setRepositoryId(int repositoryId) {
    this.repositoryId = repositoryId;
  }

  public String getEnvironmentName() {
    return environmentName;
  }

  public void setEnvironmentName(String environmentName) {
    this.environmentName = environmentName;
  }

  public int getEnvironmentId() {
    return environmentId;
  }

  public void setEnvironmentId(int environmentId) {
    this.environmentId = environmentId;
  }

  public int getRetries() {
    return retries;
  }

  public void setRetries(int retries) {
    this.retries = retries;
  }

  public int getRevision() {
    return revision;
  }

  public void setRevision(int revision) {
    this.revision = revision;
  }

  public String getStateLabel() {
    return STATES[state];
  }

  public int getState() {
    return state;
  }

  public void setState(String state) {
    this.state = STATES_TO_INT.get(state);
  }

  public Date getUpdatedAt() {
    return new Date(updatedAt);
  }

  public void setUpdatedAt(String updatedAt) throws ParseException {
    this.updatedAt = FORMATTER.parse(updatedAt.trim()).getTime();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(accountId);
    dest.writeString(author);
    dest.writeInt(userId);
    dest.writeString(comment);
    dest.writeLong(createdAt);
    dest.writeLong(deployedAt);
    dest.writeInt(id);
    dest.writeLong(lastRetryAt);
    dest.writeInt(repositoryId);
    dest.writeString(environmentName);
    dest.writeInt(environmentId);
    dest.writeInt(retries);
    dest.writeInt(revision);
    dest.writeInt(state);
    dest.writeLong(updatedAt);
  }

  private void readFromParcel(Parcel in) {
    accountId = in.readInt();
    author = in.readString();
    userId = in.readInt();
    comment = in.readString();
    createdAt = in.readLong();
    deployedAt = in.readLong();
    id = in.readInt();
    lastRetryAt = in.readLong();
    repositoryId = in.readInt();
    environmentName = in.readString();
    environmentId = in.readInt();
    retries = in.readInt();
    revision = in.readInt();
    state = in.readInt();
    updatedAt = in.readLong();
  }

  public static final Parcelable.Creator<Release> CREATOR = new Creator<Release>() {
    @Override
    public Release[] newArray(int size) {
      return new Release[size];
    }

    @Override
    public Release createFromParcel(Parcel in) {
      return new Release(in);
    }
  };

}
