package com.applicake.beanstalkclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Server {


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
  private String envitonmentName;
  private String serverEnvironmentId;
  private String revision;
  private long updatedAt;
  private String preReleaseHook;
  private String postReleaseHook;
  private boolean useActiveMode;
  private boolean authenticateByKey;
  private boolean useFeat;
  
  
  
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
  public String getEnvitonmentName() {
    return envitonmentName;
  }
  public void setEnvironmentName(String envitonmentName) {
    this.envitonmentName = envitonmentName;
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
