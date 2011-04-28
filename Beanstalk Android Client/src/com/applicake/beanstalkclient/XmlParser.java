package com.applicake.beanstalkclient;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.applicake.beanstalkclient.handlers.ChangesetHandler;


public class XmlParser {

	private XMLReader initializeReader() throws ParserConfigurationException, SAXException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		
		return xmlReader;
		
	}
	
	public ArrayList<Changeset> parseChangesetList(String xml) {
		
		try {
			XMLReader xmlReader = initializeReader();
			
			ChangesetHandler changesetHandler = new ChangesetHandler();
			//set handler
			xmlReader.setContentHandler(changesetHandler);
			//parse
			StringReader sr = new StringReader(xml);
			InputSource is = new InputSource (sr);
			xmlReader.parse(is);
			
			return null; //return parsed changeset list
			
		}
		catch (Exception e) {
			// TODO handle exceptions in XML parsing
			return null;
		}
		
	}

}
