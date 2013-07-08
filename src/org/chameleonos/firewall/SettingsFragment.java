/**
 * Class that allows user to modify settings of the app
 * 
 * Copyright (C) 2012-2014	Jason Tschohl
 * Copyright (C) 2013 The ChameleonOS Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Jason Tschohl
 * @version 1.0
 */

package org.chameleonos.firewall;

import org.chameleonos.firewall.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {
	public void onCreate(Bundle savedInstanceState) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity().getApplicationContext());
		String language = prefs.getString("locale", "en");
		Api.changeLanguage(getActivity().getApplicationContext(), language);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.user_settings);
	}

	@Override
	public void onResume() {
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		boolean enabled = getActivity().getApplicationContext().getSharedPreferences(
				Api.PREFS_NAME, 0).getBoolean(Api.PREF_ENABLED, false);
		boolean ipv6 = sharedPreferences.getBoolean("ipv6enabled", false);
		if (key.equals("ipv6enabled")) {
			if (ipv6) {
				toggleIPv6enabled();
			} else {
				if (enabled) {
					purgeIp6Rules();
				}
			}
		}
		if (key.equals("logenabled")) {
			toggleLogenabled();
		}
		if (key.equals("sdcard")) {
			sdcardSupport();
		}
		if (key.equals("vpnsupport")) {
			toggleVPNenabled();
			Api.applications = null;
		}
		if (key.equals("roamingsupport")) {
			toggleRoamenabled();
			Api.applications = null;
		}
		if (key.equals("notifyenabled")) {
			toggleNotifyenabled();
		}
		if (key.equals("taskertoastenabled")) {
			toggleTaskerNotifyenabled();
		}
		if (key.equals("locale")) {
			Api.applications = null;
			Intent intent = new Intent();
			getActivity().setResult(RESULT_OK, intent);
		}
	}

	private void purgeIp6Rules() {
		final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(getActivity(),
				res.getString(R.string.working),
				res.getString(R.string.deleting_rules), true);
		final Handler handler = new Handler() {
			final Context context = getActivity().getApplicationContext();
			public void handleMessage(Message msg) {
				try {
					progress.dismiss();
				} catch (Exception ex) {
				}
				if (Api.purgeIp6tables(context, true)) {
					Toast.makeText(context,
							R.string.rules_deleted, Toast.LENGTH_SHORT).show();
				}
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
	}

	/**
	 * Toggle log on/off
	 */
	private void toggleLogenabled() {
		final SharedPreferences prefs = getActivity().getSharedPreferences(Api.PREFS_NAME, 0);
		final boolean enabled = !prefs.getBoolean(Api.PREF_LOGENABLED, false);
		final Editor editor = prefs.edit();
		editor.putBoolean(Api.PREF_LOGENABLED, enabled);
		editor.commit();
		if (Api.isEnabled(getActivity())) {
			Api.applySavedIptablesRules(getActivity(), true);
		}
	}

	/**
	 * Toggle ipv6 on/off
	 */
	private void toggleIPv6enabled() {
		final SharedPreferences prefs = getActivity().getSharedPreferences(Api.PREFS_NAME, 0);
		final boolean enabled = !prefs.getBoolean(Api.PREF_IP6TABLES, false);
		final Editor editor = prefs.edit();
		editor.putBoolean(Api.PREF_IP6TABLES, enabled);
		editor.commit();
		if (Api.isEnabled(getActivity())) {
			Api.applySavedIptablesRules(getActivity(), true);
		}
	}

	/**
	 * Toggle VPN support on/off
	 */
	private void toggleVPNenabled() {
		final SharedPreferences prefs = getActivity().getSharedPreferences(Api.PREFS_NAME, 0);
		boolean vpnenabled = !prefs.getBoolean(Api.PREF_VPNENABLED, false);
		final Editor editor = prefs.edit();
		editor.putBoolean(Api.PREF_VPNENABLED, vpnenabled);
		editor.commit();
	}

	/**
	 * Toggle Roaming support on/off
	 */
	private void toggleRoamenabled() {
		final SharedPreferences prefs = getActivity().getSharedPreferences(Api.PREFS_NAME, 0);
		boolean roamenabled = !prefs.getBoolean(Api.PREF_ROAMENABLED, false);
		final Editor editor = prefs.edit();
		editor.putBoolean(Api.PREF_ROAMENABLED, roamenabled);
		editor.commit();
	}

	/**
	 * Toggle Notification support on/off
	 */
	private void toggleNotifyenabled() {
		final SharedPreferences prefs = getActivity().getSharedPreferences(Api.PREFS_NAME, 0);
		final boolean enabled = !prefs.getBoolean(Api.PREF_NOTIFY, false);
		final Editor editor = prefs.edit();
		editor.putBoolean(Api.PREF_NOTIFY, enabled);
		editor.commit();
	}

	/**
	 * Toggle apps on SDCard support on/off
	 */
	private void sdcardSupport() {
		final SharedPreferences prefs = getActivity().getSharedPreferences(Api.PREFS_NAME, 0);
		final boolean enabled = !prefs.getBoolean(Api.PREF_SDCARD, false);
		final Editor editor = prefs.edit();
		editor.putBoolean(Api.PREF_SDCARD, enabled);
		editor.commit();
	}

	/**
	 * Toggle Tasker Notification support on/off
	 */
	private void toggleTaskerNotifyenabled() {
		final SharedPreferences prefs = getActivity().getSharedPreferences(Api.PREFS_NAME, 0);
		final boolean enabled = !prefs.getBoolean(Api.PREF_TASKERNOTIFY, false);
		final Editor editor = prefs.edit();
		editor.putBoolean(Api.PREF_TASKERNOTIFY, enabled);
		editor.commit();
	}

	/**
	 * Set the activity result to RESULT_OK and terminate this activity.
	 */
	private void resultOk() {
	}
}