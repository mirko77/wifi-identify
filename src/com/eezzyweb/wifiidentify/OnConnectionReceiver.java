package com.eezzyweb.wifiidentify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class OnConnectionReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.e("onReceive", "received");
		
		//check network status
		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {

			//get network info
			NetworkInfo nwInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

			//check for device connected successfully
			if (NetworkInfo.State.CONNECTED.equals(nwInfo.getState())) {//This implies the WiFi connection is through

				//show notification
				ToggleNotify.onNotify(context);

			}

			else {

				//remove notification
				ToggleNotify.offNotify(context);

			}
		}

		//check on boot
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			
			//check if user wants to run notification at boot
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			boolean start_on_boot = sharedPref.getBoolean("pref_start_on_boot", true);
			
			if(start_on_boot){
				
				ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo nwInfo = cm.getActiveNetworkInfo();
				if (nwInfo == null) {
				    // There are no active networks.
				    return;
				}
				
				boolean isConnected = nwInfo.isConnected();
				
				if(isConnected){
					
					//show notification
					ToggleNotify.onNotify(context);
				}
				
			}
		}

	}//onReceive
}
