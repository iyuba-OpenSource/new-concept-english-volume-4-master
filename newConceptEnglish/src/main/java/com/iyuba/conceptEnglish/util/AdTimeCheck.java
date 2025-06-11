package com.iyuba.conceptEnglish.util;


import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 10202 on 2015/10/9.
 * 广告时间控制
 */
public class AdTimeCheck {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static String showadtime = "2019-01-28 18:00:00";


    public static boolean setAd() {
        long time = System.currentTimeMillis();
        Date date = null;
        try {
            date = sdf.parse(showadtime);
            Log.e("show-time", date.getTime() + "");
            if (time > date.getTime()) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
    }
}