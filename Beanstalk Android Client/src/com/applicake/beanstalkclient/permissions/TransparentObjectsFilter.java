package com.applicake.beanstalkclient.permissions;

import android.content.Context;

public class TransparentObjectsFilter<T> extends ObjectsFilter<T> {
  
  public TransparentObjectsFilter(Context context) {
    super(context);
  }
  
  public boolean fits(T object) {
    return true;
  }
}
