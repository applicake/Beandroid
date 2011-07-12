package com.applicake.beanstalkclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Server {

  /*
   *     name — must be unique in environment, name must be no longer than 20 chars;
    local_path — path inside the repository that will be deployed;
    remote_path — path on the remote server where Beanstalk will deploy to;
    remote_addr — remote server address;
    protocol — “ftp” or “sftp”;
    port — FTP/SFTP port;
    login — FTP/SFTP user login;
    password — FTP/SFTP user password;
    use_active_mode — “true” or “false”;
    authenticate_by_key — “true” or “false”. For SFTP servers only, will try to authenticate using Beanstalk’s public key;
    use_feat — “true” or “false”;
    pre_release_hook — “http://yourhost.com/yourhook.php”. URL that we will POST information about current release to before starting deployment;
    post_release_hook — “http://yourhost.com/yourhook.php”. URL that we will POST information about current release to before after deployment was successful;
   */
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
  private int revision;
  private long updatedAt;
  
  
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
  public int getRevision() {
    return revision;
  }
  public void setRevision(int revision) {
    this.revision = revision;
  }
  public long getUpdatedAt() {
    return updatedAt;
  }
  public void setUpdatedAt(String date) throws ParseException {
    this.updatedAt = FORMATTER.parse(date.trim()).getTime();
  }
  
}
