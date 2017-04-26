package technovations.ajuj.technovations2017;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;

import org.w3c.dom.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

/**
 * Created by uma on 4/16/2017.
 */
public class CoordinateFinder{

    public static Double[] getLatLongPositions(String a)
    {
        Geocoder coder = new Geocoder(LocationListView.getAppContext(), Locale.getDefault());
        List<Address> addresses = null;
        double longitude = 0;
        double latitude = 0;
        try {
            addresses = coder.getFromLocationName(a, 1);
            longitude = addresses.get(0).getLongitude();
            latitude = addresses.get(0).getLatitude();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String lat = "" + latitude;
        String lon = "" + longitude;
        Double[] res = new Double[2];
        res[0] = Double.parseDouble(lat);
        res[1] = Double.parseDouble(lon);
        return res;
    }

    public static double getDistance(double latitude1, double longitude1, double latitude2, double longitude2)
    {
        double theta = longitude1 - longitude2;
        double distance = Math.sin(degreeToradian(latitude1)) * Math.sin(degreeToradian(latitude2)) + Math.cos(degreeToradian(latitude1)) * Math.cos(degreeToradian(latitude2)) * Math.cos(degreeToradian(theta));
        distance = Math.acos(distance);
        distance = radianTodegree(distance);
        distance = distance * 60 * 1.1515; //in miles

        return (distance);
    }

    public static double degreeToradian(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double radianTodegree(double rad) {
        return (rad * 180 / Math.PI);
    }
}