package com.applicake.beanstalkclient.handlers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.applicake.beanstalkclient.Repository;

public class RepositoriesHandler extends DefaultHandler {

	private ArrayList<Repository> repositoryList;
	private Repository repository;
	private StringBuilder buffer = new StringBuilder();

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		buffer.setLength(0);

		if (localName == "repositories") {
			repositoryList = new ArrayList<Repository>();
		}

		if (localName == "repository") {
			repository = new Repository();
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (repository != null) {
			if (localName == "repository") {
				if (repositoryList != null) {
					repositoryList.add(repository);
				}
			}
			if (localName == "account-id") {
				try {
					repository.setAccountId(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}
			}
			if (localName == "anonymous") {

				repository.setAnonymous(Boolean.parseBoolean(buffer.toString()));

			}

			if (localName == "color-label") {
				repository.setColorLabel(buffer.toString());
			}

			if (localName == "created-at") {
				try {
					repository.setCreatedAt(buffer.toString());
				} catch (ParseException e) {
					throw new SAXException(e);
				}
			}

			if (localName == "id") {
				try {
					repository.setId(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}

			}

			if (localName == "last-commit-at") {
				if (buffer.length() != 0) {
					try {
						repository.setLastCommitAt(buffer.toString());
					} catch (ParseException e) {
						throw new SAXException(e);
					}
				}
			}

			if (localName == "name") {
				repository.setName(buffer.toString());
			}

			if (localName == "revision") {
				try {
					repository.setRevision(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}
			}

			if (localName == "storage-used-bytes") {
				try {
					repository.setStorageUsedBytes(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}
			}

			if (localName == "title") {
				repository.setTitle(buffer.toString());
			}

			if (localName == "type") {
				repository.setType(buffer.toString());
			}

			if (localName == "updated-at") {
				try {
					repository.setUpdatedAt(buffer.toString());
				} catch (ParseException e) {
					throw new SAXException(e);
				}
			}

			if (localName == "vcs") {
				repository.setVcs(buffer.toString());
			}

			if (localName == "default-branch") {
				repository.setDefaultBranch(buffer.toString());
			}

		}

	}

	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(ch, start, length);
	}

	public ArrayList<Repository> retrieveRepositoryList() {
		return repositoryList;
	}

	public HashMap<Integer, Repository> retrieveRepositoryHashMap() {
		HashMap<Integer, Repository> repoHashMap = new HashMap<Integer, Repository>();
		for (Repository r : repositoryList){
			repoHashMap.put(r.getId(), r);
		}
		return repoHashMap;
	}

}
