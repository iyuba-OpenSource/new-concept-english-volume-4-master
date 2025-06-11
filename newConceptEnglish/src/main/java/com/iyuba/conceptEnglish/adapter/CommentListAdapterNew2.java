package com.iyuba.conceptEnglish.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Concept_comment;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayManager;
import com.iyuba.conceptEnglish.protocol.AgreeAgainstRequest;
import com.iyuba.conceptEnglish.protocol.AgreeAgainstResponse;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.conceptEnglish.sqlite.op.CommentAgreeOp;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.listener.OnPlayStateChangedListener;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.event.StopTextPlayerEvent;
import com.iyuba.core.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

/**
 * 使用新的数据填充的评论界面
 */
public class CommentListAdapterNew2 extends RecyclerView.Adapter<CommentListAdapterNew2.MyViewHolder> {

    private Context mContext;
    private List<Concept_comment.Row> mList = new ArrayList<Concept_comment.Row>();
    private boolean playingVoice = false;
    private Player mediaPlayer;
    private ImageView tempVoice;
    private int voiceCount;
    private String voiceId;
    private String uid;
    private int type;
//    private MediaPlayer videoView;
    private List<VoaDetail> textDetailTemp;
    private OnItemClickListener onItemClickListener;

    public CommentListAdapterNew2(Context context, int type) {
        mContext = context;
        //mList = Comments;
        uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        this.type = type;
//        this.videoView = videoView;
        this.onItemClickListener = (OnItemClickListener) context;
    }

    public CommentListAdapterNew2(Context context, int type, OnItemClickListener onItemClickListener) {
        mContext = context;
        //mList = Comments;
        uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        this.type = type;
//        this.videoView = videoView;
        this.onItemClickListener = onItemClickListener;
    }


    public void setData(List<Concept_comment.Row> comments){
        this.mList = comments;
        notifyDataSetChanged();
    }

    public void addData(List<Concept_comment.Row> comments){
        this.mList.addAll(comments);
        notifyDataSetChanged();
    }
    public List<Concept_comment.Row>getData(){
        return mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_comment_new, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        final Concept_comment.Row curItem = mList.get(position);
        viewHolder.reread.setVisibility(View.GONE);

        try {
            if (textDetailTemp != null && curItem.ShuoShuoType == 2) {
                viewHolder.sen_re.setVisibility(View.VISIBLE);
                viewHolder.sen_index.setText("0");
                viewHolder.comment_score.setText("0分");
                viewHolder.comment_score.setVisibility(View.VISIBLE);
                viewHolder.comment_text.setVisibility(View.VISIBLE);
                String sentence = textDetailTemp.get(0).sentence.toString().trim();
                viewHolder.comment_text.setText(sentence);
                Log.e("textDetailTemp", 0 + sentence);
            } else {
                if (curItem.ShuoShuoType == 4) {
                    viewHolder.reread.setVisibility(View.VISIBLE);
                    viewHolder.comment_score.setText("0分");
                    viewHolder.comment_score.setVisibility(View.VISIBLE);
                }
                viewHolder.sen_re.setVisibility(View.GONE);
                viewHolder.comment_text.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (checkAgree(curItem.id, uid) == 1) {
            viewHolder.agreeView.setBackgroundResource(R.drawable.agree_press_line);
        } else if (checkAgree(curItem.id, uid) == 2) {
            viewHolder.againstView.setBackgroundResource(R.drawable.against_press);
        } else {
            viewHolder.agreeView.setBackgroundResource(R.drawable.agree);
            viewHolder.againstView.setBackgroundResource(R.drawable.against);
        }
        // 点赞部分
        viewHolder.agreeText.setText(String.valueOf(curItem.agreeCount));
        viewHolder.againstText.setText(String.valueOf(curItem.againstCount));
        viewHolder.agreeView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //增加登录判断
                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(mContext);
                    return;
                }

                if (checkAgree(curItem.id, uid) == 0) {
                    ClientSession.Instace().asynGetResponse(new AgreeAgainstRequest("61001", curItem.id, type), new IResponseReceiver() {
                        @Override
                        public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
                            // TODO 自动生成的方法存根
                            AgreeAgainstResponse rs = (AgreeAgainstResponse) response;
                            if (rs.result.equals("001")) {
                                handler.sendEmptyMessage(4);
                                new CommentAgreeOp(mContext).saveData(curItem.id, uid, "agree");
                                mList.get(position).agreeCount = mList.get(position) == null ? null : (++mList.get(position).agreeCount);
                                handler.sendEmptyMessage(0);
                            } else if (rs.result.equals("000")) {
                                handler.sendEmptyMessage(2);
                            }
                        }
                    });
                } else {
                    handler.sendEmptyMessage(3);
                }
            }
        });
        viewHolder.againstView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (checkAgree(curItem.id, uid) == 0) {
                    ClientSession.Instace().asynGetResponse(new AgreeAgainstRequest("61002", curItem.id, type), new IResponseReceiver() {
                        @Override
                        public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
                            // TODO 自动生成的方法存根
                            AgreeAgainstResponse rs = (AgreeAgainstResponse) response;
                            if (rs.result.equals("001")) {
                                handler.sendEmptyMessage(5);
                                new CommentAgreeOp(mContext).saveData(curItem.id, uid, "against");
                                mList.get(position).againstCount = mList.get(position) == null ? null : (++mList.get(position).againstCount);
                                handler.sendEmptyMessage(0);
                            } else if (rs.result.equals("000")) {
                                handler.sendEmptyMessage(2);
                            }
                        }
                    });
                } else {
                    handler.sendEmptyMessage(3);
                }
            }
        });
        // 是在播放，显示动画
        if (playingVoice && curItem.id.equals(voiceId)) {
            voiceAnimation(viewHolder.comment_body_voice_icon);
        } else {// 否则停止
            viewHolder.comment_body_voice_icon.setImageResource(R.drawable.chatfrom_voice_playing);
            voiceStopAnimation(viewHolder.comment_body_voice_icon);
        }
        if (curItem.UserName != null && !"none".equals(curItem.UserName) && !"".equals(curItem.UserName) && !"null".equals(curItem.UserName))
            viewHolder.name.setText(curItem.UserName);
        else viewHolder.name.setText(curItem.Userid);
        viewHolder.time.setText(curItem.CreateDate);
        viewHolder.body.setText(curItem.ShuoShuo);
        viewHolder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                if (UserInfoManager.getInstance().isLogin()) {
                    Intent intent = new Intent("toreply");
                    if (curItem.UserName == null) {
                        curItem.UserName = curItem.Userid;
                    }
                    intent.putExtra("username", curItem.UserName);
                    mContext.sendBroadcast(intent);
                } else {
                    LoginUtil.startToLogin(mContext);
                }
            }
        });
        if (type != 0) {
            viewHolder.reply.setVisibility(View.INVISIBLE);
        }
        GitHubImageLoader.getInstance().setPic(mList.get(position).Userid, viewHolder.image);
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (UserInfoManager.getInstance().isLogin()) {
                    mContext.startActivity(PersonalHomeActivity.buildIntent(mContext, Integer.parseInt(curItem.Userid), curItem.UserName, 0));
                } else {
                    LoginUtil.startToLogin(mContext);
                }
            }
        });
        // 点击语音评论进行播放
        viewHolder.comment_body_voice.setOnClickListener(v -> {
            // TODO 自动生成的方法存根
            pauseTextPlayer();
            playingVoice = true;
            if (tempVoice != null) {// 播放之前先停止其他的播放
                handler.removeMessages(1);
                tempVoice.setImageResource(R.drawable.chatfrom_voice_playing);
            }
            voiceId = curItem.id;
            if (type != 2)
                playVoice("http://daxue." + Constant.IYUBA_CN + "appApi/" + curItem.ShuoShuo);// 播放
            else playVoice("http://voa." + Constant.IYUBA_CN + "voa/" + curItem.ShuoShuo);// 播放
            voiceAnimation(v.findViewById(R.id.comment_body_voice_icon));// 播放的动画

            Log.e("音频","http://daxue." + Constant.IYUBA_CN + "appApi/" + curItem.ShuoShuo);

        });
        if (curItem.ShuoShuoType == 0) {
            viewHolder.comment_body_voice.setVisibility(View.GONE);
            viewHolder.body.setVisibility(View.VISIBLE);
        } else {
            viewHolder.comment_body_voice.setVisibility(View.VISIBLE);
            viewHolder.body.setVisibility(View.GONE);
        }
        //删除评论
        viewHolder.itemView.setOnLongClickListener(view -> {
            onItemClickListener.onItemClick(position);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image, reread, comment_body_voice_icon, deleteBox, agreeView, againstView;
        TextView body, name, time, agreeText, againstText, comment_text, sen_index, comment_score;
        Button reply;
        RelativeLayout sen_re, comment_body_voice,re_comment,comment_info_layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.comment_image);
            comment_info_layout = itemView.findViewById(R.id.comment_info_layout);
            re_comment = itemView.findViewById(R.id.re_comment);
            reread = itemView.findViewById(R.id.reread_image);
            comment_body_voice = itemView.findViewById(R.id.comment_voice_view);
            body = itemView.findViewById(R.id.comment_letter_view);
            name = itemView.findViewById(R.id.comment_name);
            time = itemView.findViewById(R.id.comment_time);
            comment_body_voice_icon = itemView.findViewById(R.id.comment_body_voice_icon);// 显示正在播放的
            reply = itemView.findViewById(R.id.reply);
            deleteBox = itemView.findViewById(R.id.checkBox_isDelete);
            agreeText = itemView.findViewById(R.id.agree_text);
            againstText = itemView.findViewById(R.id.against_text);
            agreeView = itemView.findViewById(R.id.agree);
            againstView = itemView.findViewById(R.id.against);
            comment_text = itemView.findViewById(R.id.comment_text);
            sen_index = itemView.findViewById(R.id.sen_index);
            sen_re = itemView.findViewById(R.id.sen_re);
            comment_score = itemView.findViewById(R.id.comment_score);
        }
    }

    private int checkAgree(String commentId, String uid) {
        return new CommentAgreeOp(mContext).findDataByAll(commentId, uid);
    }

    // 播放语音
    private void playVoice(String url) {

        if (mediaPlayer == null) {
            mediaPlayer = new Player(mContext, new OnPlayStateChangedListener() {

                @Override
                public void playFaild() {
                    // TODO Auto-generated method stub
                }

                @Override
                public void playCompletion() {
                    // TODO Auto-generated method stub
                    playingVoice = false;
                    handler.removeMessages(1, tempVoice);
                    tempVoice.setImageResource(R.drawable.chatfrom_voice_playing);

                }
            });
        } else {
            stopVoice();
        }
        EventBus.getDefault().post(new StopTextPlayerEvent());
        mediaPlayer.playUrl(url);
    }

    // 播放语音评论之前先在这里reset播放器
    private void stopVoice() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
    }

    public void stopVoices() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
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

    // 播放动画，参数为要显示变化的imageview
    private void voiceAnimation(View v) {
        voiceStopAnimation(v);
        voiceCount = 0;
        tempVoice = (ImageView) v;
        handler.obtainMessage(1, tempVoice).sendToTarget();
    }

    // 停止播放动画
    private void voiceStopAnimation(View v) {
        handler.removeMessages(1, v);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    notifyDataSetChanged();
                    break;
                case 1:
                    // 通过不断切换图片来表示动画
                    if (voiceCount % 3 == 0) {
                        ((ImageView) msg.obj).setImageResource(R.drawable.chatfrom_voice_playing_f1);
                    } else if (voiceCount % 3 == 1) {
                        ((ImageView) msg.obj).setImageResource(R.drawable.chatfrom_voice_playing_f2);
                    } else if (voiceCount % 3 == 2) {
                        ((ImageView) msg.obj).setImageResource(R.drawable.chatfrom_voice_playing_f3);
                    }
                    voiceCount++;
                    handler.sendMessageDelayed(handler.obtainMessage(1, msg.obj), 500);
                    break;
                case 2:
                    CustomToast.showToast(mContext, R.string.check_network);
                    break;
                case 3:
                    CustomToast.showToast(mContext, R.string.comment_already);
                    break;
                case 4:
                    CustomToast.showToast(mContext, R.string.comment_agree);
                    break;
                case 5:
                    CustomToast.showToast(mContext, R.string.comment_disagree);
                    break;
            }
        }
    };
    public void stopPlay(){
        if (mediaPlayer!=null){
            mediaPlayer.stop();
        }
    }
    public void pausePlay(){
        if (mediaPlayer!=null){
            mediaPlayer.pause();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
