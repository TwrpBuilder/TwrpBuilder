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

import static com.github.TwrpBuilder.util.Config.Sdcard;

/**
 * Created by sumit on 5/11/16.
 */

public class ShellExecuter {
    public static String TAG="ShellExecuter";

    public static final  String runAsRoot(String command)
    {

        try {
            // Executes the command.
            Process process = Runtime.getRuntime().exec(command);
            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            //       process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            process.waitFor();

            return output.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e)

        {
            throw new RuntimeException(e);
        }
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
