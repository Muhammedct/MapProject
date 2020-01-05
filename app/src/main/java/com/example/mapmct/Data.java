package com.example.mapmct;

import java.io.Serializable;

public class Data implements Serializable
{
    public String baslik,gonderen;
    public double lat, lng ;

    public Data()
    {

    }

    public Data(String baslik, String gonderen, double lat, double lng) {
        this.baslik = baslik;
        this.gonderen = gonderen;
        this.lat = lat;
        this.lng = lng;
    }


    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getGonderen() {
        return gonderen;
    }

    public void setGonderen(String gonderen) {
        this.gonderen = gonderen;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}