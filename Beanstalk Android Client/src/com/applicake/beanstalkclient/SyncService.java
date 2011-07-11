package com.applicake.beanstalkclient;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.applicake.beanstalkclient.activities.DashboardActivity;
import com.applicake.beanstalkclient.enums.ColorLabels;
import com.applicake.beanstalkclient.utils.HttpRetriever;
import com.applicake.beanstalkclient.utils.HttpRetriever.HttpConnectionErrorException;
import com.applicake.beanstalkclient.utils.HttpRetriever.UnsuccessfulServerResponseException;
import com.applicake.beanstalkclient.utils.XmlParser;
import com.applicake.beanstalkclient.utils.XmlParser.XMLParserException;

// under construction!

public class SyncService extends Service {

  public static final String TAG = SyncService.class.getSimpleName();

  // delay between enabling this feature and the initial query
  public static final int FIRST_LAUNCH_DELAY = 10 * 1000;

  private static SharedPreferences prefs;
  private String mostRecentlyViewedChangesetId;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
    Log.d(TAG, "inOnStart");
    prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    if (!prefs.getBoolean(Constants.CREDENTIALS_STORED, false)) {

      // if the credentials are not stored, the service will be disabled
      // this can happen when user logouts from the application

      Log.d(TAG, "credentails are not stored - stopping service");
      stopService(getApplicationContext());
    } else if (isOnline()) {

      mostRecentlyViewedChangesetId = prefs.getString(Constants.RECENT_CHANGESET_ID, "");
      Log.d(TAG, "the download has started");

      // starting the download task with or without custom LED colors
      new NotificationServiceTask(prefs.getBoolean(
          Constants.AUTO_UPDATE_NOTIFICATION_SERVICE_CUSTOM_LED, false)).execute();

    } else
      Log.d(TAG, "no interet connection");

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  // scheduling the service to run at certain intervals read from
  // sharedpreferences
  public static void initializeService(final Context context) {
    prefs = PreferenceManager.getDefaultSharedPreferences(context);
    Intent intent = new Intent(context, SyncService.class);
    PendingIntent pi = PendingIntent.getService(context, 0, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);

    // set minutes from settings, or if a value is not present, use default 60
    // minutes interval
    int minutes = Integer.parseInt(prefs.getString(
        Constants.AUTO_UPDATE_NOTIFICATION_SERVICE_DELAY, "60"));
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 10 * 1000,
        minutes * 60 * 1000, pi);
    Log.d(TAG, "notifications enabled");

    // making sure that the service is synchronized with the settings
    prefs.edit().putBoolean(Constants.AUTO_UPDATE_NOTIFICATION_SERVICE, true).commit();

  }

  // rescheduling the service to run at certain intervals
  public static void updateServiceReloadTime(Context context, Integer minutes) {
    prefs = PreferenceManager.getDefaultSharedPreferences(context);
    Intent intent = new Intent(context, SyncService.class);
    PendingIntent pi = PendingIntent.getService(context, 0, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);

    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 10 * 1000,
        minutes * 60 * 1000, pi);
    Log.d(TAG, "notifications enabled");

    // making sure that the service is synchronized with the settings
    prefs.edit().putBoolean(Constants.AUTO_UPDATE_NOTIFICATION_SERVICE, true).commit();

  }

  // canceling service schedule
  public static void stopService(final Context context) {

    Intent intent = new Intent(context, SyncService.class);
    PendingIntent pi = PendingIntent.getService(context, 0, intent,
        PendingIntent.FLAG_CANCEL_CURRENT);

    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    alarmManager.cancel(pi);
    pi.cancel();
    Log.d(TAG, "notifications cancelled");

    // making sure that the service is synchronized with the settings
    prefs = PreferenceManager.getDefaultSharedPreferences(context);
    prefs.edit().putBoolean(Constants.AUTO_UPDATE_NOTIFICATION_SERVICE, false).commit();

  }

  public static boolean isServiceRunning(final Context context) {
    Intent intent = new Intent(context, SyncService.class);
    PendingIntent pi = PendingIntent.getService(context, 0, intent,
        PendingIntent.FLAG_NO_CREATE);

    return (pi == null) ? false : true;
  }

  public class NotificationServiceTask extends
      AsyncTask<Void, Void, ArrayList<Changeset>> {

    boolean LEDcolorFitsRepoLabel;
    private HashMap<Integer, Repository> idToRepoMap;

    public NotificationServiceTask(boolean enableCustomLED) {
      this.LEDcolorFitsRepoLabel = enableCustomLED;
    }

    @Override
    protected ArrayList<Changeset> doInBackground(Void... params) {

      try {
        if (LEDcolorFitsRepoLabel) {
          String xmlRepositoryList = HttpRetriever.getRepositoryListXML(prefs);
          idToRepoMap = XmlParser.parseRepositoryHashMap(xmlRepositoryList);
        }

        String xmlChangesetList = HttpRetriever.getActivityListXML(prefs, 1);
        return XmlParser.parseChangesetList(xmlChangesetList);

      } catch (XMLParserException e) {
        e.printStackTrace();
      } catch (UnsuccessfulServerResponseException e) {
        e.printStackTrace();
      } catch (HttpConnectionErrorException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Changeset> changesetParserArray) {
      if (changesetParserArray != null) {
        int newChangesetsCount = 0;
        for (Changeset changeset : changesetParserArray) {
          if (changeset.getUniqueId().equals(mostRecentlyViewedChangesetId))
            break;
          newChangesetsCount++;
        }
        Log.d(TAG, String.valueOf(newChangesetsCount));

        // if there are unread commits display notification
        if (newChangesetsCount != 0) {
          Changeset mostRecentChangeset = changesetParserArray.get(0);
          String mostRecentChangesetId = mostRecentChangeset.getUniqueId();
          int ledColor;
          if (LEDcolorFitsRepoLabel) {
            ledColor = ColorLabels.getColorFromLabel(idToRepoMap.get(
                mostRecentChangeset.getRepositoryId()).getColorLabel());
          } else {
            ledColor = 0xff00ff00; // setting default LED color
          }
          // if the notification about this commit was sent already - dont send
          // it again
          if (!prefs.getString(Constants.LAST_NOTIFIED_CHANGESET_ID, "").equals(
              mostRecentChangesetId)) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            CharSequence notificationTitle = newChangesetsCount == 1 ? String
                .valueOf(newChangesetsCount) + " new commit" : String
                .valueOf(newChangesetsCount) + " new commits";

            Notification notification = new Notification(
                R.drawable.ic_ab_deployment_clicked, notificationTitle,
                System.currentTimeMillis());

            Context context = getApplicationContext();
            CharSequence contentTitle = newChangesetsCount == 1 ? String
                .valueOf(newChangesetsCount) + " new commit" : String
                .valueOf(newChangesetsCount) + " new commits";
            CharSequence contentText = "Clik here to open Beanstalk dashboard";
            Intent notificationIntent = new Intent(getApplicationContext(),
                DashboardActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, notificationIntent, 0);

            notification.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.ledARGB = ledColor;
            Log.d(TAG, String.valueOf(notification.ledARGB));
            notification.ledOnMS = 500;
            notification.ledOffMS = 500;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
            notification.number = newChangesetsCount;

            notificationManager.notify(Constants.NOTIFICATION_ID, notification);

            // store last notofied changeset in preferences
            prefs.edit()
                .putString(Constants.LAST_NOTIFIED_CHANGESET_ID, mostRecentChangesetId)
                .commit();
          }
        }
      }
    }
  }

  // a method checking in the device has network connectivity
  private boolean isOnline() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
      return true;
    }
    return false;
  }

}
