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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.TwrpBuilder.Fragment.FragmentCustomBackup;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.twrpbuilder.rootchecker.RootChecker;

import com.github.TwrpBuilder.Fragment.BackupFragment;
import com.github.TwrpBuilder.Fragment.CreditsFragment;
import com.github.TwrpBuilder.Fragment.DevsFragment;
import com.github.TwrpBuilder.Fragment.GithubReleasesFragment;
import com.github.TwrpBuilder.Fragment.HelpFragment;
import com.github.TwrpBuilder.Fragment.MainFragment;
import com.github.TwrpBuilder.Fragment.MakeMeHappy;
import com.github.TwrpBuilder.Fragment.NoNetwork;
import com.github.TwrpBuilder.Fragment.NotRooted;
import com.github.TwrpBuilder.Fragment.PreferencesFragment;
import com.github.TwrpBuilder.Fragment.StatusFragment;
import com.github.TwrpBuilder.app.LoginActivity;
import com.github.TwrpBuilder.util.Config;
import com.github.TwrpBuilder.util.FirebaseDBInstance;

import static com.github.TwrpBuilder.app.LoginActivity.name;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /*Fragments*/
    private BackupFragment mBackupFragment;
    private NotRooted mNotRooted;
    private HelpFragment mHelpFragment;
    private NoNetwork mNoNetwork;
    private CreditsFragment mFragmentCredits;
    private PreferencesFragment mFragmentPreferences;
    private GithubReleasesFragment mFragmentRelApp;;
    private MakeMeHappy mMakeMeHappy;
    private DevsFragment mDevsFragment;
    private StatusFragment statusFragment;
    private MainFragment mainFragment;
    private FragmentCustomBackup fragmentCustomBackup;

    /*Firebase*/
    private FirebaseAuth mFirebaseAuth;

    /*Ads */
    private AdView mAdView;
    private AdView mAdView1;
    private AdView mAdView2;
    private AdView mAdView3;
    private AdView mAdView4;
    private boolean mShowAds = false;
    /*Navigation drawer*/
    private NavigationView navigationView;
    private View navHeaderView;

    /*Text View*/
    TextView mUserEmail;
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

        /*Fragments*/
        mBackupFragment=new BackupFragment();
        mNotRooted=new NotRooted();
        mHelpFragment=new HelpFragment();
        mNoNetwork=new NoNetwork();
        mFragmentCredits=new CreditsFragment();
        mFragmentPreferences=new PreferencesFragment();
        mFragmentRelApp = new GithubReleasesFragment().setTargetURL(Config.URL_APP_RELEASES);
        mMakeMeHappy=new MakeMeHappy();
        mDevsFragment = new DevsFragment().getInstance(getBaseContext());
        statusFragment=new StatusFragment();
        mainFragment=new MainFragment();
        fragmentCustomBackup=new FragmentCustomBackup();
        /*Replace Fragment*/
        if (RootChecker.isDeviceRooted()) {
            updateFragment(this.mainFragment);
            setTitle("Home");
        }else {
            updateFragment(mainFragment);
            setTitle("Home");
        }
        /*ad view*/
        mAdView = findViewById(R.id.adView);
        mAdView1 = findViewById(R.id.adView1);
        mAdView2 = findViewById(R.id.adView2);
        mAdView3 = findViewById(R.id.adView3);
        mAdView4 = findViewById(R.id.adView4);

        /*Text View*/
        mUserEmail= navHeaderView.findViewById(R.id.user_email);

      /*  AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        mAdView1.loadAd(adRequest);
        mAdView2.loadAd(adRequest);
        mAdView3.loadAd(adRequest);
        mAdView4.loadAd(adRequest);*/

        /*Ads don't touch this part please */
       /* mShowAds = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("show_ads", true);
        if(!mShowAds)
        {
            mAdView.setVisibility(View.GONE);

        }else {
            mAdView.setVisibility(View.VISIBLE);
        }*/
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotification");

        /*replace email with users email*/
        mUserEmail.setText(mFirebaseAuth.getCurrentUser().getEmail());
        /*My Functions :)*/
        checkPermission();
        requestPermission();
        isOnline();
        hideItem();
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
            setTitle("Home");
        } else if (id == R.id.nav_help) {
            updateFragment(mHelpFragment);
            setTitle("Help");
    }else if (id == R.id.nav_credits) {
            updateFragment(mFragmentCredits);
            setTitle("Credits");
        }else if (id==R.id.nav_preference)
        {
            updateFragment(mFragmentPreferences);
            setTitle("SettingsActivity");
        }else if (id==R.id.nav_app_updates){
            updateFragment(mFragmentRelApp);
            setTitle("App Updates");
        }else if (id == R.id.action_log_out) {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("admin",false);
            editor.apply();
            startActivity(new Intent(MainActivity.this, LoginActivity.class)); //Go back to home page
            finish();
        }else if (id==R.id.nav_thanks)
        {
            updateFragment(mMakeMeHappy);
            setTitle("Say Thanks");
        }else if (id==R.id.nav_dev_fragment)
        {
            updateFragment(mDevsFragment);
            setTitle("Recovery Builds");
        }else if (id==R.id.check_status)
        {
            updateFragment(statusFragment);
            setTitle("Build Status");
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
    private void hideItem()
    {
        navigationView = findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_preference).setVisible(false);
        if(name==true)
        {
            nav_Menu.findItem(R.id.nav_dev_fragment).setVisible(true);
        }
        else {
            nav_Menu.findItem(R.id.nav_dev_fragment).setVisible(false);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}

