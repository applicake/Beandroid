package com.applicake.beanstalkclient.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import com.applicake.beanstalkclient.Constants;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

import android.content.SharedPreferences;
import android.util.Log;

public class HttpSender {

	private final static String HTTPS_PREFIX = "https://";
	private final static String COMMENTS_HTTP_MIDDLE = ".beanstalkapp.com/api/";
	private final static String COMMENTS_HTTP_SUFFIX = "/comments.xml";
	private final static String REPOSITORY_CREATE_HTTP_SUFFIX = ".beanstalkapp.com/api/repositories.xml";
	private final static String REPOSITORY_UPDATE_HTTP_MIDDLE = ".beanstalkapp.com/api/repositories/";
	private final static String USER_UPDATE_HTTP_MIDDLE = ".beanstalkapp.com/api/users/";
	private final static String USER_DELETE_HTTP_MIDDLE = ".beanstalkapp.com/api/users/";
	private final static String USER_CREATE_HTTP_SUFFIX = ".beanstalkapp.com/api/users.xml";
	private final static String PERMISSION_CREATE_HTTP_SUFFIX = ".beanstalkapp.com/api/permissions.xml";
	private final static String PERMISSION_DELETE_HTTP_MIDDLE = ".beanstalkapp.com/api/permissions/";
	private static final String ACCOUNT_UPDATE_HTTP_SUFFIX = ".beanstalkapp.com/api/account.xml";

	private static final DefaultHttpClient httpClient = getClient();

	public static DefaultHttpClient getClient() {
		DefaultHttpClient httpClient = null;

		// sets up parameters
		final HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "utf-8");

		HttpClientParams.setRedirecting(params, false);
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

	public static String sendCommentXML(SharedPreferences prefs, String xml, String repoId)
			throws UnsupportedEncodingException, HttpSenderException, XMLParserException,
			HttpSenderServerErrorException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		// create POST request with address
		HttpPost postRequest = new HttpPost(HTTPS_PREFIX + domain + COMMENTS_HTTP_MIDDLE
				+ repoId + COMMENTS_HTTP_SUFFIX);

		// add auth headers
		postRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
		postRequest.addHeader("Content-Type", "application/xml");

		// add xml entity
		StringEntity se = new StringEntity(xml, "UTF-8");
		postRequest.setEntity(se);

		// TODO throw exceptions if an error occurs
		try {
			HttpResponse postResponse = httpClient.execute(postRequest);
			Log.w("postRequestXml", xml);
			String entity = EntityUtils.toString(postResponse.getEntity());
			Log.w("postResponse", entity);
			int statusCode = postResponse.getStatusLine().getStatusCode();

			if (statusCode == 422) {
				StringBuilder sb = new StringBuilder();
				for (String s : XmlParser.parseErrors(entity)) {
					sb.append(s);
					sb.append("\n");
				}

				throw new HttpSenderServerErrorException(sb.toString());
			} else if (statusCode == 201) {
				return entity;
			} else {
				throw new HttpSenderException("Incorrect response from server");
			}

		} catch (IOException e) {
			postRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("IOException");
		}

	}

	public static int sendCreateNewRepostiroyXML(SharedPreferences prefs, String xml)
			throws UnsupportedEncodingException, HttpSenderException, XMLParserException,
			HttpSenderServerErrorException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		// create POST request with address
		HttpPost postRequest = new HttpPost(HTTPS_PREFIX + domain
				+ REPOSITORY_CREATE_HTTP_SUFFIX);

		// add auth headers
		postRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
		postRequest.addHeader("Content-Type", "application/xml");

		// add xml entity
		StringEntity se = new StringEntity(xml, "UTF-8");
		postRequest.setEntity(se);

		// TODO throw exceptions if an error occurs
		try {
			HttpResponse postResponse = httpClient.execute(postRequest);
			Log.w("postRequestXml", xml);
			String entity = EntityUtils.toString(postResponse.getEntity());
			Log.w("postResponse", entity);
			int statusCode = postResponse.getStatusLine().getStatusCode();

			if (statusCode == 422) {
				StringBuilder sb = new StringBuilder();
				for (String s : XmlParser.parseErrors(entity)) {
					sb.append(s);
					sb.append("\n");
				}

				throw new HttpSenderServerErrorException(sb.toString());
			} else {
				return statusCode;
			}

		} catch (IOException e) {
			postRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("IOException");
		}

	}

	public static int sendUpdateRepositoryXML(SharedPreferences prefs, String xml,
			String repoId) throws UnsupportedEncodingException, HttpSenderException,
			XMLParserException, HttpSenderServerErrorException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		// create PUT request with address
		HttpPut putRequest = new HttpPut(HTTPS_PREFIX + domain
				+ REPOSITORY_UPDATE_HTTP_MIDDLE + repoId + ".xml");

		// add auth headers
		putRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
		putRequest.addHeader("Content-Type", "application/xml");

		// add xml entity
		StringEntity se = new StringEntity(xml, "UTF-8");
		putRequest.setEntity(se);

		// TODO throw exceptions if an error occurs
		try {
			HttpResponse postResponse = httpClient.execute(putRequest);
			Log.w("postRequestXml", xml);
			String entity = EntityUtils.toString(postResponse.getEntity());
			Log.w("postResponse", entity);
			int statusCode = postResponse.getStatusLine().getStatusCode();

			if (statusCode == 422) {
				StringBuilder sb = new StringBuilder();
				for (String s : XmlParser.parseErrors(entity)) {
					sb.append(s);
					sb.append("\n");
				}

				throw new HttpSenderServerErrorException(sb.toString());
			} else {
				return statusCode;
			}

		} catch (IOException e) {
			putRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("IOException");

		}

	}

	public static int sendUpdateUserXML(SharedPreferences prefs, String xml, String userId)
			throws UnsupportedEncodingException, HttpSenderException, XMLParserException,
			HttpSenderServerErrorException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		// create PUT request with address
		HttpPut putRequest = new HttpPut(HTTPS_PREFIX + domain + USER_UPDATE_HTTP_MIDDLE
				+ userId + ".xml");

		// add auth headers
		putRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
		putRequest.addHeader("Content-Type", "application/xml");

		// add xml entity
		StringEntity se = new StringEntity(xml, "UTF-8");
		putRequest.setEntity(se);

		// TODO throw exceptions if an error occurs
		try {
			HttpResponse putResponse = httpClient.execute(putRequest);
			Log.w("postRequestXml", xml);
			String entity = EntityUtils.toString(putResponse.getEntity());
			Log.w("postResponse", entity);
			int statusCode = putResponse.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				return 200;
			} else if (statusCode == 422) {
				StringBuilder sb = new StringBuilder();
				for (String s : XmlParser.parseErrors(entity)) {
					sb.append(s);
					sb.append("\n");
				}
				throw new HttpSenderServerErrorException(sb.toString());
			} else {
				throw new HttpSenderServerErrorException(putResponse.getStatusLine()
						.getReasonPhrase());
			}

		} catch (IOException e) {
			putRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("IOException");
		}

	}

	public static int sendCreateUserXML(SharedPreferences prefs, String xml)
			throws UnsupportedEncodingException, HttpSenderException,
			HttpSenderServerErrorException, XMLParserException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		// create POST request with address
		HttpPost postRequest = new HttpPost(HTTPS_PREFIX + domain
				+ USER_CREATE_HTTP_SUFFIX);

		// add auth headers
		postRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
		postRequest.addHeader("Content-Type", "application/xml");

		// add xml entity
		StringEntity se = new StringEntity(xml, "UTF-8");
		postRequest.setEntity(se);

		try {
			HttpResponse postResponse = httpClient.execute(postRequest);
			Log.w("postRequestXml", xml);
			String entity = EntityUtils.toString(postResponse.getEntity());
			Log.w("postResponse", entity);
			int statusCode = postResponse.getStatusLine().getStatusCode();

			if (statusCode == 201) {
				return 201;
			} else if (statusCode == 422) {
				StringBuilder sb = new StringBuilder();
				for (String s : XmlParser.parseErrors(entity)) {
					sb.append(s);
					sb.append("\n");
				}
				throw new HttpSenderServerErrorException(sb.toString());
			} else
				throw new HttpSenderServerErrorException(postResponse.getStatusLine()
						.getReasonPhrase());

		} catch (IOException e) {
			postRequest.abort();
			throw new HttpSenderException("IOException");
		}

	}

	public static int sendPermissionXML(SharedPreferences prefs, String xml)
			throws UnsupportedEncodingException, HttpSenderException,
			HttpSenderServerErrorException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		// create POST request with address
		HttpPost postRequest = new HttpPost(HTTPS_PREFIX + domain
				+ PERMISSION_CREATE_HTTP_SUFFIX);

		// add auth headers
		postRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
		postRequest.addHeader("Content-Type", "application/xml");

		// add xml entity
		StringEntity se = new StringEntity(xml, "UTF-8");
		postRequest.setEntity(se);

		// TODO throw exceptions if an error occurs
		try {
			HttpResponse postResponse = httpClient.execute(postRequest);
			Log.w("postRequestXml", xml);
			String entity = EntityUtils.toString(postResponse.getEntity());
			Log.w("postResponse", entity);
			if (postResponse.getStatusLine().getStatusCode() == 201) {
				return 201;
			} else {
				throw new HttpSenderServerErrorException("Failed");
			}

		} catch (IOException e) {
			postRequest.abort();
			throw new HttpSenderException("IOException");
		}

	}

	public static int sendDeletePermissionRequest(SharedPreferences prefs,
			String permissionId) throws UnsupportedEncodingException,
			HttpSenderException, HttpSenderServerErrorException, ParseException,
			XMLParserException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		// create DELETE request with address
		HttpPost deleteRequest = new HttpPost(HTTPS_PREFIX + domain
				+ PERMISSION_DELETE_HTTP_MIDDLE + permissionId + ".xml");

		deleteRequest.addHeader("X-HTTP-Method-Override", "delete");
		deleteRequest.addHeader("Content-Type", "application/xml");
		deleteRequest.addHeader("Accept", "application/xml");

		deleteRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

		Log.w("deleteRequest", deleteRequest.getURI().toString());

		try {
			HttpResponse response = httpClient.execute(deleteRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				return 200;
			} else if (statusCode == 422) {
				StringBuilder sb = new StringBuilder();
				for (String s : XmlParser.parseErrors(EntityUtils.toString(response
						.getEntity()))) {
					sb.append(s);
					sb.append("\n");
				}
				throw new HttpSenderServerErrorException(sb.toString());
			} else {
				throw new HttpSenderServerErrorException(response.getStatusLine()
						.getReasonPhrase());
			}

		} catch (IOException e) {
			throw new HttpSenderException("IOException");
		}

	}

	public static int sendDeleteUserRequest(SharedPreferences prefs, String userId)
			throws UnsupportedEncodingException, HttpSenderException,
			HttpSenderServerErrorException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 20000);

		// create DELETE request with address
		HttpPost deleteRequest = new HttpPost(HTTPS_PREFIX + domain
				+ USER_DELETE_HTTP_MIDDLE + userId + ".xml");

		deleteRequest.addHeader("X-HTTP-Method-Override", "delete");
		deleteRequest.addHeader("Content-Type", "application/xml");
		deleteRequest.addHeader("Accept", "application/xml");

		deleteRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

		Log.w("deleteRequest", deleteRequest.getURI().toString());

		// TODO throw exceptions if an error occurs
		try {
			HttpResponse response = httpClient.execute(deleteRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				return 200;
			} else
				throw new HttpSenderServerErrorException(response.getStatusLine()
						.getReasonPhrase());

		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpSenderException("IOException");
		}

	}

	public static Integer sendUpdateAccountXML(SharedPreferences prefs,
			String accountModificationXml) throws XMLParserException,
			HttpSenderServerErrorException, HttpSenderException,
			UnsupportedEncodingException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		// create PUT request with address
		HttpPut putRequest = new HttpPut(HTTPS_PREFIX + domain
				+ ACCOUNT_UPDATE_HTTP_SUFFIX);

		// add auth headers
		putRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
		putRequest.addHeader("Content-Type", "application/xml");

		// add xml entity
		StringEntity se = new StringEntity(accountModificationXml, "UTF-8");
		putRequest.setEntity(se);

		// TODO throw exceptions if an error occurs
		try {
			HttpResponse putResponse = httpClient.execute(putRequest);
			String entity = EntityUtils.toString(putResponse.getEntity());
			int statusCode = putResponse.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				return 200;
			} else if (statusCode == 422) {
				StringBuilder sb = new StringBuilder();
				for (String s : XmlParser.parseErrors(entity)) {
					sb.append(s);
					sb.append("\n");
				}
				throw new HttpSenderServerErrorException(sb.toString());
			} else {
				throw new HttpSenderServerErrorException(putResponse.getStatusLine()
						.getReasonPhrase());
			}

		} catch (IOException e) {
			putRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("IOException");
		}

	}

	private static UsernamePasswordCredentials getCredentialsFromPreferences(
			SharedPreferences prefs) {
		return new UsernamePasswordCredentials(prefs.getString(Constants.USER_LOGIN, ""),
				prefs.getString(Constants.USER_PASSWORD, ""));

	}

	private static String getAccountDomain(SharedPreferences prefs) {
		return prefs.getString(Constants.USER_ACCOUNT_DOMAIN, "");
	}

	// a general exception for this class associated with connection errors and
	// improper responses from server
	public static class HttpSenderException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public HttpSenderException(String message) {
			super(message);
		}
	}

	// an exception carrying an error message from the server - connected with
	// malformed query
	public static class HttpSenderServerErrorException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public HttpSenderServerErrorException(String message) {
			super(message);
		}
	}

}
