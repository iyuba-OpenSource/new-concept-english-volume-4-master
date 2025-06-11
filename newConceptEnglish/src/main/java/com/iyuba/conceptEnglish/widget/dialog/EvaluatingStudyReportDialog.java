package com.iyuba.conceptEnglish.widget.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.conceptEnglish.widget.cdialog.CustomDialog;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibGlide3Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.moments.WechatMoments;

public class EvaluatingStudyReportDialog implements IStudyReportDialog {
    private static final String TIPS_SWITCH="studynew_evaluate_dialog_no_longer";

    private static final String FILE_NAME="reportDialogEvaluating.jpg";
    private Context mContext;
    private View mView;
    private CustomDialog.Builder mBuilder;
    private CustomDialog mCustomDialog;
    private List<VoaDetail> mList;
    private int mEvaluatedSentenceCount = 0;
    private int mAverageScore = 0;
    private Button shareBtn;
    private TextView type;
    private CardView tips_cardView;


    public static EvaluatingStudyReportDialog getInstance() {
        return new EvaluatingStudyReportDialog();
    }


    @Override
    public EvaluatingStudyReportDialog init(Context context) {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_study_report_evaluating, null);
        mBuilder = new CustomDialog.Builder(context);
        return this;
    }

    public EvaluatingStudyReportDialog setData(List<VoaDetail> list, int averageScore, int evaluatedSentenceCount) {
        mList = list;
        mAverageScore = averageScore;
        mEvaluatedSentenceCount = evaluatedSentenceCount;
        return this;
    }


    @Override
    public EvaluatingStudyReportDialog prepare() {
        ImageView headPortra = mView.findViewById(R.id.dialog_report_head_portrait);
        String imageUrl = "http://api."+ Constant.IYUBA_COM+"v2/api.iyuba?protocol=10005&uid=" + UserInfoManager.getInstance().getUserId() + "&size=big";
        TextView userName = mView.findViewById(R.id.dialog_report_username);
        type = mView.findViewById(R.id.dialog_report_type_text);
        TextView averageScore = mView.findViewById(R.id.average_score);
        TextView sentenceCount = mView.findViewById(R.id.sentence_count);
        initEvaluatingSnetence();
        boolean isCloseTips = ConfigManager.Instance().loadBoolean(TIPS_SWITCH, false);
        shareBtn = mView.findViewById(R.id.dialog_report_share_btn);
        LinearLayout shareLayout = mView.findViewById(R.id.shareLayout);
        if (InfoHelper.getInstance().openShare()){
            shareBtn.setVisibility(View.VISIBLE);
            shareLayout.setVisibility(View.VISIBLE);
        }else {
            shareBtn.setVisibility(View.GONE);
            shareLayout.setVisibility(View.GONE);
        }

        ImageView close = mView.findViewById(R.id.close);

        //判断是不是第一次展示，是的话 提示用户关闭位置
        tips_cardView=mView.findViewById(R.id.tips_cardView);
        Button no_longer_tips=mView.findViewById(R.id.no_longer_tips);
        Button cancel_tips=mView.findViewById(R.id.cancel_tips);
        cancel_tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tips_cardView.setVisibility(View.GONE);
            }
        });
        no_longer_tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigManager.Instance().setsendEvaReport(false);
                tips_cardView.setVisibility(View.GONE);
                ConfigManager.Instance().putBoolean(TIPS_SWITCH,true);
            }
        });
        //关闭提示的逻辑 end
        if (isCloseTips){
            tips_cardView.setVisibility(View.GONE);
        } else {
            tips_cardView.setVisibility(View.VISIBLE);
        }
        /*Glide.with(mContext)
                .load(imageUrl)
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(headPortra) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        headPortra.setImageDrawable(circularBitmapDrawable);
                    }
                });*/
        LibGlide3Util.loadCircleImg(mContext,imageUrl,0,headPortra);
        userName.setText(UserInfoManager.getInstance().getUserName());
        type.setText("恭喜您完成了本课口语测评~\n");
        averageScore.setText(mAverageScore + "");
        sentenceCount.setText(mEvaluatedSentenceCount + "");
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeFile();
                showShareOnMoment(mContext);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomDialog!=null){
                    mCustomDialog.cancel();
                }
            }
        });

        mBuilder.setContentView(mView);
        return this;
    }

    @Override
    public EvaluatingStudyReportDialog show() {
        mCustomDialog=mBuilder.create();
        mCustomDialog.show();
        WindowManager.LayoutParams params = mCustomDialog.getWindow().getAttributes();
        WindowManager w=(WindowManager)mCustomDialog.getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        int windowHeight=w.getDefaultDisplay().getHeight();
        params.height = (int) (windowHeight*0.8f);
        mCustomDialog.getWindow().setAttributes(params);
        mCustomDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return this;
    }


    private void storeFile() {
        String typeText = "";
        shareBtn.setVisibility(View.GONE);
        typeText = type.getText().toString();
        String result = "我在新概念英语APP\n完成了口语测评";
        type.setText(result);
        tips_cardView.setVisibility(View.GONE);
        LinearLayout layout = mView.findViewById(R.id.layoutView);
        Bitmap cacheBitmap;
        if (mView.getWidth() < 1 || layout.getHeight() < 1) {
            //防止崩溃出现
            new Handler().postDelayed(() -> {
                storeFile();
                //此处为半递归？？？
            }, 200);
            return;
        } else {
            cacheBitmap = Bitmap.createBitmap(mView.getWidth(), layout.getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas c = new Canvas(cacheBitmap);
        mView.draw(c);

        if (cacheBitmap == null) {
            return;
        }
        cacheBitmap.setHasAlpha(false);
        cacheBitmap.prepareToDraw();
        File newPNGFile = new File(mContext.getExternalFilesDir(null) + "/" + FILE_NAME);
        if (newPNGFile.exists()) {
            newPNGFile.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(newPNGFile);
            cacheBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        shareBtn.setVisibility(View.VISIBLE);
        type.setText(typeText);

    }

    private void initEvaluatingSnetence() {
        //比较好的句子
        LinearLayout goodLinearlayout = mView.findViewById(R.id.good_sentence_linear);
        TextView goodText = mView.findViewById(R.id.good_sentence_text);
        //需要加强的句子
        LinearLayout notBedLinearlayout = mView.findViewById(R.id.not_bed_sentence_linear);
        TextView notBedText = mView.findViewById(R.id.not_bed_sentence_text);

        int goodSentenceCount = 0;
        int notBedSentenceCount = 0;

        for (VoaDetail voaDetail : mList) {
            if (voaDetail.getReadScore() > 0 && voaDetail.getReadScore() < 60) {
                /*ConstraintLayout evaluatingItem = (ConstraintLayout) LayoutInflater.from(mContext)
                        .inflate(R.layout.dialog_study_report_item_evaluating_new, null, false);*/
                View evaluatingItem = View.inflate(mContext,R.layout.dialog_study_report_item_evaluating_new,null);
                ImageView scoreImage = evaluatingItem.findViewById(R.id.image_score);
                TextView sentence = evaluatingItem.findViewById(R.id.evaluating_sentence);
                TextView scoreText = evaluatingItem.findViewById(R.id.item_score);

                scoreImage.setImageResource(R.drawable.report_dialog_not_bed);
                sentence.setText(voaDetail.sentence);
                scoreText.setText(voaDetail.getReadScore() + "");
                notBedLinearlayout.addView(evaluatingItem);

                notBedSentenceCount++;
            } else if (voaDetail.getReadScore() > 60) {
                /*ConstraintLayout evaluatingItem = (ConstraintLayout) LayoutInflater.from(mContext)
                        .inflate(R.layout.dialog_study_report_item_evaluating_new, null, false);*/
                View evaluatingItem = View.inflate(mContext,R.layout.dialog_study_report_item_evaluating_new,null);
                ImageView scoreImage = evaluatingItem.findViewById(R.id.image_score);
                TextView sentence = evaluatingItem.findViewById(R.id.evaluating_sentence);
                TextView scoreText = evaluatingItem.findViewById(R.id.item_score);

                scoreImage.setImageResource(R.drawable.report_dialog_greater);
                sentence.setText(voaDetail.sentence);
                scoreText.setText(voaDetail.getReadScore() + "");
                goodLinearlayout.addView(evaluatingItem);

                goodSentenceCount++;
            }
        }


        if (goodSentenceCount < 1) {
            goodText.setText("");
        }
        if (notBedSentenceCount < 1) {
            notBedText.setText("");
        }
    }


    public void showShareOnMoment(Context context) {

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        if (!InfoHelper.showWeiboShare()){
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }
        //微博飞雷神
        oks.setPlatform(WechatMoments.NAME);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setImagePath(mContext.getExternalFilesDir(null) + "/"+FILE_NAME);
        oks.setText("新概念英语全四册");
        oks.setTitle("新概念英语全四册");
        oks.setSilent(true);
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (mCustomDialog!=null){
                    mCustomDialog.dismiss();
                }
                ToastUtil.showToast(mContext,"分享成功");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                ToastUtil.showToast(mContext,"分享失败");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                ToastUtil.showToast(mContext,"分享取消");
            }
        });
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams shareParams) {
                shareParams.setShareType(Platform.SHARE_IMAGE);
            }
        });
        // 启动分享GUI
        oks.show(context);
    }
}
