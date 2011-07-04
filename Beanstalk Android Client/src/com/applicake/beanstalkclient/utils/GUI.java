package com.applicake.beanstalkclient.utils;

import android.content.Context;
import android.widget.Toast;

public class GUI {

  // simple monit implementation
  public static void displayMonit(Context context, String message) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
  }

  public static void displayServerErrorMonit(Context context, String message) {
    Toast.makeText(context, "Server error: " + message, Toast.LENGTH_LONG).show();
  }

  public static void displayUnexpectedErrorMonit(Context context) {
    Toast
        .makeText(context, "Unexpected error, please try again later", Toast.LENGTH_LONG)
        .show();
  }

}
