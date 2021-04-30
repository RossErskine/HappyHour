package com.example.happyhour;

import java.io.Serializable;

public class CWaypoint implements Serializable
{
    public double latitude;          //add more suitable variables
    public double longitude;
    public String information;	//add name description etc?
    public boolean found;		//has this item been collected?

    CWaypoint()					//Empty Constructor
    {
        longitude=0;
        latitude=0;
        information = "null";
        found = false;
    }

    CWaypoint(double lon, double lat, String information, boolean found)
    {
        this.longitude=lon;
        this.latitude=lat;
        this.information = information;
        this.found = found;
    }
    CWaypoint(double lon, double lat, String information)
    {
        this.longitude=lon;
        this.latitude=lat;
        this.information = information;
    }

    void foundWP(){
        found = true;
    }
}

