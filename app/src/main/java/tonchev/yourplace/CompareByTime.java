package tonchev.yourplace;

import java.util.Comparator;

import tonchev.yourplace.modul.Place;

/**
 * Created by Vasko on 24.4.2017.
 */

public class CompareByTime implements Comparator<Place> {
    @Override
    public int compare(Place o1, Place o2) {

        return o1.getTimeValue() - o2.getTimeValue();
    }
}
