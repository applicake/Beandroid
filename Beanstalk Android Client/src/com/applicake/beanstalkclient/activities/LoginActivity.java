package com.applicake.beanstalkclient.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.applicake.beanstalkclient.Account;
import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.Permission;
import com.applicake.beanstalkclient.Plan;
import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.User;
import com.applicake.beanstalkclient.enums.UserType;
import com.applicake.beanstalkclient.permissions.PermissionsData;
import com.applicake.beanstalkclient.permissions.PermissionsPersistenceUtil;
import com.applicake.beanstalkclient.utils.GUI;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpImproperStatusCodeException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

public class LoginActivity extends Activity implements OnClickListener {

  private EditText domainaccountEditText;
  private EditText loginEditText;
  private EditText passwordEditText;

  private Context mContext;
  private SharedPreferences prefs;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = this;
    prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    setContentView(R.layout.login_activity_layout);
    // auto login with previously stored user data

    // TODO optimize memory usage

    if (prefs.getBoolean(Constants.CREDENTIALS_STORED, false)) {
      // check credentials validity

      String storedDomain = prefs.getString(Constants.USER_ACCOUNT_DOMAIN, "");
      String storedLogin = prefs.getString(Constants.USER_LOGIN, "");
      String storedPassword = prefs.getString(Constants.USER_PASSWORD, "");
      new VerifyLoginTask().execute(storedDomain, storedLogin, storedPassword);
    }

    Button loginButton = (Button) findViewById(R.id.login_button);
    loginButton.setOnClickListener(this);

    domainaccountEditText = (EditText) findViewById(R.id.accountdomain_edittext);
    loginEditText = (EditText) findViewById(R.id.login_edittext);
    passwordEditText = (EditText) findViewById(R.id.password_edittext);

    // custom input filter that allows only alphanumeric characters and "-"
    // character, but not in the beginning or the end of the string
    // TODO
    // create better implementation

    InputFilter httpAddressFilter = new InputFilter() {

      public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
          int dstart, int dend) {

        for (int i = start; i < end; i++) {
          if (!Character.isLetterOrDigit(source.charAt(i)) && !(source.charAt(i) == '-')) {
            return "";
          }
          // if (((dstart == 0) || (dend == dest.length()))
          // && (source.charAt(i) == '-')) {
          // return "";
          // }
        }
        return null;
      }

    };

    domainaccountEditText.setFilters(new InputFilter[] { httpAddressFilter });
  }

  public void onClick(View v) {
    if (v.getId() == R.id.login_button) {

      String login = loginEditText.getText().toString();
      String password = passwordEditText.getText().toString();
      String domain = domainaccountEditText.getText().toString();

      new VerifyLoginTask().execute(domain, login, password);

    }
  }

  public void clearCredentials() {
    Editor editor = prefs.edit();
    editor.putBoolean(Constants.CREDENTIALS_STORED, false);
    editor.putString(Constants.USER_ACCOUNT_DOMAIN, "");
    editor.putString(Constants.USER_LOGIN, "");
    editor.putString(Constants.USER_PASSWORD, "");
    editor.commit();
  }
  
  public AuthorizationData authenticateAndCheckUserType(String domain, String username, String password) 
      throws HttpConnectionErrorException, XMLParserException, HttpImproperStatusCodeException, UnsuccessfulServerResponseException {
    
    AuthorizationData authData = new AuthorizationData();
    
    String userInfoXML = HttpRetriever.checkCredentialsUser(domain, username, password);
    User currentUser = XmlParser.parseCurrentUser(userInfoXML);
    
    String permissionsXml = HttpRetriever.getPermissionsListForUserXML(domain, username, password, String.valueOf(currentUser.getId()));
    List<Permission> permissions = XmlParser.parsePermissionList(permissionsXml);
    
    authData.setPermissions(permissions);
    authData.setUser(currentUser);

    if(currentUser.getUserType() == UserType.OWNER) {
      String accountInfoXML = HttpRetriever.checkCredentialsAccount(domain, username, password);
      Account account = XmlParser.parseAccountInfo(accountInfoXML);
      String plansXML = HttpRetriever.checkCredentialsPlan(domain, username, password);
      HashMap<Integer, Plan> plansMap = XmlParser.parsePlan(plansXML);
      Plan currentPlan = plansMap.get(account.getPlanId());
      authData.setPlan(currentPlan);
      authData.setAccount(account);
    } 
    
    return authData;
  }
  
  public class VerifyLoginTask extends AsyncTask<String, Void, Integer> {

    private String domain;
    private String login;
    private String password;

    @SuppressWarnings("rawtypes")
    private AsyncTask thisTask = this;
    private boolean failed = false;;
    private String failMessage;
    private ProgressDialog progressDialog;
    private AuthorizationData authData;
    
    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(mContext, "Checking login credentials",
          "Please wait...");
      progressDialog.setCancelable(true);
      progressDialog.setOnCancelListener(new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
          thisTask.cancel(true);
          GUI.displayMonit(mContext, "Logging in task was cancelled");
        }
      });
    }

    @Override
    protected Integer doInBackground(String... params) {

      domain = params[0];
      login = params[1];
      password = params[2];
      try {
        authData = authenticateAndCheckUserType(domain, login, password);
        PermissionsPersistenceUtil permissionsUtil = new PermissionsPersistenceUtil(LoginActivity.this);
        permissionsUtil.savePermissionsDataToFile(authData.getPermissionsData());
        return 200;
      } catch (XMLParserException e) {
        failMessage = Strings.internalErrorMessage;
      } catch (HttpImproperStatusCodeException e) {
        return e.getStatusCode();
      } catch (HttpConnectionErrorException e) {
        failMessage = Strings.networkConnectionErrorMessage;
      } catch(UnsuccessfulServerResponseException e) {
        failMessage = Strings.networkConnectionErrorMessage; // TODO - refactor
      } catch(IOException e) {
        failMessage = Strings.internalErrorMessage;
      }

      failed = true;
      return null;

    }

    @Override
    protected void onPostExecute(Integer result) {
      Log.d("LoginActivity", "Did download task fail: " + String.valueOf(failed));
      progressDialog.dismiss();
      
      if (failed) {
        SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext,
            failMessage) {

          @Override
          public void retryAction() {
            new VerifyLoginTask().execute(domain, login, password);
          }
        };
        builder.displayDialog();

      } else {

        if ((result == HttpStatus.SC_OK) && (authData != null)) {

          User user = authData.getUser();
          UserType userType = user.getUserType();
          
          GUI.displayMonit(mContext, "Access granted");
          Editor editor = prefs.edit();
          editor.putString(Constants.USER_ACCOUNT_DOMAIN, domain);
          editor.putString(Constants.USER_LOGIN, login);
          editor.putString(Constants.USER_PASSWORD, password);
          editor.putBoolean(Constants.CREDENTIALS_STORED, true);
          editor.putString(Constants.USER_TYPE, userType.name());
          if (userType == UserType.OWNER) {
            Plan currentPlan = authData.getPlan();
            Account account = authData.getAccount();
            
            editor.putInt(Constants.PLAN_ID, currentPlan.getId());
            
            editor.putInt(Constants.NUMBER_OF_REPOS_AVAILABLE,
                currentPlan.getNumberOfRepos());
            editor.putInt(Constants.NUMBER_OF_USERS_AVAILABLE,
                currentPlan.getNumberOfUsers());
            editor.putString(Constants.USER_TIMEZONE, account.getTimeZone());
          }
          editor.commit();
          
          //PermissionsHolder.getInstance().setPermissions(authData.getPermissions());
          //PermissionsHolder.getInstance().setLoggedUser(authData.getUser());

          Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
          startActivityForResult(intent, 0);
          finish();

        } else if (result == 302) {
          GUI.displayMonit(mContext, "Invalid account domain");
        } else if (result == 401) {
          GUI.displayMonit(mContext, "Invalid username or password");
        } else if (result == 500) {
          GUI.displayMonit(mContext,
              "You must have Developer API enabled in your Beanstalk account settings");
        } else {
          GUI.displayMonit(mContext, "Server error: " + result);
        }

      }

    }
  }
  
  public class AuthorizationData {
    
    private Plan plan;
    private PermissionsData permissionsData = new PermissionsData();
    private Account account;
    
    public User getUser() {
      return permissionsData.getUser();
    }
    
    public void setUser(User user) {
      permissionsData.setUser(user);
    }
    
    public void setPlan(Plan plan) {
      this.plan = plan;
    }
    
    public Plan getPlan() {
      return plan;
    }

    public Account getAccount() {
      return account;
    }

    public void setAccount(Account account) {
      this.account = account;
    }

    public List<Permission> getPermissions() {
      return permissionsData.getPermissions();
    }

    public void setPermissions(List<Permission> permissions) {
      permissionsData.setPermissions((ArrayList<Permission>)permissions);
    }
    
    public PermissionsData getPermissionsData() {
      return permissionsData;
    }
  }

}