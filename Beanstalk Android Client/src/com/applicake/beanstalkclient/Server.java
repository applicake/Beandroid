package com.applicake.beanstalkclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.os.Parcel;
import android.os.Parcelable;

public class Server implements Parcelable{

  static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
  
  private int accountId;
  private long createdAt;
  private int id;
  private String localPath;
  private String login;
  private String name;
  private String password;
  private int port;
  private String protocol;
  private String remoteAddr;
  private String remotePath;
  private int repositoryId;
  private String environmentName;
  private String serverEnvironmentId;
  private String revision;
  private long updatedAt;
  private String preReleaseHook;
  private String postReleaseHook;
  private boolean useActiveMode;
  private boolean authenticateByKey;
  private boolean useFeat;
  

  // default constructor of Server
  public Server(){
  }
  
  // implementation of Parcelable interface
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(accountId);
    dest.writeLong(createdAt);
    dest.writeInt(id);
    dest.writeString(localPath);
    dest.writeString(login);
    dest.writeString(name);
    dest.writeString(password);
    dest.writeInt(port);
    dest.writeString(protocol);
    dest.writeString(remoteAddr);
    dest.writeString(remotePath);
    dest.writeInt(repositoryId);
    dest.writeString(environmentName);
    dest.writeString(serverEnvironmentId);
    dest.writeString(revision);
    dest.writeLong(updatedAt);    
    dest.writeString(preReleaseHook);
    dest.writeString(postReleaseHook);
    dest.writeInt(useActiveMode ? 1 : 0);
    dest.writeInt(authenticateByKey ? 1 : 0);    
    dest.writeInt(useFeat ? 1 : 0);    
  }

  public Server(Parcel in) {
    this.accountId = in.readInt();
    this.createdAt = in.readLong();
    this.id = in.readInt();
    this.localPath = in.readString();
    this.login = in.readString();
    this.name = in.readString();
    this.password = in.readString();
    this.port = in.readInt();
    this.protocol = in.readString();
    this.remoteAddr = in.readString();
    this.remotePath = in.readString();
    this.repositoryId = in.readInt();
    this.environmentName = in.readString();
    this.serverEnvironmentId = in.readString();
    this.revision = in.readString();
    this.updatedAt = in.readLong();
    this.preReleaseHook = in.readString();
    this.postReleaseHook = in.readString();
    this.useActiveMode = in.readInt() == 1 ? true : false;
    this.authenticateByKey = in.readInt() == 1 ? true : false;
    this.useFeat = in.readInt() == 1 ? true : false;
  }
  
  @Override
  public int describeContents() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  
  public static final Parcelable.Creator<Server> CREATOR = new Parcelable.Creator<Server>() {
    public Server createFromParcel(Parcel in) {
      return new Server(in);
    }

    public Server[] newArray(int size) {
      return new Server[size];
    }
  };
  // end of Parcelable implementation
  
  
  
  // le getters and setters
  public int getAccountId() {
    return accountId;
  }
  public void setAccountId(int i) {
    this.accountId = i;
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
  public String getLocalPath() {
    return localPath;
  }
  public void setLocalPath(String localPath) {
    this.localPath = localPath;
  }
  public String getLogin() {
    return login;
  }
  public void setLogin(String login) {
    this.login = login;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public int getPort() {
    return port;
  }
  public void setPort(int port) {
    this.port = port;
  }
  public String getProtocol() {
    return protocol;
  }
  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }
  public String getRemoteAddr() {
    return remoteAddr;
  }
  public void setRemoteAddr(String remoteAddr) {
    this.remoteAddr = remoteAddr;
  }
  public String getRemotePath() {
    return remotePath;
  }
  public void setRemotePath(String remotePath) {
    this.remotePath = remotePath;
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
  public String getServerEnvironmentId() {
    return serverEnvironmentId;
  }
  public void setServerEnvironmentId(String serverEnvironmentId) {
    this.serverEnvironmentId = serverEnvironmentId;
  }
  public String getRevision() {
    return revision;
  }
  public void setRevision(String revision) {
    this.revision = revision;
  }
  public long getUpdatedAt() {
    return updatedAt;
  }
  public void setUpdatedAt(String date) throws ParseException {
    this.updatedAt = FORMATTER.parse(date.trim()).getTime();
  }
  public String getPreReleaseHook() {
    return preReleaseHook;
  }
  public void setPreReleaseHook(String preReleaseHook) {
    this.preReleaseHook = preReleaseHook;
  }
  public String getPostReleaseHook() {
    return postReleaseHook;
  }
  public void setPostReleaseHook(String postReleaseHook) {
    this.postReleaseHook = postReleaseHook;
  }
  public boolean isUseActiveMode() {
    return useActiveMode;
  }
  public void setUseActiveMode(boolean useActiveMode) {
    this.useActiveMode = useActiveMode;
  }
  public boolean isAuthenticateByKey() {
    return authenticateByKey;
  }
  public void setAuthenticateByKey(boolean authenticateByKey) {
    this.authenticateByKey = authenticateByKey;
  }
  public boolean isUseFeat() {
    return useFeat;
  }
  public void setUseFeat(boolean useFeat) {
    this.useFeat = useFeat;
  }
  
}
