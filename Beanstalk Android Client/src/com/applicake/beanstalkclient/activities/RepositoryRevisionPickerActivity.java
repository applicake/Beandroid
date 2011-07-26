package com.applicake.beanstalkclient.activities;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.applicake.beanstalkclient.Changeset;
import com.applicake.beanstalkclient.Constants;

public class RepositoryRevisionPickerActivity extends RepositoryCommitsActivity {

  // overrides the OnItemClick method to
  @Override
  public void onItemClick(AdapterView<?> arg0, View arg1, int itemNumber, long arg3) {
    if (itemNumber > 0) {
      Intent intent = new Intent();
      Changeset changeset = (Changeset) arg0.getItemAtPosition(itemNumber);
      // TODO check if revision works for both GIT and SVN repositories
      intent.putExtra(Constants.COMMIT_REVISION_ID, changeset.getRevision());
      setResult(RESULT_OK, intent);
      finish();
    }
  }
}