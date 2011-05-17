package com.applicake.beanstalkclient.utils;

import java.io.IOException;
import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class XmlCreator {

	private XmlSerializer serializer;
	private StringWriter writer;

	public XmlCreator() {
		serializer = Xml.newSerializer();
		writer = new StringWriter();

	}

	public String createCommentXML(String revisionId, String commentBody) throws IllegalArgumentException, IllegalStateException, IOException {

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

	private void addRevision(String revId) throws IllegalArgumentException,
			IllegalStateException, IOException {
		serializer.startTag("", "revision");
		serializer.attribute("", "type", "integer");
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

}
