package tonchev.yourplace;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ChoseActivity extends AppCompatActivity {
    private DrawerLayout nDrawerLayoun;
    private ActionBarDrawerToggle mTogle;
    private Toolbar nToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose);
        nToolBar = (Toolbar) findViewById(R.id.navigation_action);

        setSupportActionBar(nToolBar);

        nDrawerLayoun = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTogle = new ActionBarDrawerToggle(this,nDrawerLayoun,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        nDrawerLayoun.addDrawerListener(mTogle);
        mTogle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mTogle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}