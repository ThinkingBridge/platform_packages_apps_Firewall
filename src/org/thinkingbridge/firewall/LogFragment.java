/**
 * Dialog displayed when the "Show Log" menu option is selected
 * 
 * Copyright (C) 2012-2014	Jason Tschohl
 * Copyright (C) 2013 The ChameleonOS Project
 *
 * This program is free sftware: you can redistribute it and/or modify
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

import org.thinkingbridge.firewall.R;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class LogFragment extends Fragment {

	TextView mLog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.logs_layout, null);
		String logs = Api.showLog(getActivity().getApplicationContext());
		mLog = (TextView) v.findViewById(R.id.showlogs);
		mLog.setText(logs);
		
		return v;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.log_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.clearlog:
			clearLog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Clear logs
	 */
	private void clearLog() {
		final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(getActivity(),
				res.getString(R.string.working),
				res.getString(R.string.please_wait), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					progress.dismiss();
				} catch (Exception ex) {
				}
				if (Api.clearLog(getActivity())) {
					String logs = Api.showLog(getActivity().getApplicationContext());
					mLog.setText(logs);
				}
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
	}
}
