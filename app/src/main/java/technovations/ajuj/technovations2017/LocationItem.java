package technovations.ajuj.technovations2017;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by uma on 4/16/2017.
 */
public class LocationItem {
    private String org = "";
    private String address = "";
    private String phone = "";
    private String dorr = "";
    private String distance = "";
    private String username = "";
    private double distanceVal = 0;

    public void setOrg(String org) {
        this.org = org;
    }

    public String getOrg() {
        return org;
    }

    public void setAddress(String a) {
        this.address = a;
    }

    public String getAddress() {
        return address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setUsername(String u) {
        this.username = u;
    }

    public String getUsername() {
        return username;
    }

    public void setDorr(String d) {
        if(d.equals("donor"))
            this.dorr = "Donor";
        else
            this.dorr = "Receiver";
    }

    public String getDorr() {
        return dorr;
    }

    public void setDistance() {
        Double[] myPosition = CoordinateFinder.getLatLongPositions(LocationListView.getMyAddress());
        Double[] otherPosition = CoordinateFinder.getLatLongPositions(this.address);

        Location loc1 = new Location("");
        loc1.setLatitude(myPosition[0]);
        loc1.setLongitude(myPosition[1]);

        Location loc2 = new Location("");
        loc2.setLatitude(otherPosition[0]);
        loc2.setLongitude(otherPosition[1]);

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        if(loc1.distanceTo(loc2) == 0.0)
            this.distance = 0.0 + " Miles";
        else {
            this.distanceVal = (double) (loc1.distanceTo(loc2) / 1000) * 0.621371;
            this.distance = "" + df.format(this.distanceVal) + " Miles";
        }
    }

    public String getDistance()
    {
        return distance;
    }

    public double getDistanceVal()
    {
        return distanceVal;
    }

}