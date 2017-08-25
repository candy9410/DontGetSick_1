package com.lsh2017.dontgetsick;

import android.widget.ImageView;

/**
 * Created by 이소희 on 2017-08-16.
 */

public class HospitalItem {

    String name;
    String addr;
    String iconUrl;
    boolean openHours;
    double rating;


    public HospitalItem(String name, String addr, String iconUrl, boolean openHours, double rating) {
        this.name = name;
        this.addr = addr;
        this.iconUrl = iconUrl;
        this.openHours = openHours;
        this.rating = rating;

    }
}
