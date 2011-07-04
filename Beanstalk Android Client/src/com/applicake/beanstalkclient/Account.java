package com.applicake.beanstalkclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Account {
  static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

  private Date createdAt;
  private int id;
  private String name;
  private int ownerId;
  private int planId;
  private boolean suspended;
  private String thirdLevelDomain;
  private String timeZone;
  private Date updatedAt;

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String date) throws ParseException {
    this.createdAt = FORMATTER.parse(date.trim());
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
  }

  public int getPlanId() {
    return planId;
  }

  public void setPlanId(int plan) {
    this.planId = plan;
  }

  public boolean isSuspended() {
    return suspended;
  }

  public void setSuspended(boolean suspended) {
    this.suspended = suspended;
  }

  public String getThirdLevelDomain() {
    return thirdLevelDomain;
  }

  public void setThirdLevelDomain(String thirdLevelDomain) {
    this.thirdLevelDomain = thirdLevelDomain;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String date) throws ParseException {
    this.updatedAt = FORMATTER.parse(date.trim());
  }

}
