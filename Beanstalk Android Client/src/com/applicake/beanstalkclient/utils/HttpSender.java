package com.applicake.beanstalkclient.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.applicake.beanstalkclient.Constants;

import android.content.SharedPreferences;
import android.util.Log;

public class HttpSender {
	
	private final DefaultHttpClient httpClient = new DefaultHttpClient();
	private final String HTTP_PREFIX = "https://";
	private final String COMMENTS_HTTP_MIDDLE = ".beanstalkapp.com/api/";
	private final String COMMENTS_HTTP_SUFFIX = "/comments.xml";
	
	
	public void sendCommentXML(SharedPreferences prefs, String xml, String repoId) throws UnsupportedEncodingException{
		
		UsernamePasswordCredentials credentials = getCredentialsFromPreferences(prefs);
		String domain = getAccountDomain(prefs);
	
		//create POST request with address
		HttpPost postRequest = new HttpPost(HTTP_PREFIX + domain + COMMENTS_HTTP_MIDDLE + repoId + COMMENTS_HTTP_SUFFIX);
		
		//add auth headers
		final HttpParams params = new BasicHttpParams();
		httpClient.setParams(params);
		HttpClientParams.setRedirecting(params, false);
		postRequest.addHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
		
		//add xml entity
		StringEntity se = new StringEntity(xml,"UTF-8");
		postRequest.setEntity(se);
		
		
		
		//TODO throw exceptions if an error occurs
		try {
			HttpResponse postResponse = httpClient.execute(postRequest);
			
			Log.w("postRequestXml", xml);
			Log.w("postResponse", EntityUtils.toString(postResponse.getEntity()));
		} catch (ClientProtocolException e) {
			postRequest.abort();
			e.printStackTrace();
		} catch (IOException e) {
			postRequest.abort();
			e.printStackTrace();
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


}
