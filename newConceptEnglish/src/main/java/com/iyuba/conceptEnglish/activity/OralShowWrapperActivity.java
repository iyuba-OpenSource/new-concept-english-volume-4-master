package com.iyuba.conceptEnglish.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.conceptEnglish.R;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.talkshow.TalkShowFragment;


public class OralShowWrapperActivity extends BasisActivity {

    private Context mContext;
    private FrameLayout mFrameLayout;

    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_oralshow_wrapper);
        mContext = this;
        mFrameLayout = findViewById(R.id.fraglayout);

        FragmentManager fragmentManager=this.getSupportFragmentManager();
        FragmentTransaction transaction =fragmentManager.beginTransaction();
        transaction.add(R.id.fraglayout, TalkShowFragment.newInstance());
        transaction.commit();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OralShowWrapperActivity.this.finish();
            }
        });
    }


    protected void onResume() {
        super.onResume();
    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish();
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
