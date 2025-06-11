package com.iyuba.conceptEnglish.lil.concept_other.talkshow_detail;

import android.content.Context;
import android.util.AttributeSet;

import com.iyuba.conceptEnglish.R;
import com.iyuba.core.talkshow.lesson.NormalVideoControl;


/**
 * Created by Administrator on 2017/1/17/017.
 */

public class TalkShowDetailVideoControl extends NormalVideoControl {
    public TalkShowDetailVideoControl(Context context) {
        super(context);
    }

    public TalkShowDetailVideoControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TalkShowDetailVideoControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.talk_show_detail_video_control;
    }
}
