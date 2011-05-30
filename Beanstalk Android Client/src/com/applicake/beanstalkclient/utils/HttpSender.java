package com.applicake.beanstalkclient.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ParseException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
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
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.xml.sax.SAXException;

import com.applicake.beanstalkclient.Constants;

import android.content.SharedPreferences;
import android.util.Log;

public class HttpSender {

	public static DefaultHttpClient getClient() {
		DefaultHttpClient httpClient = null;

		// sets up parameters
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "utf-8");
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

	final DefaultHttpClient httpClient = getClient();
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

	public String sendCommentXML(SharedPreferences prefs, String xml, String repoId)
			throws UnsupportedEncodingException, HttpSenderException {

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

				throw new HttpSenderException(sb.toString());
			} else if (statusCode == 201){
				return entity;
			} else {
				throw new HttpSenderException("Incorrect response from server");
			}

		} catch (ClientProtocolException e) {
			postRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("Client protocol exception");
		} catch (IOException e) {
			postRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("IOException");
		} catch (ParserConfigurationException e) {
			postRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("Parser configuration exception");
		} catch (SAXException e) {
			postRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException(e.getMessage());
		}

	}

	public int sendCreateNewRepostiroyXML(SharedPreferences prefs, String xml)
			throws UnsupportedEncodingException, HttpSenderException {

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

				throw new HttpSenderException(sb.toString());
			} else {
				return statusCode;
			}

		} catch (ClientProtocolException e) {
			postRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("Client protocol exception");
		} catch (IOException e) {
			postRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("IOException");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new HttpSenderException("Parser configuration error");
		} catch (SAXException e) {
			e.printStackTrace();
			throw new HttpSenderException(e.getMessage());
		}

	}

	public int sendUpdateRepositoryXML(SharedPreferences prefs, String xml, String repoId)
			throws UnsupportedEncodingException, HttpSenderException {

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

				throw new HttpSenderException(sb.toString());
			} else {
				return statusCode;
			}

		} catch (ClientProtocolException e) {
			putRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("Client protocol exception");
		} catch (IOException e) {
			putRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("IOException");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new HttpSenderException("Parse configuration error");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new HttpSenderException(e.getMessage());
		}

	}

	public int sendUpdateUserXML(SharedPreferences prefs, String xml, String userId)
			throws UnsupportedEncodingException, HttpSenderException {

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

				throw new HttpSenderException(sb.toString());
			} else {
				return statusCode;
			}

		} catch (ClientProtocolException e) {
			putRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("Client protocol exception");
		} catch (IOException e) {
			putRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("IOException");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new HttpSenderException("ParseException");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new HttpSenderException("ParserConfigurationException");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new HttpSenderException("SAXException");
		}

	}

	public int sendCreateUserXML(SharedPreferences prefs, String xml)
			throws UnsupportedEncodingException, HttpSenderException {

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

		// TODO throw exceptions if an error occurs
		try {
			HttpResponse postResponse = httpClient.execute(postRequest);
			Log.w("postRequestXml", xml);
			String entity = EntityUtils.toString(postResponse.getEntity());
			Log.w("postResponse", entity);
			if (postResponse.getStatusLine().getStatusCode() == 201) {
				return 201;
			} else {
				throw new HttpSenderException("Incorrect response from server");
			}

		} catch (ClientProtocolException e) {
			postRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("Client protocol exception");
		} catch (IOException e) {
			postRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("IOException");
		}

	}

	public int sendPermissionXML(SharedPreferences prefs, String xml)
			throws UnsupportedEncodingException, HttpSenderException {

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
				throw new HttpSenderException("Incorrect response from server");
			}

		} catch (ClientProtocolException e) {
			postRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("Client protocol exception");
		} catch (IOException e) {
			postRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("IOException");
		}

	}

	public int sendDeletePermissionRequest(SharedPreferences prefs, String permissionId)
			throws UnsupportedEncodingException, HttpSenderException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		// HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
		// 20000);

		// create DELETE request with address
		HttpPost deleteRequest = new HttpPost(HTTPS_PREFIX + domain
				+ PERMISSION_DELETE_HTTP_MIDDLE + permissionId + ".xml");

		deleteRequest.addHeader("X-HTTP-Method-Override", "delete");
		deleteRequest.addHeader("Content-Type", "application/xml");
		deleteRequest.addHeader("Accept", "application/xml");

		deleteRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

		Log.w("deleteRequest", deleteRequest.getURI().toString());

		// TODO throw exceptions if an error occurs
		try {
			HttpResponse response = httpClient.execute(deleteRequest);

			return response.getStatusLine().getStatusCode();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new HttpSenderException("Client protocol exception");
		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpSenderException("IOException");
		}

	}

	public int sendDeleteUserRequest(SharedPreferences prefs, String userId)
			throws UnsupportedEncodingException, HttpSenderException {

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
			return response.getStatusLine().getStatusCode();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new HttpSenderException("Client protocol exception");
		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpSenderException("IOException");
		}

	}

	// public int sendHackDeleteUserRequest(SharedPreferences prefs, String
	// userId)
	// throws HttpSenderException, IllegalArgumentException,
	// IllegalStateException, IOException {
	//
	// UsernamePasswordCredentials credentials =
	// getCredentialsFromPreferences(prefs);
	// String domain = getAccountDomain(prefs);
	//
	// HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 20000);
	//
	// // create DELETE request with address
	// HttpPost postRequest = new HttpPost(HTTPS_PREFIX + domain
	// + USER_DELETE_HTTP_MIDDLE + userId + ".xml");
	//
	// postRequest.addHeader("Content-Type", "application/xml");
	// postRequest.addHeader("Accept", "application/xml");
	//
	// postRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8",
	// false));
	//
	// String xml = new XmlCreator().createDeleteHack();
	// StringEntity se = new StringEntity(xml, "UTF-8");
	// postRequest.setEntity(se);
	//
	//
	//
	// Log.w("deleteRequestxml", xml);
	// Log.w("deleteRequest", postRequest.getURI().toString());
	//
	// // TODO throw exceptions if an error occurs
	// try {
	// httpClient.execute(postRequest);
	// return 200;
	//
	//
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// throw new HttpSenderException("Client protocol exception");
	// } catch (IOException e) {
	// e.printStackTrace();
	// throw new HttpSenderException("IOException");
	// }
	//
	// }

	private static UsernamePasswordCredentials getCredentialsFromPreferences(
			SharedPreferences prefs) {
		return new UsernamePasswordCredentials(prefs.getString(Constants.USER_LOGIN, ""),
				prefs.getString(Constants.USER_PASSWORD, ""));

	}

	private static String getAccountDomain(SharedPreferences prefs) {
		return prefs.getString(Constants.USER_ACCOUNT_DOMAIN, "");
	}

	public class HttpSenderException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public HttpSenderException(String message) {
			super(message);
		}
	}

}
