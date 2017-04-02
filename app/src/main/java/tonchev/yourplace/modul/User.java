package tonchev.yourplace.modul;

import java.util.TreeSet;

/**
 * Created by Цветомир on 2.4.2017 г..
 */

public class User {
    private String email;
    private String password;
    private String name;
    private TreeSet<Place> myPlaces;


    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.myPlaces = new TreeSet<>();
    }

    public void addPlace(Place place){
        myPlaces.add(place);
    }
}
