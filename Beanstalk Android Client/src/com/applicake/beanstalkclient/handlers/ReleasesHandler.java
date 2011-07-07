package com.applicake.beanstalkclient.handlers;

import java.text.ParseException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.applicake.beanstalkclient.Release;

public class ReleasesHandler extends DefaultHandler {

  private ArrayList<Release> releases;
  private Release release;
  private StringBuilder buffer;

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    buffer.append(ch, start, length);
  }

  @Override
  public void startElement(String uri, String localName, String qName,
      Attributes attributes) throws SAXException {
    buffer = new StringBuilder();

    if (localName == "releases") {
      releases = new ArrayList<Release>();
    } else if (localName == "release") {
      release = new Release();
    }
  }

  @Override
  public void endElement(String uri, String name, String qName) throws SAXException {
    if (name == "release") {
      releases.add(release);
    } else {
      String value = buffer.toString();
      if (name == "account-id") {
        release.setAccountId(Integer.parseInt(value));
      } else if (name == "author") {
        release.setAuthor(value);
      } else if (name == "user-id") {
        release.setUserId(Integer.parseInt(value));
      } else if (name == "comment") {
        release.setComment(value);
      } else if (name == "created-at") {
        try {
          release.setCreatedAt(value);
        } catch (ParseException e) {
          throw new SAXException(e);
        }
      } else if (name == "deployed-at") {
        try {
          release.setDeployedAt(value);
        } catch (ParseException e) {
          throw new SAXException(e);
        }
      } else if (name == "id") {
        release.setId(Integer.parseInt(value));
      } else if (name == "last-retry-at") {
        try {
          release.setLastRetryAt(value);
        } catch (ParseException e) {
          throw new SAXException(e);
        }
      } else if (name == "repository-id") {
        release.setRepositoryId(Integer.parseInt(value));
      } else if (name == "environment-name") {
        release.setEnvironmentName(value);
      } else if (name == "environment-id") {
        release.setEnvironmentId(Integer.parseInt(value));
      } else if (name == "retries") {
        release.setRetries(Integer.parseInt(value));
      } else if (name == "revision") {
        release.setRevision(Integer.parseInt(value));
      } else if (name == "state") {
        release.setState(value);
      } else if (name == "updated-at") {
        try {
          release.setUpdatedAt(value);
        } catch (ParseException e) {
          throw new SAXException(e);
        }
      }
    }
  }

  public ArrayList<Release> retrieveReleasesList() {
    return releases;
  }
}
