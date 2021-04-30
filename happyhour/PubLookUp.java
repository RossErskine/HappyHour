package com.example.happyhour;

public class PubLookUp {
    public CWaypoint cannon = new CWaypoint(53.3251,-1.1209, "The Cannon", false);
    public CWaypoint ashley = new CWaypoint(53.3172,-1.1218, "The Ashley", false);
    public CWaypoint threeLegg = new CWaypoint(53.3255,-1.1285, "Three Legged Stool", false);
    public CWaypoint romansRest = new CWaypoint(53.3258,-1.1342, "Romans Rest", false);

    public CWaypoint pubLookUp[] = {cannon, ashley, threeLegg, romansRest};

    public CWaypoint[] getPubLookUps(){
        return pubLookUp;
    }

    public int getSize(){
        return pubLookUp.length;
    }

    public double getLatitude(int i){
        return pubLookUp[i].latitude;
    }
    public double getLongitude(int i){
        return pubLookUp[i].longitude;
    }
}
