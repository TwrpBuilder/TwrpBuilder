package com.github.TwrpBuilder.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by androidlover5842 on 21/1/18.
 */

public class DateUtils {
    public static Calendar calendar;
    public static SimpleDateFormat simpleDateFormat;
    public static String Dat;

      public static String getDate(){
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Dat = simpleDateFormat.format(calendar.getTime());

        return Dat;
    }
}
