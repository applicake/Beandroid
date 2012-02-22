package com.applicake.beanstalkclient.helpers;

import com.applicake.beanstalkclient.Plan;

public class NumberToStringOrInfinityConverter {
  
  public static String convertNumberToTextOrInfinity(int number) {
    return number != Plan.INFINITY_NUMBER ? String.valueOf(number) : "\u221E";
  }
}
