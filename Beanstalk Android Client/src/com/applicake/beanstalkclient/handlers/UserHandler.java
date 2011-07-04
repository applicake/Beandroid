package com.applicake.beanstalkclient.handlers;

import java.text.ParseException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.enums.UserType;

public class UserHandler extends DefaultHandler {

  private ArrayList<User> userList;
  private User user;
  private StringBuilder buffer = new StringBuilder();

  @Override
  public void startElement(String uri, String localName, String qName,
      Attributes attributes) throws SAXException {
    buffer.setLength(0);

    if (localName == "users") {
      userList = new ArrayList<User>();
    }

    if (localName == "user") {
      user = new User();
    }

  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (user != null) {
      if (localName == "user") {
        if (userList != null) {
          userList.add(user);
        }
      }
      if (localName == "account-id") {
        try {
          user.setAccountId(Integer.parseInt(buffer.toString()));
        } catch (NumberFormatException nfe) {
          throw new SAXException(nfe);
        }
      }
      if (localName == "admin") {
        if (buffer.toString().equals("") || buffer.toString().equals("false"))
          user.setAdmin(UserType.USER);
        else if (buffer.toString().equals("true"))
          user.setAdmin(UserType.ADMIN);
        else
          throw new SAXException("Error while parsing user type");

      }

      if (localName == "owner") {
        if (buffer.toString().equals("true"))
          user.setAdmin(UserType.OWNER);

      }

      if (localName == "created-at") {
        try {
          user.setCreatedAt(buffer.toString());
        } catch (ParseException e) {
          throw new SAXException(e);
        }
      }

      if (localName == "email") {
        user.setEmail(buffer.toString());
      }

      if (localName == "first-name") {
        user.setFirstName(buffer.toString());
      }

      if (localName == "id") {
        try {
          user.setId(Integer.parseInt(buffer.toString()));
        } catch (NumberFormatException nfe) {
          throw new SAXException(nfe);
        }

      }

      if (localName == "last-name") {
        user.setLastName(buffer.toString());
      }

      if (localName == "login") {
        user.setLogin(buffer.toString());
      }

      if (localName == "timezone") {
        user.setTimezone(buffer.toString());
      }

      if (localName == "updated-at") {
        try {
          user.setUpdatedAt(buffer.toString());
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

  public ArrayList<User> retrieveUserList() {
    return userList;
  }

  public User retrieveUser() {
    return user;
  }

}
