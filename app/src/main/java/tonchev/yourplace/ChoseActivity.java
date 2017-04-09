package tonchev.yourplace;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import static tonchev.yourplace.LoginActivity.mGoogleApiClient;

public class ChoseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout nDrawerLayoun;
    private ActionBarDrawerToggle mTogle;
    private Toolbar nToolBar;
    private TabLayout nTabLayout;
    private EditText searchField;

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

        //TabLayout
        nTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        nTabLayout.addTab(nTabLayout.newTab().setText("Pick"));
        nTabLayout.addTab(nTabLayout.newTab().setText("Map"));
        TabLayout.Tab tab = nTabLayout.getTabAt(0);
        tab.select();
        // search Field
        searchField = (EditText) findViewById(R.id.search_field);
        final PickFragment f = new PickFragment();
        final MapFragment m = new MapFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.picker_layout, f,"Pick").commit();


        nTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("Map")) {
                    fragmentManager.beginTransaction().remove(f).commit();
                    fragmentManager.beginTransaction().add(R.id.picker_layout, m,"Map").commit();
                }
                if (tab.getText().equals("Pick")) {
                    fragmentManager.beginTransaction().remove(m).commit();
                    fragmentManager.beginTransaction().add(R.id.picker_layout, f,"Pick").commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(nDrawerLayoun.isDrawerOpen(GravityCompat.START)){
            nDrawerLayoun.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.search_icon){

            if(!searchField.getText().toString().equals("")) {
                item.setIcon(R.mipmap.ic_clear_black_24dp);
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        searchField.setText("");
                        item.setIcon(R.mipmap.ic_search_black_24dp);
                        return true;
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if( id == R.id.nav_my_places){

        }
        else if( id == R.id.nav_logout){
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // ...
                            Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                            startActivity(i);
                        }
                    });
        }

        nDrawerLayoun.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

}
