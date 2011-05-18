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

	public String createCommentXML(String revisionId, String commentBody) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer = Xml.newSerializer();
		writer = new StringWriter();
		serializer.setOutput(writer);
//		serializer.startDocument("UTF-8", null);
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
			String colorLabel) throws IllegalArgumentException, IllegalStateException, IOException {
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
			String colorLabel, boolean b) throws IllegalArgumentException, IllegalStateException, IOException {
		
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
	

	public String createRepositoryModifyXML(String title, String colorLabel) throws IllegalArgumentException, IllegalStateException, IOException {
		
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


	


	private void addCreateStructure(boolean b) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", "create_structure");
		serializer.attribute("", "type", "boolean");
		serializer.text(String.valueOf(b));
		serializer.endTag("", "create_structure");
		
	}

	private void addColorLabel(String colorLabel) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", "color_label");
		serializer.text(colorLabel);
		serializer.endTag("", "color_label");
		
	}

	private void addTitle(String title) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", "title");
		serializer.text(title);
		serializer.endTag("", "title");
		
	}

	private void addType(String type) throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", "type_id");
		serializer.text(type);
		serializer.endTag("", "type_id");
		
	}

	private void addRevision(String revId) throws IllegalArgumentException,
			IllegalStateException, IOException {
		serializer.startTag("", "revision");
//		serializer.attribute("", "type", "integer");
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
	
	private void addName(String name) throws IllegalArgumentException, IllegalStateException, IOException{
		serializer.startTag("", "name");
		serializer.text(name);
		serializer.endTag("", "name");
	}


}
