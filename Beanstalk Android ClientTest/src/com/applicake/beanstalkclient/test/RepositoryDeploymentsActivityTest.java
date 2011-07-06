package com.applicake.beanstalkclient.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
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

  public RepositoryDeploymentsActivityTest() {
    super("com.applicake.beanstalkclient", RepositoryDeploymentsActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    Intent intent = new Intent();
    intent.putExtra(Constants.REPOSITORY_ID, 0);
    intent.putExtra(Constants.REPOSITORY_TITLE, TITLE);
    intent.putExtra(Constants.REPOSITORY_COLOR_NO, COLOR);

    setActivityIntent(intent);

    mActivity = getActivity();

    mTitle = (TextView) mActivity.findViewById(R.id.repoTitle);
    mColor = mActivity.findViewById(R.id.colorLabel);
  }

  public void testPreconditions() {
    assertNotNull(mTitle);
    assertNotNull(mColor);
  }

  public void testOnCreate() {
    // test color-label size...
    int size = (int) mActivity.getResources().getDimension(R.dimen.color_label_size);
    assertEquals(size, mColor.getWidth());
    assertEquals(size, mColor.getHeight());
    // ...and color
    assertEquals(COLOR, mColor.getBackground().getLevel());

    // test repo title
    assertEquals(TITLE, mTitle.getText());
  }

}
