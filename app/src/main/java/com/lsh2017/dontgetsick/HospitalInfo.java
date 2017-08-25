package com.lsh2017.dontgetsick;

/**
 * Created by 이소희 on 2017-08-23.
 */

public class HospitalInfo {

    //1.병원이름
    //2.위치
    String name;
    String addr;
    double lat;
    double lng;
    String iconUrl;
    boolean openHours;
    double rating;
    boolean star=false;

    public HospitalInfo(String name, String addr, double lat, double lng, String iconUrl, boolean openHours, double rating) {
        this.name = name;
        this.addr = addr;
        this.lat = lat;
        this.lng = lng;
        this.iconUrl = iconUrl;
        this.openHours = openHours;
        this.rating = rating;
    }
}
