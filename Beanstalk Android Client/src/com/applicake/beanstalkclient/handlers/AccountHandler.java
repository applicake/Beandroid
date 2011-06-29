package com.applicake.beanstalkclient.handlers;

import java.text.ParseException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.applicake.beanstalkclient.Account;

public class AccountHandler extends DefaultHandler {

	private Account account;
	private StringBuilder buffer = new StringBuilder();

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		buffer.setLength(0);

		if (localName == "account") {
			account = new Account();
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (account != null) {

			if (localName == "created-at") {
				try {
					account.setCreatedAt(buffer.toString());
				} catch (ParseException e) {
					throw new SAXException(e);
				}
			}

			if (localName == "id") {
				try {
					account.setId(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}

			}

			if (localName == "name") {
				account.setName(buffer.toString());
			}

			if (localName == "owner-id") {
				try {
					account.setOwnerId(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}
			}

			if (localName == "plan-id") {
				try {
					account.setPlanId(Integer.parseInt(buffer.toString()));
				} catch (NumberFormatException nfe) {
					throw new SAXException(nfe);
				}
			}

			if (localName == "suspended") {
				account.setSuspended(Boolean.parseBoolean(buffer.toString()));
			}

			if (localName == "third-level-domain") {
				account.setThirdLevelDomain(buffer.toString());
			}

			if (localName == "time-zone") {
				account.setTimeZone(buffer.toString());
			}

			if (localName == "updated-at") {
				try {
					account.setUpdatedAt(buffer.toString());
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

	public Account retrieveAccount() throws SAXException {
		if (account == null)
			throw new SAXException("Error while parsing account info");
		return account;
	}
}
