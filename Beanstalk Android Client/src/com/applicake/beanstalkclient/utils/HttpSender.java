package com.applicake.beanstalkclient.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
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

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
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

import com.applicake.beanstalkclient.Constants;

import android.content.SharedPreferences;
import android.util.Log;

public class HttpSender {

	private final DefaultHttpClient httpClient = new DefaultHttpClient();
//	private final DefaultHttpClient httpClient = getNewHttpClient();
	private final String HTTPS_PREFIX = "https://";
	private final String COMMENTS_HTTP_MIDDLE = ".beanstalkapp.com/api/";
	private final String COMMENTS_HTTP_SUFFIX = "/comments.xml";
	private final String REPOSITORY_CREATE_HTTP_SUFFIX = ".beanstalkapp.com/api/repositories.xml";
	private final String REPOSITORY_UPDATE_HTTP_MIDDLE = ".beanstalkapp.com/api/repositories/";
	private final String USER_UPDATE_HTTP_MIDDLE = ".beanstalkapp.com/api/users/";
	private final String USER_DELETE_HTTP_MIDDLE = ".beanstalkapp.com/api/users/";
	private final String USER_CREATE_HTTP_SUFFIX = ".beanstalkapp.com/api/users.xml";
	
//	public class MySSLSocketFactory extends SSLSocketFactory {
//	    SSLContext sslContext = SSLContext.getInstance("TLS");
//
//	    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
//	        super(truststore);
//
//	        TrustManager tm = new X509TrustManager() {
//	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//	            }
//
//	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//	            }
//
//	            public X509Certificate[] getAcceptedIssuers() {
//	                return null;
//	            }
//	        };
//
//	        sslContext.init(null, new TrustManager[] { tm }, null);
//	    }
//
//	    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
//	        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
//	    }
//
//	    @Override
//	    public Socket createSocket() throws IOException {
//	        return sslContext.getSocketFactory().createSocket();
//	    }
//	}
//
//	public DefaultHttpClient getNewHttpClient() {
//	    try {
//	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//	        trustStore.load(null, null);
//
//	        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//	        HttpParams params = new BasicHttpParams();
//	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
//
//	        SchemeRegistry registry = new SchemeRegistry();
//	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//	        registry.register(new Scheme("https", sf, 443));
//
//	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
//
//	        return new DefaultHttpClient(ccm, params);
//	    } catch (Exception e) {
//	        return new DefaultHttpClient();
//	    }
//	}


	public String sendCommentXML(SharedPreferences prefs, String xml, String repoId)
			throws UnsupportedEncodingException, HttpSenderException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		// create POST request with address
		HttpPost postRequest = new HttpPost(HTTPS_PREFIX + domain + COMMENTS_HTTP_MIDDLE
				+ repoId + COMMENTS_HTTP_SUFFIX);

		// add auth headers
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);
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
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);
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

	public int sendUpdateRepositoryXML(SharedPreferences prefs, String xml, String repoId)
			throws UnsupportedEncodingException, HttpSenderException {

		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		// create PUT request with address
		HttpPut putRequest = new HttpPut(HTTPS_PREFIX + domain
				+ REPOSITORY_UPDATE_HTTP_MIDDLE + repoId + ".xml");

		// add auth headers
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);
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
			if (postResponse.getStatusLine().getStatusCode() == 200) {
				return 200;
			} else {
				throw new HttpSenderException("Incorrect response from server");
			}

		} catch (ClientProtocolException e) {
			putRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("Client protocol exception");
		} catch (IOException e) {
			putRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("IOException");
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
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);
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
			if (postResponse.getStatusLine().getStatusCode() == 200) {
				return 200;
			} else {
				throw new HttpSenderException("Incorrect response from server");
			}

		} catch (ClientProtocolException e) {
			putRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("Client protocol exception");
		} catch (IOException e) {
			putRequest.abort();
			e.printStackTrace();
			throw new HttpSenderException("IOException");
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
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);
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

	public int sendDeleteUserRequest(SharedPreferences prefs, String userId)
			throws UnsupportedEncodingException, HttpSenderException {

		
		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);

		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 20000);

		// create DELETE request with address
		HttpDelete deleteRequest = new HttpDelete(HTTPS_PREFIX + domain
				+ USER_DELETE_HTTP_MIDDLE + userId + ".xml");
//		deleteRequest.addHeader("Host", domain + ".beanstalkapp.com");
//		deleteRequest.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.5; rv:2.0) Gecko/20100101 Firefox/4.0");
//		deleteRequest.addHeader("Content-Type", "application/xml");
//		deleteRequest.addHeader("Accept", "application/xml");
//		deleteRequest.addHeader("Keep-Alive", "115");
//		deleteRequest.addHeader("Content-Length", "0");
		
	
		deleteRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));

		Log.w("deleteRequest", deleteRequest.getURI().toString());

		// TODO throw exceptions if an error occurs
		try {
			httpClient.execute(deleteRequest);
			return 200;
			// String entity = EntityUtils.toString(deleteResponse.getEntity());
			// Log.w("postResponse", entity);
			// if (deleteResponse.getStatusLine().getStatusCode() == 200) {
			// return 200;
			// } else {
			// throw new HttpSenderException("Incorrect response from server");
			// }

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new HttpSenderException("Client protocol exception");
		} catch (IOException e) {
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

	public class HttpSenderException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9190064875608806066L;

		public HttpSenderException(String message) {
			super(message);
		}
	}

}