package com.github.TwrpBuilder.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import eu.chainfire.libsuperuser.Shell;

import static com.github.TwrpBuilder.util.Config.Sdcard;

/**
 * Created by sumit on 5/11/16.
 */

public class ShellExecuter {
    @NonNull
    private static final String TAG = "ShellExecuter";


    public static String command(String command) {
        return Shell.SH.run(command).toString().replace("[", "").replace("]", "");
    }

    public static void mkdir(String name) {
        File makedir = new File(Sdcard + name);
        Log.d(TAG, "Request to make dir " + name + " received!");
        boolean success;
        if (!makedir.exists()) {
            success = makedir.mkdirs();
            if (success) {
                Log.i(TAG, "Dir " + name + " created successfully!");
            } else {
                Log.e(TAG, "Failed to make dir " + name);
            }
        } else {
            Log.i(TAG, name + " dir already exists!");
        }
    }

    public static void cp(@NonNull String src, @NonNull String dst) throws IOException {
        FileInputStream var2 = new FileInputStream(src);
        FileOutputStream var3 = new FileOutputStream(dst);
        byte[] var4 = new byte[1024];

        int var5;
        while ((var5 = var2.read(var4)) > 0) {
            var3.write(var4, 0, var5);
        }

        var2.close();
        var3.close();
    }


}
