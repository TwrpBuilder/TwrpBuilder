package github.grace5921.TwrpBuilder.util;

import java.io.File;

/**
 * Created by sumit on 12/11/16.
 */

public class Config
{
   public static boolean checkBackup(){return new File("/sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar").isFile();}
   public static boolean CheckDownloadedTwrp(){return new File("/sdcard/TwrpBuilder/Twrp.img").isFile();}
   public final static String URL_CONTRIBUTORS = "https://api.github.com/repos/TwrpBuilder/TwrpBuilder/contributors";
   public final static String URL_APP_RELEASES ="https://api.github.com/repos/TwrpBuilder/TwrpBuilder/releases";
}
