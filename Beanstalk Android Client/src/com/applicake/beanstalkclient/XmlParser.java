package com.applicake.beanstalkclient;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.applicake.beanstalkclient.handlers.ChangesetHandler;
import com.applicake.beanstalkclient.handlers.RepositoriesHandler;
import com.applicake.beanstalkclient.handlers.UserHandler;

public class XmlParser {

	private XMLReader initializeReader() throws ParserConfigurationException,
			SAXException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		return xmlReader;

	}

	public ArrayList<Changeset> parseChangesetList(String xml)
			throws ParserConfigurationException, SAXException, IOException {

		XMLReader xmlReader = initializeReader();

		ChangesetHandler changesetHandler = new ChangesetHandler();
		// set handler
		xmlReader.setContentHandler(changesetHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		xmlReader.parse(is);

		return changesetHandler.retrieveChangesetList();

	}

	public ArrayList<User> parseUserList(String xml) throws ParserConfigurationException,
			SAXException, IOException {

		XMLReader xmlReader = initializeReader();

		UserHandler userHandler = new UserHandler();
		// set handler
		xmlReader.setContentHandler(userHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		xmlReader.parse(is);

		return userHandler.retrieveUserList();

	}

	public ArrayList<Repository> parseRepositoryList(String xml)
			throws ParserConfigurationException, SAXException, IOException {

		XMLReader xmlReader = initializeReader();

		RepositoriesHandler repositoriesHandler = new RepositoriesHandler();
		// set handler
		xmlReader.setContentHandler(repositoriesHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		xmlReader.parse(is);

		return repositoriesHandler.retrieveRepositoryList();

	}

}
