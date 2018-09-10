package com.github.TwrpBuilder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import com.github.TwrpBuilder.Fragment.ContributorsFragment;
import com.github.TwrpBuilder.Fragment.FragmentAbout;
import com.github.TwrpBuilder.Fragment.FragmentListDevs;
import com.github.TwrpBuilder.Fragment.FragmentStatusCommon;
import com.github.TwrpBuilder.Fragment.MainFragment;
import com.github.TwrpBuilder.Fragment.NoNetwork;
import com.github.TwrpBuilder.Fragment.StatusFragment;
import com.github.TwrpBuilder.app.LoginActivity;
import com.github.TwrpBuilder.app.SettingsActivity;
import com.github.TwrpBuilder.util.Config;
import com.github.TwrpBuilder.util.FirebaseDBInstance;
import com.github.updater.Updater;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static String Cache;
    /*Fragments*/
    private NoNetwork mNoNetwork;
    private ContributorsFragment mFragmentContributors;
    private StatusFragment statusFragment;
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Cache = getCacheDir() + File.separator;
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseDBInstance.getDatabase();
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            SSLEngine engine = sslContext.createSSLEngine();

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView mUserEmail;
        boolean enabled = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("notification", false);

        /*Fragments*/
        mNoNetwork = new NoNetwork();
        mFragmentContributors = new ContributorsFragment();
        statusFragment = new StatusFragment();
        mainFragment = new MainFragment();
        /*Replace Fragment*/
        updateFragment(this.mainFragment);
        setTitle(R.string.home);

        /*Text View*/
        if (!enabled) {

            FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        }
        isOnline();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_option, menu);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.quit:
                finish();
                break;
            case R.id.action_log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class)); //Go back to home page
                finish();
                break;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                updateFragment(mainFragment);
                setTitle(R.string.home);
                break;
            case R.id.nav_contributors:
                updateFragment(mFragmentContributors);
                setTitle(R.string.contributors);
                break;
            case R.id.nav_our_team:
                updateFragment(new FragmentListDevs());
                setTitle(getString(R.string.our_team));
                break;
            case R.id.nav_build_done:
                updateFragment(new FragmentStatusCommon("Builds"));
                setTitle(R.string.completed);
                break;
            case R.id.nav_reject:
                updateFragment(new FragmentStatusCommon("Rejected"));
                setTitle(R.string.rejected);
                break;
            case R.id.nav_build_incomplete:
                updateFragment(statusFragment);
                setTitle(R.string.incomplete);
                break;
            case R.id.nav_about:
                updateFragment(new FragmentAbout());
                setTitle(R.string.app_name);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (requestCode) {
                case 1:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.e("TwrpBuilder", "Necessary App Permissions Granted");
                    } else {
                        Log.e("TwrpBuilder", "Necessary App Permissions Denied");
                        finish();
                    }
                    break;
            }
        }
    }

    /*
     * isOnline - Check if there is a NetworkConnection
     * @return void
     */
    private void isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        if (netInfo == null || !netInfo.isConnected()) {
            updateFragment(mNoNetwork);
        }
    }

}

