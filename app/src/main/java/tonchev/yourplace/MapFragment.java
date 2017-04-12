package tonchev.yourplace;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final int MAX_PLACES = 20;
    private GoogleMap mMap;
    private MapView mapView;
    private LocationManager locMan;
    private Marker[] placeMarkers;
    private Marker userMarker;
    private boolean updateFinished;
    private MarkerOptions[] places;
    private int otherIcon;
    private int foodIcon;
    private LatLng location;
    private ArrayList<tonchev.yourplace.modul.Place> returnedPlaces = new ArrayList<>();

    interface ComunicatorFragment{
        void searchResult(Place place);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
//        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        mapView = (MapView) root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        foodIcon = R.mipmap.ic_place_black_24dp;
        otherIcon = R.mipmap.ic_place_black_24dp;


        return root;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        location = new LatLng(42.656669,23.345751);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //create marker array
        placeMarkers = new Marker[MAX_PLACES];
//        update location
        locMan = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 100, (LocationListener) this);
        mMap.setMyLocationEnabled(true);
        mMap.addMarker(new MarkerOptions().position(location).title("My new Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.656669, 23.345751), 15));
        new MapFragment.GetPlaces().execute();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v("MyMapActivity", "location changed");
        Log.v("Test", "location change block");
        updatePlaces();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.v("MyMapActivity", "status changed");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.v("MyMapActivity", "provider enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.v("MyMapActivity", "provider disabled");
    }

    private void updatePlaces() {
        //get location manager
        //get last location
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLoc = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double lat = lastLoc.getLatitude();
        double lng = lastLoc.getLongitude();
        //create LatLng
        LatLng lastLatLng = new LatLng(lat, lng);

        //remove any existing marker
        if (userMarker != null) userMarker.remove();
        //create and set marker properties
        userMarker = mMap.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title("You are here")
                .snippet("Your last recorded location"));
        //move to location
        mMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);

        //build places query string

        //execute query


        Toast.makeText(getActivity(), "size" + returnedPlaces.size(), Toast.LENGTH_SHORT).show();
    }

    private class GetPlaces extends AsyncTask<Void, Void, ArrayList<tonchev.yourplace.modul.Place>> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected ArrayList<tonchev.yourplace.modul.Place> doInBackground(Void... params) {
//            //fetch places
//            updateFinished = false;
//            StringBuilder placesBuilder = new StringBuilder();
//            for (String placeSearchURL : placesURL) {
//                try {
//
//                    URL requestUrl = new URL(placeSearchURL);
//                    HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.connect();
//                    int responseCode = connection.getResponseCode();
//
//                    if (responseCode == HttpURLConnection.HTTP_OK) {
//
//                        BufferedReader reader = null;
//
//                        InputStream inputStream = connection.getInputStream();
//                        if (inputStream == null) {
//                            return "";
//                        }
//                        reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//
//                            placesBuilder.append(line + "\n");
//                        }
//
//                        if (placesBuilder.length() == 0) {
//                            return "";
//                        }
//
//                        Log.d("test", placesBuilder.toString());
//                    } else {
//                        Log.i("test", "Unsuccessful HTTP Response Code: " + responseCode);
//                    }
//                } catch (MalformedURLException e) {
//                    Log.e("test", "Error processing Places API URL", e);
//                } catch (IOException e) {
//                    Log.e("test", "Error connecting to Places API", e);
//                }
//            }
            String request = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.656669,23.345751&radius=30000&sensor=true&types=atm&key=AIzaSyCH1yrshoqnPRvH62XLDQI8PYdAFP-MehY";//ADD KEY

            try {
                URL url = new URL(request);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                Scanner sc = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();

                while (sc.hasNextLine()) {
                    response.append(sc.nextLine());
                }

                JSONObject jsonObject = new JSONObject(response.toString());
                // make an jsonObject in order to parse the response
                Log.d("test", response.toString());
                if (jsonObject.has("results")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        tonchev.yourplace.modul.Place poi = new tonchev.yourplace.modul.Place();
                        if (jsonArray.getJSONObject(i).has("name")) {
                            poi.setName(jsonArray.getJSONObject(i).optString("name"));
                            poi.setRating(jsonArray.getJSONObject(i).optString("rating", " "));
                            if (jsonArray.getJSONObject(i).has("opening_hours")) {
                                if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) {
                                    if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").getString("open_now").equals("true")) {
                                        poi.setOpenNow("YES");
                                    } else {
                                        poi.setOpenNow("NO");
                                    }
                                }
                            } else {
                                poi.setOpenNow("Not Known");
                            }
                            if (jsonArray.getJSONObject(i).has("types")) {
                                JSONArray typesArray = jsonArray.getJSONObject(i).getJSONArray("types");
                                for (int j = 0; j < typesArray.length(); j++) {
                                    poi.setCategory(typesArray.getString(j) + ", " + poi.getCategory());
                                }
                            }
                        }
                        returnedPlaces.add(poi);

                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return returnedPlaces;
        }



        //process data retrieved from doInBackground
        protected void onPostExecute(ArrayList<tonchev.yourplace.modul.Place> returnedPlaces) {
//            //parse place data returned from Google Places
//            //remove existing markers
//            if (placeMarkers != null) {
//                for (int pm = 0; pm < placeMarkers.length; pm++) {
//                    if (placeMarkers[pm] != null)
//                        placeMarkers[pm].remove();
//                }
//            }
//            try {
//                //parse JSON
//
//                //create JSONObject, pass stinrg returned from doInBackground
//                JSONObject resultObject = new JSONObject(result);
//                //get "results" array
//                JSONArray placesArray = resultObject.getJSONArray("results");
//                //marker options for each place returned
//                places = new MarkerOptions[placesArray.length()];
//                //loop through places
//
//                Log.d("test", "The placesArray length is " + placesArray.length() + "...............");
//
//                for (int p = 0; p < placesArray.length(); p++) {
//                    //parse each place
//                    //if any values are missing we won't show the marker
//                    boolean missingValue = false;
//                    LatLng placeLL = null;
//                    String placeName = "";
//                    String vicinity = "";
//                    int currIcon = otherIcon;
//                    try {
//                        //attempt to retrieve place data values
//                        missingValue = false;
//                        //get place at this index
//                        JSONObject placeObject = placesArray.getJSONObject(p);
//                        //get location section
//                        JSONObject loc = placeObject.getJSONObject("geometry")
//                                .getJSONObject("location");
//                        //read lat lng
//                        placeLL = new LatLng(Double.valueOf(loc.getString("lat")),
//                                Double.valueOf(loc.getString("lng")));
//                        //get types
//                        JSONArray types = placeObject.getJSONArray("types");
//                        //loop through types
//                        for (int t = 0; t < types.length(); t++) {
//                            //what type is it
//                            String thisType = types.get(t).toString();
//                            //check for particular types - set icons
//                            if (thisType.contains("hospital")) {
//                                currIcon = foodIcon;
//                                break;
//                            } else if (thisType.contains("health")) {
//                                currIcon = foodIcon;
//                                break;
//                            } else if (thisType.contains("doctor")) {
//                                currIcon = foodIcon;
//                                break;
//                            }
//                        }
//                        //vicinity
//                        vicinity = placeObject.getString("vicinity");
//                        //name
//                        placeName = placeObject.getString("name");
//                    } catch (JSONException jse) {
//                        Log.v("PLACES", "missing value");
//                        missingValue = true;
//                        jse.printStackTrace();
//                    }
//                    //if values missing we don't display
//                    if (missingValue) places[p] = null;
//                    else
//                        places[p] = new MarkerOptions()
//                                .position(placeLL)
//                                .title(placeName)
//                                .snippet(vicinity);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (places != null && placeMarkers != null) {
//                Log.d("test", "The placeMarkers length is " + placeMarkers.length + "...............");
//
//                for (int p = 0; p < places.length && p < placeMarkers.length; p++) {
//                    //will be null if a value was missing
//
//                    if (places[p] != null) {
//
//                        placeMarkers[p] = mMap.addMarker(places[p]);
//                    }
//                }
//            }
            Log.d("test", "Size" + returnedPlaces.size());
            for (int i = 0; i < returnedPlaces.size(); i++) {
                Log.d("resplac", returnedPlaces.get(i).getName() + " open now: " + returnedPlaces.get(i).getOpenNow() + " rating: " + returnedPlaces.get(i).getRating());
            }
        }
    }

}
