package com.applicake.beanstalkclient.activities;

import java.io.IOException;

import org.apache.http.ParseException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.Permission;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Repository;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.GravatarDowloader;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderServerErrorException;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

public class PermissionModifyActivity extends BeanstalkActivity implements
    OnClickListener {
  private User user;
  private Repository repository;
  private Permission permission;
  private Context mContext;

  private Spinner repoAccessSpinner;
  private Spinner deploymentAccessSpinner;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.permissions_modify_layout);
    mContext = this;

    Intent currnetIntent = getIntent();
    user = currnetIntent.getParcelableExtra(Constants.USER);
    repository = currnetIntent.getParcelableExtra(Constants.REPOSITORY);
    permission = currnetIntent.getParcelableExtra(Constants.PERMISSION);

    // fill with data
    ImageView userGravatar = (ImageView) findViewById(R.id.userGravatar);
    GravatarDowloader.getInstance().download(user.getEmail(), userGravatar);

    TextView userName = (TextView) findViewById(R.id.userName);
    userName.setText(user.getFirstName() + " " + user.getLastName());
    ((TextView) findViewById(R.id.userEmail)).setText(user.getEmail());

    findViewById(R.id.colorLabel).getBackground().setLevel(repository.getColorLabelNo());
    ((TextView) findViewById(R.id.repositoryTitle)).setText(repository.getTitle());
    ((TextView) findViewById(R.id.repositoryName)).setText(repository.getName());

    Button removeButton = (Button) findViewById(R.id.deleteButton);
    removeButton.setOnClickListener(this);

    Button applyButton = (Button) findViewById(R.id.applyButton);
    applyButton.setOnClickListener(this);

    repoAccessSpinner = (Spinner) findViewById(R.id.repoAccessSpinner);
    deploymentAccessSpinner = (Spinner) findViewById(R.id.deploymentAccessSpinner);
    repoAccessSpinner.getBackground().setLevel(2);
    deploymentAccessSpinner.getBackground().setLevel(1);

    repoAccessSpinner.setOnItemSelectedListener(listener);
    deploymentAccessSpinner.setOnItemSelectedListener(listener);

    if (permission != null) {
      if (permission.isWriteAccess()) {
        repoAccessSpinner.setSelection(2);
        repoAccessSpinner.getBackground().setLevel(0);
      } else if (permission.isReadAccess()) {
        repoAccessSpinner.setSelection(1);
        repoAccessSpinner.getBackground().setLevel(1);
      }
      if (permission.isFullDeploymentAccess()) {
        deploymentAccessSpinner.setSelection(1);
        deploymentAccessSpinner.getBackground().setLevel(0);
      }
    }
  }

  // a listener that changes color label of the spinner after selecting and
  // items.

  private OnItemSelectedListener listener = new OnItemSelectedListener() {

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      if (parent.getId() == R.id.repoAccessSpinner)
        parent.getBackground().setLevel(2 - position);
      if (parent.getId() == R.id.deploymentAccessSpinner)
        parent.getBackground().setLevel(1 - position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
      // TODO Auto-generated method stub

    }
  };

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.deleteButton) {
      if (permission != null) {
        new DeletePermissionsTask().execute(permission.getId());
      } else {
        GUI.displayMonit(mContext, "no changes were made");
        finish();
      }
    }

    if (v.getId() == R.id.applyButton) {
      if ((repoAccessSpinner.getSelectedItemPosition() == 0)
          && (deploymentAccessSpinner.getSelectedItemPosition() == 0)) {
        if (permission != null) {
          new DeletePermissionsTask().execute(permission.getId());
        } else {
          GUI.displayMonit(mContext, "no changes were made");
          finish();
        }
      } else {
        new ModifyPermissionsTask().execute(repoAccessSpinner.getSelectedItemPosition(),
            deploymentAccessSpinner.getSelectedItemPosition());

      }

    }

  }

  // this task creates and sends XML with modified permission data
  // the task takes two arguments where the first one represents repository
  // access and the second represents deployment access. Valid argument values
  // are : 0 - no access; 1 - read only; 2 - read and write access for
  // repository access and: 0 - read only; 1 - write access for deployment
  // access

  public class ModifyPermissionsTask extends AsyncTask<Integer, Void, Integer> {

    ProgressDialog progressDialog;
    @SuppressWarnings("rawtypes")
    private AsyncTask thisTask = this;
    private String failMessage;
    private boolean failed;
    private String errorMessage;
    private int repoAccess;
    private int deploymentAccess;

    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(mContext, "Please wait...",
          "changing user properties");

      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          GUI.displayMonit(mContext, "User data changing task was cancelled");
        }
      });
      super.onPreExecute();
      super.onPreExecute();
    }

    protected Integer doInBackground(Integer... params) {
      repoAccess = params[0];
      deploymentAccess = params[1];

      XmlCreator xmlCreator = new XmlCreator();
      try {
        boolean readAccess = (repoAccess != 0);
        boolean writeAccess = (repoAccess == 2);
        boolean deploymentBooleanAccess = (deploymentAccess == 1);
        String modifyPermissionXml = xmlCreator.createPermissionXML(
            String.valueOf(user.getId()), String.valueOf(repository.getId()), readAccess,
            writeAccess, deploymentBooleanAccess);

        return HttpSender.sendPermissionXML(prefs, modifyPermissionXml);

      } catch (IllegalArgumentException e) {
        failMessage = Strings.internalErrorMessage;
      } catch (IllegalStateException e) {
        failMessage = Strings.internalErrorMessage;
      } catch (IOException e) {
        failMessage = Strings.internalErrorMessage;
      } catch (HttpSenderException e) {
        failMessage = Strings.networkConnectionErrorMessage;
      } catch (HttpSenderServerErrorException e) {
        errorMessage = e.getMessage();
      }
      failed = true;
      return null;

    }

    @Override
    protected void onPostExecute(Integer result) {
      progressDialog.dismiss();

      if (failed) {

        SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
            failMessage) {

          @Override
          public void retryAction() {
            new ModifyPermissionsTask().execute(repoAccess, deploymentAccess);
          }

        };

        builder.displayDialog();

      } else {
        if (result == 201) {
          GUI.displayMonit(mContext, "user permissions were modified!");
          setResult(Constants.REFRESH_ACTIVITY);
          finish();

        } else {
          GUI.displayMonit(mContext, errorMessage);
        }

      }
    }

  }

  public class DeletePermissionsTask extends AsyncTask<Integer, Void, Integer> {

    ProgressDialog progressDialog;
    @SuppressWarnings("rawtypes")
    private AsyncTask thisTask = this;
    private String failMessage;
    private String errorMessage;
    private boolean failed;
    private Integer permissionId;

    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(mContext, "Please wait...",
          "removing permission");
      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          GUI.displayMonit(mContext, "User permission deleting task was cancelled");
        }
      });
      super.onPreExecute();
    }

    protected Integer doInBackground(Integer... params) {
      permissionId = params[0];

      try {

        return HttpSender
            .sendDeletePermissionRequest(prefs, String.valueOf(permissionId));

      } catch (IllegalArgumentException e) {
        failMessage = Strings.internalErrorMessage;
      } catch (IllegalStateException e) {
        failMessage = Strings.internalErrorMessage;
      } catch (IOException e) {
        failMessage = Strings.internalErrorMessage;
      } catch (HttpSenderException e) {
        failMessage = Strings.networkConnectionErrorMessage;
      } catch (HttpSenderServerErrorException e) {
        errorMessage = e.getMessage();
      } catch (ParseException e) {
        failMessage = Strings.internalErrorMessage;
      } catch (XMLParserException e) {
        failMessage = Strings.internalErrorMessage;
      }
      failed = true;
      return 0;

    }

    @Override
    protected void onPostExecute(Integer result) {
      progressDialog.dismiss();
      if (failed) {

        SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
            failMessage) {

          @Override
          public void retryAction() {
            new DeletePermissionsTask().execute(permissionId);
          }

        };

        builder.displayDialog();

      } else {
        if (result == 200) {
          GUI.displayMonit(mContext, "User permission was removed!");
          setResult(Constants.REFRESH_ACTIVITY);
          finish();

        } else {
          GUI.displayMonit(mContext, errorMessage);
        }

      }
    }

  }

}
