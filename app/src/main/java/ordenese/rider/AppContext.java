package ordenese.rider;

import android.app.Application;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;

public class AppContext extends Application  /*implements LocationListener*/ {

    private static AppContext appContext;

    private static DatabaseReference mDatabase_loc;
    private static DataSnapshot mDataSnapshot_loc;
    public static String order_id = "";

    public static LocationManager locationManager;
    public static FusedLocationProviderClient fusedLocationClient;
    public static LocationRequest locationRequest;
    public static LocationCallback locationCallback;
    public static BroadcastReceiver broadcastReceiver;

    public static AppContext getInstance() {
        return appContext;
    }

    private static final String ONESIGNAL_APP_ID = "5f274bfa-c517-4227-a602-5d5f633712f6";

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = this;

        FirebaseApp.initializeApp(this);

        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.setNotificationOpenedHandler(new NotificationOpenedHandler(appContext));
        OneSignal.setNotificationWillShowInForegroundHandler(new OneSignal.OSNotificationWillShowInForegroundHandler() {
            @Override
            public void notificationWillShowInForeground(OSNotificationReceivedEvent osNotificationReceivedEvent) {

            }
        });
//        OneSignal.provideUserConsent(true);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//        } else {
//            locationCallback = new LocationCallback() {
//                @Override
//                public void onLocationResult(LocationResult locationResult) {
//                    if (locationResult == null) {
//                        return;
//                    }
//                    if (Constant.DataGetValue(getAppContext(), Constant.DriverStatus).equals("1")) {
//                        current_loc_update(locationResult.getLastLocation());
//                    }
//                }
//            };
//        }

    }

    public static Context getAppContext() {
        return appContext.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(AppLanguageSupport.onAttach(base, "en"));
    }

    public static void startupdate(String Order_id) {
        order_id = Order_id;
    }

//    @Override
//    public void onLocationChanged(@NonNull Location location) {
//        Log.e("onLocationChanged: ", location +"" );
//        current_loc_update(location);
//    }
//
//    @Override
//    public void onProviderEnabled(@NonNull String provider) {
//        LocationListener.super.onProviderEnabled(provider);
//    }
//
//    @Override
//    public void onProviderDisabled(@NonNull String provider) {
//        LocationListener.super.onProviderDisabled(provider);
//    }

//    public static void startLocationUpdates() {
//
//        if (ActivityCompat.checkSelfPermission(getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(getAppContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
////        Intent intent = new Intent(appContext, GoogleService.class);
////        appContext.startService(intent);
////
////        appContext.registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));
//
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//            locationManager = (LocationManager) getAppContext().getSystemService(Context.LOCATION_SERVICE);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, getInstance());
//        } else {
//            locationRequest = new LocationRequest();
//            locationRequest.setFastestInterval(5000);
//            locationRequest.setInterval(10000);
//            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            if (locationRequest != null && locationCallback != null) {
//                fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext);
//                fusedLocationClient.requestLocationUpdates(locationRequest,
//                        locationCallback,
//                        Looper.getMainLooper());
//            }
//        }
//    }

//    public static void stopLocationUpdates() {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//            if (locationManager != null) {
//                locationManager.removeUpdates(getInstance());
//            }
//        } else {
//            if (fusedLocationClient != null && locationCallback != null) {
//                fusedLocationClient.removeLocationUpdates(locationCallback);
//            }
//        }
//
//        appContext.unregisterReceiver(broadcastReceiver);
//
//    }

    public void current_loc_update(Location location) {

        Log.e("current_loc_update: ", Constant.DataGetValue(getAppContext(), Constant.DriverStatus) + "");

        if (Constant.DataGetValue(getAppContext(), Constant.DriverStatus).equals("1")) {
            if (FirebaseDatabase.getInstance().getReference("drivers") != null) {
                String Id = Constant.DataGetValue(getAppContext(), Constant.Driver_Id);
                mDatabase_loc = FirebaseDatabase.getInstance().getReference("drivers");
                mDatabase_loc.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mDataSnapshot_loc = dataSnapshot;
                        if (mDatabase_loc != null) {
                            if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {

                                Log.e("onDataChange:1 ", location.getLatitude() + "");
                                Log.e("onDataChange:2 ", location.getLongitude() + "");

                                if (mDataSnapshot_loc.hasChild(Id)) {
                                    mDatabase_loc.child(Id).child("latitude").setValue(String.valueOf(location.getLatitude()));
                                    mDatabase_loc.child(Id).child("longitude").setValue(String.valueOf(location.getLongitude()));
                                    mDatabase_loc.child(Id).child("name").setValue(Constant.DataGetValue(getAppContext(), Constant.DriverName));
                                    // mDatabase_loc.child(Id).child("busy").setValue("1");
                                    mDatabase_loc.child(Id).child("telephone").setValue(Constant.DataGetValue(getAppContext(), Constant.DriverMobile));
                                } else {
                                    mDatabase_loc.child(Id).child("latitude").setValue(location.getLatitude());
                                    mDatabase_loc.child(Id).child("longitude").setValue(location.getLongitude());
                                    mDatabase_loc.child(Id).child("name").setValue(Constant.DataGetValue(getAppContext(), Constant.DriverName));
                                    //  mDatabase_loc.child(Id).child("busy").setValue("1");
                                    mDatabase_loc.child(Id).child("telephone").setValue(Constant.DataGetValue(getAppContext(), Constant.DriverMobile));
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("onCancelled: ", databaseError.getMessage() + "");
                    }
                });
            }
            /*if (order_id != null && !order_id.isEmpty()) {
                modelArrayList = new ArrayList<>();
                mDatabase = FirebaseDatabase.getInstance().getReference("task_status");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                String order_sta = Constant.DataGetValue(getAppContext(), Constant.Order_status_id);
                if (!order_sta.equals("1")) {
                    if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                        LatlongModel latlongModel = new LatlongModel();
                        latlongModel.setLatitude(location.getLatitude());
                        latlongModel.setLongitude(location.getLongitude());
                        modelArrayList.add(latlongModel);
                        mDatabase.child(order_id).child("total_distance").setValue(modelArrayList);
                    }
                }

            }*/
        }
    }



}
