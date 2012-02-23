package com.applicake.beanstalkclient.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.applicake.beanstalkclient.R;
import com.applicake.beanstalkclient.Strings;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.SimpleRetryDialogBuilder;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

public abstract class BeanstalkAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
  
  private Activity mContext;
  private Throwable th;
  private ProgressDialog progressDialog;
  
  public BeanstalkAsyncTask(Activity context) {
    this.mContext = context;
  }
  
  protected Result doInBackground(Params... params) {
    try {
      return trueDoInBackground(params);
    } catch(Throwable th) {
      this.th = th;
      return null;
    }
  }
  
  protected abstract Result trueDoInBackground(Params... params) throws Throwable;
  protected abstract void performTaskAgain();
  
  @Override
  protected void onPreExecute() {
    String progressDialogTip = getProgressDialogTip();
    if(progressDialogTip != null) {
      progressDialog = ProgressDialog.show(mContext, getContext().getString(R.string.please_wait_label), getProgressDialogTip());
      progressDialog.setCancelable(false);
    }
  }
  
  protected String getProgressDialogTip() {
    return null;
  }
  
  protected void onPostExecute(Result result) {
    dismissProgressDialog();
    if(th != null) {
      handleError(th);
    } else {
      trueOnPostExceute(result);
    }
  }
  
  protected void trueOnPostExceute(Result result) {
    // empty by default
  }
  
  protected void handleError(Throwable th) {
    String message = getErrorMessage(th);
    SimpleRetryDialogBuilder builder = new SimpleRetryDialogBuilder(mContext, message) {

      @Override
      public void retryAction() {
        performTaskAgain();
      }

      @Override
      public void noRetryAction(DialogInterface dialog) {
        super.noRetryAction(dialog);
        if(finishActivityAfterError()) {
          mContext.finish();
        }
      }

    };
    builder.displayDialog();
  }
  
  protected boolean finishActivityAfterError() {
    return true;
  }
  
  private String getErrorMessage(Throwable th) {
    String message;
    if(th instanceof HttpConnectionErrorException) {
      message = Strings.networkConnectionErrorMessage;
    } else if(th instanceof XMLParserException) {
      message = Strings.internalErrorMessage;
    } else {
      message = th.getMessage();
    }
    th.printStackTrace();
    return message;
  }  
  
  private void dismissProgressDialog() {
    if(progressDialog != null) {
      progressDialog.dismiss();
    }
  }
  
  protected Activity getContext() {
    return mContext;
  }
}
