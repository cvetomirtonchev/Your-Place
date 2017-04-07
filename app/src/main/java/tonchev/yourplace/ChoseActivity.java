package tonchev.yourplace;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

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
        // search Field
        searchField = (EditText) findViewById(R.id.search_field);
        PickFragment f = new PickFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.picker_layout, f,"Pick").commit();




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

        }else if( id == R.id.nav_logout){

        }

        nDrawerLayoun.closeDrawer(GravityCompat.START);
        return true;
    }
}
