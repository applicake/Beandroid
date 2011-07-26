package com.applicake.beanstalkclient.handlers;

import java.text.ParseException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.applicake.beanstalkclient.Server;

public class ServersHandler extends DefaultHandler {

  private ArrayList<Server> serverList;
  private Server server;
  private StringBuilder buffer = new StringBuilder();

  @Override
  public void startElement(String uri, String localName, String qName,
      Attributes attributes) throws SAXException {
    buffer.setLength(0);

    if (localName == "release-servers") {
      serverList = new ArrayList<Server>();
    }

    if (localName == "release-server") {
      server = new Server();
    }

  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (server != null) {
      if (localName == "release-server") {
        if (serverList != null) {
          serverList.add(server);
        }
      }
      if (localName == "account-id") {
        try {
          server.setAccountId(Integer.parseInt(buffer.toString()));
        } catch (NumberFormatException nfe) {
          throw new SAXException(nfe);
        }
      }
      if (localName == "created-at") {
        try {
          server.setCreatedAt(buffer.toString());
        } catch (ParseException e) {
          throw new SAXException(e);
        }
      }

      if (localName == "id") {
        try {
          server.setId(Integer.parseInt(buffer.toString()));
        } catch (NumberFormatException nfe) {
          throw new SAXException(nfe);
        }

      }

      if (localName == "local-path") {
        server.setLocalPath(buffer.toString());
      }

      if (localName == "login") {
        server.setLogin(buffer.toString());
      }

      if (localName == "name") {
        server.setName(buffer.toString());
      }

      if (localName == "password") {
        server.setPassword(buffer.toString());
      }

      if (localName == "port") {
        try {
          server.setPort(Integer.parseInt(buffer.toString()));
        } catch (NumberFormatException nfe) {
          throw new SAXException(nfe);
        }

      }

      if (localName == "protocol") {
        server.setProtocol(buffer.toString());
      }

      if (localName == "remote-addr") {
        server.setRemoteAddr(buffer.toString());
      }

      if (localName == "remote-path") {
        server.setRemotePath(buffer.toString());
      }

      if (localName == "repository-id") {
        try {
          server.setRepositoryId(Integer.parseInt(buffer.toString()));
        } catch (NumberFormatException nfe) {
          throw new SAXException(nfe);
        }
      }

      if (localName == "environment-name") {
        server.setEnvironmentName(buffer.toString());
      }

      if (localName == "server-environment-id") {
        server.setServerEnvironmentId(buffer.toString());
      }

      if (localName == "revision") {
        server.setRevision(buffer.toString());
      }

      if (localName == "updated-at") {
        try {
          server.setUpdatedAt(buffer.toString());
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

  public ArrayList<Server> retrieveServerList() {
    return serverList;
  }

  public Server retrieveUser() {
    return server;
  }

}
