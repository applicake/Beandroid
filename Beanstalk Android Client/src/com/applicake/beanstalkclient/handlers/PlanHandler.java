package com.applicake.beanstalkclient.handlers;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.applicake.beanstalkclient.Plan;

public class PlanHandler extends DefaultHandler {

  private Plan plan;
  private ArrayList<Plan> planList;
  private StringBuilder buffer = new StringBuilder();

  @Override
  public void startElement(String uri, String localName, String qName,
      Attributes attributes) {
    buffer.setLength(0);

    if (localName == "plans") {
      planList = new ArrayList<Plan>();
    }

    if (localName == "plan") {
      plan = new Plan();
    }

  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (plan != null && planList != null) {

      if (localName == "plan") {
        planList.add(plan);
      }
      if (localName == "id") {
        try {
          plan.setId(Integer.parseInt(buffer.toString()));
        } catch (NumberFormatException nfe) {
          throw new SAXException(nfe);
        }
      }
      if (localName == "repositories") {
        try {
          plan.setNumberOfRepos(Integer.parseInt(buffer.toString()));
        } catch (NumberFormatException nfe) {
          throw new SAXException(nfe);
        }
      }
      if (localName == "users") {
        try {
          plan.setNumberOfUsers(Integer.parseInt(buffer.toString()));
        } catch (NumberFormatException nfe) {
          throw new SAXException(nfe);
        }
      }

    }

  }

  @Override
  public void characters(char[] ch, int start, int length) {
    buffer.append(ch, start, length);
  }

  public HashMap<Integer, Plan> retrievePlanMap() {
    HashMap<Integer, Plan> planMap = new HashMap<Integer, Plan>();
    for (Plan p : planList) {
      planMap.put(p.getId(), p);
    }

    return planMap;
  }
}
