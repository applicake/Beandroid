package com.applicake.beanstalkclient.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.applicake.beanstalkclient.Constants;

import android.content.SharedPreferences;
import android.util.Log;

public class HttpSender {
	
	private final DefaultHttpClient httpClient = new DefaultHttpClient();
	private final String HTTP_PREFIX = "https://";
	private final String COMMENTS_HTTP_MIDDLE = ".beanstalkapp.com/api/";
	private final String COMMENTS_HTTP_SUFFIX = "/comments.xml";
	private final String REPOSITORY_CREATE_HTTP_SUFFIX = ".beanstalkapp.com/api/repositories.xml";
	private final String REPOSITORY_UPDATE_HTTP_MIDDLE = ".beanstalkapp.com/api/repositories/";
	private final String USER_UPDATE_HTTP_MIDDLE = ".beanstalkapp.com/api/users/";
	private final String USER_DELETE_HTTP_MIDDLE = ".beanstalkapp.com/api/users/";
	private final String USER_CREATE_HTTP_SUFFIX = ".beanstalkapp.com/api/users.xml";
	
	
	public String sendCommentXML(SharedPreferences prefs, String xml, String repoId) throws UnsupportedEncodingException, HttpSenderException{
		
		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);
	
		//create POST request with address
		HttpPost postRequest = new HttpPost(HTTP_PREFIX + domain + COMMENTS_HTTP_MIDDLE + repoId + COMMENTS_HTTP_SUFFIX);
		
		//add auth headers
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);
		postRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
		postRequest.addHeader("Content-Type", "application/xml");
		
		//add xml entity
		StringEntity se = new StringEntity(xml,"UTF-8");
		postRequest.setEntity(se);
		
		//TODO throw exceptions if an error occurs
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
	
	public int sendCreateNewRepostiroyXML(SharedPreferences prefs, String xml) throws UnsupportedEncodingException, HttpSenderException{
		
		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);
		
		//create POST request with address
		HttpPost postRequest = new HttpPost(HTTP_PREFIX + domain + REPOSITORY_CREATE_HTTP_SUFFIX);
		
		//add auth headers
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);
		postRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
		postRequest.addHeader("Content-Type", "application/xml");
		
		//add xml entity
		StringEntity se = new StringEntity(xml,"UTF-8");
		postRequest.setEntity(se);
		
		//TODO throw exceptions if an error occurs
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
	
	public int sendUpdateRepositoryXML(SharedPreferences prefs, String xml, String repoId) throws UnsupportedEncodingException, HttpSenderException{
		
		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);
		
		//create PUT request with address
		HttpPut putRequest = new HttpPut(HTTP_PREFIX + domain + REPOSITORY_UPDATE_HTTP_MIDDLE + repoId + ".xml");
		
		//add auth headers
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);
		putRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
		putRequest.addHeader("Content-Type", "application/xml");
		
		//add xml entity
		StringEntity se = new StringEntity(xml,"UTF-8");
		putRequest.setEntity(se);
		
		//TODO throw exceptions if an error occurs
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
	
	public int sendUpdateUserXML(SharedPreferences prefs, String xml, String userId) throws UnsupportedEncodingException, HttpSenderException{
		
		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);
		
		//create PUT request with address
		HttpPut putRequest = new HttpPut(HTTP_PREFIX + domain + USER_UPDATE_HTTP_MIDDLE + userId + ".xml");
		
		//add auth headers
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);
		putRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
		putRequest.addHeader("Content-Type", "application/xml");
		
		//add xml entity
		StringEntity se = new StringEntity(xml,"UTF-8");
		putRequest.setEntity(se);
		
		//TODO throw exceptions if an error occurs
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
	
	public int sendCreateUserXML(SharedPreferences prefs, String xml) throws UnsupportedEncodingException, HttpSenderException{
		
		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);
		
		//create POST request with address
		HttpPost postRequest = new HttpPost(HTTP_PREFIX + domain + USER_CREATE_HTTP_SUFFIX);
		
		//add auth headers
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);
		postRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
		postRequest.addHeader("Content-Type", "application/xml");
		
		//add xml entity
		StringEntity se = new StringEntity(xml,"UTF-8");
		postRequest.setEntity(se);
		
		//TODO throw exceptions if an error occurs
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
	
	public int sendDeleteUserRequest(SharedPreferences prefs, String userId) throws UnsupportedEncodingException, HttpSenderException{
		
		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);
		
		//create POST request with address
		HttpDelete deleteRequest = new HttpDelete(HTTP_PREFIX + domain + USER_DELETE_HTTP_MIDDLE + userId + ".xml");
		
		java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
		java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);

		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");

		
		//add auth headers
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		deleteRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
		
//		deleteRequest.addHeader("Content-Type", "application/xml");
		Log.w("deleteRequest", deleteRequest.getURI().toString());
		
		//TODO throw exceptions if an error occurs
		try {
			httpClient.execute(deleteRequest);
			return 200;
//			String entity = EntityUtils.toString(deleteResponse.getEntity());
//			Log.w("postResponse", entity);
//			if (deleteResponse.getStatusLine().getStatusCode() == 200) {
//				return 200;
//			} else {
//				throw new HttpSenderException("Incorrect response from server");
//			}
			
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
	
	public class HttpSenderException extends Exception{
		/**
		 * 
		 */
		private static final long serialVersionUID = 9190064875608806066L;

		public HttpSenderException(String message){
			super(message);
		}
	}


}
