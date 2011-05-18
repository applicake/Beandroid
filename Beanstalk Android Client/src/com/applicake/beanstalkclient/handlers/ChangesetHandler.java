package com.applicake.beanstalkclient.handlers;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.applicake.beanstalkclient.Changeset;

public class ChangesetHandler extends DefaultHandler {

	private ArrayList<Changeset> changesetList;
	private Changeset changeset;
	private StringBuilder buffer = new StringBuilder();

	@Override
	public void startElement(String namespaceURI, String localName, String qName,
			Attributes atts) throws SAXException {
		buffer.setLength(0);

		if (localName.equals("revision-caches")) {
			changesetList = new ArrayList<Changeset>();
		}
		if (localName.equals("revision-cache")) {
			changeset = new Changeset();
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (changeset != null) {
			if (localName.equals("revision-cache")) {
				changesetList.add(changeset);
			}

			if (localName.equals("account-id")) {

				try {
					changeset.setAccountId(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}
			}

			if (localName.equals("author")) {
				changeset.setAuthor(buffer.toString());
			}

			if (localName.equals("changed-dirs")) {
				changeset.setChangedDirs(buffer.toString());

			}

			if (localName.equals("changed-files")) {
				changeset.setChangedFiles(buffer.toString());
			}

			if (localName.equals("changed-properties")) {
				changeset.setChangedProperties(buffer.toString());
			}

			if (localName.equals("email")) {
				changeset.setEmail(buffer.toString());

			}

			if (localName.equals("hash-id")) {
				changeset.setHashId(buffer.toString());
			}

			if (localName.equals("message")) {
				changeset.setMessage(buffer.toString());
			}

			if (localName.equals("repository-id")) {
				changeset.setRepositoryId(Integer.parseInt(buffer.toString()));
			}

			if (localName.equals("revision")) {

				changeset.setRevision(buffer.toString());

			}

			if (localName.equals("time")) {
				changeset.setTime(buffer.toString());
			}

			if (localName.equals("too-large")) {
				if (buffer.toString() == "true" || buffer.toString() == "false") {
					changeset.setTooLarge(Boolean.parseBoolean(buffer.toString()));
				} else {
					changeset.setTooLarge(false);
				}

			}

			if (localName.equals("user-id")) {
				if (buffer.length() != 0) {

					try {
						changeset.setUserId(Integer.parseInt(buffer.toString()));
					} catch (NumberFormatException nfe) {
						throw new SAXException(nfe);
					}
				}
			}

		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(ch, start, length);
	}

	public ArrayList<Changeset> retrieveChangesetList() {
		if (changesetList != null) {
			return changesetList;
		} else
			return new ArrayList<Changeset>();

	}

}
