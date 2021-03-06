package com.applicake.beanstalkclient.activities;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.GravatarDowloader;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderServerErrorException;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;

public class UserDetailsActivity extends BeanstalkActivity implements OnClickListener {

  private User user;
  private Context mContext;
  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.user_details_layout);
    mContext = this;

    user = getIntent().getParcelableExtra(Constants.USER);
    UserType userType = user.getUserType();
    ImageView userGravatar = (ImageView) findViewById(R.id.userGravatar);

    GravatarDowloader.getInstance().download(user.getEmail(), userGravatar);

    if (userType == UserType.ADMIN) {
      findViewById(R.id.adminLabel).setVisibility(View.VISIBLE);

    } else if (userType == UserType.OWNER) {
      findViewById(R.id.ownerLabel).setVisibility(View.VISIBLE);
      findViewById(R.id.buttonDeleteUser).setVisibility(View.GONE);
    }

    ((TextView) findViewById(R.id.userName)).setText(user.getFirstName() + " "
        + user.getLastName());
    ((TextView) findViewById(R.id.userLogin)).setText(user.getLogin());
    ((TextView) findViewById(R.id.userEmail)).setText(user.getEmail());

    // add button listeners
    Button userPermissionsButton = (Button) findViewById(R.id.buttonUserPermissions);
    Button modifyPropertiesButton = (Button) findViewById(R.id.buttonModifyProperties);
    Button deleteUserButton = (Button) findViewById(R.id.buttonDeleteUser);

    userPermissionsButton.setOnClickListener(this);
    modifyPropertiesButton.setOnClickListener(this);
    deleteUserButton.setOnClickListener(this);

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Constants.REFRESH_ACTIVITY)
      setResult(resultCode);
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("Are you sure you want to delete this user?").setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            new SendDeleteUserRequest().execute();
          }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
          }
        });
    AlertDialog alert = builder.create();

    return alert;
  }

  @Override
  public void onClick(View v) {

    if (v.getId() == R.id.buttonUserPermissions) {
      if ((user.getUserType() != UserType.USER)) {
        GUI.displayMonit(mContext,
            "Owner and admins have full access to all repositories and deployment environments");
      } else {
        Intent intent = new Intent(getApplicationContext(), UserPermissionsActivity.class);
        intent.putExtra(Constants.USER, (Parcelable)user);
        startActivityForResult(intent, 0);
      }

    }

    if (v.getId() == R.id.buttonModifyProperties) {
      Intent intent = new Intent(getApplicationContext(),
          UserModifyPropertiesActivity.class);
      intent.putExtra(Constants.USER, (Parcelable)user);
      startActivityForResult(intent, 0);
    }

    if (v.getId() == R.id.buttonDeleteUser) {
      showDialog(0);

    }

  }

  public class SendDeleteUserRequest extends AsyncTask<Void, Void, Integer> {

    @SuppressWarnings("rawtypes")
    private AsyncTask thisTask = this;
    private String failMessage;
    private String errorMessage;
    private boolean failed = false;

    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(mContext, "Please wait...", "deleting user");

      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          GUI.displayMonit(mContext, "Data sending task was cancelled");
        }
      });
      super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
      try {
        return HttpSender.sendDeleteUserRequest(prefs, String.valueOf(user.getId()));

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
        return 0;
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
            new SendDeleteUserRequest().execute();
          }
        };

        builder.displayDialog();
      } else {
        if (result == 200) {
          GUI.displayMonit(mContext, "The user was deleted!");
          setResult(Constants.REFRESH_ACTIVITY);
          finish();
        } else if (errorMessage != null)
          GUI.displayServerErrorMonit(mContext, errorMessage);
        else
          GUI.displayUnexpectedErrorMonit(mContext);
      }

    }

  }

}
