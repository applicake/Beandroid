package com.applicake.beanstalkclient;

import java.io.Serializable;

public class Plan implements Serializable {

  public static final int NO_PLAN_ID_SET = -1;
  public static final int INFINITY_NUMBER = 1000;
  
  private String name;
  private int price;
  private int id;
  private int storageInMegaBytes;
  private int numberOfRepos;
  private int numberOfUsers;
  private int numberOfServers;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getNumberOfRepos() {
    return numberOfRepos;
  }

  public void setNumberOfRepos(int numberOfRepos) {
    this.numberOfRepos = numberOfRepos;
  }

  public int getNumberOfUsers() {
    return numberOfUsers;
  }

  public void setNumberOfUsers(int numberOfUsers) {
    this.numberOfUsers = numberOfUsers;
  }

  public int getNumberOfServers() {
    return numberOfServers;
  }

  public void setNumberOfServers(int numberOfServers) {
    this.numberOfServers = numberOfServers;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public int getStorageInMegaBytes() {
    return storageInMegaBytes;
  }

  public void setStorageInMegaBytes(int storageInMegaBytes) {
    this.storageInMegaBytes = storageInMegaBytes;
  }

}
