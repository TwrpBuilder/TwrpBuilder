package com.github.TwrpBuilder.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import eu.chainfire.libsuperuser.Shell;

import static com.github.TwrpBuilder.util.Config.Sdcard;

/**
 * Created by sumit on 5/11/16.
 */

public class ShellExecuter {
    public static String TAG="ShellExecuter";


    public static String command(String command){
        return Shell.SH.run(command).toString().replace("[","").replace("]","");
    }
    public static String command(String command,boolean root){
        return Shell.SU.run(command).toString().replace("[","").replace("]","");
    }

    public static void rm(Context context,String name){
        /*
        * Usage
        * rm (getContext,"path to file");
        * */
        File dir = Environment.getExternalStorageDirectory();
        Log.d(TAG,"Request to delete "+ dir+"/"+name + " received .");
        File file = new File(dir, "/"+name);
        boolean deleted = file.delete();
        Log.d(TAG,"File "+ name + " deleted .");
    }

    public static void mkdir(String name){
        File makedir = new File(Sdcard+name );
        Log.d(TAG,"Request to make folder "+name+" received .");
        boolean success = true;
        if (!makedir.exists()) {
            success = makedir.mkdirs();
            if (success) {
                Log.i(TAG,"Dir "+name+" made .");
            } else {
                Log.e(TAG,"Failed to make dir "+name);
            }
        }else
        {
            Log.i(TAG,name+" dir alredy exist");
            Log.e(TAG,"Failed to make dir "+name);

        }
       }

    public static void cp(String src, String dst) throws IOException {
        FileInputStream var2 = new FileInputStream(src);
        FileOutputStream var3 = new FileOutputStream(dst);
        byte[] var4 = new byte[1024];

        int var5;
        while((var5 = var2.read(var4)) > 0) {
            var3.write(var4, 0, var5);
        }

        var2.close();
        var3.close();
    }


}
