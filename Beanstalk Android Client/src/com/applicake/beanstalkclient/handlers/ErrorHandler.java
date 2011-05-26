package com.applicake.beanstalkclient.handlers;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ErrorHandler extends DefaultHandler {

	private String error;
	private ArrayList<String> errorList;
	private StringBuilder buffer = new StringBuilder();

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		buffer.setLength(0);

		if (localName == "errors") {
			errorList = new ArrayList<String>();
		}

		if (localName == "error") {
			error = new String();
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (error != null) {

			if (localName == "error") {
				error = buffer.toString();
				errorList.add(error);
			}

		}

	}

	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(ch, start, length);
	}

	public ArrayList<String> retrieveErrorList() throws SAXException {
		return errorList;
	}
}
