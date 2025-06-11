package com.iyuba.conceptEnglish.han.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;
import com.iyuba.conceptEnglish.util.ScreenUtils;

/**
 * 苏州爱语吧科技有限公司
 *
 * @Date: 2022/8/22
 * @Author: han rong cheng
 */
public class ColorfulMonthView extends MonthView {

    private int mRadius;
    private final Paint paintText;
    private final Paint paintCircle;
    /**
     * 浅蓝
     * */
    private final int unScanColor=Color.rgb(160,206,248);
    /**
     * 深蓝
     * */
    private final int scanColor=Color.rgb(4,30,141);
    public static final String scan="scan";
    public static final String unScan="unScan";

    public ColorfulMonthView(Context context) {
        super(context);
        paintText=getCurrPaint(context);
        paintCircle=getCurrPaint(context);
    }

    private Paint getCurrPaint(Context context){
        Paint paint=new Paint();
        paint.setTextSize(ScreenUtils.sp2px(context,13));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }


    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
    }

    /**
     * 如果需要点击Scheme没有效果，则return true
     *
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param y         日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return false 则不绘制onDrawScheme，因为这里背景色是互斥的
     */
    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;
        if (calendar.getScheme()==null){
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentMonth()
                            ? mCurMonthTextPaint : mOtherMonthTextPaint);
            return;
        }
        int cy = y + mItemHeight / 2;
        switch (calendar.getScheme()){
            case unScan:
                paintText.setColor(Color.BLACK);
                paintText.setStrokeWidth(0);
                paintCircle.setColor(unScanColor);
                canvas.drawCircle(cx,cy+3,mItemWidth/4+20,paintCircle);
                canvas.drawText(String.valueOf(calendar.getDay()),cx,baselineY,paintText);
                break;
            case scan:
                paintText.setColor(Color.WHITE);
                paintText.setStrokeWidth(0);
                paintCircle.setColor(scanColor);
                canvas.drawCircle(cx,cy+3,mItemWidth/4+20,paintCircle);
                canvas.drawText(String.valueOf(calendar.getDay()),cx,baselineY,paintText);
                break;
            default:
                //正常日期的显示
                canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
                break;
        }
    }
}
