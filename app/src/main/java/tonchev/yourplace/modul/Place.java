package tonchev.yourplace.modul;


import android.location.Location;
import android.support.annotation.NonNull;


import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by Цветомир on 2.4.2017 г..
 */

public class Place implements Comparable<Place>,Serializable {



//    public enum Type {CLUB,BAR,HOTEl,ATM,CASIONO}

//    private Type type;








    private String name;
    private String adress;
    private String rating;
    private String category;
    private String openNow;
//    private String adress;
//    private Time workTime;
//    private double distance;

//    private ArrayList<Integer> pictures;


    public Place() {
    }

    public Place(String name) {
        this.name = name;
    }
    @Override
    public int compareTo(Place o) {
        //TODO
        return this.name.compareTo(o.name);
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
}
