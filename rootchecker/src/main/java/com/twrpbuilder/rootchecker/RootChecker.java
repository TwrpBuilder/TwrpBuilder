package com.twrpbuilder.rootchecker;

import android.util.Log;

import java.io.File;

/**
 * @author Orhan Obut
 */
public final class RootChecker {

    private static final String TAG = RootChecker.class.getSimpleName();

    /**
     * Contains all possible places to check binaries
     */
    private static final String[] pathList;

    /**
     * The binary which grants the root privileges
     */
    private static final String KEY_SU = "su";

    static {
        pathList = new String[]{
                "/sbin/",
                "/system/bin/",
                "/system/xbin/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/"
        };
    }

    public static boolean isDeviceRooted() {
        return doesFileExists(KEY_SU);
    }

    /**
     * Checks the all path until it finds it and return immediately.
     *
     * @param value must be only the binary name
     * @return if the value is found in any provided path
     */
    private static boolean doesFileExists(String value) {
        boolean result = false;
        for (String path : pathList) {
            File file = new File(path + "/" + value);
            result = file.exists();
            if (result) {
                Log.d(TAG, path + " contains su binary");
                break;
            }
        }
        return result;
    }
}
