package ordenese.rider.Common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.ContextThemeWrapper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import ordenese.rider.AppContext;
import ordenese.rider.R;


public class GpsLocationTracker extends Service implements LocationListener {

    /**
     * context of calling class
     */
    private Context mContext;

    /**
     * flag for gps status
     */
    private boolean isGpsEnabled = false;

    /**
     * flag for network status
     */
    private boolean isNetworkEnabled = false;

    /**
     * flag for gps
     */
    private boolean canGetLocation = false;

    /**
     * location
     */
    private Location mLocation;

    /**
     * latitude
     */
    private double mLatitude;

    /**
     * longitude
     */
    private double mLongitude;

    /**
     * min distance change to get location update
     */
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 1;

    /**
     * min time for location update
     * 60000 = 1min
     */
    private static final long MIN_TIME_FOR_UPDATE = 1000;

    /**
     * location manager
     */
    private LocationManager mLocationManager;


    /**
     * @param mContext constructor of the class
     */
    public GpsLocationTracker(Context mContext) {

        this.mContext = mContext;
        getLocation();
    }


    /**
     * @return location
     */
    @SuppressLint("MissingPermission")
    public Location getLocation() {

        try {

            if (ActivityCompat.checkSelfPermission(AppContext.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(AppContext.getAppContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            } else {
                mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

                if (mLocationManager != null) {

                    /*getting status of the gps*/
                    isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                    /*getting status of network provider*/
                    isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    if (!isGpsEnabled && !isNetworkEnabled) {

                        /*no location provider enabled*/
                    } else {

                        this.canGetLocation = true;

                        /*getting location from network provider*/
                        if (isNetworkEnabled) {

                            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_CHANGE_FOR_UPDATE, this);

                            if (mLocationManager != null) {

                                mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                                if (mLocation != null) {

                                    mLatitude = mLocation.getLatitude();

                                    mLongitude = mLocation.getLongitude();
                                }
                            }
                            /*if gps is enabled then get location using gps*/
                            if (isGpsEnabled) {

                                if (mLocation == null) {

                                    if (mLocationManager != null) {

                                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_CHANGE_FOR_UPDATE, this);

                                        if (mLocationManager != null) {

                                            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                            if (mLocation != null) {

                                                mLatitude = mLocation.getLatitude();

                                                mLongitude = mLocation.getLongitude();
                                            }

                                        }

                                    }

                                }

                            }
                        }
                    }

                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return mLocation;
    }

    /**
     * call this function to stop using gps in your application
     */
    public void stopUsingGps() {

        if (mLocationManager != null) {

            mLocationManager.removeUpdates(GpsLocationTracker.this);

        }
    }

    /**
     * @return latitude
     * <p/>
     * function to get latitude
     */
    public double getLatitude() {

        if (mLocation != null) {

            mLatitude = mLocation.getLatitude();
        }
        return mLatitude;
    }

    /**
     * @return longitude
     * function to get longitude
     */
    public double getLongitude() {

        if (mLocation != null) {

            mLongitude = mLocation.getLongitude();

        }

        return mLongitude;
    }

    /**
     * @return to check gps or wifi is enabled or not
     */
    public boolean canGetLocation() {

        return this.canGetLocation;
    }

    /**
     * function to prompt user to open
     * settings to enable gps
     */
    public void showSettingsAlert() {

        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.AppTheme));

        mAlertDialog.setTitle(AppContext.getAppContext().getResources().getString(R.string.gps_disabled));

        mAlertDialog.setMessage(AppContext.getAppContext().getResources().getString(R.string.gps_is_not_enabled));

        mAlertDialog.setPositiveButton(AppContext.getAppContext().getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent mIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(mIntent);
            }
        });

        mAlertDialog.setNegativeButton(AppContext.getAppContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();

            }
        });

        final AlertDialog mcreateDialog = mAlertDialog.create();
        mcreateDialog.show();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocation();
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
}
