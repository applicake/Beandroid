package com.applicake.beanstalkclient.permissions;

import java.io.Serializable;

import android.content.Context;

public abstract class ObjectsFilter<T> implements Serializable {
  
  private Context context;
  
  public ObjectsFilter(Context context) {
    this.context = context;
  }
  
  public abstract boolean fits(T object);
  
  
  protected Context getContext() {
    return context;
  }
  
  
}
