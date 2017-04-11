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
    private String name, status, image, profilePic, timeStamp, interests, uid, dorr, receiver;

    public FeedItem() {
    }

    public FeedItem(int id, String name, String status, String profilePic, String timeStamp, String interests, String uid, String dorr, String receiver) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.interests = interests;
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

    public String getUrl() {
        return interests;
    }

    public void setUrl(String interests) {
        this.interests = interests;
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

}