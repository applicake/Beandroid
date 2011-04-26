package com.applicake.beanstalkclient;

import android.content.Context;
import android.widget.Toast;

public class GUI {
	
	// simple monit implementation
	public static void displayMonit(Context context, String message){
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

}
