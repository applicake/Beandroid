package com.applicake.beanstalkclient;

import java.io.IOException;
import java.util.ArrayList;

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

import android.util.Log;

public class HttpRetriever {

	private DefaultHttpClient httpClient = new DefaultHttpClient();
	private final String HTTP_PREFIX = "https://";
	private final String AUTH_HTTP_SUFFIX = ".beanstalkapp.com/api/users.xml";
	private final String ACTIVITY_HTTP_SUFFIX = ".beanstalkapp.com/api/changesets.xml";
	private final String REPOSITORY_HTTP_SUFFIX = ".beanstalkapp.com/api/repositories.xml";
	

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

	public String getActivityListXML(String domain, String username, String password)
			throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				username, password);

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
	public String getRepositoryListXML(String domain, String username, String password)
			throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				username, password);

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

	public class HttpRetreiverException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public HttpRetreiverException(String message) {
			super(message);
		}
	}

}
