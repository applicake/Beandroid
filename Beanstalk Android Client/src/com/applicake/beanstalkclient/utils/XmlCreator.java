package com.applicake.beanstalkclient.utils;

import java.io.IOException;
import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

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

	public String createPasswordChangeXML(String password)
			throws IllegalArgumentException, IllegalStateException, IOException {

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
			String email, boolean admin) throws IllegalArgumentException,
			IllegalStateException, IOException {

		serializer = Xml.newSerializer();
		writer = new StringWriter();
		serializer.setOutput(writer);

		serializer.startDocument("UTF-8", null);
		serializer.startTag("", "user");

		addFirstName(firstName);
		addLastName(lastName);
		addEmail(email);
		addAdmin(admin ? "1" : "");

		serializer.endTag("", "user");
		serializer.endDocument();
		return writer.toString();
	}


	public String createNewUserXML(String login, String firstName, String lastName,
			String email, boolean admin, String password)
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
		addAdmin(admin ? "1" : "");
		addPassword(password);

		serializer.endTag("", "user");
		serializer.endDocument();
		return writer.toString();
	}
	
	public String createPermissionXML(String userId, String repoId, boolean repoRead,
			boolean repoWrite, boolean deploymentAccess)
	throws IllegalArgumentException, IllegalStateException, IOException {
		
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


	private void addDeploymentAccess(boolean deploymentAccess) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", "full-deployments-access");
		serializer.attribute("", "type", "boolean");
		serializer.text(String.valueOf(deploymentAccess));
		serializer.endTag("", "full-deployments-access");
		
	}

	private void addRepoWriteAccess(boolean repoWrite) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", "write");
		serializer.attribute("", "type", "boolean");
		serializer.text(String.valueOf(repoWrite));
		serializer.endTag("", "write");
		
	}

	private void addRepoReadAccess(boolean repoRead) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", "read");
		serializer.attribute("", "type", "boolean");
		serializer.text(String.valueOf(repoRead));
		serializer.endTag("", "read");
		
	}

	private void addRepoId(String repoId) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", "repository-id");
		serializer.attribute("", "type", "integer");
		serializer.text(String.valueOf(repoId));
		serializer.endTag("", "repository-id");
		
	}

	private void addUserId(String userId) throws IllegalArgumentException, IllegalStateException, IOException {
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
