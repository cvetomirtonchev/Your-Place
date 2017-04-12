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



    public enum Type {CLUB,BAR,HOTEl,ATM,CASIONO}

    private Type type;
    private String name;
    private String adress;
    private Time workTime;
    private double distance;
    private int frontPic;
    private ArrayList<Integer> pictures;
    private double rating;


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
}
