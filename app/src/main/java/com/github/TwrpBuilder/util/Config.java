package com.github.TwrpBuilder.util;

import android.os.Build;
import android.os.Environment;

import java.io.File;

import static com.github.TwrpBuilder.MainActivity.Cache;

/**
 * Created by sumit on 12/11/16.
 */

public class Config
{
   private static ShellExecuter shell;
   public final static String URL_CONTRIBUTORS = "https://api.github.com/repos/TwrpBuilder/TwrpBuilder/contributors";
   public final static String TwrpBackFName ="TwrpBuilderRecoveryBackup.tar.gz";
   public final static String Sdcard= Environment.getExternalStorageDirectory().getPath()+File.separator;
   public static boolean checkBackup(){return new File(Cache+TwrpBackFName).exists();}
   public static String APP_UPDATE_URL="https://raw.githubusercontent.com/TwrpBuilder/TwrpBuilder/master/app/version.json";
   public static int Version =3;
   public static String OfficialWebsite="https://twrpbuilder.github.io/";
   public static String TGramSupport="https://t.me/TWBuilder";
   public static String GithubSource="https://github.com/TwrpBuilder";
   public static String XdaThread="https://forum.xda-developers.com/android/apps-games/twrpbuilder-t3744253";
   private static String BuildModel=Build.MODEL;
   private static String BuildBoard=Build.BOARD;
   private static String BuildBrand=Build.BRAND;
   private static String BuildFingerprint=Build.FINGERPRINT;
   private static String BuildProduct=Build.PRODUCT;

   public static String getBuildModel() {
      return BuildModel;
   }

   public static String getBuildFingerprint() {
      return BuildFingerprint;
   }

   public static String getBuildProduct() {
      return BuildProduct;
   }

   public static String getBuildBoard() {
      if (BuildBoard.equals("unknown"))
      {
         String hello=shell.command("getprop ro.board.platform");
         if (hello.isEmpty())
         {
            if (shell.command("getprop ro.mediatek.platform").isEmpty())
            {
               return BuildBoard;
            }
            else {
               return shell.command("getprop ro.mediatek.platform");
            }
         }
         else {
            return hello;
         }
      }
      else {
         return BuildBoard;
      }
   }

   public static String getBuildBrand() {
      return BuildBrand;
   }

   public static String buildProp(){
      String data="# Build.prop v1\n";
      data+="ro.product.brand="+getBuildBrand()+"\n";
      data+="ro.board.platform="+getBuildBoard()+"\n";
      data+="ro.product.model="+getBuildModel()+"\n";
      data+="ro.build.product="+getBuildProduct()+"\n";
      data+="ro.build.fingerprint="+getBuildFingerprint()+"\n";
      return data;
   }

}
