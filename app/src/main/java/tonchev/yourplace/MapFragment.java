package tonchev.yourplace;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import java.util.Collections;
import java.util.Scanner;

import tonchev.yourplace.modul.Comment;
import tonchev.yourplace.modul.MyConnectionChecker;

import static android.content.Context.LOCATION_SERVICE;
import static tonchev.yourplace.ChoseActivity.location;
import static tonchev.yourplace.ChoseActivity.setRadius;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener,  GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private MapView mapView;
    private LocationManager locMan;
    private ArrayList<tonchev.yourplace.modul.Place> returnedPlaces;
    private Button generateList;
    private Button backToMap;
    private RecyclerView recyclerView;
    private LinearLayout sortingLayout;
    private Button sortKm;
    private Button sortTime;
    private Button sortOpen;
    private double[] ll = new double[2];

    private Button sortRating;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        generateList = (Button) root.findViewById(R.id.button_generate_list);
        backToMap = (Button) root.findViewById(R.id.button_back_to_map);
        sortingLayout = (LinearLayout) root.findViewById(R.id.map_sorting_buttons);
        sortKm = (Button) root.findViewById(R.id.sort_by_km);
        sortTime = (Button) root.findViewById(R.id.sort_by_time);
        sortOpen = (Button) root.findViewById(R.id.sort_by_availability);
        sortRating = (Button) root.findViewById(R.id.sort_by_rating);
        mapView = (MapView) root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        returnedPlaces = new ArrayList<>();
        recyclerView = (RecyclerView) root.findViewById(R.id.map_list_view);
        final PlaceListAdapter adapter = new PlaceListAdapter(getActivity(),returnedPlaces);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false));

        recyclerView.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tonchev.yourplace.modul.Place temp = null;
                String clickedName = (String) ((TextView)v.findViewById(R.id.list_name_text)).getText();
                for (tonchev.yourplace.modul.Place p : returnedPlaces) {
                    if (clickedName.equals(p.getName())) {
                        temp = p;
                        break;
                    }
                }
                Intent intent = new Intent(getActivity(), PlaceActivity.class );
                double[] ll = {temp.getLatLng().latitude, temp.getLatLng().longitude};
                temp.setLatLng(null);
                intent.putExtra("mqsto", temp);
                intent.putExtra("ID", temp.getId());
                intent.putExtra("LL",ll);

                startActivity(intent);

            }
        });

        generateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateList.setVisibility(View.GONE);
                backToMap.setVisibility(View.VISIBLE);
                sortingLayout.setVisibility(View.VISIBLE);
                mapView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
        backToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMap.setVisibility(View.GONE);
                sortingLayout.setVisibility(View.GONE);
                generateList.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);

            }
        });
        sortKm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(returnedPlaces, new CompareByKm());
                recyclerView.setAdapter(adapter);
            }
        });
        sortTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(returnedPlaces, new CompareByTime());
                recyclerView.setAdapter(adapter);
            }
        });
        sortOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(returnedPlaces);
                recyclerView.setAdapter(adapter);
            }
        });
        sortRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(returnedPlaces, new CompareByRating());
                recyclerView.setAdapter(adapter);
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
        locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 100, (LocationListener) this);

//        locMan = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 100, (LocationListener) this);

        mMap.setMyLocationEnabled(true);
          if (location!=null) {
            mMap.addMarker(new MarkerOptions().position(location).title("You are here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
          }
          if(ChoseActivity.selection!=null) {
            returnedPlaces.clear();
            new MapFragment.GetPlaces().execute();

          }
          mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getTitle().equals("You are here")) {
            return false;
        }
        if(!MyConnectionChecker.haveNetworkConnection(getActivity())) {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            return false;
        }
        tonchev.yourplace.modul.Place temp = (tonchev.yourplace.modul.Place) marker.getTag();
        Intent intent = new Intent(getActivity(), PlaceActivity.class );
        if (temp.getLatLng() == null) {
            LatLng ltLg = new LatLng(ll[0], ll[1]);
            temp.setLatLng(ltLg);
        }
        ll[0] = temp.getLatLng().latitude;
        ll[1] = temp.getLatLng().longitude;
        temp.setLatLng(null);
        intent.putExtra("mqsto", temp);
        intent.putExtra("ID", temp.getId());
        intent.putExtra("LL",ll);

        startActivity(intent);
        return true;
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
//            String request = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location.latitude + "," + location.longitude+"&radius=2000&types="+ChoseActivity.selection+"&key=AIzaSyCH1yrshoqnPRvH62XLDQI8PYdAFP-MehY";//ADD KEY

            String request = "https://maps.googleapis.com/maps/api/place/textsearch/json?query="+ChoseActivity.selection+"&location="+location.latitude + "," + location.longitude+"&radius=1000&types="+ChoseActivity.selection+"&key=AIzaSyD_p-9ERNaIcdNGwFOc2OJfo-4R1__d9TU";

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
                Log.d("test123", response.toString());

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
                        if (jsonArray.getJSONObject(i).has("id")) {
                            poi.setId(jsonArray.getJSONObject(i).optString("place_id"));
                        }
                        if (jsonArray.getJSONObject(i).has("name")) {
                            poi.setName(jsonArray.getJSONObject(i).optString("name"));
                            poi.setRating(jsonArray.getJSONObject(i).optString("rating", " "));
                        }
                        if (jsonArray.getJSONObject(i).has("opening_hours")) {
                            if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) {
                                if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").getString("open_now").equals("true")) {
                                        poi.setOpenNow("YES");
                                    } else {
                                        poi.setOpenNow("NO");
                                    }
                                }

                            } else {
                                poi.setOpenNow("N/A");
                            }
                            if (jsonArray.getJSONObject(i).has("types")) {
                                JSONArray typesArray = jsonArray.getJSONObject(i).getJSONArray("types");
                                for (int j = 0; j < typesArray.length(); j++) {
                                    poi.setCategory(typesArray.getString(j) + ", " + poi.getCategory());
                                }
                            }
                        Log.d("vasko", "id:" + poi.getId());
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
                distanceRequest = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+location.latitude+","+location.longitude+"&destinations="+placeLatLng.latitude+"%2C"+placeLatLng.longitude+"&key=AIzaSyD_p-9ERNaIcdNGwFOc2OJfo-4R1__d9TU";
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

                    String distanceKm = jsonObject2.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getString("text");
                    int distanceVal = jsonObject2.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getInt("value");
                    String distanceMinute = jsonObject2.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text");
                    int timeVal = jsonObject2.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getInt("value");
                    String address = jsonObject2.getString("destination_addresses");
                    Log.d("testdi", jsonObject2.toString());
                    if(!returnedPlaces.isEmpty()) {
                        returnedPlaces.get(i).setDistance(distanceKm);
                        returnedPlaces.get(i).setDistanceTime(distanceMinute);
                        returnedPlaces.get(i).setDistValue(distanceVal);
                        returnedPlaces.get(i).setTimeValue(timeVal);
                    }
//                    returnedPlaces.get(i).setAdress(address);
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
            ArrayList<tonchev.yourplace.modul.Place> notInRadius = new ArrayList<>();
            for (tonchev.yourplace.modul.Place p : returnedPlaces) {
                if (p.getDistValue()>setRadius) {
                    notInRadius.add(p);
                }
            }
            returnedPlaces.removeAll(notInRadius);

                new GetDetails().execute();
        }
    }

    private class GetDetails extends AsyncTask <Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {


            for (int i = 0; i < returnedPlaces.size(); i ++) {
                tonchev.yourplace.modul.Place temp = returnedPlaces.get(i);
                String placeID = temp.getId();
                String request = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeID + "&key=AIzaSyD_p-9ERNaIcdNGwFOc2OJfo-4R1__d9TU";
                //AIzaSyC5kPaJ2sfQvnqINcskPKDxDmrgfsQ9ACk
                //AIzaSyCH1yrshoqnPRvH62XLDQI8PYdAFP-MehY
                //serverkey: AIzaSyAbDm9W9flFxQZlAQVt1KSYnTBN1HEBHEM
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

                        if (comments!=null) {
                            for (int j = 0; j < comments.length(); j++) {
                                String user = comments.getJSONObject(j).getString("author_name");
                                String review = comments.getJSONObject(j).getString("text");
                                String rating = comments.getJSONObject(j).getString("rating");
                                Comment comm = new Comment(user, review, rating);
                                temp.getComments().add(comm);
                                Log.d("comentari", "Name: " + temp.getComments().get(j).getUser() + "  text: " + temp.getComments().get(j).getText() + "  rating: " + temp.getComments().get(j).getRating());
                            }

                        }

                        String phone = jsonObject.getJSONObject("result").optString("formatted_phone_number");
                        temp.setPhoneNumber(phone);
                        String webAddress = jsonObject.getJSONObject("result").optString("website");
                        temp.setWebAdress(webAddress);
                        String address = jsonObject.getJSONObject("result").optString("vicinity");
                        temp.setAdress(address);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for (int i = 0; i < returnedPlaces.size(); i++) {
                tonchev.yourplace.modul.Place temp = returnedPlaces.get(i);
                Log.d("resplac", returnedPlaces.get(i).getName() + " open now: " + returnedPlaces.get(i).getOpenNow() + " rating: " + returnedPlaces.get(i).getRating());

                Marker newPlace = mMap.addMarker(new MarkerOptions()
                        .position(temp.getLatLng())
                        .title(temp.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .alpha(0.4f)
                        .flat(true));
                newPlace.setTag(temp);
            }
        }
    }

}
