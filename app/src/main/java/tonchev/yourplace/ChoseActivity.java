package tonchev.yourplace;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import tonchev.yourplace.MapFragment.ComunicatorFragment;

import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import static tonchev.yourplace.LoginActivity.acct;
import static tonchev.yourplace.LoginActivity.mGoogleApiClient;

public class ChoseActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, ComunicatorFragment, OnConnectionFailedListener {

    private DrawerLayout nDrawerLayoun;
    private ActionBarDrawerToggle mTogle;
    private Toolbar nToolBar;
    public static TabLayout nTabLayout;
    public static String selection;
    public static LatLng location;
    public static int setRadius;

    PlaceAutocompleteFragment searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose);

        // Toolbar and navigation drawer
        nToolBar = (Toolbar) findViewById(R.id.navigation_action);
        setSupportActionBar(nToolBar);
        nDrawerLayoun = (DrawerLayout) findViewById(R.id.drawer_layout);

        mTogle = new ActionBarDrawerToggle(this, nDrawerLayoun, nToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        nDrawerLayoun.addDrawerListener(mTogle);
        mTogle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mTogle.setDrawerIndicatorEnabled(true);
        nToolBar.setNavigationIcon(R.mipmap.ic_menu_black_24dp);

        nToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView navigationTvName = (TextView) nDrawerLayoun.findViewById(R.id.navigation_username);
                navigationTvName.setText(acct.getDisplayName());
                TextView navigationTvMail = (TextView) nDrawerLayoun.findViewById(R.id.navigation_email);
                navigationTvMail.setText(acct.getEmail());
                nDrawerLayoun.openDrawer(Gravity.LEFT);
            }
        });

        //TabLayout
        nTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        nTabLayout.addTab(nTabLayout.newTab().setText("Pick"));
        nTabLayout.addTab(nTabLayout.newTab().setText("Map"));
        TabLayout.Tab tab = nTabLayout.getTabAt(0);
        tab.select();
        final PickFragment chartFragment = new PickFragment();
        final MapFragment mapFragment = new MapFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.picker_layout, chartFragment, "Pick").commit();




        nTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               if (tab.getText().equals("Map")) {
                   if(!mapFragment.isAdded()) {
                       fragmentManager.beginTransaction()
                               .remove(chartFragment)
                               .add(R.id.picker_layout, mapFragment, "Map")
                               .commit();
                   }

                }
                if (tab.getText().equals("Pick")) {
                    fragmentManager.beginTransaction()
                            .remove(mapFragment)
                            .add(R.id.picker_layout, chartFragment, "Pick")
                            .commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //SearchBar

        searchBar = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        // Choose Country
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("BG")
                .setTypeFilter(21)
                .build();
        searchBar.setFilter(typeFilter);

        searchBar.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Intent intent = new Intent(ChoseActivity.this, PlaceActivity.class);
                String name = null;
                String address = null;
                String id = null;
                String rating = null;
                String phone = null;
                String webAdress = null ;
                if( place.getName()!=null){
                    name = place.getName().toString();
                }
                if( place.getAddress()!=null){
                    address = place.getAddress().toString();
                }
                if( place.getId()!=null){
                    id = place.getId();
                }
                if(String.valueOf(place.getRating())!= null) {
                    rating = String.valueOf(place.getRating());
                }
                if( place.getPhoneNumber()!=null) {
                    phone = place.getPhoneNumber().toString();
                }
                if(place.getWebsiteUri()!=null) {
                    webAdress = place.getWebsiteUri().toString();
                }
                LatLng placeLatLng = place.getLatLng();
                double[] ll = {placeLatLng.latitude, placeLatLng.longitude};
                tonchev.yourplace.modul.Place mqsto = new tonchev.yourplace.modul.Place(id, name, address, rating, phone, webAdress);
                intent.putExtra("mqsto", mqsto);
                intent.putExtra("ID", place.getId());
                intent.putExtra("LL",ll);
                startActivity(intent);



            }

            @Override
            public void onError(Status status) {

            }
        });
    }

    private boolean isValid(String name, String address, String id, String rating, String phone, String webAdress) {
        if(name!=null&&webAdress!=null&&id!=null&&rating!=null&&phone!=null&webAdress!=null){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (nDrawerLayoun.isDrawerOpen(GravityCompat.START)) {
            nDrawerLayoun.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_my_places) {

        } else if (id == R.id.nav_logout) {
            mGoogleApiClient.connect();
            mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
;
                    if(mGoogleApiClient.isConnected()) {
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    Toast.makeText(ChoseActivity.this, "User logged out", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ChoseActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onConnectionSuspended(int i) {
                    Log.d("tagche", "Google API Client Connection Suspended");
                }
            });
        }

        nDrawerLayoun.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void searchResult(Place place) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}
