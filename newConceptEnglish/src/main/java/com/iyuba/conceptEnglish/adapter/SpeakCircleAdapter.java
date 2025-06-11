package com.iyuba.conceptEnglish.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.api.ApiRetrofit;
import com.iyuba.conceptEnglish.api.SpeakCircleApi;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayManager;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.BaseBean;
import com.iyuba.conceptEnglish.sqlite.mode.SpeakCircleBean;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.op.CommentAgreeOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.study.StudyNewActivity;
import com.iyuba.conceptEnglish.util.OnPlayStateChangedListener;
import com.iyuba.conceptEnglish.util.Player;
import com.iyuba.conceptEnglish.util.ShareUtils;
import com.iyuba.conceptEnglish.widget.CircleImageView;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import butterknife.BindView;
import butterknife.ButterKnife;
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by iyuba on 2017/11/21.
 */

public class SpeakCircleAdapter extends RecyclerView.Adapter<SpeakCircleAdapter.MyViewHolder> {


    private Context mContext;
    private List<SpeakCircleBean.DataBean> list = new ArrayList<>();
    private Player mediaPlayer;

    private int voiceCount;
    private ImageView tempVoice;
    private List<Integer> ids = new ArrayList<>();

    private SpeakCircleApi speakCircleApi;


    private VoaDetailOp voaDetailOp;
    private VoaOp voaOp;
    private Consumer<Integer> onItemLongClickListener;

    public void setOnItemLongClickListener(Consumer<Integer> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    // 通过不断切换图片来表示动画
                    if (voiceCount % 3 == 0) {
                        ((ImageView) msg.obj).setImageResource(R.drawable.chatfrom_voice_playing_new_f1);
                    } else if (voiceCount % 3 == 1) {
                        ((ImageView) msg.obj).setImageResource(R.drawable.chatfrom_voice_playing_new_f2);
                    } else if (voiceCount % 3 == 2) {
                        ((ImageView) msg.obj).setImageResource(R.drawable.chatfrom_voice_playing_new_f3);
                    }
                    voiceCount++;
                    handler.sendMessageDelayed(handler.obtainMessage(0, msg.obj), 500);
                    break;
                case 7:
                    break;
                case 9:
                    break;

            }
        }
    };

    public SpeakCircleAdapter(Context context, List<SpeakCircleBean.DataBean> list) {
        this.mContext = context;
        this.list = list;
        speakCircleApi = ApiRetrofit.getInstance().getSpeakCircleApi();
        voaDetailOp = new VoaDetailOp(mContext);
        voaOp = new VoaOp(mContext);
        initPlayer();
    }

    private void initPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new Player(mContext, new OnPlayStateChangedListener() {
                @Override
                public void playCompletion() {
                    handler.removeMessages(0);
                }

                @Override
                public void playFaild() {
                    handler.removeMessages(0);
                }

                @Override
                public void playSuccess() {

                }

            });
        }
    }


    @NonNull
    @Override
    public SpeakCircleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SpeakCircleAdapter.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_speak_circle_new, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int position) {

        final SpeakCircleBean.DataBean dataBean = list.get(position);

        Glide.with(mContext)
                .load(dataBean.getImgSrc())
                .placeholder(R.drawable.defaultavatar)
                .dontAnimate()
                .into(viewHolder.comment_image);
        /*if (dataBean.getVip().equals("0")) {
            //隐藏Drawables
            viewHolder.comment_name.setCompoundDrawables(null, null, null, null);
        } else {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_speak_vip);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.comment_name.setCompoundDrawables(null, null, drawable, null);
        }*/
        if (TextUtils.isEmpty(dataBean.getUserName()) || "null".equals(dataBean.getUserName())) {
            viewHolder.comment_name.setText(dataBean.getUserid());
        } else {
            viewHolder.comment_name.setText(dataBean.getUserName());
        }


        try {

            int voaId = Integer.parseInt(dataBean.getTopicid());
            String type;

            if (voaId > 10000) {
                voaId = voaId / 10;
                type = "(英)";
            } else {
                type = "(美)";
            }


            Voa voa = voaOp.findDataById(voaId);
            int currBook = voa.voaId / 1000;
            int currLesson = voa.voaId % 1000;
            viewHolder.comment_title.setText(new StringBuilder().append("来自文章:新概念英语-" + "第")
                    .append(currBook).append("册-第")
                    .append(currLesson).append("课")
                    .append(type)
                    .toString()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }


        viewHolder.tv_num_agree.setText(dataBean.getAgreeCount());

        if (checkAgree(dataBean.getId(), String.valueOf(UserInfoManager.getInstance().getUserId())) == 1) {
            dataBean.setAgree(true);
            viewHolder.image_comment_agree.setImageResource(R.drawable.agree_press_line);
            viewHolder.tv_num_agree.setTextColor(Color.parseColor("#333333"));
        } else {
            dataBean.setAgree(false);
            viewHolder.image_comment_agree.setImageResource(R.drawable.agree_normal);
            viewHolder.tv_num_agree.setTextColor(Color.parseColor("#CACCD7"));
        }


        if (dataBean.getShuoShuoType() == 4) {
            viewHolder.image_flag.setImageResource(R.drawable.ic_speak_borad);

        } else {
            viewHolder.image_flag.setImageResource(R.drawable.ic_speak_eval);
        }

//        if (dataBean.getScore() > 60) {
        viewHolder.comment_score.setText(dataBean.getScore() + "分");
//        } else {
//            viewHolder.comment_score.setBackground(mContext.getResources().getDrawable(R.drawable.icon_speak_score_fail_bg));
//            viewHolder.comment_score.setText("");
//        }

        viewHolder.comment_time.setText(dataBean.getCreateDate().trim());


        viewHolder.comment_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserInfoManager.getInstance().isLogin()) {
                    LoginUtil.startToLogin(mContext);
                    return;
                }
                mContext.startActivity(PersonalHomeActivity.buildIntent(mContext, Integer.parseInt(dataBean.getUserid()), dataBean.getUserName(), 2));

            }
        });


        viewHolder.comment_body_voice_icon.setOnClickListener(v -> {
            pauseTextPlayer();
            initPlayer();
            if (tempVoice != null) {// 播放之前先停止其他的播放
                handler.removeMessages(0);
                tempVoice.setImageResource(R.drawable.chatfrom_voice_playing_new_f3);
            }
            mediaPlayer.playUrl(Constant.EVAL_PREFIX + dataBean.getShuoShuo());
            voiceAnimation(viewHolder.comment_body_voice_icon);// 播放的动画

        });

        viewHolder.itemView.setOnClickListener(v -> {
            getVoaDetail(Integer.parseInt(dataBean.getTopicid()));
        });
        viewHolder.itemView.setOnLongClickListener(v -> {
            onItemLongClickListener.accept(position);
            return true;
        });

        viewHolder.image_comment_agree.setOnClickListener(v -> {
            final String userId = String.valueOf(UserInfoManager.getInstance().getUserId());
            SpeakCircleBean.DataBean clickBean = list.get(position);
            if (clickBean.isAgree() || clickBean.isClick() || "0".equals(userId)) {
                return;
            }
            clickBean.setClick(true);
            int agreedNum = Integer.parseInt(viewHolder.tv_num_agree.getText().toString().trim()) + 1;
            viewHolder.tv_num_agree.setText(String.valueOf(agreedNum));
            //点赞接口
            speakCircleApi.giveFive(SpeakCircleApi.url, SpeakCircleApi.protocol_give_five, dataBean.getId(), Integer.parseInt(userId)).enqueue(new Callback<BaseBean>() {
                @Override
                public void onResponse(Call<BaseBean> call, Response<BaseBean> response) {
                    try {
                        viewHolder.image_comment_agree.setImageResource(R.drawable.agree_press_line);
                        new CommentAgreeOp(mContext).saveData(dataBean.getId(), userId, "agree");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<BaseBean> call, Throwable t) {
                }
            });
        });

        viewHolder.iv_comment_share.setOnClickListener(v -> {
            String content = "语音评测";
            String siteUrl = "http://voa." + Constant.IYUBA_CN + "voa/play.jsp?id=" + dataBean.getId() + "&addr=" + dataBean.getShuoShuo() + "&apptype=concept";
            String imageUrl = "http://app." + Constant.IYUBA_CN + "android/images/newconcept/newconcept.png";
            String title = viewHolder.comment_name.getText().toString() + "在语音评测中获得了" +
                    dataBean.getScore() + "分";
            ShareUtils localShareUtils = new ShareUtils();
            localShareUtils.showShare(mContext, imageUrl, siteUrl, title, content, localShareUtils.defaultPlatformActionListener,null);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comment_image)
        CircleImageView comment_image;

        @BindView(R.id.comment_name)
        TextView comment_name;

        @BindView(R.id.comment_time)
        TextView comment_time;

        @BindView(R.id.comment_body_voice_icon)
        ImageView comment_body_voice_icon;


        @BindView(R.id.comment_title)
        TextView comment_title;

        @BindView(R.id.comment_score)
        TextView comment_score;

        @BindView(R.id.iv_comment_share)
        ImageView iv_comment_share;

        @BindView(R.id.agree)
        ImageView image_comment_agree;

        @BindView(R.id.agree_text)
        TextView tv_num_agree;

        @BindView(R.id.image_flag)
        ImageView image_flag;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void stopPLayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            handler.removeMessages(0);
        }

    }


    private int checkAgree(String commentId, String uid) {
        return new CommentAgreeOp(mContext).findDataByAll(commentId, uid);
    }

    public void getVoaDetail(final int voaId) {

        new Thread(() -> {
            // 从本地数据库中查找
            VoaDataManager.Instace().voaTemp = voaOp.findDataById(voaId);
            VoaDataManager.Instace().voaDetailsTemp = voaDetailOp.findDataByVoaId(voaId);
            if (VoaDataManager.Instace().voaDetailsTemp != null && VoaDataManager.Instace().voaDetailsTemp.size() != 0) {
                VoaDataManager.Instace().setSubtitleSum(VoaDataManager.Instace().voaTemp, VoaDataManager.Instace().voaDetailsTemp);
                VoaDataManager.Instace().setPlayLocalType(0);
                Intent intent = new Intent();
                intent.setClass(mContext, StudyNewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                intent.putExtra("curVoaId", voaId + "");
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
            }
        }).start();
    }

    // 播放动画，参数为要显示变化的imageview
    private void voiceAnimation(View v) {
        voiceStopAnimation(v);
        voiceCount = 0;
        tempVoice = (ImageView) v;
        handler.obtainMessage(0, tempVoice).sendToTarget();
    }

    // 停止播放动画
    private void voiceStopAnimation(View v) {
        handler.removeMessages(0, v);
    }

    private void pauseTextPlayer() {
//        if (BackgroundManager.Instace().bindService != null) {
//            MediaPlayer extendedPlayer = BackgroundManager.Instace().bindService.getPlayer();
//
//            if (extendedPlayer != null && extendedPlayer.isPlaying()) {
//                extendedPlayer.pause();
//            }
//        }
        EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_audio_pause));
        EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_pause));
    }

}
