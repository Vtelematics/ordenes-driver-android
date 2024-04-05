package ordenese.rider.Common;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;


public class service extends Service {



    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent,startId);
        return super.onStartCommand(intent, flags, startId);

    }

    public void onStart(Intent intent, int startid) {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      //  Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        LocationListener ll = new MyLocationListener();
        //lm.requestLocationUpdates( LocationManager.GPS_PROVIDER, 5*60*10000, 0, ll);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);

    }


    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(Location loc)
        {
            if(loc!=null)
            {
                double x;


                String Text = "My current location is: " +"Latitud = " + loc.getLatitude() +"Longitud = " + loc.getLongitude();
              //  Toast.makeText( getApplicationContext(),Text,Toast.LENGTH_SHORT).show();
              //  Log.e( "onLocationChanged: ",""+Text );
        /*  cr.moveToFirst();
            while(!cr.isAfterLast()) {
                 al.add(cr.getString(cr.getColumnIndex(dbAdapter.KEY_NAME))); //add the item
                 cr.moveToNext();
            }*/
                //  lm.addProximityAlert(loc.getLatitude(), loc.getLongitude(), 10009,, intent)

                //for loop, alerters retrieving one by one
//      x= calcDistance(loc.getLatitude(),loc.getLongitude(),z,y);
                //      if(x<1){
                //        Intent myIntent = new Intent(ViewAlerters.this,CallMode.class);
                //        startActivity(myIntent);
            }

        }



        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }


    }
}
