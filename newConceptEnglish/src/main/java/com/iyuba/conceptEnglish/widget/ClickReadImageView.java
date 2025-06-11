package com.iyuba.conceptEnglish.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.api.GetDataForReadClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickReadImageView extends androidx.appcompat.widget.AppCompatImageView {
    private Context mContext;
    private static final int UNUSEFUL_ID = -1;
    private static final float ORIGINAL_WIDTH = 750.00f;
    private static final float ORIGINAL_HEIGHT = 1060.00f;

    private float realWidth = 0.00f;
    private float realHeight = 0.00f;

    private float widthScale = 1.00f;
    private float heightScale = 1.00f;
    private int startParaIdTimp = UNUSEFUL_ID;

    private AudioCallback audioCallback;

    private Paint mPaint;

    private Map<Integer, GetDataForReadClick.VoatextDetail> details = new HashMap<>();

    public AudioCallback getAudioCallback() {
        return audioCallback;
    }

    public void setAudioCallback(AudioCallback audioCallback) {
        this.audioCallback = audioCallback;
    }

    public Map<Integer, GetDataForReadClick.VoatextDetail> getDetails() {
        return details;
    }

    public void setDetails(Map<Integer, GetDataForReadClick.VoatextDetail> details) {
        this.details = details;
    }

    public void addDetails(GetDataForReadClick.VoatextDetail detail) {
        details.put(detail.ParaId, detail);
    }

    public ClickReadImageView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ClickReadImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ClickReadImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.colorPrimary));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        realWidth = getMeasuredWidth();
        realHeight = getMeasuredHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        widthScale = realWidth / ORIGINAL_WIDTH;
        heightScale = realHeight / ORIGINAL_HEIGHT;
//        Log.d("wangwenyang", event.getX() + "");
//        Log.d("wangwenyang", event.getY() + "");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                startParaIdTimp = belongToSentenceId(event.getX(), event.getY());
//                if (startParaIdTimp == UNUSEFUL_ID) {
//                    return false;
//                }
                break;
            case MotionEvent.ACTION_UP:
                List<Integer> upId = belongToSentenceId(event.getX(), event.getY());
//                if (startParaIdTimp == upId
//                        && upId != UNUSEFUL_ID) {
                if (upId.get(0) != UNUSEFUL_ID) {
                    GetDataForReadClick.VoatextDetail detail = details.get(upId.get(0));
                    for (Integer paraId : upId) {
                        GetDataForReadClick.VoatextDetail timpDetail = details.get(paraId);
                        if (detail.Timing > timpDetail.Timing) {
                            detail.Timing = timpDetail.Timing;
                            detail.Sentence = timpDetail.Sentence + detail.Sentence;
                            detail.sentence_cn = timpDetail.sentence_cn + detail.sentence_cn;
                        }
                        if (detail.EndTiming < timpDetail.EndTiming) {
                            detail.EndTiming = timpDetail.EndTiming;
                            detail.Sentence = detail.Sentence + timpDetail.Sentence;
                            detail.sentence_cn = detail.sentence_cn + timpDetail.sentence_cn;
                        }
                    }

                    if (audioCallback != null) {
                        audioCallback.playAudio(detail);
                    }
//                    float realNeedX = event.getX() * widthScale;
//                    float realNeedY = event.getY() * heightScale;
//                    String str = "";
//                    str = "实际点击坐标 x:" + event.getX() + ",y:" + event.getY() + "\n"
//                            + "按比例点击坐标 x:" + realNeedX + ",y:" + realNeedY + "\n"
//                            + "有效区间范围x:" + details.get(startParaIdTimp).Start_x + "-" + details.get(startParaIdTimp).End_x + "\n"
//                            + "有效区间范围Y:" + details.get(startParaIdTimp).Start_y + "-" + details.get(startParaIdTimp).End_y;
//                    Log.d("wangwenyang", str);
//                    Toast.makeText(mContext, str, LENGTH_LONG).show();
                }
                startParaIdTimp = UNUSEFUL_ID;
                break;
        }
        return true;
    }

    private List<Integer> belongToSentenceId(float pointX, float pointY) {
        List<Integer> list = new ArrayList<>();
        float realNeedX = pointX / widthScale;
        float realNeedY = pointY / heightScale;
        for (GetDataForReadClick.VoatextDetail detail : details.values()) {
            if (realNeedX < detail.Start_x || realNeedX > detail.End_x) {
                continue;
            }
            if (realNeedY < detail.Start_y || realNeedY > detail.End_y) {
                continue;
            }

            list.add(detail.ParaId);
        }

        if (list.size() == 0) {
            list.add(UNUSEFUL_ID);
        }
        return list;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        widthScale = realWidth / ORIGINAL_WIDTH;
        heightScale = realHeight / ORIGINAL_HEIGHT;
        //绘制有效点击框
        for (GetDataForReadClick.VoatextDetail detail : details.values()) {
            canvas.drawRect(detail.Start_x * widthScale,
                    detail.Start_y * heightScale,
                    detail.End_x * widthScale,
                    detail.End_y * heightScale,
                    mPaint);
        }
    }

    public interface AudioCallback {
        void playAudio(GetDataForReadClick.VoatextDetail detail);
    }
}
