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
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayGame extends AppCompatActivity {

    // PlayGame local variables
    double longitude = 0, latitude = 0;
    int satelliteNum =0;
    int pints = 0;
    private int seconds = 14400;
    private boolean running;
    CWaypoint homeWaypoint;
    int currentWaypoint = -1;
    int numOfPubWayPoints = 4;

    //button reference
    Button homeBTN;
    Button drinkBTN;



    ArrayList<CWaypoint> pubLookUp=new ArrayList<CWaypoint>();


    //CWaypoint pubLookUp[] = {cannon, ashley, threeLegg, romansRest};
    double[][] wpLookup = new double[100][2];//used for sorting - a lookup table

    //int currentWaypoint = 0; //pointer variable
    //int numOfWayPoints = 0;
    int nearestWayPoint= -1; //-1 not available.
    Location currLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        // sets number of pints drunk
        TextView numOfPints = (TextView)findViewById(R.id.numOfPints);
        numOfPints.setText("Pints drunk: " + pints);

        // set timer
        running = true;
        runTimer();

        // add pub waypoints to pub look up table
        addPubWaypoints();

        // disable home & drink btn's until pints have been drunk
        homeBTN = (Button) findViewById(R.id.homeBTN); //get form reference to button
        homeBTN.setEnabled(false);						//disable button
        drinkBTN = (Button) findViewById(R.id.drinkBTN); //get form reference to button
        drinkBTN.setEnabled(false);

        // when timer runs out
        if(seconds  == 0){
            homeBTN.setEnabled(true);
        }

        // get home waypoint
        Intent intent = getIntent();
        homeWaypoint = (CWaypoint) intent.getSerializableExtra("message");

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

    void addPubWaypoints() {
        // pub Lookup table
        pubLookUp.add(new CWaypoint( -1.12086, 53.3250799999,"The Cannon", false));
        pubLookUp.add(new CWaypoint( -1.1217716666,53.31722, "The Ashley", false));
        pubLookUp.add(new CWaypoint( -1.128458333, 53.32547666,"Three Legged Stool", false));
        pubLookUp.add(new CWaypoint( -1.1341583333, 53.3258483333, "Romans Rest", false));
    }

    private void runTimer(){
        final TextView countDown = (TextView) findViewById(R.id.countDown);
        final Handler handler = new Handler();
        handler.post(new Runnable(){
            @Override
            public void run(){
                int hours = seconds/3600;
                int minutes = (seconds%3600) / 60;
                int secs = seconds%60;
                String time = String.format("%d:%02d:%02d", hours, minutes, secs);
                countDown.setText("Time left:" + time);
                if(running){
                    seconds--;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    //Sort list into a lookup
    void calcDistances() //calc distance from current to all waypoints
    {
        for (int i =0; i < numOfPubWayPoints; i++){
            if(!pubLookUp.get(i).found) {
                float distance[] = new float[5];
                currLocation.distanceBetween(latitude, longitude, pubLookUp.get(i).latitude, pubLookUp.get(i).longitude, distance);

                double distanceToWP = distance[0]; // picks top distance in array (nearest way point)
                wpLookup[i][0] = i;
                wpLookup[i][1] = distanceToWP;
            }else{
                wpLookup[i][0] = i;
                wpLookup[i][1] = -1;
            }
        }

        //sort by distance
        for(int i=0;i<numOfPubWayPoints-1;i++)
        {
            for(int j=0;j<numOfPubWayPoints-1;j++)
            {
                if (wpLookup[j][1]> wpLookup[j+1][1])
                {
                    // swap
                    double tempWP = wpLookup[j][0];
                    double tempWPDist = wpLookup[j][1];

                    wpLookup[j][0] = wpLookup[j + 1][0];
                    wpLookup[j][1] = wpLookup[j + 1][1];

                    wpLookup[j + 1][0] = tempWP;
                    wpLookup[j + 1][1] = tempWPDist;
                }
            }
        }


        if (wpLookup[0][1] < 3 && wpLookup[0][1]  >= 0) // neraest pub waypoint
        {
            currentWaypoint = (int)wpLookup[0][0];
            drinkBTN.setEnabled(true);
        }
        else
        {
            drinkBTN.setEnabled(false);
            currentWaypoint = -1;
        }

    }

    // have pint
    public void drinkPint(View view){
        if(currentWaypoint >= 0){
            pubLookUp.remove(currentWaypoint);
            numOfPubWayPoints--;
            pints++;
            TextView numOfPints = (TextView)findViewById(R.id.numOfPints);
            numOfPints.setText("Pints drunk: " + pints);
            consequence();
        }
    }

    // consequence of having a pint
    public void consequence(){
        Random rand = new Random(System.currentTimeMillis());
        double chance = pints  / 10;
        double punkProbability =  rand.nextDouble();
        double policeProbability = rand.nextDouble();
        if(punkProbability < chance){
            Intent intent = new Intent(PlayGame.this, Punked.class);
            startActivity(intent);
        }
        if(policeProbability < chance){
            Intent intent = new Intent(PlayGame.this, Arrested.class);
            startActivity(intent);
        }
    }

    //location class
    /* Class My Location Listener */
    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location loc)
        {
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();

            calcDistances();
            // for testing
            String buffer = "";
            String nearShortDist = String.format("%.2f", wpLookup[0][1]);
            buffer+= "Nearest pub is:" + wpLookup[0][0] + "  and is "+nearShortDist +"m away\n";

            for(int i=0;i<pubLookUp.size();i++) //shows all lookup list - sorted
            {
                String shortDistance = String.format("%.2f", wpLookup[i][1]);
                buffer+= "WP: " + wpLookup[i][0] + " distance:" + shortDistance ;
                buffer+="\n";
            }
            /*
            for(int i=0;i<pubLookUp.size();i++)//displays all waypoints - used for testing
            {
                buffer+= "lon: " + pubLookUp.get(i).latitude +": ";
                buffer+= "lat: " + pubLookUp.get(i).longitude +"\n";
            }*/
            TextView test = (TextView) findViewById(R.id.wayView);
            test.setText(buffer);

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