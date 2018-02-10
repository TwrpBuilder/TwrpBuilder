package com.github.TwrpBuilder.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

import com.github.TwrpBuilder.R;

/**
 * Created by: veli
 * Date: 10/26/16 8:04 PM
 */

public class ReleaseTools
{
    public static void openFile(Context context, File file)
    {
        // there may not be any application to open file which causes exceptions
        try
        {
            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            openIntent.setDataAndType(Uri.fromFile(file), getFileContentType(file.getAbsolutePath()));
            context.startActivity(Intent.createChooser(openIntent, context.getString(R.string.choose_app_to_open)));
        } catch (Exception e) {}
    }

    public static String getFileContentType(String fileUrl)
    {
        int dotPos = fileUrl.lastIndexOf(".");

        if (dotPos != -1 && fileUrl.substring(dotPos).equals(".img"))
            return "application/vflasher";

        FileNameMap nameMap = URLConnection.getFileNameMap();
        String fileType = nameMap.getContentTypeFor(fileUrl);

        return (fileType == null) ? "*/*" : fileType;
    }
}
