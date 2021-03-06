package com.applicake.beanstalkclient.utils;

import java.io.IOException;
import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import android.text.TextUtils;
import android.util.Xml;

import com.applicake.beanstalkclient.Server;
import com.applicake.beanstalkclient.ServerEnvironment;

public class XmlCreator {

  private XmlSerializer serializer;
  private StringWriter writer;

  public XmlCreator() {

  }

  public String createCommentXML(String revisionId, String commentBody)
      throws IllegalArgumentException, IllegalStateException, IOException {
    serializer = Xml.newSerializer();
    writer = new StringWriter();
    serializer.setOutput(writer);
    // serializer.startDocument("UTF-8", null);
    // open
    serializer.startTag("", "comment");

    addRevision(revisionId);
    addBody(commentBody);
    addFilePath("");
    addLineNumber("");

    serializer.endTag("", "comment");
    serializer.endDocument();

    return writer.toString();

  }

  public String createGitRepositoryCreationXML(String name, String title,
      String colorLabel) throws IllegalArgumentException, IllegalStateException,
      IOException {
    serializer = Xml.newSerializer();
    writer = new StringWriter();
    serializer.setOutput(writer);
    serializer.startDocument("UTF-8", null);
    // open
    serializer.startTag("", "repository");

    addName(name);
    addType("git");
    addTitle(title);
    addColorLabel(colorLabel);

    serializer.endTag("", "repository");
    serializer.endDocument();
    return writer.toString();
  }

  public String createSVNRepositoryCreationXML(String name, String title,
      String colorLabel, boolean b) throws IllegalArgumentException,
      IllegalStateException, IOException {

    serializer = Xml.newSerializer();
    writer = new StringWriter();
    serializer.setOutput(writer);

    serializer.startDocument("UTF-8", null);
    serializer.startTag("", "repository");

    addName(name);
    addType("subversion");
    addTitle(title);
    addCreateStructure(b);
    addColorLabel(colorLabel);

    serializer.endTag("", "repository");
    serializer.endDocument();
    return writer.toString();

  }

  public String createRepositoryModifyXML(String title, String colorLabel)
      throws IllegalArgumentException, IllegalStateException, IOException {

    serializer = Xml.newSerializer();
    writer = new StringWriter();
    serializer.setOutput(writer);

    serializer.startDocument("UTF-8", null);
    serializer.startTag("", "repository");

    addTitle(title);
    addColorLabel(colorLabel);

    serializer.endTag("", "repository");
    serializer.endDocument();
    return writer.toString();

  }

  public String createPasswordChangeXML(String password) throws IllegalArgumentException,
      IllegalStateException, IOException {

    serializer = Xml.newSerializer();
    writer = new StringWriter();
    serializer.setOutput(writer);

    serializer.startDocument("UTF-8", null);
    serializer.startTag("", "user");

    addPassword(password);

    serializer.endTag("", "user");
    serializer.endDocument();
    return writer.toString();
  }

  public String createUserPropertiesChangeXML(String firstName, String lastName,
      String email, String timezone, boolean admin) throws IllegalArgumentException,
      IllegalStateException, IOException {

    serializer = Xml.newSerializer();
    writer = new StringWriter();
    serializer.setOutput(writer);

    serializer.startDocument("UTF-8", null);
    serializer.startTag("", "user");

    addFirstName(firstName);
    addLastName(lastName);
    addEmail(email);
    addTimezone(timezone);
    addAdmin(admin ? "1" : "");

    serializer.endTag("", "user");
    serializer.endDocument();
    return writer.toString();
  }

  public String createNewUserXML(String login, String firstName, String lastName,
      String email, String timezone, boolean admin, String password)
      throws IllegalArgumentException, IllegalStateException, IOException {

    serializer = Xml.newSerializer();
    writer = new StringWriter();
    serializer.setOutput(writer);

    serializer.startDocument("UTF-8", null);
    serializer.startTag("", "user");

    addLogin(login);
    addFirstName(firstName);
    addLastName(lastName);
    addEmail(email);
    addTimezone(timezone);
    addAdmin(admin ? "1" : "");
    addPassword(password);

    serializer.endTag("", "user");
    serializer.endDocument();
    return writer.toString();
  }

  public String createPermissionXML(String userId, String repoId, boolean repoRead,
      boolean repoWrite, boolean deploymentAccess) throws IllegalArgumentException,
      IllegalStateException, IOException {

    serializer = Xml.newSerializer();
    writer = new StringWriter();
    serializer.setOutput(writer);

    serializer.startDocument("UTF-8", null);
    serializer.startTag("", "permission");

    addUserId(userId);
    addRepoId(repoId);
    addRepoReadAccess(repoRead);
    addRepoWriteAccess(repoWrite);
    addDeploymentAccess(deploymentAccess);

    serializer.endTag("", "permission");
    serializer.endDocument();
    return writer.toString();
  }

  public String createAccountPropertiesChangeXML(String name, String timezone)
      throws IllegalArgumentException, IllegalStateException, IOException {
    serializer = Xml.newSerializer();
    writer = new StringWriter();
    serializer.setOutput(writer);

    serializer.startDocument("UTF-8", null);
    serializer.startTag("", "account");

    addName(name);
    addTimezoneAccount(timezone);

    serializer.endTag("", "account");
    serializer.endDocument();
    return writer.toString();
  }

  public String createNewServerEnvironmentXML(ServerEnvironment serverEnvironment)
      throws IllegalArgumentException, IllegalStateException, IOException {
    serializer = Xml.newSerializer();
    writer = new StringWriter();
    serializer.setOutput(writer);

    serializer.startDocument("UTF-8", null);
    serializer.startTag("", "server-environment");

    addName(serverEnvironment.getName());
    addIsAutomatic(serverEnvironment.isAutomatic());

    serializer.endTag("", "server-environment");
    serializer.endDocument();
    return writer.toString();
  }

  public String createModifyServerEnvironmentXML(ServerEnvironment serverEnvironment)
      throws IllegalArgumentException, IllegalStateException, IOException {
    serializer = Xml.newSerializer();
    writer = new StringWriter();
    serializer.setOutput(writer);

    serializer.startDocument("UTF-8", null);
    serializer.startTag("", "server-environment");

    addName(serverEnvironment.getName());
    addIsAutomatic(serverEnvironment.isAutomatic());
    addBranchName(serverEnvironment.getBranchName());

    serializer.endTag("", "server-environment");
    serializer.endDocument();
    return writer.toString();
  }

  public String createNewServerXML(Server server) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer = Xml.newSerializer();
    writer = new StringWriter();
    serializer.setOutput(writer);

    serializer.startDocument("UTF-8", null);
    serializer.startTag("", "server");

    addName(server.getName());
    addLocalPath(server.getLocalPath());
    addRemotePath(server.getRemotePath());
    addRemoteAddr(server.getRemoteAddr());
    addProtocol(server.getProtocol());
    addPort(server.getPort());
    addLogin(server.getLogin());
    addPassword(server.getPassword());
    addUseActiveMode(server.isUseActiveMode());
    addAuthenticateByKey(server.isAuthenticateByKey());
    addUseFeat(server.isUseFeat());
    addPreReleaseHook(server.getPreReleaseHook());
    addPostReleaseHook(server.getPostReleaseHook());

    serializer.endTag("", "server");
    serializer.endDocument();
    return writer.toString();
  }

  public String createModifyServerXML(Server server) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer = Xml.newSerializer();
    writer = new StringWriter();
    serializer.setOutput(writer);

    serializer.startDocument("UTF-8", null);
    serializer.startTag("", "release_server");

    // TODO refactor for better performance
    addName(server.getName());
    addLocalPath(server.getLocalPath());
    addRemotePath(server.getRemotePath());
    addRemoteAddr(server.getRemoteAddr());
    addProtocol(server.getProtocol());
    addPort(server.getPort());
    addLogin(server.getLogin());
    addPassword(server.getPassword());
    addUseActiveMode(server.isUseActiveMode());
    addAuthenticateByKey(server.isAuthenticateByKey());
    addUseFeat(server.isUseFeat());
    addPreReleaseHook(server.getPreReleaseHook());
    addPostReleaseHook(server.getPostReleaseHook());

    serializer.endTag("", "release_server");
    serializer.endDocument();
    return writer.toString();

  }

  public String createNewReleaseXML(String revision, String comment,
      boolean deployFromScratch, int environmentId) throws IllegalArgumentException, IllegalStateException, IOException {
    // TODO Auto-generated method stub
    serializer = Xml.newSerializer();
    writer = new StringWriter();
    serializer.setOutput(writer);
    serializer.startDocument("UTF-8", null);
    serializer.startTag("", "release");
    
    addRevision(revision);
    addComment(comment);
    addDeployFromScratch(deployFromScratch);
    addEnvironmentId(environmentId);
    
    serializer.endTag("", "release");
    serializer.endDocument();
    return writer.toString();
  }

  private void addEnvironmentId(int environmentId) throws IllegalArgumentException, IllegalStateException, IOException {
    if (environmentId != 0){
      serializer.startTag("", "environment_id");
      serializer.text(String.valueOf(environmentId));
      serializer.endTag("", "environment_id");
    }
  }

  private void addDeployFromScratch(boolean deployFromScratch) throws IllegalArgumentException, IllegalStateException, IOException {

    serializer.startTag("", "deploy_from_scratch");
    serializer.text(deployFromScratch ? "true" : "false");
    serializer.endTag("", "deploy_from_scratch");
    
  }

  private void addComment(String comment) throws IllegalArgumentException, IllegalStateException, IOException {
    if (!TextUtils.isEmpty(comment)){
      serializer.startTag("", "comment");
      serializer.text(comment);
      serializer.endTag("", "comment");
    }
    
  }

  private void addPostReleaseHook(String postReleaseHook)
      throws IllegalArgumentException, IllegalStateException, IOException {

    serializer.startTag("", "post_release_hook");
    serializer.text(postReleaseHook);
    serializer.endTag("", "post_release_hook");

  }

  private void addPreReleaseHook(String preReleaseHook) throws IllegalArgumentException,
      IllegalStateException, IOException {

    serializer.startTag("", "pre_release_hook");
    serializer.text(preReleaseHook);
    serializer.endTag("", "pre_release_hook");

  }

  private void addUseFeat(boolean useFeat) throws IllegalArgumentException,
      IllegalStateException, IOException {

    serializer.startTag("", "use_feat");
    serializer.text(useFeat ? "true" : "false");
    serializer.endTag("", "use_feat");

  }

  private void addAuthenticateByKey(boolean authenticateByKey)
      throws IllegalArgumentException, IllegalStateException, IOException {

    serializer.startTag("", "authenticate_by_key");
    serializer.text(authenticateByKey ? "true" : "false");
    serializer.endTag("", "authenticate_by_key");

  }

  private void addUseActiveMode(boolean useActiveMode) throws IllegalArgumentException,
      IllegalStateException, IOException {

    serializer.startTag("", "use_active_mode");
    serializer.text(useActiveMode ? "true" : "false");
    serializer.endTag("", "use_active_mode");

  }

  private void addPort(int port) throws IllegalArgumentException, IllegalStateException,
      IOException {

    serializer.startTag("", "port");
    serializer.text(String.valueOf(port));
    serializer.endTag("", "port");

  }

  private void addProtocol(String protocol) throws IllegalArgumentException,
      IllegalStateException, IOException {

    serializer.startTag("", "protocol");
    serializer.text(protocol);
    serializer.endTag("", "protocol");

  }

  private void addRemoteAddr(String remoteAddr) throws IllegalArgumentException,
      IllegalStateException, IOException {

    serializer.startTag("", "remote_addr");
    serializer.text(remoteAddr);
    serializer.endTag("", "remote_addr");

  }

  private void addRemotePath(String remotePath) throws IllegalArgumentException,
      IllegalStateException, IOException {

    serializer.startTag("", "remote_path");
    serializer.text(remotePath);
    serializer.endTag("", "remote_path");

  }

  private void addLocalPath(String localPath) throws IllegalArgumentException,
      IllegalStateException, IOException {

    serializer.startTag("", "local_path");
    serializer.text(localPath);
    serializer.endTag("", "local_path");

  }

  private void addBranchName(String branchName) throws IllegalArgumentException,
      IllegalStateException, IOException {
    if (branchName != null && !branchName.equals("")) {
      serializer.startTag("", "branch_name");
      serializer.text(branchName);
      serializer.endTag("", "branch_name");
    }

  }

  private void addIsAutomatic(boolean automatic) throws IllegalArgumentException,
      IllegalStateException, IOException {

    serializer.startTag("", "automatic");
    serializer.text(automatic ? "true" : "false");
    serializer.endTag("", "automatic");

  }

  private void addTimezone(String timezone) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "timezone");
    serializer.text(timezone);
    serializer.endTag("", "timezone");

  }

  private void addTimezoneAccount(String timezone) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "time-zone");
    serializer.text(timezone);
    serializer.endTag("", "time-zone");

  }

  private void addDeploymentAccess(boolean deploymentAccess)
      throws IllegalArgumentException, IllegalStateException, IOException {
    serializer.startTag("", "full-deployments-access");
    serializer.attribute("", "type", "boolean");
    serializer.text(String.valueOf(deploymentAccess));
    serializer.endTag("", "full-deployments-access");

  }

  private void addRepoWriteAccess(boolean repoWrite) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "write");
    serializer.attribute("", "type", "boolean");
    serializer.text(String.valueOf(repoWrite));
    serializer.endTag("", "write");

  }

  private void addRepoReadAccess(boolean repoRead) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "read");
    serializer.attribute("", "type", "boolean");
    serializer.text(String.valueOf(repoRead));
    serializer.endTag("", "read");

  }

  private void addRepoId(String repoId) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "repository-id");
    serializer.attribute("", "type", "integer");
    serializer.text(String.valueOf(repoId));
    serializer.endTag("", "repository-id");

  }

  private void addUserId(String userId) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "user-id");
    serializer.attribute("", "type", "integer");
    serializer.text(String.valueOf(userId));
    serializer.endTag("", "user-id");

  }

  private void addLogin(String login) throws IllegalArgumentException,
      IllegalStateException, IOException {

    serializer.startTag("", "login");
    serializer.text(login);
    serializer.endTag("", "login");

  }

  private void addAdmin(String admin) throws IllegalArgumentException,
      IllegalStateException, IOException {

    serializer.startTag("", "admin");
    serializer.text(admin);
    serializer.endTag("", "admin");

  }

  private void addEmail(String email) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "email");
    serializer.text(email);
    serializer.endTag("", "email");
  }

  private void addLastName(String lastName) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "last_name");
    serializer.text(lastName);
    serializer.endTag("", "last_name");

  }

  private void addFirstName(String firstName) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "first_name");
    serializer.text(firstName);
    serializer.endTag("", "first_name");

  }

  private void addPassword(String password) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "password");
    serializer.text(password);
    serializer.endTag("", "password");

  }

  private void addCreateStructure(boolean b) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "create_structure");
    serializer.attribute("", "type", "boolean");
    serializer.text(String.valueOf(b));
    serializer.endTag("", "create_structure");

  }

  private void addColorLabel(String colorLabel) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "color_label");
    serializer.text(colorLabel);
    serializer.endTag("", "color_label");

  }

  private void addTitle(String title) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "title");
    serializer.text(title);
    serializer.endTag("", "title");

  }

  private void addType(String type) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "type_id");
    serializer.text(type);
    serializer.endTag("", "type_id");

  }

  private void addRevision(String revId) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "revision");
    // serializer.attribute("", "type", "integer");
    serializer.text(revId);
    serializer.endTag("", "revision");
  }

  private void addBody(String body) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "body");
    serializer.text(body);
    serializer.endTag("", "body");
  }

  private void addFilePath(String filePath) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "file-path");
    serializer.text(filePath);
    serializer.endTag("", "file-path");
  }

  private void addLineNumber(String lineNumeber) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "line-number");
    serializer.text(lineNumeber);
    serializer.endTag("", "line-number");
  }

  private void addName(String name) throws IllegalArgumentException,
      IllegalStateException, IOException {
    serializer.startTag("", "name");
    serializer.text(name);
    serializer.endTag("", "name");
  }

}
