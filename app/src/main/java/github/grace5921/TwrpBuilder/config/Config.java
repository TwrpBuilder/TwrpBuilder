package github.grace5921.TwrpBuilder.config;

import java.io.File;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by sumit on 12/11/16.
 */

public class Config
{
   public static boolean suAvailable(){return new File("/system/xbin/su").isFile();}
   public static boolean checkBackup(){return new File("/sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar").isFile();}

}
