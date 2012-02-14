package com.applicake.beanstalkclient.handlers;

import java.text.ParseException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.applicake.beanstalkclient.ServerEnvironment;

/* 
 *   <server-environment>
 <account-id type="integer">4</account-id>
 <automatic type="boolean">true</automatic>
 <branch-name>production</branch-name>
 <created-at type="datetime">2010-07-01T13:58:55+08:00</created-at>
 <current-version>319dded3c14a2f901686b98fc58d5026f102ba65</current-version>
 <id type="integer">6</id>
 <name>Mascarpone</name>
 <repository-id type="integer">58</repository-id>
 <updated-at type="datetime">2010-08-09T11:32:53+08:00</updated-at>
 </server-environment>
 */

public class ServerEnvironmentHandler extends DefaultHandler {

  private ArrayList<ServerEnvironment> serverEnvironmentList;
  private ServerEnvironment serverEnvironment;
  private StringBuilder buffer = new StringBuilder();

  @Override
  public void startElement(String uri, String localName, String qName,
      Attributes attributes) throws SAXException {
    buffer.setLength(0);

    if (localName == "server-environments") {
      serverEnvironmentList = new ArrayList<ServerEnvironment>();
    }

    if (localName == "server-environment") {
      serverEnvironment = new ServerEnvironment();
    }

  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (serverEnvironment != null) {
      if (localName == "server-environment") {
        if (serverEnvironmentList != null) {
          serverEnvironmentList.add(serverEnvironment);
        }
      }

      if (localName == "account-id") {
        try {
          serverEnvironment.setAccountId(Integer.parseInt(buffer.toString()));
        } catch (NumberFormatException nfe) {
          throw new SAXException(nfe);
        }
      }
      if (localName == "automatic") {

        serverEnvironment.setAutomatic(Boolean.parseBoolean(buffer.toString()));

      }

      if (localName == "branch-name") {
        serverEnvironment.setBranchName(buffer.toString());
      }

      if (localName == "created-at") {
        try {
          serverEnvironment.setCreatedAt(buffer.toString());
        } catch (ParseException e) {
          throw new SAXException(e);
        }
      }

      if (localName == "current-version") {
        serverEnvironment.setCurrentVersion(buffer.toString());
      }

      if (localName == "id") {
        try {
          serverEnvironment.setId(Integer.parseInt(buffer.toString()));
        } catch (NumberFormatException nfe) {
          throw new SAXException(nfe);
        }
      }

      if (localName == "name") {
        serverEnvironment.setName(buffer.toString());
      }

      if (localName == "repository-id") {
        try {
          serverEnvironment.setRepositoryId(Integer.parseInt(buffer.toString()));
        } catch (NumberFormatException nfe) {
          throw new SAXException(nfe);
        }
      }

      if (localName == "updated-at") {
        try {
          serverEnvironment.setUpdatedAt(buffer.toString());
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

  public ArrayList<ServerEnvironment> retrieveServerEnvironmentList() {
    return serverEnvironmentList;
  }

}
