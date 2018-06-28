package com.github.TwrpBuilder.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by androidlover5842 on 21/1/18.
 */

public class DateUtils {

    public static String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

        return simpleDateFormat.format(calendar.getTime());
    }
}
