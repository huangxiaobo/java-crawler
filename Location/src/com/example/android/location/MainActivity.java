/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.example.android.location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 室内没有GPS
 * 
 * The update interval is hard-coded to be 5 seconds.
 */
public class MainActivity extends Activity {
	private static String TAG = "hxb";
	/*
	 * Note if updates have been turned on. Starts out as "false"; is set to
	 * "true" in the method handleRequestSuccess of LocationUpdateReceiver.
	 */
	boolean mUpdatesRequested = false;

	NeoXLocationManager mgr;

	LocationManager locationManager;

	private final LocationListener locationListenerNetwork = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			Log.d(TAG, "network onLocationChanged");
			if (location != null) {
				Log.i(TAG, "Location changed : Lat: " + location.getLatitude()
						+ " Lng: " + location.getLongitude());
			}
		}

		public void onProviderDisabled(String provider) {
			Log.d(TAG, "Network onProviderDisabled");
		}

		public void onProviderEnabled(String provider) {
			Log.d(TAG, "Network onProviderEnabled");
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d(TAG, "Network onStatusChanged: " + status);
		}
	};

	private final LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) { // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
			// log it when the location changes
			Log.d(TAG, "gps onLocationChanged");
			if (location != null) {
				Log.i(TAG, "Location changed : Lat: " + location.getLatitude()
						+ " Lng: " + location.getLongitude());
			}
			Toast.makeText(getApplicationContext(),
					"Location changed : Lat: " + location.getLatitude()
							+ " Lng: " + location.getLongitude(),
					Toast.LENGTH_SHORT);
		}

		public void onProviderDisabled(String provider) {
			// Provider被disable时触发此函数，比如GPS被关闭
			Log.d(TAG, "Gps onProviderDisabled");
		}

		public void onProviderEnabled(String provider) {
			// Provider被enable时触发此函数，比如GPS被打开
			Log.d(TAG, "Gps onProviderEnabled");
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// Provider的转态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
			Log.d(TAG, "Gps onStatusChanged");
		}
	};

	/*
	 * Initialize the Activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// mgr = new NeoXLocationManager();
		// boolean result = mgr.startUpdatingLocation(this);
		// Log.d(TAG, "start result: " + result);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		boolean gps_enabled = false;
		boolean network_enabled = false;

		// exceptions will be thrown if provider is not permitted.
		try {
			gps_enabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			Log.d(TAG, "gps_enabled + " + ex);
		}
		try {
			network_enabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
			Log.d(TAG, "network_enabled + " + ex);
		}

		Log.d(TAG, "network_enabled: " + network_enabled + " gps_enabled: "
				+ gps_enabled);

		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_FINE); // 精度高
		c.setPowerRequirement(Criteria.POWER_LOW); // 电量消耗低
		c.setAltitudeRequired(false); // 不需要海拔
		c.setSpeedRequired(false); // 不需要速度
		c.setCostAllowed(false); // 不需要费用
		String provider = locationManager.getBestProvider(c, false); // false是指不管当前适配器是否可用
		Log.d(TAG, provider);
		
		
		////////////////////////////////////////* NETWORK *////////////////////////////////////////////
		final TextView label_network_lat_lng = (TextView)findViewById(R.id.label_network_lat_lng);
		
		Button start_network_location = (Button)findViewById(R.id.start_network_location);
		start_network_location.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
			}
		});
		
		Button stop_network_location = (Button)findViewById(R.id.stop_network_location);
		stop_network_location.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				locationManager.removeUpdates(locationListenerNetwork);
			}
		});
		
		Button update_network_location = (Button)findViewById(R.id.update_network_location);
		update_network_location.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location == null)
					return;
				double longitude = location.getLongitude();
				double latitude = location.getLatitude();
				String result = "经度: " + latitude + "纬度: " + longitude;
				label_network_lat_lng.setText(result);
				Log.d(TAG, latitude + "," + longitude);
			}
		});		
		
		////////////////////////////////////////* GPS*////////////////////////////////////////////
		final TextView label_gps_lat_lng = (TextView)findViewById(R.id.label_gps_lat_lng);
		
		Button start_gps_location = (Button)findViewById(R.id.start_gps_location);
		start_gps_location.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
			}
		});
		
		Button stop_gps_location = (Button)findViewById(R.id.stop_gps_location);
		stop_gps_location.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				locationManager.removeUpdates(locationListenerGps);
			}
		});
		
		Button update_gps_location = (Button)findViewById(R.id.update_gps_location);
		update_gps_location.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (location == null)
					return;
				double longitude = location.getLongitude();
				double latitude = location.getLatitude();
				String result = "经度: " + latitude + "纬度: " + longitude;
				label_gps_lat_lng.setText(result);
				Log.d(TAG, latitude + "," + longitude);
			}
		});		
		
		
		////////////////////////////
		mgr = new NeoXLocationManager();
		mgr.startUpdatingLocation(this);
	}

	/*
	 * Called when the Activity is no longer visible at all. Stop updates and
	 * disconnect.
	 */
	@Override
	public void onStop() {

		super.onStop();
	}

	/*
	 * Called when the Activity is going into the background. Parts of the UI
	 * may be visible, but the Activity is inactive.
	 */
	@Override
	public void onPause() {
		super.onPause();

		locationManager.removeUpdates(locationListenerGps);
		locationManager.removeUpdates(locationListenerNetwork);
	}

	/*
	 * Called when the Activity is restarted, even before it becomes visible.
	 */
	@Override
	public void onStart() {

		super.onStart();

	}

	/*
	 * Called when the system detects that this Activity is now visible.
	 */
	@Override
	public void onResume() {
		super.onResume();

	}

}
