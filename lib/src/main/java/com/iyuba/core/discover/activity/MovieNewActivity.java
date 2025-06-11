package com.iyuba.core.discover.activity;

import android.os.Bundle;
import android.view.Window;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.core.lil.base.BaseStackActivity;
import com.iyuba.core.talkshow.TalkShowFragment;
import com.iyuba.lib.R;

/**
 * 视频
 */

public class MovieNewActivity extends BaseStackActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.movie_activity);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.root, TalkShowFragment.newInstance());
        transaction.commit();
    }


}
