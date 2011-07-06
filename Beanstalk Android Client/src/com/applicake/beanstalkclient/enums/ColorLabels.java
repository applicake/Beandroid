package com.applicake.beanstalkclient.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;

// this enum consists of 3 fields: 1 is the level number assigned to the colorlabel, 
//second one is the label name used in beanstalk API, 
//and the third one is the color for LED notifications
public enum ColorLabels {
  WHITE(0, "label-white", Color.WHITE), RED(1, "label-red", Color.RED), ORANGE(2,
      "label-orange", 0xffFFA93A), YELLOW(3, "label-yellow", Color.YELLOW), GREEN(4,
      "label-green", Color.GREEN), BLUE(5, "label-blue", Color.BLUE), PINK(6,
      "label-pink", Color.MAGENTA), GREY(7, "label-grey", Color.GRAY);

  private static final Map<Integer, String> intToString = new HashMap<Integer, String>();
  private static final Map<String, Integer> stringToInt = new HashMap<String, Integer>();
  private static final Map<String, Integer> stringToColor = new HashMap<String, Integer>();

  static {
    for (ColorLabels cl : EnumSet.allOf(ColorLabels.class)) {
      intToString.put(cl.code, cl.label);
      stringToInt.put(cl.label, cl.code);
      stringToColor.put(cl.label, cl.color);
    }
  }

  private int code;
  private String label;
  private int color;

  ColorLabels(int number, String label, int color) {
    this.label = label;
    this.code = number;
    this.color = color;
  }

  public String getLabel() {
    return label;
  }

  public int getNumber() {
    return code;
  }

  public int getColor() {
    return color;
  }

  public static String getLabelFromNumber(int i) {
    return intToString.get(i);
  }

  public static String getLabelFromNumberForButton(int i) {
    return intToString.get(i).substring(6);
  }

  public static int getNumberFromLabel(String label) {
    return stringToInt.get(label);
  }

  public static int getColorFromLabel(String label) {
    return stringToColor.get(label);
  }

}
