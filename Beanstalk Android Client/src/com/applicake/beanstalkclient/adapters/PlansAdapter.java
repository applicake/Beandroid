package com.applicake.beanstalkclient.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.Plan;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.enums.PlanColors;
import com.applicake.beanstalkclient.helpers.MegabytesConverter;
import com.applicake.beanstalkclient.helpers.NumberToStringOrInfinityConverter;

public class PlansAdapter extends ArrayAdapter<Plan> {

  private int currentPlanId = Plan.NO_PLAN_ID_SET;
  private MegabytesConverter mbConverter = new MegabytesConverter();
  private Map<Integer, String> descriptionsMap = new HashMap<Integer, String>();

  public PlansAdapter(Context context, int textViewResourceId, List<Plan> plans) {
    super(context, textViewResourceId, plans);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(
          R.layout.collapsed_plan_item, null);
    }
    View colorLabel = convertView.findViewById(R.id.colorLabel);
    TextView planName = (TextView) convertView.findViewById(R.id.plan_name);
    TextView planPrice = (TextView) convertView.findViewById(R.id.price);
    TextView currentPlanMarker = (TextView) convertView
        .findViewById(R.id.current_plan_marker);
    TextView shortDescLabel = (TextView) convertView.findViewById(R.id.short_info_label);

    Plan plan = getItem(position);

    colorLabel.setBackgroundColor(PlanColors.getColorResIdForPlan(plan));
    planName.setText(plan.getName());
    planPrice.setText(plan.getPrice() + " " + Constants.DEFAULT_CURRENCY_SYMBOL);

    int targetVisibilityOfMarker = View.GONE;
    if (currentPlanId == plan.getId()) {
      targetVisibilityOfMarker = View.VISIBLE;
    }
    if (currentPlanMarker.getVisibility() != targetVisibilityOfMarker) {
      currentPlanMarker.setVisibility(targetVisibilityOfMarker);
    }

    shortDescLabel.setText(getShortDescriptionForPlan(plan));

    return convertView;
  }

  public void setCurrentPlanId(int planId) {
    this.currentPlanId = planId;
  }

  private String getShortDescriptionForPlan(Plan plan) {
    if (!descriptionsMap.containsKey(plan.getId())) {
      StringBuffer descriptionBuffer = new StringBuffer();
      descriptionBuffer.append(mbConverter.parseMegabytes(plan.getStorageInMegaBytes()));
      descriptionBuffer.append(" / ");
      descriptionBuffer.append(NumberToStringOrInfinityConverter
          .convertNumberToTextOrInfinity(plan.getNumberOfRepos()));
      descriptionBuffer.append(" / ");
      descriptionBuffer.append(NumberToStringOrInfinityConverter
          .convertNumberToTextOrInfinity(plan.getNumberOfServers()));
      descriptionBuffer.append(" / ");
      descriptionBuffer.append(NumberToStringOrInfinityConverter
          .convertNumberToTextOrInfinity(plan.getNumberOfServers()));
      descriptionBuffer.append(" / ");
      descriptionBuffer.append(NumberToStringOrInfinityConverter
          .convertNumberToTextOrInfinity(plan.getNumberOfUsers()));
      descriptionsMap.put(plan.getId(), descriptionBuffer.toString());
    }
    return descriptionsMap.get(plan.getId());
  }

}
