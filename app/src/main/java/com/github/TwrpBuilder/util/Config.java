package com.github.TwrpBuilder.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * Created by sumit on 12/11/16.
 */

public class Config {
    public final static String URL_CONTRIBUTORS = "https://api.github.com/repos/TwrpBuilder/TwrpBuilder/contributors";
    public final static String TwrpBackFName = "TwrpBuilderRecoveryBackup.tar.gz";
    public final static String Sdcard = Environment.getExternalStorageDirectory().getPath() + File.separator;
    @NonNull
    public static final String APP_UPDATE_URL = "https://api.github.com/repos/TwrpBuilder/TwrpBuilder/releases/latest";
    @NonNull
    public static final String OfficialWebsite = "https://twrpbuilder.github.io/";
    @NonNull
    public static final String TGramSupport = "https://t.me/TWBuilder";
    @NonNull
    public static final String GithubSource = "https://github.com/TwrpBuilder";
    @NonNull
    public static final String XdaThread = "https://forum.xda-developers.com/android/apps-games/twrpbuilder-t3744253";
    public static final String ReportIssue = "https://github.com/TWRPBuilder/TWRPBuilder/issues";

    public static final int MIN_BACKUP_SIZE = 3000000;

    private static final String BuildModel = Build.MODEL;
    private static final String BuildBoard = Build.BOARD;
    private static final String BuildBrand = Build.BRAND;
    private static final String BuildFingerprint = Build.FINGERPRINT;
    private static final String BuildProduct = Build.PRODUCT;

    public static String getBuildModel() {
        return BuildModel;
    }

    private static String getBuildFingerprint() {
        return BuildFingerprint;
    }

    public static String getBuildProduct() {
        return BuildProduct;
    }

    public static String getBuildBoard() {
        if (BuildBoard.equals("unknown")) {
            String hello = ShellExecuter.command("getprop ro.board.platform");
            if (hello.isEmpty()) {
                if (ShellExecuter.command("getprop ro.mediatek.platform").isEmpty()) {
                    return BuildBoard;
                } else {
                    return ShellExecuter.command("getprop ro.mediatek.platform");
                }
            } else {
                return hello;
            }
        } else {
            return BuildBoard;
        }
    }

    public static String getBuildBrand() {
        return BuildBrand;
    }

    private static String getBuildAbi() {
        return ShellExecuter.command("getprop ro.product.cpu.abi");
    }

    @NonNull
    public static String buildProp() {
        String data = "# Build.prop v1\n";
        data += "ro.product.brand=" + getBuildBrand() + "\n";
        data += "ro.board.platform=" + getBuildBoard() + "\n";
        data += "ro.product.model=" + getBuildModel() + "\n";
        data += "ro.build.product=" + getBuildProduct() + "\n";
        data += "ro.build.fingerprint=" + getBuildFingerprint() + "\n";
        data += "ro.product.cpu.abi=" + getBuildAbi();
        return data;
    }

    public static double getAppVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return Double.parseDouble(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 1.0;
        }
    }
}
