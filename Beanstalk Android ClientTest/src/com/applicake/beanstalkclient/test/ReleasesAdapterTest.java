package com.applicake.beanstalkclient.test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Release;
import com.applicake.beanstalkclient.ReleasesAdapter;

public class ReleasesAdapterTest extends AndroidTestCase {

  private static final String MOCK_TIMESTAMP = "2011-07-08T11:01:40Z";
  private ReleasesAdapter mAdapter;
  private View mRoot;
  private ListView mList;
  private ArrayList<Release> mReleases;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    // inflate layout
    final Context context = getContext();
    final LayoutInflater inflater = LayoutInflater.from(context);
    mRoot = inflater.inflate(R.layout.repository_deployments_layout, null);

    // measure and lay it out
    mRoot.measure(480, 800);
    mRoot.layout(0, 0, 480, 800);

    // get handles to views
    mList = (ListView) mRoot.findViewById(R.id.releases_list);

    // set list adapter
    mReleases = new ArrayList<Release>();
    mAdapter = new ReleasesAdapter(getContext(), R.layout.releases_list_item, mReleases);
    mList.setAdapter(mAdapter);
  }

  @SmallTest
  public void testPreconditions() {
    assertNotNull("list not initialized", mList);
    assertNotNull("list adapter not set", mList.getAdapter());
  }

  public void testEmpty() {
    mReleases.clear();
    mAdapter.notifyDataSetChanged();
    assertEquals(0, mList.getCount());
  }

  public void testOneItem() {
    Release release = new Release();
    release.setAccountId(123);
    release.setAuthor("abc");
    release.setComment("qwerty");
    try {
      release.setCreatedAt(MOCK_TIMESTAMP);
    } catch (ParseException e) {
      fail("setCreatedAt() shouldn't throw an exception");
    }
    try {
      release.setDeployedAt(MOCK_TIMESTAMP);
    } catch (ParseException e) {
      fail("setDeployedAt() shouldn't throw an exception");
    }
    release.setEnvironmentId(456);
    release.setEnvironmentName("zxcvb");
    release.setId(789);
    try {
      release.setLastRetryAt(null);
    } catch (ParseException e) {
      fail("setLastRetryAt() shouldn't throw an exception");
    }
    release.setRepositoryId(1);
    release.setRetries(0);
    release.setRevision(54);
    release.setState("success");
    try {
      release.setUpdatedAt(MOCK_TIMESTAMP);
    } catch (ParseException e) {
      fail("setUpdatedAt() shouldn't throw an exception");
    }
    release.setUserId(69);

    mReleases.add(release);
    mAdapter.notifyDataSetChanged();

    assertEquals(1, mList.getCount());
    assertNotNull(mList.getItemAtPosition(0));
  }

  public void testManyItems() {
    mReleases.clear();

    final int numReleases = 40;

    for (int i = 0; i < numReleases; ++i) {
      Release release = new Release();
      release.setAccountId(("accountId" + i).hashCode());
      release.setAuthor("author" + i);
      release.setComment("comment" + i);
      try {
        release.setCreatedAt(MOCK_TIMESTAMP);
      } catch (ParseException e) {
        fail("setCreatedAt() shouldn't throw an exception");
      }
      try {
        release.setDeployedAt(MOCK_TIMESTAMP);
      } catch (ParseException e) {
        fail("setDeployedAt() shouldn't throw an exception");
      }
      release.setEnvironmentId(("environmentId" + i).hashCode());
      release.setEnvironmentName("environmentName" + i);
      release.setId(("id" + i).hashCode());
      try {
        release.setLastRetryAt(null);
      } catch (ParseException e) {
        fail("setLastRetryAt() shouldn't throw an exception");
      }
      release.setRepositoryId(("repositoryId" + i).hashCode());
      release.setRetries(0);
      release.setRevision(("revision" + i).hashCode());
      release.setState("success");
      try {
        release.setUpdatedAt(MOCK_TIMESTAMP);
      } catch (ParseException e) {
        fail("setUpdatedAt() shouldn't throw an exception");
      }
      release.setUserId(("userId" + i).hashCode());

      mReleases.add(release);
    }

    mAdapter.notifyDataSetChanged();

    assertEquals(numReleases, mList.getCount());

    // assertTrue(mList.getChildCount() > 0);

    List<View> recycled = new ArrayList<View>();

    for (int i = 0; i < 10; ++i) {
      View view = mAdapter.getView(i, null, mList);

      CharSequence environmentName = ((TextView) view.findViewById(R.id.environment_name))
          .getText();
      CharSequence comment = ((TextView) view.findViewById(R.id.comment)).getText();
      CharSequence state = ((TextView) view.findViewById(R.id.state)).getText();
      CharSequence revision = ((TextView) view.findViewById(R.id.revision)).getText();
      CharSequence author = ((TextView) view.findViewById(R.id.author)).getText();
      CharSequence deployedAt = ((TextView) view.findViewById(R.id.deployed_at))
          .getText();

      assertEquals("environmentName" + i, environmentName);
      assertEquals("comment" + i, comment);
      assertEquals("success", state);
      assertEquals(("revision" + i).hashCode(), Integer.parseInt((String) revision));
      assertEquals("author" + i, author);
      try {
        assertEquals(Release.FORMATTER.parse(MOCK_TIMESTAMP).toLocaleString(), deployedAt);
      } catch (ParseException e) {
        fail();
      }

      recycled.add(view);
    }

    for (int i = 0; i < 10; ++i) {
      int j = i + 10;
      View convertView = recycled.get(i);
      View view = mAdapter.getView(j, convertView, mList);
      assertSame(convertView, view);
      
      CharSequence environmentName = ((TextView) view.findViewById(R.id.environment_name))
          .getText();
      CharSequence comment = ((TextView) view.findViewById(R.id.comment)).getText();
      CharSequence state = ((TextView) view.findViewById(R.id.state)).getText();
      CharSequence revision = ((TextView) view.findViewById(R.id.revision)).getText();
      CharSequence author = ((TextView) view.findViewById(R.id.author)).getText();
      CharSequence deployedAt = ((TextView) view.findViewById(R.id.deployed_at))
          .getText();

      assertEquals("environmentName" + j, environmentName);
      assertEquals("comment" + j, comment);
      assertEquals("success", state);
      assertEquals(("revision" + j).hashCode(), Integer.parseInt((String) revision));
      assertEquals("author" + j, author);
      try {
        assertEquals(Release.FORMATTER.parse(MOCK_TIMESTAMP).toLocaleString(), deployedAt);
      } catch (ParseException e) {
        fail();
      }
    }
  }

  // private View getChildAt(int position) {
  // assertFalse(
  // String.format("view at pos %d is not displayed on screen", position),
  // position < mList.getFirstVisiblePosition()
  // || position > mList.getLastVisiblePosition());
  // position -= mList.getFirstVisiblePosition();
  // return mList.getChildAt(position);
  // }

}
