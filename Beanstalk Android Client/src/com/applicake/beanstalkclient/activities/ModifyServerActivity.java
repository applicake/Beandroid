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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Server;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderServerErrorException;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

public class ModifyServerActivity extends BeanstalkActivity implements OnClickListener {

  private Server activityServer;
  private Context mContext;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.modify_server_layout);
    mContext = this;

    Intent intent = getIntent();
    activityServer = intent.getParcelableExtra(Constants.SERVER);
    // attach listener to "apply" button
    ((Button) findViewById(R.id.apply_button)).setOnClickListener(this);
    ((Button) findViewById(R.id.delete_button)).setOnClickListener(this);

    // fill this activity fields with current values of the server
    fillFieldsFromServerInstance(activityServer);

  }

  public void fillFieldsFromServerInstance(Server server) {

    ((EditText) findViewById(R.id.name_edittext)).setText(server.getName());
    ((EditText) findViewById(R.id.local_path_edittext)).setText(server.getLocalPath());
    ((EditText) findViewById(R.id.remote_path_edittext)).setText(server.getRemotePath());
    ((EditText) findViewById(R.id.remote_addr_edittext)).setText(server.getRemoteAddr());
    ((Spinner) findViewById(R.id.protocol_spinner)).setSelection(server.getProtocol()
        .toLowerCase().equals("ftp") ? 0 : 1);
    ((EditText) findViewById(R.id.port_edittext))
        .setText(String.valueOf(server.getPort()));
    ((EditText) findViewById(R.id.login_edittext)).setText(server.getLogin());
    ((EditText) findViewById(R.id.password_edittext)).setText(server.getPassword());
    ((CheckBox) findViewById(R.id.use_active_mode_checkbox)).setChecked(server
        .isUseActiveMode());
    ((CheckBox) findViewById(R.id.authenticate_by_key_checkbox)).setChecked(server
        .isAuthenticateByKey());
    ((CheckBox) findViewById(R.id.use_feat_checkbox)).setChecked(server.isUseFeat());
    ((EditText) findViewById(R.id.pre_release_hook_edittext)).setText(server
        .getPreReleaseHook());
    ((EditText) findViewById(R.id.post_release_hook_edittext)).setText(server
        .getPostReleaseHook());
    
    

  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    switch (id) {
    case R.id.delete_button:
      showDialog(0);
      break;
    case R.id.apply_button:
      // initialize server instance with values entered into appropriate fields

      activityServer.setName(((EditText) findViewById(R.id.name_edittext)).getText()
          .toString());
      activityServer.setLocalPath(((EditText) findViewById(R.id.local_path_edittext))
          .getText().toString());
      activityServer.setRemotePath(((EditText) findViewById(R.id.remote_path_edittext))
          .getText().toString());
      activityServer.setRemoteAddr(((EditText) findViewById(R.id.remote_addr_edittext))
          .getText().toString());
      activityServer.setProtocol(((Spinner) findViewById(R.id.protocol_spinner))
          .getSelectedItem().toString().toLowerCase());
      try {
        activityServer
            .setPort(Integer.parseInt(((EditText) findViewById(R.id.port_edittext))
                .getText().toString()));
      } catch (NumberFormatException e) {
        // server.setPort(0);
      }
      activityServer.setLogin(((EditText) findViewById(R.id.login_edittext)).getText()
          .toString());
      activityServer.setPassword(((EditText) findViewById(R.id.password_edittext))
          .getText().toString());
      activityServer
          .setUseActiveMode(((CheckBox) findViewById(R.id.use_active_mode_checkbox))
              .isChecked());
      activityServer
          .setAuthenticateByKey(((CheckBox) findViewById(R.id.authenticate_by_key_checkbox))
              .isChecked());
      activityServer.setUseFeat(((CheckBox) findViewById(R.id.use_feat_checkbox))
          .isChecked());
      activityServer
          .setPreReleaseHook(((EditText) findViewById(R.id.pre_release_hook_edittext))
              .getText().toString());
      activityServer
          .setPostReleaseHook(((EditText) findViewById(R.id.post_release_hook_edittext))
              .getText().toString());

      new SendModifyServerTask(this).execute(activityServer);
      break;
    }
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("Are you sure you want to delete this server?")
        .setCancelable(false)
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

  public class SendModifyServerTask extends AsyncTask<Server, Void, Integer> {

    ProgressDialog progressDialog;
    String errorMessage;

    private AsyncTask<Server, Void, Integer> thisTask;
    private String failMessage;
    private boolean failed;
    private Context mContext;
    private Server mServer;

    public SendModifyServerTask(Context context) {
      mContext = context;
      thisTask = this;
    }

    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(mContext, "Please wait...",
          "Modifying server properties");

      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          GUI.displayMonit(mContext, "Modificaiton task was cancelled");
        }
      });
      super.onPreExecute();
    }

    protected Integer doInBackground(Server... params) {

      // get Server from this tasks params

      mServer = params[0];
      XmlCreator xmlCreator = new XmlCreator();
      try {
        String serverModificationXml = xmlCreator.createModifyServerXML(mServer);
        Log.d("xxx", serverModificationXml);
        return HttpSender.sendModifyServerXML(prefs, serverModificationXml,
            mServer.getRepositoryId(), mServer.getId());

      } catch (XMLParserException e) {
        e.printStackTrace();
        failMessage = Strings.internalErrorMessage;
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        failMessage = Strings.internalErrorMessage;
      } catch (IllegalStateException e) {
        e.printStackTrace();
        failMessage = Strings.internalErrorMessage;
      } catch (IOException e) {
        e.printStackTrace();
        failMessage = Strings.internalErrorMessage;
      } catch (HttpSenderException e) {
        e.printStackTrace();
        failMessage = Strings.networkConnectionErrorMessage;
      } catch (HttpSenderServerErrorException e) {
        e.printStackTrace();
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
            new SendModifyServerTask(mContext).execute(mServer);
          }
        };

        builder.displayDialog();
      }

      if (result == 200) {
        GUI.displayMonit(mContext, "Server properties were modified!");
        setResult(Constants.REFRESH_ACTIVITY);
        finish();
      } else if ((result == 0) && (errorMessage != null)) {
        GUI.displayMonit(mContext, "Server error: " + errorMessage);
      } else {
        GUI.displayMonit(mContext, "Unexpected error, please try again later");
      }

    }

  }

  public class SendDeleteUserRequest extends AsyncTask<Void, Void, Integer> {

    private AsyncTask<Void, Void, Integer> thisTask = this;
    private String failMessage;
    private String errorMessage;
    private boolean failed = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(mContext, "Please wait...", "deleting server");

      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          GUI.displayMonit(mContext, "Deleting server task was cancelled");
        }
      });
      super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
      try {
        return HttpSender.sendDeleteServerRequest(prefs,
            activityServer.getRepositoryId(), activityServer.getId());

      } catch (IllegalArgumentException e) {
        failMessage = Strings.internalErrorMessage;
      } catch (IllegalStateException e) {
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
          GUI.displayMonit(mContext, "The server was deleted!");
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
