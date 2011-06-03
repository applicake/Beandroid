package com.applicake.beanstalkclient.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.applicake.beanstalkclient.Account;
import com.applicake.beanstalkclient.Changeset;
import com.applicake.beanstalkclient.Comment;
import com.applicake.beanstalkclient.Permission;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.handlers.AccountHandler;
import com.applicake.beanstalkclient.handlers.ChangesetHandler;
import com.applicake.beanstalkclient.handlers.CommentsHandler;
import com.applicake.beanstalkclient.handlers.ErrorHandler;
import com.applicake.beanstalkclient.handlers.PermissionsHandler;
import com.applicake.beanstalkclient.handlers.RepositoriesHandler;
import com.applicake.beanstalkclient.handlers.UserHandler;

public class XmlParser {

	private static XMLReader initializeReader() throws ParserConfigurationException,
			SAXException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		return xmlReader;

	}

	public static ArrayList<Changeset> parseChangesetList(String xml)
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

	public static ArrayList<User> parseUserList(String xml) throws ParserConfigurationException,
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

	public static ArrayList<Repository> parseRepositoryList(String xml)
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
	
	public static Repository parseRepository(String xml)
	throws ParserConfigurationException, SAXException, IOException {
		
		XMLReader xmlReader = initializeReader();
		
		RepositoriesHandler repositoriesHandler = new RepositoriesHandler();
		// set handler
		xmlReader.setContentHandler(repositoriesHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		xmlReader.parse(is);
		
		return repositoriesHandler.retrieveRepository();
		
	}

	public static HashMap<Integer, Repository> parseRepositoryHashMap(String xml)
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
	
	public static ArrayList<Permission> parsePermissionList(String xml) throws SAXException, IOException, ParserConfigurationException{
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
	
	public static HashMap<Integer, Permission> parseRepoIdToPermissionHashMap(String xml) throws SAXException, IOException, ParserConfigurationException{
		XMLReader xmlReader = initializeReader();
		
		PermissionsHandler permissionsHandler = new PermissionsHandler();
		// set handler
		xmlReader.setContentHandler(permissionsHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		xmlReader.parse(is);
		
		return permissionsHandler.retrievePermissionHashMap();
		
	}
	
	
	public static Account parseAccountInfo(String xml) throws SAXException, IOException, ParserConfigurationException{
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

	public static ArrayList<Comment> parseCommentList(String xml) throws SAXException, IOException, ParserConfigurationException {
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

	public static Comment parseComment(String xml) throws SAXException, IOException, ParserConfigurationException {
		XMLReader xmlReader = initializeReader();
		
		CommentsHandler commentsHandler = new CommentsHandler();
		// set handler
		xmlReader.setContentHandler(commentsHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		xmlReader.parse(is);
		
		return commentsHandler.retrieveComment();
		
	}
	
	public static ArrayList<String> parseErrors(String xml) throws IOException, ParserConfigurationException, SAXException {
		XMLReader xmlReader = initializeReader();
		
		ErrorHandler errorHandler = new ErrorHandler();
		// set handler
		xmlReader.setContentHandler(errorHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		xmlReader.parse(is);
		
		return errorHandler.retrieveErrorList();
		
	}

}
