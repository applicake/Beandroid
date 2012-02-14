package com.applicake.beanstalkclient;

import java.util.List;

import android.os.Parcelable;

public interface RepositoryDataHolder extends Parcelable {
  
  public String getName();
  public List<Integer> getIds();
}
