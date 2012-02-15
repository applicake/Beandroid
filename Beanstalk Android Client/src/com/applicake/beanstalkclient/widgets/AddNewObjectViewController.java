package com.applicake.beanstalkclient.widgets;

import com.applicake.beanstalkclient.R;

public class AddNewObjectViewController {
  
  private static final int DEFAULT_DISABLED_BACKGROUND = R.drawable.bn_disabled;
  private static final int DEFAULT_ENABLED_BACKGROUND = R.drawable.bn_generic;
  
  private AddNewObjectViewData enabledData;
  private AddNewObjectViewData disabledData;
  
  public AddNewObjectViewController() {
    
  }

  public AddNewObjectViewData getEnabledData() {
    return enabledData;
  }

  protected void setEnabledData(AddNewObjectViewData enabledData) {
    this.enabledData = enabledData;
    if(enabledData.getBackgroundResId() == AddNewObjectViewData.NO_COLOR_SET) {
      enabledData.setBackgroundResId(DEFAULT_ENABLED_BACKGROUND);
    }
  }

  public AddNewObjectViewData getDisabledData() {
    return disabledData;
  }

  protected void setDisabledData(AddNewObjectViewData disabledData) {
    this.disabledData = disabledData;
    if(disabledData.getBackgroundResId() == AddNewObjectViewData.NO_COLOR_SET) {
      disabledData.setBackgroundResId(DEFAULT_DISABLED_BACKGROUND);
    }
  }
  
  public AddNewObjectViewData getViewData(boolean enabled) {
    return enabled ? getEnabledData() : getDisabledData();
  }
  
  
}
