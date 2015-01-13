package com.eezzyweb.wifiidentify;

import java.util.Locale;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

public class ToggleNotify extends Activity {

	public Intent result_intent;

	private ToggleNotify() {

	}

	public static void onNotify(Context ctx) {
		
		//destroy notification to refresh layout
		ToggleNotify.offNotify(ctx);

		// get user preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

		boolean show_ip = prefs.getBoolean("pref_show_ip", false);
		boolean show_link_speed = prefs.getBoolean("pref_show_link_speed", false);
		boolean show_signal_strength = prefs.getBoolean("pref_show_signal_strength", false);
		int selected_infos = 0;
		String content_txt = "";
		String sub_txt = "";
		String tap_option = prefs.getString("pref_tap_option", "app");

		WifiManager wifiMgr = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		String connection_name = wifiInfo.getSSID();

		// if the device is not connected, do not show any notification
		if (TextUtils.isEmpty(connection_name)) {
			return;
		}

		// get values from WifiInfo
		int ip_address = wifiInfo.getIpAddress();
		int signal_strength = wifiInfo.getRssi();
		
		String mac_address = wifiInfo.getMacAddress();
		
		Log.i("mac_address", String.valueOf(mac_address));

		String signal_level = String.valueOf(WifiManager.calculateSignalLevel(signal_strength, 100));

		Log.i("signal level", String.valueOf(signal_level));

		String link_speed = String.valueOf(wifiInfo.getLinkSpeed());

		if (link_speed.equalsIgnoreCase("-1")) {

			link_speed = "n/a";
		}

		if (show_ip) {
			content_txt += "IP: " + formatIP(ip_address) + " ";
			selected_infos++;
		}

		if (show_link_speed) {
			content_txt += "Speed: " + link_speed + " Mbps";
			selected_infos++;
		}

		if (show_signal_strength) {
			sub_txt += "Strength: " + signal_level + "%";
			selected_infos++;
		}

		// // Build a notification (onGoing)
		// NotificationCompat.Builder mBuilder = new
		// NotificationCompat.Builder(ctx)//
		// // .setPriority(Notification.PRIORITY_HIGH)//
		// .setSmallIcon(R.drawable.icon_notification)//
		// .setContentTitle(connection_name)//
		//
		// // .setSubText(sub_txt)//
		// .setWhen(0)// to hide timestamp
		// .setOngoing(true);//
		NotificationCompat.Builder mBuilder;
		
		// Build a notification with subText (onGoing)
		 mBuilder = new NotificationCompat.Builder(ctx)//
				// .setPriority(Notification.PRIORITY_HIGH)//
				.setSmallIcon(R.drawable.icon_notification)//
				.setContentTitle(connection_name)//
				.setWhen(0)// to hide timestamp
				.setOngoing(true);//
		
		if (selected_infos == 3) {
				
			mBuilder.setContentText(content_txt);
			mBuilder.setSubText(sub_txt);

		} else {
			
			content_txt += " " + sub_txt;
			// Build a notification WITHOUT subText (onGoing)
			mBuilder.setContentText(content_txt);
		}

		Log.i("content_txt", content_txt);

		// TODO: this will work only on Android 4.1
		// // if the user selects all the option, build multi-row notification
		// like
		// // Gmail
		// if (selected_infos == 3) {
		// NotificationCompat.InboxStyle inboxStyle = new
		// NotificationCompat.InboxStyle();
		//
		// // Sets a title for the Inbox in expanded layout
		// inboxStyle.setBigContentTitle(connection_name);
		//
		// // Moves events into the expanded layout
		// inboxStyle.addLine("IP: " + formatIP(ip_address) + " Speed: " +
		// link_speed + " Mbps");
		// inboxStyle.addLine("Signal strength: " + signal_level + "%");
		//
		// // Moves the expanded layout object into the notification object.
		// mBuilder.setStyle(inboxStyle);
		// }

		// Creates an explicit intent for an Activity in your app
		Intent appIntent = new Intent(ctx, SettingsActivity.class);
		Intent wifiIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);

		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(SettingsActivity.class);

		// Adds the Intent that starts the Activity to the top of the stack
		if (tap_option.equalsIgnoreCase("app")) {
			stackBuilder.addNextIntent(appIntent);
		} else {
			stackBuilder.addNextIntent(wifiIntent);
		}

		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(SettingsActivity.ID, mBuilder.build());

	}

	public static void offNotify(Context ctx) {

		// cancel notification
		NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(SettingsActivity.ID);

	}

	// format IP address in human readable format
	public static String formatIP(int ip) {
		return String.format(Locale.getDefault(), "%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
	}

}
