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
package org.chameleonos.firewall;

import org.chameleonos.firewall.R;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LogFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.logs_layout, null);
		String logs = Api.showLog(getActivity().getApplicationContext());
		TextView text = (TextView) v.findViewById(R.id.showlogs);
		Log.d(logs, "debugaf");
		text.setText(logs);
		
		return v;
	}

}
