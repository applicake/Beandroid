package com.applicake.beanstalkclient.utils;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.applicake.beanstalkclient.Constants;

import android.content.SharedPreferences;
import android.util.Log;

public class HttpRetriever {

	public static DefaultHttpClient getClient() {
		DefaultHttpClient ret = null;

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
		ret = new DefaultHttpClient(manager, params);
		return ret;
	}

	final DefaultHttpClient httpClient = getClient();

	private final String HTTP_PREFIX = "https://";
	private final String AUTH_HTTP_SUFFIX = ".beanstalkapp.com/api/account.xml";
	private final String USERS_HTTP_SUFFIX = ".beanstalkapp.com/api/users.xml";
	private final String PERMISSIONS_FOR_USER_HTTP_SUFFIX = ".beanstalkapp.com/api/permissions/";
	private final String ACTIVITY_HTTP_SUFFIX = ".beanstalkapp.com/api/changesets.xml";
	private final String SPECIFIED_REPOSITORY_ACTIVITY_HTTP_SUFFIX = ".beanstalkapp.com/api/changesets/repository.xml?repository_id=";
	private final String REPOSITORY_HTTP_SUFFIX = ".beanstalkapp.com/api/repositories.xml";
	private final String REPOSITORY_HTTP_MIDDLE = ".beanstalkapp.com/api/repositories/";
	private final String COMMENTS_HTTP_MIDDLE = ".beanstalkapp.com/api/";
	private final String COMMENTS_HTTP_SUFFIX = "/comments.xml";
	private final String COMMENTS_REVISION_HTTP_SUFFIX = "?revision=";

	// COMMENTED PART FOR DEBUGGING VIA CHARLES PROXY
	// public class MySSLSocketFactory extends SSLSocketFactory {
	// SSLContext sslContext = SSLContext.getInstance("TLS");
	//
	// public MySSLSocketFactory(KeyStore truststore) throws
	// NoSuchAlgorithmException,
	// KeyManagementException, KeyStoreException, UnrecoverableKeyException {
	// super(truststore);
	//
	// TrustManager tm = new X509TrustManager() {
	// public void checkClientTrusted(X509Certificate[] chain, String authType)
	// throws CertificateException {
	// }
	//
	// public void checkServerTrusted(X509Certificate[] chain, String authType)
	// throws CertificateException {
	// }
	//
	// public X509Certificate[] getAcceptedIssuers() {
	// return null;
	// }
	// };
	//
	// sslContext.init(null, new TrustManager[] { tm }, null);
	// }
	//
	// public Socket createSocket(Socket socket, String host, int port, boolean
	// autoClose)
	// throws IOException, UnknownHostException {
	// return sslContext.getSocketFactory().createSocket(socket, host, port,
	// autoClose);
	// }
	//
	// @Override
	// public Socket createSocket() throws IOException {
	// return sslContext.getSocketFactory().createSocket();
	// }
	// }
	//
	// public DefaultHttpClient getNewHttpClient() {
	// try {
	// KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	// trustStore.load(null, null);
	//
	// SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	// sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	//
	// HttpParams params = new BasicHttpParams();
	// HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	// HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
	//
	// SchemeRegistry registry = new SchemeRegistry();
	// registry.register(new Scheme("http",
	// PlainSocketFactory.getSocketFactory(),
	// 80));
	// registry.register(new Scheme("https", sf, 443));
	//
	// ClientConnectionManager ccm = new ThreadSafeClientConnManager(params,
	// registry);
	//
	// return new DefaultHttpClient(ccm, params);
	// } catch (Exception e) {
	// return new DefaultHttpClient();
	// }
	// }

	public String checkCredentials(String domain, String username, String password) throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				username, password);

		String auth_http = HTTP_PREFIX + domain + AUTH_HTTP_SUFFIX;

		HttpGet getRequest = new HttpGet(auth_http);
//		final HttpParams params = new BasicHttpParams();
//		httpClient.setParams(params);
//		HttpClientParams.setRedirecting(params, false);
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

		} catch (Exception e) { // TODO exception handling
			e.printStackTrace();
			getRequest.abort();
			throw new HttpRetreiverException("666");
			
		}

	}

	public String getUserListXML(SharedPreferences prefs) throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		String auth_http = HTTP_PREFIX + domain + USERS_HTTP_SUFFIX;

		HttpGet getRequest = new HttpGet(auth_http);
//		final HttpParams params = new BasicHttpParams();
//		httpClient.setParams(params);
//		HttpClientParams.setRedirecting(params, false);
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

	public String getActivityListXML(SharedPreferences prefs)
			throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		String activity_http = HTTP_PREFIX + domain + ACTIVITY_HTTP_SUFFIX;

		HttpGet getRequest = new HttpGet(activity_http);
//		final HttpParams params = new BasicHttpParams();
//		httpClient.setParams(params);
//		HttpClientParams.setRedirecting(params, false);

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

		String activity_http = HTTP_PREFIX + domain
				+ SPECIFIED_REPOSITORY_ACTIVITY_HTTP_SUFFIX + repoId;

		HttpGet getRequest = new HttpGet(activity_http);
//		final HttpParams params = new BasicHttpParams();
//		httpClient.setParams(params);
//		HttpClientParams.setRedirecting(params, false);

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
//		final HttpParams params = new BasicHttpParams();
//		httpClient.setParams(params);
//		HttpClientParams.setRedirecting(params, false);

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
	
	public String getRepositoryXML(SharedPreferences prefs, int repoId)
	throws HttpRetreiverException {
		
		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);
		
		String activity_http = HTTP_PREFIX + domain + REPOSITORY_HTTP_MIDDLE + String.valueOf(repoId) + ".xml";
		
		HttpGet getRequest = new HttpGet(activity_http);
//		final HttpParams params = new BasicHttpParams();
//		httpClient.setParams(params);
//		HttpClientParams.setRedirecting(params, false);
		
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
//		final HttpParams params = new BasicHttpParams();
//		httpClient.setParams(params);
//		HttpClientParams.setRedirecting(params, false);

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

	public String getCommentsListForRevisionXML(SharedPreferences prefs, String repoId,
			String revision) throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		String activity_http = HTTP_PREFIX + domain + COMMENTS_HTTP_MIDDLE
				+ String.valueOf(repoId) + COMMENTS_HTTP_SUFFIX
				+ COMMENTS_REVISION_HTTP_SUFFIX + revision;

		HttpGet getRequest = new HttpGet(activity_http);
//		final HttpParams params = new BasicHttpParams();
//		httpClient.setParams(params);
//		HttpClientParams.setRedirecting(params, false);

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

	public String getPermissionListForUserXML(SharedPreferences prefs, String userId)
			throws HttpRetreiverException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		String activity_http = HTTP_PREFIX + domain + PERMISSIONS_FOR_USER_HTTP_SUFFIX
				+ String.valueOf(userId) + ".xml";

		HttpGet getRequest = new HttpGet(activity_http);
//		final HttpParams params = new BasicHttpParams();
//		httpClient.setParams(params);
		

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
				throw new Exception("Http connection error");

		} catch (IOException io) {
			// TODO handle various HTTP exceptions
			getRequest.abort();
			io.printStackTrace();
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
