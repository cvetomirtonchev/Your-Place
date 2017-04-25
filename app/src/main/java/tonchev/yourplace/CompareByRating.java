package tonchev.yourplace;

import java.util.Comparator;

import tonchev.yourplace.modul.Place;

/**
 * Created by Vasko on 25.4.2017.
 */

public class CompareByRating implements Comparator<Place> {
    @Override
    public int compare(Place o1, Place o2) {
        return - o1.getRating().compareTo(o2.getRating());
    }
}
