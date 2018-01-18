package github.grace5921.TwrpBuilder.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by sumit on 5/11/16.
 */

public class ShellExecuter {
    public static String command;
    public static String TAG="ShellExecuter";

    public static final  String runAsRoot()
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
}