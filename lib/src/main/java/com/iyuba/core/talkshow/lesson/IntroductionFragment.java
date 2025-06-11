package com.iyuba.core.talkshow.lesson;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.iyuba.lib.R;
import com.umeng.analytics.MobclickAgent;


public class IntroductionFragment extends Fragment {
    private static final String TAG = IntroductionFragment.class.getSimpleName();

    private static final String INTRODUCTION = "introduction";

    TextView tIntroduction;

    public static IntroductionFragment newInstance(String introduction) {
        IntroductionFragment fragment = new IntroductionFragment();
        Bundle args = new Bundle();
        args.putString(INTRODUCTION, introduction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduction, container, false);
        tIntroduction = view.findViewById(R.id.introduction);


        if (getArguments() != null) {
            String introduction = getArguments().getString(INTRODUCTION);
            tIntroduction.setText(introduction);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    public void setText(String text){
        tIntroduction.setText(text);
    }
}
