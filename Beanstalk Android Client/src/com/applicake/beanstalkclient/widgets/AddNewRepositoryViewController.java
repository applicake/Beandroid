package com.applicake.beanstalkclient.widgets;

import com.applicake.beanstalkclient.R;

public class AddNewRepositoryViewController extends AddNewObjectViewController {

  public AddNewRepositoryViewController() {
    AddNewObjectViewData enabledData = new AddNewObjectViewData();
    
    enabledData.setMainTextStringId(R.string.add_new_repository);
    enabledData.setAvailbilityStringId(R.string.available_repositories);
    enabledData.setImageResId(R.drawable.ic_extra_add_repo);
    
    AddNewObjectViewData disabledData = new AddNewObjectViewData();
    disabledData.setMainTextStringId(R.string.cant_add_new_repository);
    disabledData.setAvailbilityStringId(R.string.available_repositories);
    disabledData.setImageResId(R.drawable.ic_extra_add_repo);
    
    setEnabledData(enabledData);
    setDisabledData(disabledData);
  }
}
