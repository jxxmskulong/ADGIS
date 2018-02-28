package com.xia.adgis.Main.Bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 *
 * Created by xiati on 2018/1/30.
 */

public class AD extends BmobObject implements Serializable {

    private double latitude;
    private double longitude;
    private String name;
    private String imageID;


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }
}
