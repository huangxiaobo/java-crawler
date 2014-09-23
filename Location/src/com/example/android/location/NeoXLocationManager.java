package com.example.android.location;



import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;


//http://stackoverflow.com/questions/3145089/what-is-the-simplest-and-most-robust-way-to-get-the-users-current-location-in-a
public class NeoXLocationManager 
{
	LocationManager locationManager = null;

	boolean gps_enabled = false;

	boolean network_enabled = false;

	class NeoXLocationListener implements LocationListener {
		public void onLocationChanged(Location location) 
		{
			locationChanged(location);
		}

		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {} 
	};

	private final NeoXLocationListener locationListenerGps = new NeoXLocationListener();
	private final NeoXLocationListener locationListenerNetwork = new NeoXLocationListener();

	public boolean startUpdatingLocation(Context context)
	{
		Log.i("NeoXLocationManager", "hxb startUpdatingLocation 1");
		if(locationManager == null)
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		Log.i("NeoXLocationManager", "hxb startUpdatingLocation 2");
		//exceptions will be thrown if provider is not permitted.
		try { gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); } catch(Exception ex) {}
		Log.i("NeoXLocationManager", "hxb startUpdatingLocation 3");
		try { network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); } catch(Exception ex) {}
		Log.i("NeoXLocationManager", "hxb startUpdatingLocation 4");

		//don't start listeners if no provider is enabled
		if (!gps_enabled && !network_enabled)
			return false;
		Log.i("NeoXLocationManager", "hxb startUpdatingLocation 5");
		if (gps_enabled)
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps, Looper.getMainLooper());
		Log.i("NeoXLocationManager", "hxb startUpdatingLocation 6");
		if (network_enabled)
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork, Looper.getMainLooper());
		Log.i("NeoXLocationManager", "hxb startUpdatingLocation 7");
		return true;		
	}

	public void stopUpdatingLocation(Context context)
	{
		if (locationManager == null) {
			return;
		}
		
		if (gps_enabled) {
		 locationManager.removeUpdates(locationListenerGps);
		}

		if (network_enabled) {
		 locationManager.removeUpdates(locationListenerNetwork);
		}

		return;
	}	



	private void locationChanged(Location location)
	{
		if (location == null) return;
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
		Log.i("NeoXLocationManager", "hxb locationChanged: latitude:" + latitude + " longitude:" + longitude);
		//NativeInterface.NativeOnLocationUpdated(longitude, latitude, System.currentTimeMillis() / 1.0e3);
	}
}