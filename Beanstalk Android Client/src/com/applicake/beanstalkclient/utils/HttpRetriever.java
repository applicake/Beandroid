package com.applicake.beanstalkclient.utils;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import com.applicake.beanstalkclient.Constants;

import android.content.SharedPreferences;
import android.util.Log;

public class HttpRetriever {

	public static DefaultHttpClient getClient() {
		DefaultHttpClient httpClient = null;

		// sets up parameters
		final HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "utf-8");
		HttpClientParams.setRedirecting(params, false);
		params.setBooleanParameter("http.protocol.expect-continue", false);

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
		return httpClient;
	}

	final static DefaultHttpClient httpClient = getClient();

	private static final String HTTP_PREFIX = "https://";
	private static final String AUTH_HTTP_SUFFIX = ".beanstalkapp.com/api/account.xml";
	private static final String USERS_HTTP_SUFFIX = ".beanstalkapp.com/api/users.xml";
	private static final String PERMISSIONS_FOR_USER_HTTP_SUFFIX = ".beanstalkapp.com/api/permissions/";
	private static final String ACTIVITY_HTTP_SUFFIX = ".beanstalkapp.com/api/changesets.xml";
	private static final String SPECIFIED_REPOSITORY_ACTIVITY_HTTP_SUFFIX = ".beanstalkapp.com/api/changesets/repository.xml?repository_id=";
	private static final String REPOSITORY_HTTP_SUFFIX = ".beanstalkapp.com/api/repositories.xml";
	private static final String REPOSITORY_HTTP_MIDDLE = ".beanstalkapp.com/api/repositories/";
	private static final String COMMENTS_HTTP_MIDDLE = ".beanstalkapp.com/api/";
	private static final String COMMENTS_HTTP_SUFFIX = "/comments.xml";
	private static final String COMMENTS_REVISION_HTTP_SUFFIX = "?revision=";


	public static String checkCredentials(String domain, String username, String password)
			throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				username, password);

		String auth_http = HTTP_PREFIX + domain + AUTH_HTTP_SUFFIX;

		HttpGet getRequest = new HttpGet(auth_http);
		getRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

		try {
			// parsing
			HttpResponse getResponse = httpClient.execute(getRequest);
			// response code
			int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return EntityUtils.toString(getResponse.getEntity());
			}
			throw new HttpRetreiverException(String.valueOf(statusCode));

		} catch (ClientProtocolException cpe) { // TODO exception handling
			cpe.printStackTrace();
			getRequest.abort();
			throw new HttpRetreiverException("0");
			
		} catch (IOException ioe) { // TODO exception handling
			ioe.printStackTrace();
			getRequest.abort();
			throw new HttpRetreiverException("0");
		}

	}

	public static String getUserListXML(SharedPreferences prefs) throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		String auth_http = HTTP_PREFIX + domain + USERS_HTTP_SUFFIX;

		HttpGet getRequest = new HttpGet(auth_http);
		// final HttpParams params = new BasicHttpParams();
		// httpClient.setParams(params);
		// HttpClientParams.setRedirecting(params, false);
		getRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

		try {
			// parsing
			HttpResponse getResponse = httpClient.execute(getRequest);
			// response code
			int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return EntityUtils.toString(getResponse.getEntity());
			} else
				throw new Exception("Http connection error");

		} catch (IOException io) {
			// TODO handle various HTTP exceptions
			getRequest.abort();
			throw new HttpRetreiverException("Http parsing IOException");
		} catch (Exception e) {
			getRequest.abort();
			e.printStackTrace();
			throw new HttpRetreiverException("Http parsing exception");

		}
	}

	public static String getActivityListXML(SharedPreferences prefs, int pageNumber)
			throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		String activity_http = HTTP_PREFIX + domain + ACTIVITY_HTTP_SUFFIX + "?page=" + String.valueOf(pageNumber);
		Log.w("Beansdroid", activity_http);
		
		HttpGet getRequest = new HttpGet(activity_http);
		getRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

		try {
			// parsing
			HttpResponse getResponse = httpClient.execute(getRequest);
			// response code
			int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return EntityUtils.toString(getResponse.getEntity());
			} else
				throw new Exception("Http connection error");

		} catch (IOException io) {
			getRequest.abort();
			throw new HttpRetreiverException("Http parsing IOException");
		} catch (Exception e) {
			getRequest.abort();
			e.printStackTrace();
			throw new HttpRetreiverException("Http parsing exception");

		}

	}

	public static String getChangesetForReposiotoryXML(SharedPreferences prefs, String repoId)
			throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		String activity_http = HTTP_PREFIX + domain
				+ SPECIFIED_REPOSITORY_ACTIVITY_HTTP_SUFFIX + repoId;

		HttpGet getRequest = new HttpGet(activity_http);
		getRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

		try {
			// parsing
			HttpResponse getResponse = httpClient.execute(getRequest);
			// response code
			int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return EntityUtils.toString(getResponse.getEntity());
			} else
				throw new Exception("Http connection error");

		} catch (IOException io) {
			getRequest.abort();
			throw new HttpRetreiverException("Http parsing IOException");
		} catch (Exception e) {
			getRequest.abort();
			e.printStackTrace();
			throw new HttpRetreiverException("Http parsing exception");
		}
	}

	public static String getRepositoryListXML(SharedPreferences prefs)
			throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		String activity_http = HTTP_PREFIX + domain + REPOSITORY_HTTP_SUFFIX;

		HttpGet getRequest = new HttpGet(activity_http);

		getRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

		try {
			// parsing
			HttpResponse getResponse = httpClient.execute(getRequest);
			// response code
			int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return EntityUtils.toString(getResponse.getEntity());
			} else
				throw new Exception("Http connection error");

		} catch (IOException io) {
			getRequest.abort();
			throw new HttpRetreiverException("Http parsing IOException");
		} catch (Exception e) {
			getRequest.abort();
			e.printStackTrace();
			throw new HttpRetreiverException("Http parsing exception");

		}
	}

	public static String getRepositoryXML(SharedPreferences prefs, int repoId)
			throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		String activity_http = HTTP_PREFIX + domain + REPOSITORY_HTTP_MIDDLE
				+ String.valueOf(repoId) + ".xml";

		HttpGet getRequest = new HttpGet(activity_http);

		getRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

		try {
			// parsing
			HttpResponse getResponse = httpClient.execute(getRequest);
			// response code
			int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return EntityUtils.toString(getResponse.getEntity());
			} else
				throw new Exception("Http connection error");

		} catch (IOException io) {
			getRequest.abort();
			throw new HttpRetreiverException("Http parsing IOException");
		} catch (Exception e) {
			getRequest.abort();
			e.printStackTrace();
			throw new HttpRetreiverException("Http parsing exception");

		}

	}

	public static String getCommentsListXML(SharedPreferences prefs, String repoId)
			throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		String activity_http = HTTP_PREFIX + domain + COMMENTS_HTTP_MIDDLE
				+ String.valueOf(repoId) + COMMENTS_HTTP_SUFFIX;

		HttpGet getRequest = new HttpGet(activity_http);

		getRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

		try {
			// parsing
			HttpResponse getResponse = httpClient.execute(getRequest);
			// response code
			int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return EntityUtils.toString(getResponse.getEntity());
			} else
				throw new Exception("Http connection error");

		} catch (IOException io) {
			getRequest.abort();
			throw new HttpRetreiverException("Http parsing IOException");
		} catch (Exception e) {
			getRequest.abort();
			e.printStackTrace();
			throw new HttpRetreiverException("Http parsing exception");

		}

	}

	public static String getCommentsListForRevisionXML(SharedPreferences prefs, String repoId,
			String revision, int pageNumber) throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		String comments_http = HTTP_PREFIX + domain + COMMENTS_HTTP_MIDDLE
				+ String.valueOf(repoId) + COMMENTS_HTTP_SUFFIX
				+ COMMENTS_REVISION_HTTP_SUFFIX + revision + "&page=" + String.valueOf(pageNumber);
		
		HttpGet getRequest = new HttpGet(comments_http);
		
		getRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

		try {
			// parsing
			HttpResponse getResponse = httpClient.execute(getRequest);
			// response code
			int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return EntityUtils.toString(getResponse.getEntity());
			} else
				throw new HttpRetreiverException(getResponse.getStatusLine().getReasonPhrase());

		} catch (IOException io) {
			getRequest.abort();
			throw new HttpRetreiverException("Http connection error");
		} 

	}

	public static String getPermissionListForUserXML(SharedPreferences prefs, String userId)
			throws HttpConnectionErrorException, UnsuccessfulServerResponseException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		String activity_http = HTTP_PREFIX + domain + PERMISSIONS_FOR_USER_HTTP_SUFFIX
				+ String.valueOf(userId) + ".xml";

		HttpGet getRequest = new HttpGet(activity_http);

		getRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

		try {
			// parsing
			HttpResponse getResponse = httpClient.execute(getRequest);
			// response code
			int statusCode = getResponse.getStatusLine().getStatusCode();
			// Log.w("status code", String.valueOf(statusCode));
			if (statusCode == HttpStatus.SC_OK) {
				return EntityUtils.toString(getResponse.getEntity());
			} else
				throw new UnsuccessfulServerResponseException(getResponse.getStatusLine().getReasonPhrase());

		} catch (IOException io) {
			// TODO handle various HTTP exceptions
			getRequest.abort();
			io.printStackTrace();
			throw new HttpConnectionErrorException(io);
		} 

	}

	public static class HttpRetreiverException extends Exception {
		
		private static final long serialVersionUID = 1L;

		public HttpRetreiverException(String message) {
			super(message);
		}
	}
	
	public static class UnsuccessfulServerResponseException extends Exception {
		
		public UnsuccessfulServerResponseException(String reasonPhrase){
			super(reasonPhrase);
			
		}
		
	}
	
	public static class HttpConnectionErrorException extends Exception {
		
		public HttpConnectionErrorException(Exception e){
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
