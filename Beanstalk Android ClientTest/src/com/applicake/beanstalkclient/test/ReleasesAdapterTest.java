package com.applicake.beanstalkclient.test;

import com.applicake.beanstalkclient.ReleasesAdapter;

import android.test.AndroidTestCase;

public class ReleasesAdapterTest extends AndroidTestCase {

  private ReleasesAdapter mAdapter;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    mAdapter = new ReleasesAdapter(getContext(), 0, null);
  }

}
