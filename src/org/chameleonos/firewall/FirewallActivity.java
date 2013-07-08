/*
 * Copyright (C) 2013 The ChameleonOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.chameleonos.firewall;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Clark Scheff
 *
 */
public class FirewallActivity extends Activity {
	private ListView mDrawerList;
	private DrawerLayout mDrawerLayout;
	private CharSequence mTitle = "";
	private ActionBarDrawerToggle mDrawerToggle;
	private List<NavigationDrawerItem> mNavItems;
	
	enum Type { Header, Fragment }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.firewall_activity);

		//mPlanetTitles = getResources().getStringArray(R.array.planets_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		setupNavItems();
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer_holo_dark,
                0,
                0
                ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
	}
	
	private void setupNavItems() {
		mNavItems = new ArrayList<NavigationDrawerItem>();
		mNavItems.add(new NavigationDrawerItem(MainFragment.class.getName(), "Firewall", Type.Fragment, R.drawable.ic_firewall));
		mNavItems.add(new NavigationDrawerItem(LogFragment.class.getName(), "Log", Type.Fragment, R.drawable.ic_log));
		mNavItems.add(new NavigationDrawerItem(ShowRulesFragment.class.getName(), "View Rules", Type.Fragment, R.drawable.ic_show_rules));
		mNavItems.add(new NavigationDrawerItem(ProfilesFragment.class.getName(), "Profiles", Type.Fragment, R.drawable.ic_profiles));
		mNavItems.add(new NavigationDrawerItem(HelpFragment.class.getName(), "Help", Type.Fragment, R.drawable.ic_help));
		mNavItems.add(new NavigationDrawerItem(SettingsFragment.class.getName(), "Settings", Type.Fragment, R.drawable.ic_settings));
		// Set the adapter for the list view
		mDrawerList.setAdapter(new NavigationAdapter());
		selectItem(0);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
	    // Create a new fragment and specify the planet to show based on position
	    Fragment fragment = null;
		try {
			fragment = (Fragment)(Class.forName(mNavItems.get(position).fragment)).newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (ClassNotFoundException e) {
		}

		if (fragment != null) {
			// Insert the fragment by replacing any existing fragment
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
	                   .replace(R.id.content_frame, fragment)
	                   .commit();
			setTitle(mNavItems.get(position).title);
		}

	    // Highlight the selected item, update the title, and close the drawer
	    mDrawerList.setItemChecked(position, true);
	    mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
	    mTitle = title;
	    getActionBar().setTitle(mTitle);
	}
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private class NavigationDrawerItem {
		Type type;
		String fragment;
		String title;
		int iconId;
		
		public NavigationDrawerItem(String fragment, String title, Type type) {
			this(fragment, title, type, 0);
		}

		public NavigationDrawerItem(String fragment, String title, Type type, int iconId) {
			this.fragment = fragment;
			this.title = title;
			this.type = type;
			this.iconId = iconId;
		}
	}
	
	private class NavigationAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mNavItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mNavItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = getLayoutInflater().inflate(R.layout.navigation_list_item, null);
			NavigationDrawerItem item = mNavItems.get(position);
			
			ImageView iv = (ImageView) convertView.findViewById(R.id.icon);
			iv.setImageResource(item.iconId);
			
			TextView tv = (TextView) convertView.findViewById(R.id.title);
			tv.setText(item.title);
			
			return convertView;
		}
		
	}
}
