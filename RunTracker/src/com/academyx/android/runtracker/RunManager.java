package com.academyx.android.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class RunManager {
	
	private static final String TAG = "RunManager";
	public static final String ACTION_LOCATION = "com.academyx.android.runtracker.ACTION_LOCATION";
	
	private static RunManager sRunManager;
	private Context mAppContext;
	private LocationManager mLocationManager;
	
	// The private constructor forces users to use RunManager.get(Context)
	private RunManager(Context appContext) {
		mAppContext = appContext;
		mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public static RunManager get(Context c) {
		if (sRunManager == null) {
			sRunManager = new RunManager(c.getApplicationContext());
		}
		return sRunManager;
	}
	
	private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
		return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
	}
	
	public void startLocationUpdates() {
		Log.d(TAG, "startLocationUpdates()");
		String provider = LocationManager.GPS_PROVIDER;
		
		// get the last known location and broadcast it if we have one
        Location lastKnown = mLocationManager.getLastKnownLocation(provider);
        if (lastKnown != null) {
            // reset the time to now
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }
		
		// Start updates from the location manager
		PendingIntent pi = getLocationPendingIntent(true);
		mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
	}
	
	public void stopLocationUpdates() {
		Log.d(TAG, "stopLocationUpdates()");
		PendingIntent pi = getLocationPendingIntent(true);
		if (pi != null) {
			mLocationManager.removeUpdates(pi);
			pi.cancel();
		}
	}
	
	public boolean isTrackingRun() {
		Log.d(TAG, "isTrackingRun()");
		return getLocationPendingIntent(false) != null;
	}
	
	private void broadcastLocation(Location location) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }

}
