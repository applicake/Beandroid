package com.applicake.beanstalkclient.helpers;

import java.util.ArrayList;
import java.util.List;

import com.applicake.beanstalkclient.Plan;
import com.applicake.beanstalkclient.R;

public class PlanPropertiesHolder implements PropertiesHolder {

  private List<Property> properties;
  private MegabytesConverter mbConverter = new MegabytesConverter();

  public PlanPropertiesHolder(Plan currentPlan) {    
    properties = new ArrayList<Property>();
    properties.add(new Property(R.string.available_storage_label, mbConverter
        .parseMegabytes(currentPlan.getStorageInMegaBytes())));
    properties.add(new Property(R.string.available_repositories_label,
        NumberToStringOrInfinityConverter.convertNumberToTextOrInfinity(currentPlan.getNumberOfRepos())));
    properties.add(new Property(R.string.available_deployment_servers_label,
        NumberToStringOrInfinityConverter.convertNumberToTextOrInfinity(currentPlan.getNumberOfServers())));
    properties.add(new Property(R.string.available_users_label,
        NumberToStringOrInfinityConverter.convertNumberToTextOrInfinity(currentPlan.getNumberOfUsers())));
  }


  @Override
  public List<Property> getProperties() {
    return properties;
  }
}
