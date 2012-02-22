package com.applicake.beanstalkclient.enums;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;

import com.applicake.beanstalkclient.Plan;

public class PlanColors {
  
  private static Map<String, Integer> colorsForPlans = new HashMap<String, Integer>();
  
  static {
    colorsForPlans.put("trial", Color.WHITE);
    colorsForPlans.put("bronze", 0xff5C3317);
    colorsForPlans.put("silver", 0xffE6E8FA);
    colorsForPlans.put("gold", 0xffB8860B);
    colorsForPlans.put("platinum", 0xffC6E2FF);
    colorsForPlans.put("diamond", 0xffADADAD);
  }
  
  public static int getColorResIdForPlan(Plan plan) {
    return colorsForPlans.get(plan.getName().toLowerCase());
  }
  
}
