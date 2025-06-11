package com.iyuba.conceptEnglish.study;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayManager;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.study.voaStructure.VoaStructureExerciseNewFragment;
import com.iyuba.configation.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 习题界面
 */
public class ExerciseFragment extends Fragment {

    private static String IMAGE_URL = "http://static2." + Constant.IYUBA_CN + "newconcept/images/";
    private static final String PNG = ".png";

    private int curSelectActivity = 0;
    private Context mContext;


    Unbinder unbinder;

    @BindView(R.id.multiple_choice)
    ImageView multipleChoice;
    @BindView(R.id.voa_diffculty)
    ImageView voaDiffculty;
    @BindView(R.id.voa_structure)
    ImageView voaStructure;

    @BindView(R.id.backlayout)
    RelativeLayout backlayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.re_double_course)
    RelativeLayout reDoubleCourse;

    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    @BindView(R.id.cur_time)
    TextView curTime;
    @BindView(R.id.total_time)
    TextView totalTime;

    @BindView(R.id.video_play)
    ImageView videoPlay;


    private ExerciseDoubleAdapter adapter;
    private VoaOp voaOp;
    private int voaId;

//    private IJKPlayer player;
//    private MediaPlayer player;
    private ExoPlayer exoPlayer;

    private View rootView;
    private List<String> list = new ArrayList<>();
    private boolean isPrepare = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (getActivity().isDestroyed() || !isAdded()) {
                return;
            }

            switch (msg.what) {
                case 1:
//                    if (player != null && player.isPlaying()) {
//                        seekBar.setMax(player.getDuration());
//                        seekBar.setProgress(player.getCurrentPosition());
//                        curTime.setText(getTime(player.getCurrentPosition()));
//                        totalTime.setText(getTime(player.getDuration()));
//                        videoPlay.setImageResource(R.drawable.image_pause);
//                    } else {
//                        videoPlay.setImageResource(R.drawable.image_play);
//                    }
                    if (exoPlayer != null && exoPlayer.isPlaying()) {
                        seekBar.setMax((int) exoPlayer.getDuration());
                        seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                        curTime.setText(getTime(exoPlayer.getCurrentPosition()));
                        totalTime.setText(getTime(exoPlayer.getDuration()));
                        videoPlay.setImageResource(R.drawable.image_pause);
                    } else {
                        videoPlay.setImageResource(R.drawable.image_play);
                    }
                    handler.sendEmptyMessageDelayed(1, 1000L);
                    break;
            }
        }
    };

    public static ExerciseFragment getInstance(){
        ExerciseFragment fragment = new ExerciseFragment();
        return fragment;
    }

    public void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);
        this.mContext = getActivity();
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater paramLayoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle paramBundle) {
        if (this.rootView == null) {
            this.rootView = paramLayoutInflater.inflate(R.layout.fragment_exercise, viewGroup, false);
        }
        unbinder = ButterKnife.bind(this, rootView);
        voaOp = new VoaOp(mContext);
        voaId = VoaDataManager.Instace().voaTemp.voaId;
        isPrepare = true;
        initPlayer();

        if (/*1 == voaId / 1000 && 0 == voaId % 2*/false) {
            backlayout.setVisibility(View.GONE);
            reDoubleCourse.setVisibility(View.VISIBLE);

            int picNum = Integer.parseInt(voaOp.findDataById(voaId).pic);
            for (int i = 0; i < picNum; i++) {
                list.add(IMAGE_URL + voaId + "/" + (i + 1) + PNG);
//           http://static2."+Constant.IYUBA_CN+"newconcept/images/1002/1.png
            }

            handler.sendEmptyMessage(1);
        } else {
            backlayout.setVisibility(View.VISIBLE);
            reDoubleCourse.setVisibility(View.GONE);
        }

        initRecyclerView();


        curSelectActivity = 0;
        clickExerciseTab();
        return rootView;
    }


    private void initRecyclerView() {
        adapter = new ExerciseDoubleAdapter(list, mContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemViewCacheSize(20);
    }

    private void clickExerciseTab() {
        multipleChoice.setImageResource(R.drawable.multiple_choice_normal_new);
        voaStructure.setImageResource(R.drawable.voa_improtant_sentences_normal_new);
        voaDiffculty.setImageResource(R.drawable.voa_diffcult_normal_new);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        switch (curSelectActivity) {
            case 0:
                ft.replace(R.id.exerciseBody, new MultipleChoiceFragment());
                ft.commitAllowingStateLoss();
                multipleChoice.setImageResource(R.drawable.multiple_choice_press_new);
                break;
            case 1:
//                ft.replace(R.id.exerciseBody, new VoaStructureExerciseFragment());
                ft.replace(R.id.exerciseBody, new VoaStructureExerciseNewFragment());
                ft.commitAllowingStateLoss();
                this.voaStructure.setImageResource(R.drawable.voa_improtent_sentences_press_new);
                break;
            case 2:
                ft.replace(R.id.exerciseBody, new VoaDiffcultyExerciseFragment());
                ft.commitAllowingStateLoss();
                this.voaDiffculty.setImageResource(R.drawable.voa_diffcult_press_new);
                break;
        }

    }

    @OnClick({R.id.video_play})
    void playOrPause() {
        if (exoPlayer.isPlaying()) {
            videoPlay.setImageResource(R.drawable.image_play);
            exoPlayer.pause();
            handler.removeMessages(1);
            return;
        }
        if (!exoPlayer.isPlaying()) {
            videoPlay.setImageResource(R.drawable.image_play);
            exoPlayer.play();
            handler.sendEmptyMessage(1);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        handler.removeMessages(1);
    }

    @OnClick({R.id.ll_voa_structure})
    void goToAnnotations() {
        this.curSelectActivity = 1;
        clickExerciseTab();
    }

    @OnClick({R.id.ll_voa_diffculty})
    void goToImportant() {
        this.curSelectActivity = 2;
        clickExerciseTab();
    }

    @OnClick({R.id.ll_multiple_choice})
    void goToWord() {
        this.curSelectActivity = 0;
        clickExerciseTab();
    }


    public void onDestroyView() {
        super.onDestroyView();
        this.unbinder.unbind();
    }

    public void onResume() {
        super.onResume();
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            videoPlay.setImageResource(R.drawable.image_pause);
            handler.sendEmptyMessage(1);
        } else {
            videoPlay.setImageResource(R.drawable.image_play);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isPrepare) {
            return;
        }
        if (isVisibleToUser) {
            if (exoPlayer != null && exoPlayer.isPlaying()) {
                videoPlay.setImageResource(R.drawable.image_pause);
                handler.sendEmptyMessage(1);
            } else {
                videoPlay.setImageResource(R.drawable.image_play);
            }

        } else {
            handler.removeMessages(1);
        }

    }

    public void refreshView() {

        voaId = VoaDataManager.Instace().voaTemp.voaId;

        if (/*1 == voaId / 1000 && 0 == voaId % 2*/false) {
            backlayout.setVisibility(View.GONE);
            reDoubleCourse.setVisibility(View.VISIBLE);
            int picNum = Integer.parseInt(voaOp.findDataById(voaId).pic);
            for (int i = 0; i < picNum; i++) {
                list.add(IMAGE_URL + voaId + "/" + (i + 1) + PNG);
            }
            adapter.notifyDataSetChanged();
        } else {
            backlayout.setVisibility(View.VISIBLE);
            reDoubleCourse.setVisibility(View.GONE);
            this.curSelectActivity = 0;
            clickExerciseTab();
        }
    }

    private void initPlayer() {

//        player = BackgroundManager.Instace().bindService.getPlayer();
        exoPlayer = ConceptBgPlayManager.getInstance().getPlayService().getPlayer();
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            videoPlay.setImageResource(R.drawable.image_pause);
        } else {
            videoPlay.setImageResource(R.drawable.image_play);
        }

        seekBar.getParent().requestDisallowInterceptTouchEvent(true);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                if (fromUser) {
                    exoPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    private String getTime(long progress) {
        progress /= 1000;
        return String.format("%02d:%02d", new Object[]{Integer.valueOf((int) (progress / 60 % 60)), Integer.valueOf((int) (progress % 60))});
    }
}
