package com.iyuba.conceptEnglish.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class AnimSpread extends View {

    private Paint mPaint = new Paint();

    int distance = 10;

    public boolean isDraw = false;

    private int centerX, centerY;


    public AnimSpread(Context context) {
        super(context);

    }

    public AnimSpread(Context context, AttributeSet attrs) {
        super(context, attrs);


    }

    public AnimSpread(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        if (isDraw) {
            for (int i = 0; i < 5; i++) {

                int position = (int) (Math.random() * 4);
                int length = 40 - Math.abs(15 * (position + 1 - 3));
                int startY = centerY - length / 2;
                int endY = centerY + length / 2;
                int indexX;
                if (i > 2) {
                    indexX = centerX + Math.abs(distance * (i + 1 - 3));
                } else {
                    indexX = centerX - Math.abs(distance * (i + 1 - 3));
                }

                canvas.drawRoundRect(new RectF(indexX, startY, indexX + 4, endY), 10, 10, mPaint);
            }

            postInvalidateDelayed(200);
        } else {

            for (int i = 0; i < 5; i++) {
                int middleIndex = 40 / 2;
                int length = 40 - Math.abs(15 * (i + 1 - 3));
                int startY = centerY - length / 2;
                int endY = centerY + length / 2;
                int indexX;
                if (i > 2) {
                    indexX = centerX + Math.abs(distance * (i + 1 - 3));
                } else {
                    indexX = centerX - Math.abs(distance * (i + 1 - 3));
                }
                canvas.drawRoundRect(new RectF(indexX, startY, indexX + 4, endY), 10, 10, mPaint);
            }
        }

    }


    public void setIsDraw(boolean isDraw) {
        this.isDraw = isDraw;
        if(!isDraw){
            Log.e("停止动画",isDraw+"");
        }
        postInvalidateDelayed(200);
    }

}
