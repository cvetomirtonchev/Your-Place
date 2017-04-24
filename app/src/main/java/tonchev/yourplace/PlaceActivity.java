package tonchev.yourplace;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import tonchev.yourplace.modul.Place;


public class PlaceActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private TextView name;
    private Place place;
    private TextView placeRating;
    private GoogleMap mMap;
    private MapView mapView;
    private GoogleApiClient mGoogleApiClient2;
    private ImageView firstImage;
    private String placeID;
    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback;
    private RatingBar ratingBar;
    private Button call;
    private Button direction;
    private Button webAdress;
    private Button savePlace;
    private TextView adress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        call = (Button) findViewById(R.id.activity_place_call);
        direction = (Button) findViewById(R.id.activity_place_direction_button);
        webAdress = (Button) findViewById(R.id.activity_place_web);
        savePlace = (Button) findViewById(R.id.activity_place_save);
        name = (TextView) findViewById(R.id.activity_place_name);
        mapView = (MapView) findViewById(R.id.map_view);
        firstImage = (ImageView) findViewById(R.id.image_v);
        ratingBar = (RatingBar) findViewById(R.id.activity_place_rating);
        placeRating = (TextView) findViewById(R.id.activity_place_rating_text);
        adress = (TextView) findViewById(R.id.activity_place_adress);

        if (getIntent().getSerializableExtra("mqsto") != null) {
            place = (Place) getIntent().getSerializableExtra("mqsto");
            name.setText(place.getName());
            ratingBar.setRating(Float.parseFloat(place.getRating()));
            placeRating.setText(place.getRating());
            adress.setText(place.getAdress());
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(place.getPhoneNumber()!=null) {
                        String phone = place.getPhoneNumber();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                        if (ActivityCompat.checkSelfPermission(PlaceActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(PlaceActivity.this, "Sorry, we don't have phone number.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            webAdress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(place.getWebAdress()!=null) {
                        String url = place.getWebAdress();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(PlaceActivity.this, "Sorry, we don't have web page.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            direction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getIntent().getExtras().getDoubleArray("LL")!= null) {
                        double[] coord = getIntent().getExtras().getDoubleArray("LL");
                        LatLng placeLatLng = new LatLng(coord[0],coord[1]);
                        place.setLatLng(placeLatLng);
                        Toast.makeText(PlaceActivity.this, "" + place.getLatLng(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + place.getLatLng().latitude+","+place.getLatLng().longitude));
                        startActivity(intent);
                    }
                }
            });

       }
        if (getIntent().getExtras().getString("ID")!= null){
           placeID = getIntent().getExtras().getString("ID");
       }


        mGoogleApiClient2 = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        if(placeID != null) {
            new PhotoAsyncTask().execute(placeID);
        }


    }

       private class PhotoAsyncTask extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... params) {
                final String placeId = params[0];
                Places.GeoDataApi.getPlacePhotos(mGoogleApiClient2, placeId)
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
                                            .getScaledPhoto(mGoogleApiClient2, firstImage.getWidth(),
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