package com.applicake.beanstalkclient.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public abstract class SimpleRetryDialogBuilder {

  private AlertDialog dialog;

  /*
   * 
   * A simple abstract builder for creating retry alert dialogs for asynctasks.
   * 
   * The dialog will perform the action defined in retryAction() method after
   * pressing Yes, or the action defined in noRetryAction() after pressing No
   */

  public SimpleRetryDialogBuilder(Context context, String reason) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setMessage(reason + "\n" + "Do you want to retry?").setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            retryAction();
          }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            noRetryAction(dialog);
          }
        });
    dialog = builder.create();
  }

  public void displayDialog() {
    dialog.show();
  }

  // action to be implemented after pressing 'Yes'
  public abstract void retryAction();

  // default action after pressing 'No'
  public void noRetryAction(DialogInterface dialog) {
    dialog.cancel();
  }

}
