package tonchev.yourplace;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class OpitActivity extends AppCompatActivity {

    private TextView tv;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opit);
        img = (ImageView) findViewById(R.id.place_img);
        tv = (TextView) findViewById(R.id.text_name);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                tv.setText(place.getName());

            }


            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Toast.makeText(OpitActivity.this, "An error occurred: " + status, Toast.LENGTH_LONG).show();
            }
        });


    }
}
