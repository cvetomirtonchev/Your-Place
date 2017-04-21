package tonchev.yourplace;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import java.lang.ref.PhantomReference;

import tonchev.yourplace.modul.Place;


public class PlaceActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private TextView name ;
    private Place place;
    private TextView placeRating;
    private GoogleMap mMap;
    private MapView mapView;
    private GoogleApiClient mGoogleApiClient;
    private ImageView firstImage;
    private String placeID;
    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        place = (Place) getIntent().getSerializableExtra("mqsto");
        placeID = getIntent().getExtras().getString("ID");

        name = (TextView) findViewById(R.id.activity_place_name);
        name.setText(place.getName());
        mapView = (MapView) findViewById(R.id.map_view);
        firstImage = (ImageView) findViewById(R.id.image_v);
        ratingBar = (RatingBar) findViewById(R.id.activity_place_rating);
        ratingBar.setRating(Float.parseFloat(place.getRating()));
        placeRating = (TextView) findViewById(R.id.activity_place_rating_text);
        placeRating.setText(place.getRating());
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        if(placeID != null) {
            new PhotoAsyncTask().execute(placeID);
        }
    }

        class PhotoAsyncTask extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... params) {
                final String placeId = params[0];
                Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                        .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {


                            @Override
                            public void onResult(PlacePhotoMetadataResult photos) {
                                if (!photos.getStatus().isSuccess()) {
                                    return;
                                }

                                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                                if (photoMetadataBuffer.getCount() > 0) {
                                    // Display the first bitmap in an ImageView in the size of the view
                                    photoMetadataBuffer.get(0)
                                            .getScaledPhoto(mGoogleApiClient, firstImage.getWidth(),
                                                    firstImage.getHeight())
                                            .setResultCallback(mDisplayPhotoResultCallback);
                                }
                                photoMetadataBuffer.release();
                            }
                        });
                return null;

            }
            @Override
            protected void onPostExecute(Void aVoid) {

                mDisplayPhotoResultCallback = new ResultCallback<PlacePhotoResult>() {
                    @Override
                    public void onResult(PlacePhotoResult placePhotoResult) {
                        if (!placePhotoResult.getStatus().isSuccess()) {
                            return;
                        }
                        firstImage.setImageBitmap(placePhotoResult.getBitmap());
                    }
                };
            }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}