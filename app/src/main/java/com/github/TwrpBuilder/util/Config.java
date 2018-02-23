package com.github.TwrpBuilder.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by sumit on 12/11/16.
 */

public class Config
{
   public final static String URL_CONTRIBUTORS = "https://api.github.com/repos/TwrpBuilder/TwrpBuilder/contributors";
   public final static String TwrpBackFName ="TwrpBuilderRecoveryBackup.tar.gz";
   public final static String Sdcard= Environment.getExternalStorageDirectory().getPath()+File.separator;
   public static boolean checkBackup(){return new File(Sdcard+"/TwrpBuilder/"+TwrpBackFName).isFile();}
   public static String APP_UPDATE_URL="https://raw.githubusercontent.com/TwrpBuilder/TwrpBuilder/master/app/version.json";
   public static int Version =1;
   public static String OfficialWebsite="https://twrpbuilder.github.io/";
   public static String TGramSupport="https://t.me/TWBuilder";
   public static String GithubSource="https://github.com/TwrpBuilder";
   public static String XdaThread="https://forum.xda-developers.com/android/apps-games/twrpbuilder-t3744253";

}
