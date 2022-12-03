package org.odk.collect.android.pkl.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;


/**
 * @author isfann
 */

public class LocationService implements LocationListener {
    public static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 1;

    private final String TAG = "Location Service";

    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    //The minimum time beetwen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

    public double getLongitude() {
        return bestLocation.getLongitude();
    }

    public double getLatitude() {
        return bestLocation.getLatitude();
    }

    private final static boolean forceNetwork = false;

    private static LocationService instance = null;

    private LocationManager locationManager;
    public Location location, bestLocation;
    public double longitude;
    public double latitude;
    public boolean isGPSEnabled;
    public boolean isNetworkEnabled;
    public boolean locationServiceAvailable;


    /**
     * Singleton implementation
     *
     * @return
     */
    public static LocationService getLocationManager(Context context) {
        if (instance == null) {
            instance = new LocationService(context);
        }
        return instance;
    }

    /**
     * LocalizerTask constructor
     */
    private LocationService(Context context) {

        initLocationService(context);
        Log.d("LocationService: ", "LocationService created");
    }

    /**
     * Sets up location service after permissions is granted
     */
    @TargetApi(23)
    private void initLocationService(Context context) {

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            this.longitude = 0.0;
            this.latitude = 0.0;
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (forceNetwork) isGPSEnabled = false;

            if (!isNetworkEnabled && !isGPSEnabled) {
                // cannot get location
                this.locationServiceAvailable = false;
                Log.d(TAG, "initLocationService: Net/GPS disabled");
            }
            //else
            {
                this.locationServiceAvailable = true;

                if (isNetworkEnabled) {
                    Log.d(TAG, "initLocationService: Net enabled");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        updateCoordinates();
                    }
                }//end if

                if (isGPSEnabled) {
                    Log.d(TAG, "initLocationService: GPS enabled");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                        updateCoordinates();
                    }
                }
            }

            bestLocation = location;

        } catch (Exception ex) {
            Log.e("", "Error creating location service: " + ex.getMessage());

        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled: called");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled: called");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged: called");
    }

    @Override
    public void onLocationChanged(Location location) {
        // do stuff here with location object
        Log.d(TAG, "onLocationChanged: Called");
        Log.d(TAG, "onLocationChanged: Latitude " + location.getLatitude());
        Log.d(TAG, "onLocationChanged: Longitude " + location.getLongitude());
        Log.d(TAG, "onLocationChanged: Accuracy " + location.getAccuracy());
        Log.d(TAG, "onLocationChanged: Provider " + location.getProvider());
        if (bestLocation != null) {
            if (bestLocation.getAccuracy() >= location.getAccuracy()) {
                bestLocation = location;
                Log.d(TAG, "onLocationChanged: bestLocation UPDATED");
            }
        } else {
            bestLocation = location;
        }
    }

    public Location getBestLoc() {
        return bestLocation;

    }

    public void stop(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager = null;
            instance = null;
            Log.d(TAG, "stop: Service Stopped");
        }
    }
}

