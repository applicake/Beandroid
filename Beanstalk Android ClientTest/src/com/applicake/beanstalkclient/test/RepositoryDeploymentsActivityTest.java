package com.applicake.beanstalkclient.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.RepositoryDeploymentsActivity;

public class RepositoryDeploymentsActivityTest extends
    ActivityInstrumentationTestCase2<RepositoryDeploymentsActivity> {

  private static final int COLOR = 1;
  private static final String TITLE = "title";
  private TextView mTitle;
  private View mColor;
  private RepositoryDeploymentsActivity mActivity;
  private ListView mReleases;
  private ListView mServers;
  private Button mReleasesTab;
  private Button mServersTab;

  public RepositoryDeploymentsActivityTest() {
    super("com.applicake.beanstalkclient", RepositoryDeploymentsActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    Intent intent = new Intent();
    intent.putExtra(Constants.REPOSITORY_ID, 0);
    intent.putExtra(Constants.REPOSITORY_TITLE, TITLE);
    intent.putExtra(Constants.REPOSITORY_COLOR_NO, COLOR);

    setActivityIntent(intent);

    mActivity = getActivity();

    mTitle = (TextView) mActivity.findViewById(R.id.repoTitle);
    mColor = mActivity.findViewById(R.id.colorLabel);
    mReleases = (ListView) mActivity.findViewById(R.id.releases_list);
    mServers = (ListView) mActivity.findViewById(R.id.servers_list);
    mReleasesTab = (Button) mActivity.findViewById(R.id.releases_button);
    mServersTab = (Button) mActivity.findViewById(R.id.servers_button);
  }

  public void testPreconditions() {
    // check if there are views for title and label
    assertNotNull("can't find title view", mTitle);
    assertNotNull("can't find color label view", mColor);

    // check if there are views for release releases and servers list
    assertNotNull("can't find releases view", mReleases);
    assertNotNull("can't find servers view", mServers);

    // check tab buttons
    assertNotNull("can't find releases tab", mReleasesTab);
    assertNotNull("can't find servers tab", mServersTab);
  }

  public void testTitleUi() {
    // test color-label size...
    int size = (int) mActivity.getResources().getDimension(R.dimen.color_label_size);
    assertEquals("wrong label size", size, mColor.getWidth());
    assertEquals("wrong label size", size, mColor.getHeight());
    // ...and color
    assertEquals("wrong label color", COLOR, mColor.getBackground().getLevel());
    
    // test repo title
    assertEquals("wrong title", TITLE, mTitle.getText());
    
    // test relative location between title and color
    int[] colorLocation = new int[2], titleLocation = new int[2];
    mColor.getLocationInWindow(colorLocation);
    mTitle.getLocationInWindow(titleLocation);
    // is color to the left?
    assertFalse("color label not to the left of title",
        colorLocation[0] + mColor.getWidth() > titleLocation[0]);
    // is it vertically aligned?
    assertFalse("color label above title",
        colorLocation[1] + mColor.getHeight() < titleLocation[1]);
    assertFalse("color label below title",
        colorLocation[1] > titleLocation[1] + mTitle.getHeight());
  }
  
  public void testTabsUi() {
    // at first releases should be visible and servers hidden
    assertReleasesVisible();
    
    // do nothing (tap active)
    TouchUtils.tapView(this, mReleasesTab);
    assertReleasesVisible();
    
    // do nothing (tap inactive but cancel)
    TouchUtils.touchAndCancelView(this, mServersTab);
    assertReleasesVisible();
    
    // switch tabs
    TouchUtils.tapView(this, mServersTab);
    assertServersVisible();
    
    // do nothing (tap active)
    TouchUtils.tapView(this, mServersTab);
    assertServersVisible();
    
    // do nothing (tap inactive but cancel)
    TouchUtils.touchAndCancelView(this, mReleasesTab);
    assertServersVisible();
    
    // switch tabs
    TouchUtils.tapView(this, mReleasesTab);
    assertReleasesVisible();
  }

  private void assertReleasesVisible() {
    assertTrue("releases not visible", mReleases.isShown());
    assertFalse("servers visible", mServers.isShown());
  }

  private void assertServersVisible() {
    assertFalse("releases visible", mReleases.isShown());
    assertTrue("servers not visible", mServers.isShown());
  }
  
}