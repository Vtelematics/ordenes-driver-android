package ordenese.rider.Service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class GoogleService extends Service implements LocationListener {

    LocationManager locationManager;
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;

    public GoogleService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(str_receiver);
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // Register listener ONCE — updates will come via onLocationChanged
        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    10000,  // minimum 10 seconds between updates
                    5,      // minimum 5 meters movement
                    this
            );
            // Send last known location immediately so map isn't blank
            Location last = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (last != null) fn_update(last);

        } else if (isGPSEnabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,  // minimum 10 seconds between updates
                    5,      // minimum 5 meters movement
                    this
            );
            Location last = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (last != null) fn_update(last);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // This is called automatically when location changes — no timer needed
        fn_update(location);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up listener when service stops
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    private void fn_update(Location location) {
        intent.putExtra("latitude", String.valueOf(location.getLatitude()));
        intent.putExtra("longitude", String.valueOf(location.getLongitude()));
        sendBroadcast(intent);
    }
}