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
    try {
      if (plan != null && planList != null) {
        if (localName.equals("plan")) {
          planList.add(plan);
        }
        else if (localName.equals("name")) {
          plan.setName(buffer.toString());
        }
        else if (localName.equals("price")) {
          plan.setPrice(Integer.parseInt(buffer.toString()));
        }
        else if (localName.equals("storage")) {
          plan.setStorageInMegaBytes(Integer.parseInt(buffer.toString()));
        }
        else if (localName.equals("id")) {
          plan.setId(Integer.parseInt(buffer.toString()));
        }
        else if (localName.equals("repositories")) {
          plan.setNumberOfRepos(Integer.parseInt(buffer.toString()));
        }
        else if (localName.equals("users")) {
          plan.setNumberOfUsers(Integer.parseInt(buffer.toString()));
        }
        else if (localName.equals("servers")) {
          plan.setNumberOfServers(Integer.parseInt(buffer.toString()));
        }
      }
    } catch (NumberFormatException nfe) {
      throw new SAXException(nfe);
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
