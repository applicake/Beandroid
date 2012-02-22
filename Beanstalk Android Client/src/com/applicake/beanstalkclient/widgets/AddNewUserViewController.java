package com.applicake.beanstalkclient.widgets;

import com.applicake.beanstalkclient.R;

public class AddNewUserViewController extends AddNewObjectViewController {
  
  public AddNewUserViewController() {
    
    AddNewObjectViewData enabledViewData = new AddNewObjectViewData();
    enabledViewData.setMainTextStringId(R.string.add_new_user);
    enabledViewData.setImageResId(R.drawable.ic_extras_adduser_normal);
    enabledViewData.setAvailbilityStringId(R.string.available_users);
    
    AddNewObjectViewData disabledViewData = new AddNewObjectViewData();
    disabledViewData.setMainTextStringId(R.string.cant_add_new_user);
    disabledViewData.setImageResId(R.drawable.ic_extras_adduser_normal);
    disabledViewData.setAvailbilityStringId(R.string.available_users);
    
    setEnabledData(enabledViewData);
    setDisabledData(disabledViewData);

  }

}
