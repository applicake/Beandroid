package com.applicake.beanstalkclient;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.applicake.beanstalkclient.handlers.AccountHandler;
import com.applicake.beanstalkclient.handlers.ChangesetHandler;
import com.applicake.beanstalkclient.handlers.CommentsHandler;
import com.applicake.beanstalkclient.handlers.PermissionsHandler;
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

	public HashMap<Integer, Repository> parseRepositoryHashMap(String xml)
	throws ParserConfigurationException, SAXException, IOException {
		
		XMLReader xmlReader = initializeReader();
		
		RepositoriesHandler repositoriesHandler = new RepositoriesHandler();
		// set handler
		xmlReader.setContentHandler(repositoriesHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		xmlReader.parse(is);
		
		return repositoriesHandler.retrieveRepositoryHashMap();
		
	}
	
	public ArrayList<Permission> parsePermissionList(String xml) throws SAXException, IOException, ParserConfigurationException{
		XMLReader xmlReader = initializeReader();

		PermissionsHandler permissionsHandler = new PermissionsHandler();
		// set handler
		xmlReader.setContentHandler(permissionsHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		xmlReader.parse(is);

		return permissionsHandler.retrievePermissionList();

		
		
	}
	
	public Account parseAccountInfo(String xml) throws SAXException, IOException, ParserConfigurationException{
		XMLReader xmlReader = initializeReader();

		AccountHandler accountHandler = new AccountHandler();
		// set handler
		xmlReader.setContentHandler(accountHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		xmlReader.parse(is);

		return accountHandler.retrieveAccount();

		
	}

	public ArrayList<Comment> parseCommentList(String xml) throws SAXException, IOException, ParserConfigurationException {
		XMLReader xmlReader = initializeReader();

		CommentsHandler commentsHandler = new CommentsHandler();
		// set handler
		xmlReader.setContentHandler(commentsHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		xmlReader.parse(is);

		return commentsHandler.retrieveCommentList();
		
	}

}
