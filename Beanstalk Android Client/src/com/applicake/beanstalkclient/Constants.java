package com.applicake.beanstalkclient;

import java.util.HashMap;

public class Constants {
	
	public static final String SHARED_PREFERENCES = "shared_preferences";
	
	//current user saved data
	
	public static final String CREDENTIALS_STORED = "user_credentials_stored";
	public static final String REMEBER_ME_CHECKBOX = "remember_me_preference";
	public static final String USER_LOGIN = "user_login";
	public static final String USER_ACCOUNT_DOMAIN = "user_account_domain";
	public static final String USER_PASSWORD = "user_password";
	
	//activity result codes
	
	public static final int CLOSE_ALL_ACTIVITIES = 0;
	
	public static final HashMap<String,Integer> COLOR_LABELS_HASH_MAP = new HashMap<String,Integer>();
	{
		COLOR_LABELS_HASH_MAP.put("label-red", R.color.red);
		COLOR_LABELS_HASH_MAP.put("label-orange", R.color.orange);
		COLOR_LABELS_HASH_MAP.put("label-pink", R.color.pink);
		COLOR_LABELS_HASH_MAP.put("label-green", R.color.green);
		COLOR_LABELS_HASH_MAP.put("label-blue", R.color.blue);
		COLOR_LABELS_HASH_MAP.put("label-grey", R.color.gray);
		COLOR_LABELS_HASH_MAP.put("label-yellow", R.color.yellow);
	};
	
	
	//color labels

}
