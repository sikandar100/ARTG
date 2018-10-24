package com.example.dell.vrtg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //boolean gstLogin = false;
    Button btn;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    GoogleApiClient mGoogleApiClient;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        String Status = preferences.getString(getString(R.string.status),"False");
        if(Status.equals("True"))
        {
            NavigationView navigationView= findViewById(R.id.nav_view);

            Menu menuNav=navigationView.getMenu();
            MenuItem nav_item2 = menuNav.findItem(R.id.add_POI);
            nav_item2.setEnabled(false);

        }

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

        Welcome welcome = new Welcome();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content, welcome).commit();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event "+event.getRawX()+","+event.getRawY()+" "+x+","+y+" rect "+w.getLeft()+","+w.getTop()+","+w.getRight()+","+w.getBottom()+" coords "+scrcoords[0]+","+scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            String Status = preferences.getString(getString(R.string.status),"False");
            if(Status.equals("True"))
            {
                startActivity(new Intent(BaseActivity.this,BaseActivity.class));
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Home) {
            // Handle the camera action
            Welcome welcome = new Welcome();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content, welcome).commit();
        } else if (id == R.id.nav_POI) {
            Intent i=new Intent(getApplicationContext(),Annoucement.class);
            startActivity(i);

        } else if (id == R.id.add_POI) {
            Add_Poi Add = new Add_Poi();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content, Add).commit();

        } else if (id == R.id.search_POI) {
            Search search = new Search();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content, search).commit();


        } else if (id == R.id.nav_AboutUs) {
            AboutUs about = new AboutUs();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content, about).commit();

        } else if (id == R.id.nav_ContactUs) {
            ContactUs contact = new ContactUs();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content, contact).commit();

        } else if (id == R.id.nav_Logout) {
            String Status = preferences.getString(getString(R.string.status),"False");
            if(Status.equals("True"))
            {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

                startActivity(new Intent(BaseActivity.this,MainActivity.class));
                Toast.makeText(this,"Loged out As Guest",Toast.LENGTH_SHORT).show();
            }
            else {
                Intent i = new Intent(getApplicationContext(), SecondOne.class);
                startActivity(i);
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
