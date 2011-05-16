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
	
	public static final int CLOSE_ALL_ACTIVITIES = 1996;
	
	public static final HashMap<String,Integer> COLOR_LABELS_HASH_MAP = new HashMap<String,Integer>();

	//names for extras passed via intents
	
	static final String CHANGESET_ENTRY = "changeset_entry";
	static final String CHANGEDFILES_ARRAYLIST = "changed_files_array";
	static final String CHANGEDDIRS_ARRAYLIST = "changed_dirs_array";

	static final String COMMIT_USERNAME = "commit_username";
	static final String COMMIT_MESSAGE = "commit_message";

	public static final String COMMIT_REPOSITORY_ID = "commit_repository_id";

	public static final String COMMIT_REVISION_ID = "commit_revision_id";

	public static final String REPOSITORY = "repository";
	{
		COLOR_LABELS_HASH_MAP.put("label-white", 0);
		COLOR_LABELS_HASH_MAP.put("label-red", 1);
		COLOR_LABELS_HASH_MAP.put("label-orange", 2);
		COLOR_LABELS_HASH_MAP.put("label-yellow", 3);
		COLOR_LABELS_HASH_MAP.put("label-green", 4);
		COLOR_LABELS_HASH_MAP.put("label-blue", 5);
		COLOR_LABELS_HASH_MAP.put("label-pink", 6);
		COLOR_LABELS_HASH_MAP.put("label-grey", 7);
		
	};
	
	
	//color labels

}
