package com.applicake.beanstalkclient.adapters;

import java.text.Format;
import java.util.List;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Release;

public class ReleasesAdapter extends BaseAdapter {

  private List<Release> mReleases;
  // private Context mContext;
  private int mLayoutId;
  private LayoutInflater mInflater;
  private Format mDf;

  public ReleasesAdapter(Context context, int layoutResourceId, List<Release> releases) {
    // mContext = context;
    mLayoutId = layoutResourceId;
    mReleases = releases;
    mInflater = LayoutInflater.from(context);
    mDf = DateFormat.getDateFormat(context);
  }

  @Override
  public int getCount() {
    return mReleases.size();
  }

  @Override
  public Object getItem(int position) {
    return mReleases.get(position);
  }

  @Override
  public long getItemId(int position) {
    return mReleases.get(position).getId();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Release release = mReleases.get(position);

    View view = convertView;

    if (view == null) {
      view = mInflater.inflate(mLayoutId, null);
    }

    TextView environmentName = (TextView) view.findViewById(R.id.environment_name);
    TextView comment = (TextView) view.findViewById(R.id.comment);
    TextView state = (TextView) view.findViewById(R.id.state);
//    TextView revision = (TextView) view.findViewById(R.id.revision);
    TextView author = (TextView) view.findViewById(R.id.author);
    TextView deployedAt = (TextView) view.findViewById(R.id.deployed_at);

    environmentName.setText(release.getEnvironmentName());
    comment.setText(release.getComment());
    state.setText(release.getStateLabel());
//    revision.setText(String.valueOf(release.getRevision()));
    author.setText(release.getAuthor());
    deployedAt.setText(mDf.format(release.getDeployedAt()));

    return view;
  }

}
