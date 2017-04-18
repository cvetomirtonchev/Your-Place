package tonchev.yourplace;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


import static android.content.Context.LOCATION_SERVICE;

import static tonchev.yourplace.ChoseActivity.location;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener,  GoogleApiClient.OnConnectionFailedListener {

    private static final int MAX_PLACES = 20;
    private GoogleMap mMap;
    private MapView mapView;
    private LocationManager locMan;
    private Marker userMarker;
    private ArrayList<tonchev.yourplace.modul.Place> returnedPlaces;
    private Button generateList;
    private Button backToMap;
    private RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        generateList = (Button) root.findViewById(R.id.button_generate_list);
        backToMap = (Button) root.findViewById(R.id.button_back_to_map);
        mapView = (MapView) root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        returnedPlaces = new ArrayList<>();
        recyclerView = (RecyclerView) root.findViewById(R.id.map_list_view);
        final PlaceListAdapter adapter = new PlaceListAdapter(getActivity(),returnedPlaces);

        generateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateList.setVisibility(View.GONE);
                backToMap.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false));
                mapView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
        backToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMap.setVisibility(View.GONE);
                generateList.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);

            }
        });
        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
//        update location

        locMan = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 100, (LocationListener) this);

        locMan = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 100, (LocationListener) this);

        mMap.setMyLocationEnabled(true);
          if (location!=null) {
            mMap.addMarker(new MarkerOptions().position(location).title("You are here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
          }
        if(ChoseActivity.selection!=null) {
            returnedPlaces.clear();
            new MapFragment.GetPlaces().execute();

        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    interface ComunicatorFragment{
        void searchResult(Place place);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v("MyMapActivity", "location changed");
        Log.v("Test", "location change block");
//        updatePlaces();
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

//    private void updatePlaces() {
//        //get location manager
//        //get last location
//        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        double lat = lastLoc.getLatitude();
//        double lng = lastLoc.getLongitude();
//        //create LatLng
//        LatLng lastLatLng = new LatLng(lat, lng);
//
//        //remove any existing marker
//        if (userMarker != null) userMarker.remove();
//        //create and set marker properties
//        userMarker = mMap.addMarker(new MarkerOptions()
//                .position(lastLatLng)
//                .title("You are here")
//                .snippet("Your last recorded location"));
//        //move to location
//        mMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);
//
//    }

    private class GetPlaces extends AsyncTask<Void, Void, ArrayList<tonchev.yourplace.modul.Place>> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected ArrayList<tonchev.yourplace.modul.Place> doInBackground(Void... params) {
            String request = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location.latitude + "," + location.longitude+"&radius=2000&sensor=true&types="+ChoseActivity.selection+"&key=AIzaSyCH1yrshoqnPRvH62XLDQI8PYdAFP-MehY";//ADD KEY

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
                        //get lat & lng
                        JSONObject temp = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
                        double lat = temp.getDouble("lat");
                        double lng = temp.getDouble("lng");
                        LatLng latLng = new LatLng(lat, lng);
                        tonchev.yourplace.modul.Place poi = new tonchev.yourplace.modul.Place();
                        poi.setLatLng(latLng);
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

            Log.d("test", "Size" + returnedPlaces.size());
            for (int i = 0; i < returnedPlaces.size(); i++) {
                tonchev.yourplace.modul.Place temp = returnedPlaces.get(i);
                Log.d("resplac", returnedPlaces.get(i).getName() + " open now: " + returnedPlaces.get(i).getOpenNow() + " rating: " + returnedPlaces.get(i).getRating());
                Marker newPlace = mMap.addMarker(new MarkerOptions()
                        .position(temp.getLatLng())
                        .title(temp.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .alpha(0.4f)
                        .flat(true));
            }
            new GetDistance().execute();
        }
    }

    private class GetDistance extends AsyncTask <Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String distanceRequest;
            LatLng placeLatLng;
            for (int i = 0; i < returnedPlaces.size(); i ++) {
                placeLatLng = returnedPlaces.get(i).getLatLng();
                distanceRequest = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+location.latitude+","+location.longitude+"&destinations="+placeLatLng.latitude+"%2C"+placeLatLng.longitude+"&key=AIzaSyCH1yrshoqnPRvH62XLDQI8PYdAFP-MehY";
                try {
                    URL url2 = new URL(distanceRequest);
                    HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
                    connection2.setRequestMethod("GET");
                    Scanner sc2 = new Scanner(connection2.getInputStream());
                    StringBuilder responseDist = new StringBuilder();

                    while (sc2.hasNextLine()) {
                        responseDist.append(sc2.nextLine());
                    }
                    // Get distance and time for obj
                    JSONObject jsonObject2 = new JSONObject(responseDist.toString());

                    String object = jsonObject2.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getString("text");

                    Log.d("testdi", jsonObject2.toString());
                    returnedPlaces.get(i).setDistance(object);

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (tonchev.yourplace.modul.Place place : returnedPlaces) {
                Log.d("distanceTest", place.getName() + " " + place.getDistance());
            }
        }
    }

}
