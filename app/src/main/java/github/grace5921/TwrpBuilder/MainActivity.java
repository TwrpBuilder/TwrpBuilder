package github.grace5921.TwrpBuilder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import github.grace5921.TwrpBuilder.Fragment.BackupFragment;
import github.grace5921.TwrpBuilder.Fragment.CreditsFragment;
import github.grace5921.TwrpBuilder.Fragment.DevsFragment;
import github.grace5921.TwrpBuilder.Fragment.GithubReleasesFragment;
import github.grace5921.TwrpBuilder.Fragment.HelpFragment;
import github.grace5921.TwrpBuilder.Fragment.MakeMeHappy;
import github.grace5921.TwrpBuilder.Fragment.NoNetwork;
import github.grace5921.TwrpBuilder.Fragment.NotRooted;
import github.grace5921.TwrpBuilder.Fragment.PreferencesFragment;
import github.grace5921.TwrpBuilder.app.LoginActivity;
import github.grace5921.TwrpBuilder.app.SettingsActivity;
import github.grace5921.TwrpBuilder.util.Config;

import static github.grace5921.TwrpBuilder.Fragment.BackupFragment.mDownloadRecovery;
import static github.grace5921.TwrpBuilder.Fragment.BackupFragment.mUploadBackup;
import static github.grace5921.TwrpBuilder.Fragment.BackupFragment.riversRef;
import static github.grace5921.TwrpBuilder.util.Config.suAvailable;

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
    /*Strings*/
    private String Email="user@user.com";

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFirebaseAuth=FirebaseAuth.getInstance();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeaderView= navigationView.inflateHeaderView(R.layout.nav_header_main);
        mUserEmail = (TextView) navHeaderView.findViewById(R.id.user_email);

        /*Fragments*/
        mBackupFragment=new BackupFragment();
        mNotRooted=new NotRooted();
        mHelpFragment=new HelpFragment();
        mNoNetwork=new NoNetwork();
        mFragmentCredits=new CreditsFragment();
        mFragmentPreferences=new PreferencesFragment();
        mFragmentRelApp = new GithubReleasesFragment().setTargetURL(Config.URL_APP_RELEASES);
        mMakeMeHappy=new MakeMeHappy();
        mDevsFragment = new DevsFragment();
        /*Replace Fragment*/
        if(suAvailable()) {
            updateFragment(this.mBackupFragment);
            setTitle("Request Twrp");
        }else {
            updateFragment(this.mNotRooted);
            setTitle("Device Not Rooted :(");
        }
        /*ad view*/
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView1 = (AdView) findViewById(R.id.adView1);
        mAdView2 = (AdView) findViewById(R.id.adView2);
        mAdView3 = (AdView) findViewById(R.id.adView3);
        mAdView4 = (AdView) findViewById(R.id.adView4);

        /*Text View*/
        mUserEmail=(TextView)navHeaderView.findViewById(R.id.user_email);

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        mAdView1.loadAd(adRequest);
        mAdView2.loadAd(adRequest);
        mAdView3.loadAd(adRequest);
        mAdView4.loadAd(adRequest);

        /*Ads don't touch this part please */
        mShowAds = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("show_ads", true);
        if(!mShowAds)
        {
            mAdView.setVisibility(View.GONE);

        }else {
            mAdView.setVisibility(View.VISIBLE);
        }

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main,menu);
        MenuItem item = menu.findItem(R.id.action_cancel_request);
        if(mUploadBackup.getVisibility()==View.VISIBLE)
        {
            /*Nothing to do*/
        }else {
           // item.setVisible(true);
        }
        if(mDownloadRecovery.getVisibility()==View.VISIBLE) {
            /*Nothing to do*/
        }
        else {
            //item.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel_request) {
// Delete the file
            riversRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Snackbar.make(getCurrentFocus(), R.string.successfully_delete_data, Snackbar.LENGTH_INDEFINITE)
                            .setAction("Action", null).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(getCurrentFocus(), R.string.failed_to_delete_data, Snackbar.LENGTH_INDEFINITE)
                            .setAction("Action", null).show();

                }
            });


            return true;
        }

        if(id==R.id.action_settings)
        {
            Intent i = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_backup) {
            updateFragment(mBackupFragment);
            setTitle("Request Twrp");
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
            startActivity(new Intent(MainActivity.this, LoginActivity.class)); //Go back to home page
            finish();
        }else if (id==R.id.nav_thanks)
        {
            updateFragment(mMakeMeHappy);
            setTitle("Say Thanks");
        }else if (id==R.id.nav_dev_fragment)
        {
            updateFragment(mDevsFragment);
            setTitle("Devs stuff");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    public void CheckOverlay() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
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
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_preference).setVisible(false);
        if(suAvailable()){
            /*Good Job!*/
        }else {
            nav_Menu.findItem(R.id.nav_backup).setVisible(false);
        }
        if (mFirebaseAuth.getCurrentUser().getEmail()==Email)
        {
            nav_Menu.findItem(R.id.nav_dev_fragment).setVisible(true);
        }
        else
        {
            /*Can't do anything for your sorry*/
        }
    }


}

