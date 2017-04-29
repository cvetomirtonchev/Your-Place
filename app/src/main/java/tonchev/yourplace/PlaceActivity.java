package tonchev.yourplace;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import tonchev.yourplace.modul.Comment;
import tonchev.yourplace.modul.MyConnectionChecker;
import tonchev.yourplace.modul.Place;


public class PlaceActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private TextView name;
    private Place place;
    private TextView placeRating;
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
    private static int counterPhotos = 0;
    private ImageView secondImage;
    private ImageView thirdImage;
    private boolean isClicked1;
    private boolean isClicked2;
    private Button priviusButton;
    private Button nextButton;
    private TextView openNow;
    private volatile boolean slideShowed;
    private RecyclerView commentsView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        call = (Button) findViewById(R.id.activity_place_call);
        direction = (Button) findViewById(R.id.activity_place_direction_button);
        webAdress = (Button) findViewById(R.id.activity_place_web);
        savePlace = (Button) findViewById(R.id.activity_place_save);
        name = (TextView) findViewById(R.id.activity_place_name);
        openNow = (TextView) findViewById(R.id.activity_place_open_now);
        firstImage = (ImageView) findViewById(R.id.image_v1);
        ratingBar = (RatingBar) findViewById(R.id.activity_place_rating);
        placeRating = (TextView) findViewById(R.id.activity_place_rating_text);
        adress = (TextView) findViewById(R.id.activity_place_adress);
        secondImage = (ImageView) findViewById(R.id.image_v2);
        thirdImage = (ImageView) findViewById(R.id.image_v3);
        priviusButton = (Button) findViewById(R.id.place_activity_button_photo_back);
        nextButton = (Button) findViewById(R.id.place_activity_button_photo_next);
        commentsView = (RecyclerView) findViewById(R.id.comments_recycler_view);


        if (getIntent().getSerializableExtra("mqsto") != null) {
            place = (Place) getIntent().getSerializableExtra("mqsto");
            if (place.getName() != null) {
                name.setText(place.getName());
            }
            if (place.getRating() != null) {
                try {
                    ratingBar.setRating(Float.parseFloat(place.getRating()));
                    placeRating.setText(place.getRating());
                } catch (NumberFormatException e) {
                    placeRating.setText("N/A");
                }
            } else {
                placeRating.setText("N/A");
            }
            if (place.getAdress() != null) {
                adress.setText("Address: " + place.getAdress());
            }
            if (place.getOpenNow() != null) {
                openNow.setText("Open now: " + place.getOpenNow());
            } else {
                openNow.setText("Open now: N/A");
            }
            if (getIntent().getExtras().getDoubleArray("LL") != null) {
                double[] coord = getIntent().getExtras().getDoubleArray("LL");
                LatLng placeLatLng = new LatLng(coord[0], coord[1]);
                place.setLatLng(placeLatLng);
            }
            if(!place.getComments().isEmpty()) {
                CommentListAdapter commentAdapter = new CommentListAdapter(this, place.getComments());
                commentsView.setAdapter(commentAdapter);
                commentsView.setLayoutManager(new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false));
            }else{
                new GetComments().execute();

            }

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (place.getPhoneNumber() != null && !place.getPhoneNumber().isEmpty()) {
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
                    } else {
                        Toast.makeText(PlaceActivity.this, "Sorry, we don't have phone number.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            Log.d("placeweb", "web: " + place.getWebAdress() + " phone: " + place.getPhoneNumber());
            webAdress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!MyConnectionChecker.haveNetworkConnection(PlaceActivity.this)) {
                        Toast.makeText(PlaceActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (place.getWebAdress() != null && !place.getWebAdress().isEmpty()) {
                            String url = place.getWebAdress();

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        } else {
                            Toast.makeText(PlaceActivity.this, "Sorry, we don't have web page.", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
            direction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getIntent().getExtras().getDoubleArray("LL") != null) {
                        Toast.makeText(PlaceActivity.this, "" + place.getLatLng(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + place.getLatLng().latitude + "," + place.getLatLng().longitude));
                        startActivity(intent);
                    }
                }
            });

        }
        if (getIntent().getExtras().getString("ID") != null) {
            placeID = getIntent().getExtras().getString("ID");
            Log.d("placeid", "id:" + placeID);
        }


        mGoogleApiClient2 = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        if (placeID != null) {
            new PhotoAsyncTask().execute(placeID);
            Log.d("placeIDtest", placeID);
        }
//        priviusButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(isClicked1==true&&isClicked2==false){
//                    secondImage.setVisibility(View.GONE);
//                    firstImage.setVisibility(View.VISIBLE);
//                    isClicked1=false;
//
//                }
//                if(isClicked1==true&&isClicked2==true){
//                    thirdImage.setVisibility(View.GONE);
//                    secondImage.setVisibility(View.VISIBLE);
//                    isClicked2=false;
//                }
//            }
//        });
//        nextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(isClicked1==true&&isClicked2==false){
//                    secondImage.setVisibility(View.GONE);
//                    thirdImage.setVisibility(View.VISIBLE);
//                    isClicked2=true;
//                }
//                if(isClicked1==false&&isClicked2==false){
//                    firstImage.setVisibility(View.GONE);
//                    secondImage.setVisibility(View.VISIBLE);
//                    isClicked1=true;
//
//                }
//            }
//        });


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
                            try {
                                if (photoMetadataBuffer.getCount() > 0) {
                                    // Display the first bitmap in an ImageView in the size of the view
                                    photoMetadataBuffer.get(counterPhotos)
                                            .getScaledPhoto(mGoogleApiClient2, firstImage.getWidth(),
                                                    firstImage.getHeight())
                                            .setResultCallback(mDisplayPhotoResultCallback);
                                }
                            } catch (IllegalStateException e) {
                                Toast.makeText(PlaceActivity.this, "Images unavailable", Toast.LENGTH_SHORT).show();

                            }


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
                    if (counterPhotos == 0) {
                        firstImage.setImageBitmap(placePhotoResult.getBitmap());
                        new PhotoAsyncTask().execute(placeID);
                    }
                    if (counterPhotos == 1) {
                        secondImage.setImageBitmap(placePhotoResult.getBitmap());
                        new PhotoAsyncTask().execute(placeID);

                    }
                    if (counterPhotos == 2) {
                        thirdImage.setImageBitmap(placePhotoResult.getBitmap());
                        new SlideShow().execute();

                    }
                    counterPhotos++;
                    if (counterPhotos == 3) {
                        counterPhotos = 0;
                    }
                }
            };
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class SlideShow extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int counter = 0;
            slideShowed = true;
            while (slideShowed) {
                try {
                    if (counter == 0) {
                        publishProgress(counter);
                        Thread.sleep(5000);
                        counter++;
                    }
                    if (counter == 1) {
                        publishProgress(counter);
                        Thread.sleep(5000);
                        counter++;
                    }
                    if (counter == 2) {
                        publishProgress(counter);
                        Thread.sleep(5000);
                        counter = 0;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Integer counter = values[0];
            if (counter == 0) {
                thirdImage.setVisibility(View.GONE);
                secondImage.setVisibility(View.GONE);
                firstImage.setVisibility(View.VISIBLE);
            }
            if (counter == 1) {
                firstImage.setVisibility(View.GONE);
                thirdImage.setVisibility(View.GONE);
                secondImage.setVisibility(View.VISIBLE);
            }
            if (counter == 2) {
                secondImage.setVisibility(View.GONE);
                firstImage.setVisibility(View.GONE);
                thirdImage.setVisibility(View.VISIBLE);
            }


        }
    }

    private class GetComments extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            String request = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeID + "&key=AIzaSyCH1yrshoqnPRvH62XLDQI8PYdAFP-MehY";
            //AIzaSyC5kPaJ2sfQvnqINcskPKDxDmrgfsQ9ACk
            //AIzaSyCH1yrshoqnPRvH62XLDQI8PYdAFP-MehY
            try {
                URL url = new URL(request);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                Scanner sc = new Scanner(connection.getInputStream());
                StringBuilder result = new StringBuilder();
                while (sc.hasNextLine()) {
                    result.append(sc.nextLine());
                }
                Log.d("akostane", "" + result.toString());
                JSONObject jsonObject = new JSONObject(result.toString());

                if (jsonObject.has("result")) {
                    JSONArray comments = jsonObject.getJSONObject("result").getJSONArray("reviews");

                    if (comments != null) {
                        for (int j = 0; j < comments.length(); j++) {
                            String user = comments.getJSONObject(j).getString("author_name");
                            String review = comments.getJSONObject(j).getString("text");
                            String rating = comments.getJSONObject(j).getString("rating");
                            Comment comm = new Comment(user, review, rating);
                            place.getComments().add(comm);
                            Log.d("comentari", "Name: " + place.getComments().get(j).getUser() + "  text: " + place.getComments().get(j).getText() + "  rating: " + place.getComments().get(j).getRating());
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            CommentListAdapter commentAdapter = new CommentListAdapter(PlaceActivity.this, place.getComments());
            commentsView.setAdapter(commentAdapter);
            commentsView.setLayoutManager(new GridLayoutManager(PlaceActivity.this, 1, LinearLayoutManager.VERTICAL, false));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       firstImage.setImageDrawable(null);
        secondImage.setImageDrawable(null);
        thirdImage.setImageDrawable(null);
        slideShowed = false;
    }
}