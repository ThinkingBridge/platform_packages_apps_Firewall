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

package org.thinkingbridge.firewall;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_IPV6 = "ipv6enabled";
    private static final String KEY_VPN = "vpnsupport";
    private static final String KEY_ROAMING = "roamingsupport";
    private static final String KEY_LOG = "logenabled";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.user_settings);

        Preference preference = findPreference(KEY_IPV6);
        preference.setOnPreferenceChangeListener(this);

        preference = findPreference(KEY_VPN);
        preference.setOnPreferenceChangeListener(this);

        preference = findPreference(KEY_ROAMING);
        preference.setOnPreferenceChangeListener(this);

        preference = findPreference(KEY_LOG);
        preference.setOnPreferenceChangeListener(this);
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

   @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        String key = preference.getKey();
        if (key.equals(KEY_IPV6)) {
            boolean enabled = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getBoolean(Api.PREF_ENABLED, false);
            boolean ipv6 = ((Boolean)o).booleanValue();
            if (!ipv6 && enabled) {
                purgeIp6Rules();
            }
            if (Api.isEnabled(getActivity())) {
                Api.applySavedIptablesRules(getActivity(), true);
            }
        } else if (key.equals(KEY_VPN)) {
            Api.applications = null;
        } else if (key.equals(KEY_ROAMING)) {
            Api.applications = null;
        } else if (key.equals(KEY_LOG)) {
            if (Api.isEnabled(getActivity())) {
                Api.applySavedIptablesRules(getActivity(), true);
            }
        }
        return true;
    }
}