package com.iyuba.conceptEnglish.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.concept_other.util.HelpUtil;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayManager;
import com.iyuba.conceptEnglish.protocol.AgreeAgainstRequest;
import com.iyuba.conceptEnglish.protocol.AgreeAgainstResponse;
import com.iyuba.conceptEnglish.protocol.DeleteCommentRequest;
import com.iyuba.conceptEnglish.protocol.DeleteCommentResponse;
import com.iyuba.conceptEnglish.sqlite.mode.Comment;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.conceptEnglish.sqlite.op.CommentAgreeOp;
import com.iyuba.conceptEnglish.util.ShareUtils;
import com.iyuba.configation.Constant;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.listener.OnPlayStateChangedListener;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IErrorReceiver;
import com.iyuba.core.common.network.INetStateReceiver;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.event.StopTextPlayerEvent;
import com.iyuba.core.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Comment> mList = new ArrayList<Comment>();
    private boolean playingVoice = false;
    private Player mediaPlayer;
    private ImageView tempVoice;
    private int voiceCount;
    private String voiceId;
    private String uid;
    /**
     * 当前页面的uid
     */
    private String pageUid;
    private int type;
    private MediaPlayer videoView;
    private List<VoaDetail> textDetailTemp;
    private AlertDialog deleteDialog;

    private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {
        @Override
        public void onStartConnect(BaseHttpRequest request, int rspCookie) {

        }

        @Override
        public void onConnected(BaseHttpRequest request, int rspCookie) {

        }

        @Override
        public void onStartSend(BaseHttpRequest request, int rspCookie, int totalLen) {

        }

        @Override
        public void onSend(BaseHttpRequest request, int rspCookie, int len) {

        }

        @Override
        public void onSendFinish(BaseHttpRequest request, int rspCookie) {

        }

        @Override
        public void onStartRecv(BaseHttpRequest request, int rspCookie, int totalLen) {

        }

        @Override
        public void onRecv(BaseHttpRequest request, int rspCookie, int len) {

        }

        @Override
        public void onRecvFinish(BaseHttpRequest request, int rspCookie) {

        }

        @Override
        public void onNetError(BaseHttpRequest request, int rspCookie, ErrorResponse errorInfo) {

        }

        @Override
        public void onCancel(BaseHttpRequest request, int rspCookie) {

        }
    };

    public CommentListAdapter(Context context, ArrayList<Comment> Comments,
                              int type, List<VoaDetail> textDetailTemp, String pageUid) {
        mContext = context;
        mList = Comments;
        uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        this.pageUid = pageUid;
        this.type = type;
        this.textDetailTemp = textDetailTemp;
    }


    public void setData(ArrayList<Comment> Comments) {
        // TODO Auto-generated method stub
        mList = Comments;
    }

    public void addList(ArrayList<Comment> Comments) {
        // TODO Auto-generated method stub
        mList.addAll(Comments);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (uid.equals(pageUid) && !"0".equals(uid)) {
                    int pos = myViewHolder.getAdapterPosition();
                    showDeleteDialog(mList.get(pos).id, pos);
                    return true;
                }
                return false;
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        // TODO Auto-generated method stub
        final Comment curItem = mList.get(position);

        try {

            if (curItem.shuoshuoType == 4) {
                viewHolder.sen_index.setBackgroundResource(R.drawable.ic_bg_select_blue);
                viewHolder.sen_index.setText("合成");
                viewHolder.comment_score.setText(curItem.score + "分");
                viewHolder.comment_text.setVisibility(View.GONE);
            } else if (curItem.shuoshuoType == 2) {
                viewHolder.sen_index.setBackgroundResource(R.drawable.ic_bg_select_green);

                viewHolder.sen_index.setText(curItem.index + "");
                viewHolder.comment_score.setText(curItem.score + "分");
                viewHolder.comment_text.setVisibility(View.VISIBLE);
                String sentence = "";
                for (VoaDetail detail : textDetailTemp) {
                    if (curItem.index == Integer.parseInt(detail.lineN))
                        sentence = detail.sentence.trim();
                }

                viewHolder.comment_text.setText(HelpUtil.transTitleStyle(sentence));
                Log.e("textDetailTemp", curItem.index + sentence);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (checkAgree(curItem.id, uid) == 1) {
            viewHolder.agreeView.setImageResource(R.drawable.ic_agree_press);
        } else if (checkAgree(curItem.id, uid) == 2) {
            viewHolder.againstView
                    .setBackgroundResource(R.drawable.against_press);
        } else {
            viewHolder.agreeView.setImageResource(R.drawable.ic_agree);
            viewHolder.againstView.setBackgroundResource(R.drawable.against);
        }
        // 点赞部分
        viewHolder.agreeText.setText(String.valueOf(curItem.agreeCount));
        viewHolder.againstText.setText(String.valueOf(curItem.againstCount));
        viewHolder.agreeView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //增加登录判断
                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(mContext);
                    return;
                }

                if (checkAgree(curItem.id, uid) == 0) {
                    ClientSession.Instace().asynGetResponse(
                            new AgreeAgainstRequest("61001", curItem.id, type),
                            new IResponseReceiver() {
                                @Override
                                public void onResponse(
                                        BaseHttpResponse response,
                                        BaseHttpRequest request, int rspCookie) {
                                    // TODO 自动生成的方法存根
                                    AgreeAgainstResponse rs = (AgreeAgainstResponse) response;
                                    if (rs.result.equals("001")) {
                                        handler.sendEmptyMessage(4);
                                        new CommentAgreeOp(mContext).saveData(
                                                curItem.id, uid, "agree");
                                        mList.get(position).agreeCount = mList
                                                .get(position) == null ? null
                                                : (++mList.get(position).agreeCount);

                                        //这里将位置数据传送过去，刷新的是时候需要
                                        Message message = new Message();
                                        message.what = 0;
                                        message.obj = position;
                                        handler.sendMessage(message);
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
        viewHolder.againstView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (checkAgree(curItem.id, uid) == 0) {
                    ClientSession.Instace().asynGetResponse(
                            new AgreeAgainstRequest("61002", curItem.id, type),
                            new IResponseReceiver() {
                                @Override
                                public void onResponse(
                                        BaseHttpResponse response,
                                        BaseHttpRequest request, int rspCookie) {
                                    // TODO 自动生成的方法存根
                                    AgreeAgainstResponse rs = (AgreeAgainstResponse) response;
                                    if (rs.result.equals("001")) {
                                        handler.sendEmptyMessage(5);
                                        new CommentAgreeOp(mContext).saveData(
                                                curItem.id, uid, "against");
                                        mList.get(position).againstCount = mList
                                                .get(position) == null ? null
                                                : (++mList.get(position).againstCount);
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
            viewHolder.comment_body_voice_icon
                    .setImageResource(R.drawable.ic_play_3);
            voiceStopAnimation(viewHolder.comment_body_voice_icon);
        }
        if (curItem.username != null && !"none".equals(curItem.username) && !"".equals(curItem.username) && !"null".equals(curItem.username))
            viewHolder.name.setText(curItem.username);
        else
            viewHolder.name.setText(curItem.userId);
        viewHolder.time.setText(curItem.createdate.substring(0, 10));

        GitHubImageLoader.getInstance().setPic(mList.get(position).userId,
                viewHolder.image);
        viewHolder.image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (UserInfoManager.getInstance().isLogin()) {
                    mContext.startActivity(PersonalHomeActivity.buildIntent(mContext, Integer.parseInt(curItem.userId), curItem.username, 0));
                } else {
                    LoginUtil.startToLogin(mContext);
                }
            }
        });

        //分享
        if (InfoHelper.getInstance().openShare()){
            viewHolder.iv_comment_share.setVisibility(View.VISIBLE);
        }else {
            viewHolder.iv_comment_share.setVisibility(View.INVISIBLE);
        }
        viewHolder.iv_comment_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String content = "爱语吧语音评测";
                String siteUrl = "http://voa." + Constant.IYUBA_CN + "voa/play.jsp?id=" + curItem.id + "&addr=" + curItem.shuoshuo + "&apptype=concept";
                String imageUrl = "http://app." + Constant.IYUBA_CN + "android/images/newconcept/newconcept.png";
                String title = viewHolder.name.getText().toString() + "在爱语吧语音评测中获得了" + curItem.score + "分";
                ShareUtils localShareUtils = new ShareUtils();
                localShareUtils.showShare(mContext, imageUrl, siteUrl, title, content, localShareUtils.defaultPlatformActionListener,null);

            }
        });
        // 点击语音评论进行播放
        viewHolder.comment_body_voice_icon.setOnClickListener(v -> {
            // TODO 自动生成的方法存根
            pauseTextPlayer();
            playingVoice = true;//
            if (tempVoice != null) {// 播放之前先停止其他的播放
                handler.removeMessages(1);
                tempVoice.setImageResource(R.drawable.ic_play_3);
            }
            voiceId = curItem.id;
         /*   if (type != 2)
                playVoice("http://daxue." + Constant.IYUBA_CN + "appApi/" + curItem.shuoshuo);// 播放
            else*/
            playVoice(Constant.EVAL_PREFIX + curItem.shuoshuo);// 播放
            Log.e("播放音频", Constant.EVAL_PREFIX + curItem.shuoshuo);
            voiceAnimation(v.findViewById(R.id.comment_body_voice_icon));// 播放的动画
        });


    }

    private void showDeleteDialog(String commentId, int position) {
        if (deleteDialog != null) {
            if (deleteDialog.isShowing()) {
                deleteDialog.dismiss();
            }
        }

        deleteDialog = new AlertDialog.Builder(mContext)
                .setTitle("警告")
                .setMessage("此操作将删除你的配音记录且无法撤回，是否继续？")
                .setPositiveButton("确定", (dialog, which) -> deleteRecord(commentId, position))
                .setNegativeButton("取消", null)
                .create();
        deleteDialog.show();

    }

    private Handler deleteRecordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String id = (String) msg.obj;
            ClientSession.Instace().asynGetResponse(new DeleteCommentRequest(id), new IResponseReceiver() {
                @Override
                public void onResponse(BaseHttpResponse response, BaseHttpRequest request, int rspCookie) {
                    DeleteCommentResponse tr = (DeleteCommentResponse) response;
                    if ("OK".equals(tr.Message)) {
                        ((Activity) mContext).runOnUiThread(() -> {
                            Toast.makeText(mContext, "删除记录成功", Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        Looper.prepare();
                        Toast.makeText(mContext, "发生未知错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }, new IErrorReceiver() {
                @Override
                public void onError(ErrorResponse errorResponse, BaseHttpRequest request, int rspCookie) {

                }
            }, mNetStateReceiver);
        }
    };

    private void deleteRecord(String commentId, int position) {
        Message message = Message.obtain();
        message.what = 0;
        message.obj = commentId;

        mList.remove(position);
        notifyDataSetChanged();

        deleteRecordHandler.sendMessage(message);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    private int checkAgree(String commentId, String uid) {
        return new CommentAgreeOp(mContext).findDataByAll(commentId, uid);
    }

    // 播放语音
    private void playVoice(String url) {

        if (mediaPlayer == null) {
            mediaPlayer = new Player(mContext,
                    new OnPlayStateChangedListener() {
                        @Override
                        public void playFaild() {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void playCompletion() {
                            // TODO Auto-generated method stub
                            playingVoice = false;
                            handler.removeMessages(1, tempVoice);
                            tempVoice.setImageResource(R.drawable.ic_play_3);

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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    // TODO: 2023/10/24 这里李涛在新概念群组中要求只刷新单个item，看起来应该可以
                    if (msg.obj != null){
                        int position = (int) msg.obj;
                        notifyItemChanged(position);
                    }else {
                        notifyDataSetChanged();
                    }
                    break;
                case 1:
                    // 通过不断切换图片来表示动画
                    if (voiceCount % 3 == 0) {
                        ((ImageView) msg.obj)
                                .setImageResource(R.drawable.ic_play_1);
                    } else if (voiceCount % 3 == 1) {
                        ((ImageView) msg.obj)
                                .setImageResource(R.drawable.ic_play_2);
                    } else if (voiceCount % 3 == 2) {
                        ((ImageView) msg.obj)
                                .setImageResource(R.drawable.ic_play_3);
                    }
                    voiceCount++;
                    handler.sendMessageDelayed(handler.obtainMessage(1, msg.obj),
                            500);
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

    public class MyViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.comment_image)
        ImageView image;// 头像图片

        @BindView(R.id.agree)
        ImageView agreeView;// 点赞按钮

        @BindView(R.id.against)
        ImageView againstView;// 点踩按钮

        @BindView(R.id.agree_text)
        TextView agreeText; // 多少赞
        @BindView(R.id.against_text)
        TextView againstText; // 多少踩


        @BindView(R.id.comment_body_voice_icon)
        ImageView comment_body_voice_icon;// 显示正在播放的

        @BindView(R.id.comment_name)
        TextView name; // 用户名

        @BindView(R.id.comment_time)
        TextView time; // 发布时间


        @BindView(R.id.comment_text)
        TextView comment_text;

        @BindView(R.id.sen_index)
        TextView sen_index;

        @BindView(R.id.comment_score)
        TextView comment_score;

        @BindView(R.id.iv_comment_share)
        ImageView iv_comment_share;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
