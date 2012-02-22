package com.applicake.beanstalkclient.helpers;

public class MegabytesConverter {
  
  private static final String MEGABYTE_UNIT = "MB";
  private static final String GIGABYTE_UNIT = "GB";
  
  public MegabytesConverter() { }
  
  public String parseMegabytes(int sizeInMegabytes) {
    String unit = MEGABYTE_UNIT;
    if(sizeInMegabytes > 1024) {
      unit = GIGABYTE_UNIT;
      sizeInMegabytes /= 1024;
    }
    return sizeInMegabytes + " " + unit;
  }
}
