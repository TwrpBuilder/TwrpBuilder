package github.grace5921.TwrpBuilder.app;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import github.grace5921.TwrpBuilder.util.ShellUtils;


/**
 * Created by: veli
 * Date: 10/20/16 4:05 PM
 */

public class Activity extends AppCompatActivity
{
    private ShellUtils mShellInstance;

    public ShellUtils getShellSession()
    {
        if (this.mShellInstance == null || this.mShellInstance.getSession() == null)
            loadShell();

        return this.mShellInstance;
    }

    protected void loadShell()
    {
        this.mShellInstance = new ShellUtils(this);
    }

    protected void init()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        // try to find device code model number
        if (!sp.contains(Build.MODEL))
        {
            String deviceCode = Build.MODEL;

            sp.edit().putString(Build.MODEL, deviceCode).commit();

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        init();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mShellInstance != null)
        {
            mShellInstance.closeSession();
            mShellInstance = null;
        }
    }
}
