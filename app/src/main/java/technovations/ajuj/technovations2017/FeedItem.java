package technovations.ajuj.technovations2017;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by jenny on 3/23/2017.
 */
public class FeedItem {
    private int id;
    private String name, status, image, profilePic, timeStamp, uid, dorr, receiver;
    private boolean vegetables, dairy, meat, bread, fats;

    public FeedItem() {
    }

    public FeedItem(int id, String name, String status, String profilePic, String timeStamp, boolean vegetables, boolean dairy, boolean meat, boolean bread, boolean fats, String uid, String dorr, String receiver) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.vegetables = vegetables;
        this.dairy = dairy;
        this.meat = meat;
        this.bread = bread;
        this.fats = fats;
        this.uid = uid;
        this.dorr = dorr;
        this.receiver = receiver;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImge() {
        return image;
    }

    public void setImge(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean getVegetables() {
        return vegetables;
    }

    public void setVegetables(boolean vegetables) {
        this.vegetables = vegetables;
    }

    public boolean getDairy() {
        return dairy;
    }

    public void setDairy(boolean dairy) {
        this.dairy = dairy;
    }

    public boolean getMeat() {
        return meat;
    }

    public void setMeat(boolean meat) {
        this.meat = meat;
    }

    public boolean getBread() {
        return bread;
    }

    public void setBread(boolean bread) {
        this.bread = bread;
    }

    public boolean getFats() {
        return fats;
    }

    public void setFats(boolean fats) {
        this.fats = fats;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDorr() {
        return dorr;
    }

    public void setDorr(String dorr) {
        this.dorr = dorr;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String[] getInterests(){
        String[] interests = new String[5];
        if(getVegetables())
            interests[0]="vegetable";
        if(getDairy())
            interests[1]="dairy";
        if(getMeat())
            interests[2]="meat";
        if(getBread())
            interests[3]="bread";
        if(getFats())
            interests[4]="fats";
        return interests;
    }

}