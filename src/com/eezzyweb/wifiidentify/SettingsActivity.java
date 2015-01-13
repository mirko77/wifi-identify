package com.eezzyweb.wifiidentify;

import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = false;
	private static final String KEY_SHOW_NOTIFICATION = "pref_status_bar_notification";
	private static final String KEY_START_ON_BOOT = "pref_start_on_boot";
	private static final String KEY_SHOW_IP = "pref_show_ip";
	private static final String KEY_SHOW_LINK_SPEED = "pref_show_link_speed";
	private static final String KEY_SHOW_SIGNAL_STRENGTH = "pref_show_signal_strength";
	private static final String KEY_TAP_OPTION = "pref_tap_option";
	public static final int ID = 77;

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setupSimplePreferencesScreen();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}

		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		ActionBar actionBar = getActionBar();

		Log.e("sfsdf", "dsdfsd");

		// Add 'notification' preferences.
		addPreferencesFromResource(R.xml.pref_notification);

		
	}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			
			
			
            if(value instanceof Boolean) {
               //handle boolean
            } else if(value instanceof String) {
                //handle String
            	String stringValue = value.toString();

    			// For all other preferences, set the summary to the value's
    			// simple string representation.
    			preference.setSummary(stringValue);

    			Log.i("value", stringValue);
            }
           
			
			
			
			

			return true;
		}
	};

	
	/**
	 * This fragment shows notification preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class NotificationPreferenceFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_notification);
			
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		boolean toggle = false;

		if (key.equals(KEY_SHOW_NOTIFICATION)) {

			Preference notificationPref = findPreference(key);

			//show/hide notification
			toggle = sharedPreferences.getBoolean(KEY_SHOW_NOTIFICATION, false);

			if (toggle) {
				ToggleNotify.onNotify(this);
			} else {
				ToggleNotify.offNotify(this);
			}

		}

		if (key.equals(KEY_START_ON_BOOT)) {
			Preference connectionPref = findPreference(key);
		}

		if (key.equals(KEY_SHOW_IP) || key.equals(KEY_SHOW_LINK_SPEED) || key.equals(KEY_TAP_OPTION) || key.equals(KEY_SHOW_SIGNAL_STRENGTH)) {
			ToggleNotify.onNotify(this);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!isSimplePreferences(this)) {

			//if we are using fragments we need to get hold of default preferences object
			getPreferenceManager().getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

		} else {

			getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		}

	}

	@Override
	protected void onPause() {
		super.onPause();

		if (!isSimplePreferences(this)) {
			getPreferenceManager().getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

		} else {

			getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.done:

			this.finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
