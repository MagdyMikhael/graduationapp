package com.example.migosoft.graduationproject;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

public class GPSTracker extends Service implements LocationListener {

    boolean GPSAllowed = false;
    boolean networkState = false;
    boolean canGetLocation = false;
    Location myLocation;
    LocationManager myManager;
    double longitude, latitude;
    Context getContext;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public GPSTracker(Context getContext) {
        this.getContext = getContext;
        getMyLocation();
    }

    public Location getMyLocation() {
        try {
            myManager = (LocationManager) getContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            GPSAllowed = myManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            networkState = myManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!GPSAllowed && !networkState) {
                // no network provider and GPS provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (networkState) {
                    myManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (myManager != null) {
                        myLocation = myManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (myLocation != null) {
                            latitude = myLocation.getLatitude();
                            longitude = myLocation.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                else if (GPSAllowed) {
                    if (myLocation == null) {
                        myManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (myManager != null) {
                            myLocation = myManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (myLocation != null) {
                                latitude = myLocation.getLatitude();
                                longitude = myLocation.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return myLocation;
    }

    public void stopUsingGPS() {
        if (myManager != null) {
            myManager.removeUpdates(GPSTracker.this);
        }
    }

    public double getLatitude() {
        if (myLocation != null) {
            latitude = myLocation.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (myLocation != null) {
            longitude = myLocation.getLongitude();
        }
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext);

        alertDialog.setTitle("GPS settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to enable it?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getContext.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


}
