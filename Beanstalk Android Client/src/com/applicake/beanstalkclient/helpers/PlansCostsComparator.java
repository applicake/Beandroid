package com.applicake.beanstalkclient.helpers;

import java.util.Comparator;

import com.applicake.beanstalkclient.Plan;

public class PlansCostsComparator implements Comparator<Plan> {
  
  @Override
  public int compare(Plan lhs, Plan rhs) {
    double result = lhs.getPrice() - rhs.getPrice(); // have to do this this way, because prices are floating.
    if(result > 0) {
      return 1;
    } else if(result == 0) {
      return 0;
    } else {
      return -1;
    }
  }
}
