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
import com.applicake.beanstalkclient.Plan;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.handlers.AccountHandler;
import com.applicake.beanstalkclient.handlers.ChangesetHandler;
import com.applicake.beanstalkclient.handlers.CommentsHandler;
import com.applicake.beanstalkclient.handlers.ErrorHandler;
import com.applicake.beanstalkclient.handlers.PermissionsHandler;
import com.applicake.beanstalkclient.handlers.PlanHandler;
import com.applicake.beanstalkclient.handlers.RepositoriesHandler;
import com.applicake.beanstalkclient.handlers.UserHandler;

public class XmlParser {

	private static XMLReader initializeReader() throws XMLParserException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			parser = factory.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();
			return xmlReader;
		} catch (ParserConfigurationException e) {
			throw new XMLParserException(e);
		} catch (SAXException e) {
			throw new XMLParserException(e);
		}

	}

	public static ArrayList<Changeset> parseChangesetList(String xml)
			throws XMLParserException {

		XMLReader xmlReader = initializeReader();

		ChangesetHandler changesetHandler = new ChangesetHandler();
		// set handler
		xmlReader.setContentHandler(changesetHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		try {
			xmlReader.parse(is);

		} catch (IOException e) {
			throw new XMLParserException(e);
		} catch (SAXException e) {
			throw new XMLParserException(e);
		}

		return changesetHandler.retrieveChangesetList();

	}

	public static ArrayList<User> parseUserList(String xml) throws XMLParserException {

		XMLReader xmlReader = initializeReader();

		UserHandler userHandler = new UserHandler();
		// set handler
		xmlReader.setContentHandler(userHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		try {
			xmlReader.parse(is);

		} catch (IOException e) {
			throw new XMLParserException(e);
		} catch (SAXException e) {
			throw new XMLParserException(e);
		}

		return userHandler.retrieveUserList();

	}

	public static User parseCurrentUser(String currentUserXML) throws XMLParserException {
		XMLReader xmlReader = initializeReader();

		UserHandler userHandler = new UserHandler();
		// set handler
		xmlReader.setContentHandler(userHandler);
		// parse
		StringReader sr = new StringReader(currentUserXML);
		InputSource is = new InputSource(sr);
		try {
			xmlReader.parse(is);

		} catch (IOException e) {
			throw new XMLParserException(e);
		} catch (SAXException e) {
			throw new XMLParserException(e);
		}

		return userHandler.retrieveUser();
	}

	public static HashMap<Integer, Plan> parsePlan(String planXML)
			throws XMLParserException {
		XMLReader xmlReader = initializeReader();

		PlanHandler planHandler = new PlanHandler();
		// set handler
		xmlReader.setContentHandler(planHandler);
		// parse
		StringReader sr = new StringReader(planXML);
		InputSource is = new InputSource(sr);
		try {
			xmlReader.parse(is);

		} catch (IOException e) {
			throw new XMLParserException(e);
		} catch (SAXException e) {
			throw new XMLParserException(e);
		}

		return planHandler.retrievePlanMap();
	}

	public static ArrayList<Repository> parseRepositoryList(String xml)
			throws XMLParserException {

		XMLReader xmlReader = initializeReader();

		RepositoriesHandler repositoriesHandler = new RepositoriesHandler();
		// set handler
		xmlReader.setContentHandler(repositoriesHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		try {
			xmlReader.parse(is);

		} catch (IOException e) {
			throw new XMLParserException(e);
		} catch (SAXException e) {
			throw new XMLParserException(e);
		}

		return repositoriesHandler.retrieveRepositoryList();

	}

	public static Repository parseRepository(String xml) throws XMLParserException {

		XMLReader xmlReader = initializeReader();

		RepositoriesHandler repositoriesHandler = new RepositoriesHandler();
		// set handler
		xmlReader.setContentHandler(repositoriesHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		try {
			xmlReader.parse(is);
		} catch (IOException e) {
			throw new XMLParserException(e);
		} catch (SAXException e) {
			throw new XMLParserException(e);
		}

		return repositoriesHandler.retrieveRepository();

	}

	public static HashMap<Integer, Repository> parseRepositoryHashMap(String xml)
			throws XMLParserException {

		XMLReader xmlReader = initializeReader();

		RepositoriesHandler repositoriesHandler = new RepositoriesHandler();
		// set handler
		xmlReader.setContentHandler(repositoriesHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		try {
			xmlReader.parse(is);
		} catch (IOException e) {
			throw new XMLParserException(e);
		} catch (SAXException e) {
			throw new XMLParserException(e);
		}

		return repositoriesHandler.retrieveRepositoryHashMap();

	}

	public static ArrayList<Permission> parsePermissionList(String xml)
			throws XMLParserException {
		XMLReader xmlReader = initializeReader();

		PermissionsHandler permissionsHandler = new PermissionsHandler();
		// set handler
		xmlReader.setContentHandler(permissionsHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		try {
			xmlReader.parse(is);
			return permissionsHandler.retrievePermissionList();
		} catch (IOException e) {
			throw new XMLParserException(e);
		} catch (SAXException e) {
			throw new XMLParserException(e);
		}

	}

	public static HashMap<Integer, Permission> parseRepoIdToPermissionHashMap(String xml)
			throws XMLParserException {
		XMLReader xmlReader = initializeReader();

		PermissionsHandler permissionsHandler = new PermissionsHandler();
		// set handler
		xmlReader.setContentHandler(permissionsHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		try {
			xmlReader.parse(is);
		} catch (IOException e) {
			throw new XMLParserException(e);
		} catch (SAXException e) {
			throw new XMLParserException(e);
		}

		return permissionsHandler.retrievePermissionHashMap();

	}

	public static Account parseAccountInfo(String xml) throws XMLParserException {
		XMLReader xmlReader = initializeReader();

		AccountHandler accountHandler = new AccountHandler();
		// set handler
		xmlReader.setContentHandler(accountHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		try {
			xmlReader.parse(is);
			return accountHandler.retrieveAccount();
		} catch (IOException e) {
			throw new XMLParserException(e);
		} catch (SAXException e) {
			throw new XMLParserException(e);
		}

	}

	public static ArrayList<Comment> parseCommentList(String xml)
			throws XMLParserException {
		XMLReader xmlReader = initializeReader();

		CommentsHandler commentsHandler = new CommentsHandler();
		// set handler
		xmlReader.setContentHandler(commentsHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		try {
			xmlReader.parse(is);
		} catch (IOException e) {
			throw new XMLParserException(e);
		} catch (SAXException e) {
			throw new XMLParserException(e);
		}

		return commentsHandler.retrieveCommentList();

	}

	public static Comment parseComment(String xml) throws XMLParserException {
		XMLReader xmlReader = initializeReader();

		CommentsHandler commentsHandler = new CommentsHandler();
		// set handler
		xmlReader.setContentHandler(commentsHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		try {
			xmlReader.parse(is);
		} catch (IOException e) {
			throw new XMLParserException(e);
		} catch (SAXException e) {
			throw new XMLParserException(e);
		}

		return commentsHandler.retrieveComment();

	}

	public static ArrayList<String> parseErrors(String xml) throws XMLParserException {
		XMLReader xmlReader = initializeReader();

		ErrorHandler errorHandler = new ErrorHandler();
		// set handler
		xmlReader.setContentHandler(errorHandler);
		// parse
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);

		try {
			xmlReader.parse(is);
			return errorHandler.retrieveErrorList();
		} catch (IOException e) {
			throw new XMLParserException(e);
		} catch (SAXException e) {
			throw new XMLParserException(e);
		}

	}

	public static class XMLParserException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Exception cause;

		public XMLParserException(Exception e) {
			super();
			cause = e;
		}

		public Exception getException() {
			// TODO Auto-generated method stub
			return cause;
		}
	}

}
