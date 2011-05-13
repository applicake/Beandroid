package com.applicake.beanstalkclient.handlers;

import java.text.ParseException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.applicake.beanstalkclient.Comment;

public class CommentsHandler extends DefaultHandler {
	private ArrayList<Comment> commentList;
	private Comment comment;
	private StringBuilder buffer = new StringBuilder();

	@Override
	public void startElement(String namespaceURI, String localName, String qName,
			Attributes atts) throws SAXException {
		buffer.setLength(0);

		if (localName.equals("comments")) {
			commentList = new ArrayList<Comment>();
		}
		if (localName.equals("comment")) {
			comment = new Comment();
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (comment != null) {
			if (localName.equals("comment")) {
				commentList.add(comment);
			}

			if (localName.equals("account-id")) {

				try {
					comment.setAccountId(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}
			}

			if (localName.equals("author-email")) {
				comment.setAuthorEmail(buffer.toString());
			}

			if (localName.equals("author-id")) {

				try {
					comment.setAuthorId(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}
			}

			if (localName.equals("author-login")) {
				comment.setAuthorLogin(buffer.toString());
			}

			if (localName.equals("author-name")) {
				comment.setAuthorName(buffer.toString());
			}

			if (localName.equals("body")) {
				comment.setBody(buffer.toString());
			}

			if (localName == "created-at") {
				try {
					comment.setCreatedAt(buffer.toString());
				} catch (ParseException e) {
					throw new SAXException(e);
				}
			}

			if (localName.equals("file-path")) {
				comment.setFilePath(buffer.toString());
			}

			if (localName.equals("id")) {

				try {
					comment.setId(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}
			}

			if (localName.equals("line-number")) {
				comment.setLineNumber(buffer.toString());
			}

			if (localName.equals("rendered-body")) {
				comment.setRenderedBody(buffer.toString());
			}

			if (localName.equals("repository-id")) {

				try {
					comment.setRepositoryId(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}
			}

			if (localName.equals("revision")) {
				comment.setRevision(buffer.toString());
			}

			if (localName == "updated-at") {
				try {
					comment.setUpdatedAt(buffer.toString());
				} catch (ParseException e) {
					throw new SAXException(e);
				}
			}
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(ch, start, length);
	}

	public ArrayList<Comment> retrieveCommentList() {
		if (commentList == null) return new ArrayList<Comment>();
//			throw new SAXException("Error while parsing comment list");
		return commentList;

	}
}
