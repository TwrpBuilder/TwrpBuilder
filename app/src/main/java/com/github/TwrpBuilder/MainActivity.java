package com.github.TwrpBuilder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.TwrpBuilder.Fragment.FragmentAbout;
import com.github.TwrpBuilder.Fragment.FragmentStatusCommon;
import com.github.TwrpBuilder.app.SettingsActivity;
import com.github.updater.Updater;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import com.github.TwrpBuilder.Fragment.CreditsFragment;
import com.github.TwrpBuilder.Fragment.MainFragment;
import com.github.TwrpBuilder.Fragment.NoNetwork;
import com.github.TwrpBuilder.Fragment.StatusFragment;
import com.github.TwrpBuilder.app.LoginActivity;
import com.github.TwrpBuilder.util.Config;
import com.github.TwrpBuilder.util.FirebaseDBInstance;

import static com.github.TwrpBuilder.R.menu.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /*Fragments*/
    private NoNetwork mNoNetwork;
    private CreditsFragment mFragmentCredits;
    private StatusFragment statusFragment;
    private MainFragment mainFragment;

    /*Firebase*/
    private FirebaseAuth mFirebaseAuth;

    /*Navigation drawer*/
    private NavigationView navigationView;
    private View navHeaderView;

    /*Text View*/
    TextView mUserEmail;

    private boolean enabled;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFirebaseAuth=FirebaseAuth.getInstance();
        FirebaseDBInstance.getDatabase();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeaderView= navigationView.inflateHeaderView(R.layout.nav_header_main);
        mUserEmail = navHeaderView.findViewById(R.id.user_email);
        enabled = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("notification", false);

        /*Fragments*/
        mNoNetwork=new NoNetwork();
        mFragmentCredits=new CreditsFragment();
        statusFragment=new StatusFragment();
        mainFragment=new MainFragment();
        /*Replace Fragment*/
        updateFragment(this.mainFragment);
        setTitle("Home");

        /*Text View*/
        mUserEmail= navHeaderView.findViewById(R.id.user_email);
        if (!enabled) {

            FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        }
        /*replace email with users email*/
        mUserEmail.setText(mFirebaseAuth.getCurrentUser().getEmail());
        /*My Functions :)*/
        checkPermission();
        requestPermission();
        isOnline();
        new Updater(MainActivity.this,Config.Version,Config.APP_UPDATE_URL,false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.activity_option,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.quit:
                finish();
                break;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            updateFragment(mainFragment);
            setTitle(R.string.home);
        } else if (id == R.id.nav_credits) {
            updateFragment(mFragmentCredits);
            setTitle(R.string.credits);
        }
        else if (id==R.id.nav_build_done)
        {
            updateFragment(new FragmentStatusCommon("Builds",true));
            setTitle(R.string.completed);
        }
        else if (id==R.id.nav_reject)
        {
            updateFragment(new FragmentStatusCommon("Rejected",true));
            setTitle(R.string.rejected);
        }
        else if (id == R.id.action_log_out) {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("admin",false);
            editor.apply();
            startActivity(new Intent(MainActivity.this, LoginActivity.class)); //Go back to home page
            finish();
        }else if (id==R.id.check_status)
        {
            updateFragment(statusFragment);
            setTitle(R.string.build_status);
        }else if (id==R.id.nav_about)
        {
            updateFragment(new FragmentAbout());
            setTitle(R.string.app_name);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    protected void updateFragment(Fragment fragment)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    private boolean checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
        }
        return false;
    }

    private void requestPermission() {
        if (android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App SettingsActivity.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (requestCode) {
                case 1:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.e("value", "Permission Granted .");
                    } else {
                        Log.e("value", "Permission Denied .");
                        finish();
                    }
                    break;
            }
        }
    }

    /*
     * isOnline - Check if there is a NetworkConnection
     * @return boolean
     */
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            updateFragment(mNoNetwork);
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}

