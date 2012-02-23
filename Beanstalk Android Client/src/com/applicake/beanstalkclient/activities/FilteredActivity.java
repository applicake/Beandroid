package com.applicake.beanstalkclient.activities;

import com.applicake.beanstalkclient.permissions.ObjectsFilter;

public interface FilteredActivity<T> {
  
  public ObjectsFilter<T> getObjectsFilter();
}
