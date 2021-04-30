package com.example.happyhour;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    double longitude = 0, latitude = 0;
    int satelliteNum =0;
    CWaypoint homeWaypoint;

    //button reference
    Button shortGametBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shortGametBTN = (Button) findViewById(R.id.shortGametBTN); //get form reference to button
        shortGametBTN.setEnabled(false);						//disable button

        //get gps
        //Use the LocationManager class to obtain GPS locations
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new MyLocationListener();


        //Ask for Permission to get location
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        //ACCESS_COARSE_LOCATION.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
    }

    //button click - create waypoints
    public void setHomePoint(View view) throws IOException
    {
        homeWaypoint = (new CWaypoint(longitude, latitude, "information", true)); // after false add number of sprite
        shortGametBTN.setEnabled(true);

    }

    //Launch new activity with data
    public void loadScreen(View view)
    {
        Intent intent = new Intent(MainActivity.this, PlayGame.class);
        intent.putExtra("message", homeWaypoint); //sends named as message
        startActivity(intent);
    }


    //location class
    /* Class My Location Listener */
    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location loc)
        {
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
            // round latitude to 2 decimal place
            String shortLat = String.format("%.2f", latitude);
            String shortLon = String.format("%.2f", longitude);

            TextView tLat = (TextView) findViewById(R.id.latView);
            tLat.setText("Latitude: " + shortLat);

            TextView tLong = (TextView) findViewById(R.id.lonView);
            tLong.setText("Longitude: " + shortLon);

            satelliteNum = loc.getExtras().getInt("satellites");

            //TextView tSatNum = (TextView) findViewById(R.id.satNumTxt);
            //tSatNum.setText("NumOfSats: " + satNum + "    NumOfWp: " + numOfWayPoints);

        }

        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}

        // @Override
        //public void onLocationChanged(@NonNull Location location) {

        //}

        public void onStatusChanged(String provider, int status, Bundle extras) {}

    }/* End of Class MyLocation*/
}