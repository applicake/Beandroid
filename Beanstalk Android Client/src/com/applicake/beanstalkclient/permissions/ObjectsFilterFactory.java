package com.applicake.beanstalkclient.permissions;

import android.content.Context;

public class ObjectsFilterFactory<T> {
  
  public ObjectsFilter<?> generateFilter(Context context, Class<?> classId) {
    ObjectsFilter<?> filter = null;
    if(classId == null) {
      filter = new TransparentObjectsFilter<T>(context);
    } else if(classId.equals(CanReleaseFromRepositoryFilter.class)) {
      filter = new CanReleaseFromRepositoryFilter(context);
    } else if(classId.equals(CanAddNewEnvironmentToRepositoryFilter.class)) {
      filter = new CanAddNewEnvironmentToRepositoryFilter(context);
    }
    return filter;
  }
}
