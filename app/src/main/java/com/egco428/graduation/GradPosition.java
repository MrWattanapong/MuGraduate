package com.egco428.graduation;

/**
 * Created by Lostl2ose on 12/18/2016.
 */
public class GradPosition {

    private String username;
    private String latitude;
    private String longitude;

    public GradPosition(String username,String latitude,String longitude){
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GradPosition(){}

    public String getUsername(){return username;}
    public String getLatitude(){return latitude;}
    public String getLongitude(){return longitude;}
}
