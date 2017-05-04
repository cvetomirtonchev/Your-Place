package tonchev.yourplace.modul;


import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Цветомир on 2.4.2017 г..
 */

public class Place implements Comparable<Place>,Serializable {


    private String id;
    private String name;
    private String adress;
    private String rating;
    private String category;
    private String openNow;
    private String phoneNumber;
    private String webAdress;
    private LatLng latLng;
    private String distance;
    private String distanceTime;
    private int distValue;
    private int timeValue;
    private ArrayList<Comment> comments = new ArrayList<>();

    public Place() {
    }

    public Place(String name) {
        this.name = name;
    }


    public Place(String id, String name, String adress, String rating, String phoneNumber, String webAdress) {
        this.id = id;
        this.name = name;
        this.adress = adress;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.webAdress = webAdress;

    }

    @Override
    public int compareTo(Place o) {
        //TODO
        int isOpen = 0;
        int isOpenO = 0;
        if (this.getOpenNow().equals("YES")) {
            isOpen = -1;
        }
        if (this.getOpenNow().equals("NO")) {
            isOpen = 0;
        }
        if (this.getOpenNow().equals("N/A")) {
            isOpen = 1;
        }
        if (o.getOpenNow().equals("YES")) {
            isOpenO = -1;
        }
        if (o.getOpenNow().equals("NO")) {
            isOpenO = 0;
        }
        if (o.getOpenNow().equals("N/A")) {
            isOpenO = 1;
        }
        return isOpen-isOpenO;
    }

    public String getDistanceTime() {
        return distanceTime;
    }

    public void setDistanceTime(String distanceTime) {
        this.distanceTime = distanceTime;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setWebAdress(String webAdress) {
        this.webAdress = webAdress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOpenNow() {
        return openNow;
    }

    public void setOpenNow(String open) {
        this.openNow = open;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdress() {
        return adress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebAdress() {
        return webAdress;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getDistValue() {
        return distValue;
    }

    public void setDistValue(int distValue) {
        this.distValue = distValue;
    }

    public int getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(int timeValue) {
        this.timeValue = timeValue;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }
}
