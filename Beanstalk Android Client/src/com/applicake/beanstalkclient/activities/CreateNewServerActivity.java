package com.applicake.beanstalkclient.activities;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Server;
import com.applicake.beanstalkclient.ServerEnvironment;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpSender;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderException;
import com.applicake.beanstalkclient.utils.HttpSender.HttpSenderServerErrorException;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlCreator;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

public class CreateNewServerActivity extends BeanstalkActivity implements OnClickListener {

  private ServerEnvironment mServerEnvironment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.create_new_server_layout);

    Intent intent = getIntent();
    mServerEnvironment = intent.getParcelableExtra(Constants.SERVER_ENVIRONMENT);

    // attach listener to "create" button
    ((Button) findViewById(R.id.create_button)).setOnClickListener(this);

  }

  @Override
  public void onClick(View arg0) {
    // initialize server instance with values entered into appropriate fields

    Server server = new Server();
    server.setName(((EditText) findViewById(R.id.name_edittext)).getText().toString());
    server.setLocalPath(((EditText) findViewById(R.id.local_path_edittext)).getText()
        .toString());
    server.setRemotePath(((EditText) findViewById(R.id.remote_path_edittext)).getText()
        .toString());
    server.setRemoteAddr(((EditText) findViewById(R.id.remote_addr_edittext)).getText()
        .toString());
    server.setProtocol(((Spinner) findViewById(R.id.protocol_spinner)).getSelectedItem()
        .toString().toLowerCase());
    try {
      // TODO might be a buggy solution; consider storing port as a string
      server.setPort(Integer.parseInt(((EditText) findViewById(R.id.port_edittext))
          .getText().toString()));
    } catch (NumberFormatException e) {
//      server.setPort(0);
    }
    server.setLogin(((EditText) findViewById(R.id.login_edittext)).getText().toString());
    server.setPassword(((EditText) findViewById(R.id.password_edittext)).getText()
        .toString());
    server.setUseActiveMode(((CheckBox) findViewById(R.id.use_active_mode_checkbox))
        .isChecked());
    server
        .setAuthenticateByKey(((CheckBox) findViewById(R.id.authenticate_by_key_checkbox))
            .isChecked());
    server.setUseFeat(((CheckBox) findViewById(R.id.use_feat_checkbox)).isChecked());
    server.setPreReleaseHook(((EditText) findViewById(R.id.pre_release_hook_edittext))
        .getText().toString());
    server.setPostReleaseHook(((EditText) findViewById(R.id.post_release_hook_edittext))
        .getText().toString());

    new SendNewServer(this, server).execute();

  }

  class SendNewServer extends AsyncTask<Void, Void, Integer> {

    ProgressDialog progressDialog;
    String errorMessage;

    private AsyncTask<Void, Void, Integer> thisTask;
    private Context mContext;
    private Server mServer;
    private String failMessage;
    private boolean failed;

    public SendNewServer(Context context, Server server) {
      mServer = server;
      mContext = context;
      thisTask = this;
    }

    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(mContext, "Please wait...",
          "creating repository");

      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          GUI.displayMonit(mContext, "Logging in task was cancelled");
        }
      });
    }

    protected Integer doInBackground(Void... params) {

      XmlCreator xmlCreator = new XmlCreator();

      try {

        String newServerXml = xmlCreator.createNewServerXML(mServer);

        return HttpSender.sendCreateServerXML(prefs, newServerXml,
            mServerEnvironment.getRepositoryId(), mServerEnvironment.getId());

      } catch (XMLParserException e) {
        failMessage = Strings.internalErrorMessage;
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
            new SendNewServer(mContext, mServer).execute();
          }
        };

        builder.displayDialog();
      } else {

        if (result == 201) {
          GUI.displayMonit(mContext, "Reposiotry was created successfully!");
          setResult(Constants.REFRESH_ACTIVITY);
          finish();
        }
        if ((result == 0) && (errorMessage != null))
          GUI.displayMonit(mContext, errorMessage);

      }
    }

  }

}
