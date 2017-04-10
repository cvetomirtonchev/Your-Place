package tonchev.yourplace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import tonchev.yourplace.modul.Place;


public class PlaceActivity extends AppCompatActivity {
    private TextView name ;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        place = (Place) getIntent().getSerializableExtra("mqsto");

        name= (TextView) findViewById(R.id.activity_place_name);
        name.setText(place.getName());
    }
}
