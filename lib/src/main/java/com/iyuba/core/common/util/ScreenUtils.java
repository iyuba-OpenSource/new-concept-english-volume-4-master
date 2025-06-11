package com.iyuba.core.common.util;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.Display;

/**
 * Created by holybible on 16/6/11.
 */
public class ScreenUtils {

    /**
     *
     * @param activity
     * @return int[0] => width, int[1] => height
     */
    public static int[] getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        return new int[]{display.getWidth(), display.getHeight()};
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

}
