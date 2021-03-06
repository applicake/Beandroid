package com.applicake.beanstalkclient.utils;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.content.SharedPreferences;
import android.util.Log;

import com.applicake.beanstalkclient.Constants;

public class HttpRetriever {

  public static HttpClient getClient(boolean allowRedirection) {
    DefaultHttpClient httpClient = null;

    // sets up parameters
    final HttpParams params = new BasicHttpParams();
    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
    HttpProtocolParams.setContentCharset(params, "utf-8");

    HttpClientParams.setRedirecting(params, allowRedirection);
    params.setBooleanParameter("http.protocol.expect-continue", false);

    HttpConnectionParams.setStaleCheckingEnabled(params, false);
    HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
    HttpConnectionParams.setSoTimeout(params, 20 * 1000);
    HttpConnectionParams.setSocketBufferSize(params, 8192);

    // registers schemes for both http and https
    SchemeRegistry registry = new SchemeRegistry();
    registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
    sslSocketFactory
        .setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    registry.register(new Scheme("https", sslSocketFactory, 443));

    ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params,
        registry);

    httpClient = new DefaultHttpClient(manager, params);
    httpClient.removeRequestInterceptorByClass(RequestAddCookies.class);

    return httpClient;
  }

  // final static HttpClient httpClient = AndroidHttpClient.newInstance("");
  final static HttpClient httpClient = getClient(true);
  final static HttpClient noRedirectionHttpClient = getClient(false);

  private static final String HTTP_PREFIX = "https://";
  private static final String ACCOUNT_HTTP_SUFFIX = ".beanstalkapp.com/api/account.xml";
  private static final String USERS_HTTP_SUFFIX = ".beanstalkapp.com/api/users.xml";
  private static final String PLANS_HTTP_SUFFIX = ".beanstalkapp.com/api/plans.xml";
  private static final String CURRENT_USER_HTTP_SUFFIX = ".beanstalkapp.com/api/users/current.xml";
  private static final String PERMISSIONS_FOR_USER_HTTP_SUFFIX = ".beanstalkapp.com/api/permissions/";
  private static final String ACTIVITY_HTTP_SUFFIX = ".beanstalkapp.com/api/changesets.xml";
  private static final String SPECIFIED_REPOSITORY_ACTIVITY_HTTP_SUFFIX = ".beanstalkapp.com/api/changesets/repository.xml?repository_id=";
  private static final String SINGLE_CHANGESET_HTTP_SUFFIX = ".beanstalkapp.com/api/changesets/%s.xml?repository_id=%d";

  private static final String REPOSITORY_HTTP_SUFFIX = ".beanstalkapp.com/api/repositories.xml";
  private static final String REPOSITORY_HTTP_MIDDLE = ".beanstalkapp.com/api/repositories/";

  private static final String COMMENTS_HTTP_MIDDLE = ".beanstalkapp.com/api/";
  private static final String COMMENTS_HTTP_SUFFIX = "/comments.xml";
  private static final String COMMENTS_REVISION_HTTP_SUFFIX = "?revision=";

  private static final String RELEASES_HTTP_MIDDLE = ".beanstalkapp.com/api/";
  private static final String RELEASES_HTTP_SUFFIX = "/releases.xml";

  private static final String SERVER_ENVIRONMENT_FOR_REPOSITORY_HTTP_MIDDLE = ".beanstalkapp.com/api/";
  private static final String SERVER_ENVIRONMENT_FOR_REPOSITORY_HTTP_SUFFIX = "/server_environments.xml";

  private static final String SERVERS_FOR_ENVIRONMENT_HTTP_MIDDLE = ".beanstalkapp.com/api/";
  private static final String SERVERS_FOR_ENVIRONMENT_FOR_REPOSITORY_HTTP_SUFFIX = "/release_servers.xml?environment_id=";
  
  // checking credentials for Owner user
  public static String checkCredentialsAccount(String domain, String username,
      String password) throws HttpImproperStatusCodeException,
      HttpConnectionErrorException {

    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username,
        password);

    String auth_http = HTTP_PREFIX + domain + ACCOUNT_HTTP_SUFFIX;

    HttpGet getRequest = new HttpGet(auth_http);
    getRequest.setHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
    HttpResponse getResponse = null;
    try {
      // parsing
      getResponse = noRedirectionHttpClient.execute(getRequest);
      for (Header h : getRequest.getAllHeaders()) {
        Log.w("request", h.getName() + " " + h.getValue());
      }

      // response code
      int statusCode = getResponse.getStatusLine().getStatusCode();

      // Log.w("responsecache", ResponseCache.getDefault().toString());
      Log.w("credentials", username + " " + password);
      Log.w("login attempt result - account", String.valueOf(statusCode));
      if (statusCode == HttpStatus.SC_OK) {
        String response = EntityUtils.toString(getResponse.getEntity());
        getResponse.getEntity().consumeContent();
        Log.w("login attempt response - account", response);
        return response;
      }
      throw new HttpImproperStatusCodeException(statusCode);

    } catch (IOException ioe) {
      throw new HttpConnectionErrorException(ioe);
    } finally {
      if (getResponse != null)
        try {
          getResponse.getEntity().consumeContent();
        } catch (IOException e) {
          throw new HttpConnectionErrorException(e);
        }
    }
  }
  
  // checking credentials for Admins
  public static String checkCredentialsUser(String domain, String username,
      String password) throws HttpImproperStatusCodeException,
      HttpConnectionErrorException {

    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username,
        password);

    String auth_http = HTTP_PREFIX + domain + CURRENT_USER_HTTP_SUFFIX;

    HttpGet getRequest = new HttpGet(auth_http);
    getRequest.setHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
    HttpResponse getResponse = null;
    try {
      // parsing
      getResponse = noRedirectionHttpClient.execute(getRequest);
      Log.w("request", getRequest.toString());
      // response code
      int statusCode = getResponse.getStatusLine().getStatusCode();
      Log.w("login attempt result - user", String.valueOf(statusCode));
      if (statusCode == HttpStatus.SC_OK) {
        String response = EntityUtils.toString(getResponse.getEntity());
        getResponse.getEntity().consumeContent();
        Log.w("login attempt response - user", response);
        return response;
      }
      throw new HttpImproperStatusCodeException(statusCode);

    } catch (IOException ioe) {
      throw new HttpConnectionErrorException(ioe);
    } finally {
      if (getResponse != null)
        try {
          getResponse.getEntity().consumeContent();
        } catch (IOException e) {
          throw new HttpConnectionErrorException(e);
        }
    }
  }

  // checking credentials for regular user
  public static String checkCredentialsPlan(String domain, String username,
      String password) throws HttpImproperStatusCodeException,
      HttpConnectionErrorException {

    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username,
        password);

    String auth_http = HTTP_PREFIX + domain + PLANS_HTTP_SUFFIX;

    HttpGet getRequest = new HttpGet(auth_http);
    getRequest.setHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
    HttpResponse getResponse = null;

    try {
      // parsing

      getResponse = noRedirectionHttpClient.execute(getRequest);
      Log.w("request", getRequest.toString());
      // response code
      int statusCode = getResponse.getStatusLine().getStatusCode();
      Log.w("login attempt result - plan", String.valueOf(statusCode));

      if (statusCode == HttpStatus.SC_OK) {
        String response = EntityUtils.toString(getResponse.getEntity());
        getResponse.getEntity().consumeContent();
        Log.w("login attempt response - plan", response);

        return response;
      }
      throw new HttpImproperStatusCodeException(statusCode);

    } catch (IOException ioe) {
      throw new HttpConnectionErrorException(ioe);

    } finally {
      if (getResponse != null)
        try {
          getResponse.getEntity().consumeContent();
        } catch (IOException e) {
          throw new HttpConnectionErrorException(e);
        }
    }

  }

  public static String getAccountInfo(SharedPreferences prefs)
      throws HttpImproperStatusCodeException, HttpConnectionErrorException,
      UnsuccessfulServerResponseException {
    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);

    String auth_http = HTTP_PREFIX + domain + ACCOUNT_HTTP_SUFFIX;

    return executeRequest(credentials, auth_http);

  }

  public static String getUserListXML(SharedPreferences prefs)
      throws UnsuccessfulServerResponseException, HttpConnectionErrorException {

    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);

    String auth_http = HTTP_PREFIX + domain + USERS_HTTP_SUFFIX;

    return executeRequest(credentials, auth_http);

  }

  public static String getAvailablePlans(SharedPreferences prefs)
      throws UnsuccessfulServerResponseException, HttpConnectionErrorException {
    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);

    String domain = getAccountDomain(prefs);

    String url = HTTP_PREFIX + domain + PLANS_HTTP_SUFFIX;

    return executeRequest(credentials, url);

  }

  public static String getActivityListXML(SharedPreferences prefs, int pageNumber)
      throws UnsuccessfulServerResponseException, HttpConnectionErrorException {

    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);

    String activity_http = HTTP_PREFIX + domain + ACTIVITY_HTTP_SUFFIX + "?page="
        + String.valueOf(pageNumber);
    Log.w("Beansdroid", activity_http);

    return executeRequest(credentials, activity_http);

  }

  public static String getChangesetForReposiotoryXML(SharedPreferences prefs,
      String repoId) throws UnsuccessfulServerResponseException,
      HttpConnectionErrorException {

    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);

    String activity_http = HTTP_PREFIX + domain
        + SPECIFIED_REPOSITORY_ACTIVITY_HTTP_SUFFIX + repoId;

    return executeRequest(credentials, activity_http);
  }

  public static String getSingleChangesetXml(SharedPreferences prefs,
      String revisionNumber, int repositoryId)
      throws UnsuccessfulServerResponseException, HttpConnectionErrorException {

    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);

    // TODO got error because SINGLE_CHANGESET_HTTP_SUFFIX didn't include domain
    // name, what test could help avoid this?
    String activity_http = HTTP_PREFIX + domain
        + String.format(SINGLE_CHANGESET_HTTP_SUFFIX, revisionNumber, repositoryId);

    return executeRequest(credentials, activity_http);

  }

  public static String getRepositoryListXML(SharedPreferences prefs)
      throws UnsuccessfulServerResponseException, HttpConnectionErrorException {

    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);

    String activity_http = HTTP_PREFIX + domain + REPOSITORY_HTTP_SUFFIX;

    return executeRequest(credentials, activity_http);
  }

  public static String getRepositoryXML(SharedPreferences prefs, int repoId)
      throws UnsuccessfulServerResponseException, HttpConnectionErrorException {

    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);

    String activity_http = HTTP_PREFIX + domain + REPOSITORY_HTTP_MIDDLE
        + String.valueOf(repoId) + ".xml";

    return executeRequest(credentials, activity_http);
  }

  public static String getCommentsListXML(SharedPreferences prefs, String repoId)
      throws UnsuccessfulServerResponseException, HttpConnectionErrorException {

    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);

    String activity_http = HTTP_PREFIX + domain + COMMENTS_HTTP_MIDDLE
        + String.valueOf(repoId) + COMMENTS_HTTP_SUFFIX;

    return executeRequest(credentials, activity_http);

  }

  public static String getCommentsListForRevisionXML(SharedPreferences prefs,
      String repoId, String revision, int pageNumber)
      throws UnsuccessfulServerResponseException, HttpConnectionErrorException {

    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);

    String comments_http = HTTP_PREFIX + domain + COMMENTS_HTTP_MIDDLE
        + String.valueOf(repoId) + COMMENTS_HTTP_SUFFIX + COMMENTS_REVISION_HTTP_SUFFIX
        + revision + "&page=" + String.valueOf(pageNumber);

    return executeRequest(credentials, comments_http);

  }

  public static String getPermissionListForUserXML(SharedPreferences prefs, String userId)
      throws HttpConnectionErrorException, UnsuccessfulServerResponseException {

    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);
    
    return getPermissionsListForUserXML(credentials, domain, userId);
  }
  
  public static String getPermissionsListForUserXML(String domain, String user, String password, String userId)
    throws HttpConnectionErrorException, UnsuccessfulServerResponseException {
    
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, password);
    return getPermissionsListForUserXML(credentials, domain, userId);
    
  }
  
  private static String getPermissionsListForUserXML(UsernamePasswordCredentials credentials, String domain, String userId) 
      throws HttpConnectionErrorException, UnsuccessfulServerResponseException {
    String activity_http = HTTP_PREFIX + domain + PERMISSIONS_FOR_USER_HTTP_SUFFIX
        + String.valueOf(userId) + ".xml";

    return executeRequest(credentials, activity_http);
  }
  
  public static String getReleasesListForRepositoryXML(SharedPreferences prefs, int repoId)
      throws HttpConnectionErrorException, UnsuccessfulServerResponseException {

    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);

    String activity_http = HTTP_PREFIX + domain + RELEASES_HTTP_MIDDLE
        + String.valueOf(repoId) + RELEASES_HTTP_SUFFIX;

    return executeRequest(credentials, activity_http);

  }

  public static String getReleasesListForAllRepositories(SharedPreferences prefs)
      throws HttpConnectionErrorException, UnsuccessfulServerResponseException {

    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);

    String activity_http = HTTP_PREFIX + domain + RELEASES_HTTP_MIDDLE
        + RELEASES_HTTP_SUFFIX;

    return executeRequest(credentials, activity_http);
  }

  public static String getServerEnvironmentListForRepositoryXML(SharedPreferences prefs,
      int repoId) throws HttpConnectionErrorException,
      UnsuccessfulServerResponseException {

    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);

    String activity_http = HTTP_PREFIX + domain
        + SERVER_ENVIRONMENT_FOR_REPOSITORY_HTTP_MIDDLE + String.valueOf(repoId)
        + SERVER_ENVIRONMENT_FOR_REPOSITORY_HTTP_SUFFIX;

    return executeRequest(credentials, activity_http);
  }

  public static String getServerEnvironmentListForRepositoryXML(SharedPreferences prefs,
      String repoId) throws UnsuccessfulServerResponseException,
      HttpConnectionErrorException {
    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);

    String activity_http = HTTP_PREFIX + domain
        + SERVER_ENVIRONMENT_FOR_REPOSITORY_HTTP_MIDDLE + String.valueOf(repoId)
        + SERVER_ENVIRONMENT_FOR_REPOSITORY_HTTP_SUFFIX;

    return executeRequest(credentials, activity_http);
  }

  public static String getServerListForEnviromentXML(SharedPreferences prefs, int repoId,
      int environmentId) throws HttpConnectionErrorException,
      UnsuccessfulServerResponseException {

    UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
    String domain = getAccountDomain(prefs);

    String activity_http = HTTP_PREFIX + domain + SERVERS_FOR_ENVIRONMENT_HTTP_MIDDLE
        + String.valueOf(repoId) + SERVERS_FOR_ENVIRONMENT_FOR_REPOSITORY_HTTP_SUFFIX
        + String.valueOf(environmentId);

    return executeRequest(credentials, activity_http);
  }

  private static String executeRequest(UsernamePasswordCredentials credentials,
      String httpAddress) throws UnsuccessfulServerResponseException,
      HttpConnectionErrorException {
    HttpGet getRequest = new HttpGet(httpAddress);
    Log.d("http", httpAddress);
    getRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
    HttpResponse getResponse = null;
    try {
      // parsing
      getResponse = httpClient.execute(getRequest);
      // response code
      int statusCode = getResponse.getStatusLine().getStatusCode();
      // Log.w("status code", String.valueOf(statusCode));
      if (statusCode == HttpStatus.SC_OK) {
        return EntityUtils.toString(getResponse.getEntity());
      } else
        throw new UnsuccessfulServerResponseException(getResponse.getStatusLine()
            .getReasonPhrase());

    } catch (IOException ioe) {
      throw new HttpConnectionErrorException(ioe);
    } finally {
      if (getResponse != null)
        try {
          getResponse.getEntity().consumeContent();
        } catch (IOException e) {
          throw new HttpConnectionErrorException(e);
        }
    }
  }

  // this exception is passing the status code of failed Http Request up the
  // activity stack
  public static class HttpImproperStatusCodeException extends Exception {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;
    private int statusCode;

    public HttpImproperStatusCodeException(int statusCode) {
      super();
      this.statusCode = statusCode;
    }

    public int getStatusCode() {
      return statusCode;
    }

  }

  // exception thrown by this class after receiving improper response from
  // server
  public static class UnsuccessfulServerResponseException extends Exception {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;

    public UnsuccessfulServerResponseException(String reasonPhrase) {
      super(reasonPhrase);

    }

  }

  // exception thrown after unsuccessful connection with http server (i.e. no
  // internet connection)
  public static class HttpConnectionErrorException extends Exception {

    /**
		 * 
		 */
    private static final long serialVersionUID = 1L;

    public HttpConnectionErrorException(Exception e) {
      super(e);
    }

  }

  // helpers

  private static UsernamePasswordCredentials getCredentialsFromPreferences(
      SharedPreferences prefs) {
    return new UsernamePasswordCredentials(prefs.getString(Constants.USER_LOGIN, ""),
        prefs.getString(Constants.USER_PASSWORD, ""));

  }

  private static String getAccountDomain(SharedPreferences prefs) {
    return prefs.getString(Constants.USER_ACCOUNT_DOMAIN, "");
  }

}
