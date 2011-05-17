package com.applicake.beanstalkclient.utils;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.applicake.beanstalkclient.Constants;

import android.content.SharedPreferences;
public class HttpRetriever {

	private final DefaultHttpClient httpClient = new DefaultHttpClient();
	private final String HTTP_PREFIX = "https://";
	private final String AUTH_HTTP_SUFFIX = ".beanstalkapp.com/api/users.xml";
	private final String ACTIVITY_HTTP_SUFFIX = ".beanstalkapp.com/api/changesets.xml";
	private final String SPECIFIED_REPOSITORY_ACTIVITY_HTTP_SUFFIX = ".beanstalkapp.com/api/changesets/repository.xml?repository_id=";
	private final String REPOSITORY_HTTP_SUFFIX = ".beanstalkapp.com/api/repositories.xml";
	private final String COMMENTS_HTTP_MIDDLE = ".beanstalkapp.com/api/";
	private final String COMMENTS_HTTP_SUFFIX = "/comments.xml";
	private final String COMMENTS_REVISION_HTTP_SUFFIX = "?revision=";
	

	public int checkCredentials(String domain, String username, String password) {

		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				username, password);

		String auth_http = HTTP_PREFIX + domain + AUTH_HTTP_SUFFIX;

		HttpGet getRequest = new HttpGet(auth_http);
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);
		getRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

		try {
			// parsing
			HttpResponse getResponse = httpClient.execute(getRequest);
			// response code
			int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return statusCode;
			}
			return statusCode;

		} catch (Exception e) { // TODO exception handling
			getRequest.abort();
			return 666; // exception before returning a value
		}

	}

	public String getActivityListXML(SharedPreferences prefs)
			throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);
		
		String activity_http = HTTP_PREFIX + domain + ACTIVITY_HTTP_SUFFIX;

		HttpGet getRequest = new HttpGet(activity_http);
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);

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
	
	public String getChangesetForReposiotoryXML(SharedPreferences prefs, String repoId)
	throws HttpRetreiverException {
		
		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);
		
		String activity_http = HTTP_PREFIX + domain + SPECIFIED_REPOSITORY_ACTIVITY_HTTP_SUFFIX + repoId;
		
		HttpGet getRequest = new HttpGet(activity_http);
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);
		
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

	public String getRepositoryListXML(SharedPreferences prefs)
			throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		String activity_http = HTTP_PREFIX + domain + REPOSITORY_HTTP_SUFFIX;

		HttpGet getRequest = new HttpGet(activity_http);
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);

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

	public String getCommentsListXML(SharedPreferences prefs, String repoId)
			throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		String activity_http = HTTP_PREFIX + domain + COMMENTS_HTTP_MIDDLE
				+ String.valueOf(repoId) + COMMENTS_HTTP_SUFFIX;

		HttpGet getRequest = new HttpGet(activity_http);
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);

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

	public String getCommentsListForRevisionXML(SharedPreferences prefs, String repoId, String revision)
	throws HttpRetreiverException {
		
		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);
		
		String activity_http = HTTP_PREFIX + domain + COMMENTS_HTTP_MIDDLE
		+ String.valueOf(repoId) + COMMENTS_HTTP_SUFFIX + COMMENTS_REVISION_HTTP_SUFFIX + revision;
		
		HttpGet getRequest = new HttpGet(activity_http);
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);
		
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

	public static class HttpRetreiverException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public HttpRetreiverException(String message) {
			super(message);
		}
	}

	// helper

	private static UsernamePasswordCredentials getCredentialsFromPreferences(
			SharedPreferences prefs) {
		return new UsernamePasswordCredentials(prefs.getString(Constants.USER_LOGIN, ""),
				prefs.getString(Constants.USER_PASSWORD, ""));

	}

	private static String getAccountDomain(SharedPreferences prefs) {
		return prefs.getString(Constants.USER_ACCOUNT_DOMAIN, "");
	}


}
