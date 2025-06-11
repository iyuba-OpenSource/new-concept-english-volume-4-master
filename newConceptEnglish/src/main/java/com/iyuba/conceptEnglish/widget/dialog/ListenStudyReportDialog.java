package com.iyuba.conceptEnglish.widget.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.view.NoScrollLinearLayoutManager;
import com.iyuba.conceptEnglish.util.ConceptApplication;
import com.iyuba.conceptEnglish.widget.cdialog.CustomDialog;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.InfoHelper;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.share.SharePlatform;

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
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 原文的学习报告
 */
public class ListenStudyReportDialog implements IStudyReportDialog {
    private static final String TIPS_SWITCH="studynew_listen_dialog_no_longer";

    private static final String FILE_NAME = "reportDialogListen.jpg";
    private Context mContext;
    private View mView;
//    private CustomDialog.Builder mBuilder;
    private AlertDialog.Builder mBuilder;
//    private CustomDialog mCustomDialog;
    private AlertDialog mCustomDialog;
    private List<VoaWord2> mListWords;
    private List<VoaWord2> mListNewWords;
    private DialogCallBack mCallback;
    private String reward = "";
    private Button shareBtn;
    private TextView type;
    private TextView rewardText;
    private CardView tips_cardView;
    private LinearLayout layout;
    private Consumer<Integer> onDialogTouchListener;

    public ListenStudyReportDialog setOnDialogTouchListener(Consumer<Integer> onDialogTouchListener) {
        this.onDialogTouchListener = onDialogTouchListener;
        return this;
    }

    public static ListenStudyReportDialog getInstance() {
        //脱裤子放屁
        return new ListenStudyReportDialog();
    }


    @Override
    public ListenStudyReportDialog init(Context context) {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_study_report_listen, null);
        mBuilder = new AlertDialog.Builder(mContext);
        return this;
    }

    public ListenStudyReportDialog setData(String reward,List<VoaWord2> listWords, List<VoaWord2> listNewWords, DialogCallBack callback) {
        this.reward = reward;
        mListWords = listWords;
        mListNewWords = listNewWords;
        mCallback = callback;
        return this;
    }

    public void closeSelf(){
        if (mCustomDialog != null) {
            mCustomDialog.dismiss();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public ListenStudyReportDialog prepare() {
        layout = mView.findViewById(R.id.layoutView);
        layout.setOnTouchListener((view, motionEvent) -> {
            onDialogTouchListener.accept(0);
            return false;
        });
        ImageView headPortra = mView.findViewById(R.id.dialog_report_head_portrait);
        String imageUrl = "http://api."+ Constant.IYUBA_COM+"v2/api.iyuba?protocol=10005&uid=" + UserInfoManager.getInstance().getUserId() + "&size=big";
        TextView userName = mView.findViewById(R.id.dialog_report_username);
        type = mView.findViewById(R.id.dialog_report_type_text);
        rewardText = mView.findViewById(R.id.dialog_report_reward_text);
        initWords();
        shareBtn = mView.findViewById(R.id.dialog_report_share_btn);
        ImageView close = mView.findViewById(R.id.close);

        //关闭提示的逻辑 start

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
                ConfigManager.Instance().setSendListenReport(false);
                tips_cardView.setVisibility(View.GONE);
                ConfigManager.Instance().putBoolean(TIPS_SWITCH,true);
            }
        });
        //关闭提示的逻辑 end
        boolean isCloseTips = ConfigManager.Instance().loadBoolean(TIPS_SWITCH, false);
        if (isCloseTips){
            tips_cardView.setVisibility(View.GONE);
        } else {
            tips_cardView.setVisibility(View.VISIBLE);
        }
        userName.setText(UserInfoManager.getInstance().getUserName());
        type.setText("恭喜您完成了本课听力学习~\n");
        //增加奖励信息
        if (!TextUtils.isEmpty(reward)){
            double rewardData = Double.parseDouble(reward);
            if (rewardData>0){
                String formatStr = "本次学习获得%1$s元红包奖励";
                String showStr = String.format(formatStr,reward);

                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(showStr);
                ForegroundColorSpan priceSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.btn_red));
                ssb.setSpan(priceSpan,showStr.indexOf(reward),showStr.indexOf(reward)+reward.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                rewardText.setText(ssb);
                rewardText.setMovementMethod(LinkMovementMethod.getInstance());
                rewardText.setVisibility(View.VISIBLE);
            }else {
                rewardText.setVisibility(View.GONE);
            }
        }else {
            rewardText.setVisibility(View.GONE);
        }
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeFile();
                showShareOnMoment(mContext);
            }
        });
        close.setOnClickListener(v -> closeSelf());

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
//        mBuilder.setContentView(mView);
        mBuilder.setView(mView);

        return this;
    }

    @Override
    public ListenStudyReportDialog show() {
        mCustomDialog = mBuilder.create();
        mCustomDialog.setOnDismissListener(dialog -> {
            if (mCallback != null) {
                mCallback.callback();
            }
        });
        mCustomDialog.show();

        WindowManager.LayoutParams params = mCustomDialog.getWindow().getAttributes();
        WindowManager w=(WindowManager)mCustomDialog.getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        int windowHeight=w.getDefaultDisplay().getHeight();
        int windowWidth = w.getDefaultDisplay().getWidth();

        params.width = (int) (windowWidth*0.8f);
        params.height = (int) (windowHeight*0.8f);
        mCustomDialog.getWindow().setAttributes(params);
        mCustomDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        return this;
    }


    private void initWords() {
        CardView cardView = mView.findViewById(R.id.cardView);
        LinearLayout relative = mView.findViewById(R.id.relative);
        //比较好的句子
//        LinearLayout wordLinear = mView.findViewById(R.id.words_linear);
        RecyclerView wordLinear = mView.findViewById(R.id.words_linear);
        ImageView noWordImage = mView.findViewById(R.id.no_word_img);

//        LinearLayout newWordLinear = mView.findViewById(R.id.new_words_linear);
        RecyclerView newWordLinear = mView.findViewById(R.id.new_words_linear);
        TextView noNewWordTxt = mView.findViewById(R.id.no_new_word_text);

        //单词显示
        ListenStudyReportAllAdapter allAdapter = new ListenStudyReportAllAdapter(mContext,mListWords);
        wordLinear.setLayoutManager(new NoScrollLinearLayoutManager(mContext,false));
        wordLinear.setAdapter(allAdapter);

        if (mListWords == null || mListWords.size() < 1) {
            wordLinear.setVisibility(View.GONE);
            noWordImage.setVisibility(View.VISIBLE);
        } else {
//            for (VoaWord2 word2 : mListWords) {
//                if (TextUtils.isEmpty(word2.word) || TextUtils.isEmpty(word2.def)) {
//                    continue;
//                }
//                wordLinear.addView(getWordLayout(word2));
//            }
            wordLinear.setVisibility(View.VISIBLE);
            noWordImage.setVisibility(View.GONE);
        }

        //显示生词
        ListenStudyReportCollectAdapter collectAdapter = new ListenStudyReportCollectAdapter(mContext,mListNewWords);
        newWordLinear.setLayoutManager(new NoScrollLinearLayoutManager(mContext,false));
        newWordLinear.setAdapter(collectAdapter);

        if (mListNewWords == null || mListNewWords.size() < 1) {
            newWordLinear.setVisibility(View.GONE);
            noNewWordTxt.setVisibility(View.VISIBLE);
        } else {
//            for (VoaWord2 word2 : mListNewWords) {
//                if (TextUtils.isEmpty(word2.word) || TextUtils.isEmpty(word2.def)) {
//                    continue;
//                }
//                newWordLinear.addView(getWordLayout(word2));
//            }
            newWordLinear.setVisibility(View.VISIBLE);
            noNewWordTxt.setVisibility(View.GONE);
        }

    }

    private LinearLayout getWordLayout(VoaWord2 word2){
        LinearLayout evaluatingItem = (LinearLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_study_report_item_words, null, false);
        TextView word = evaluatingItem.findViewById(R.id.word);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String space="  ";
        builder.append(word2.word)
                .append(space)
                .append(word2.def);
        StyleSpan boldSpan=new StyleSpan(Typeface.BOLD);
        int end=builder.toString().indexOf(space);
        builder.setSpan(boldSpan,0,end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        word.setText(builder);
        return evaluatingItem;
    }

    private void storeFile() {
        String typeText = "";
        shareBtn.setVisibility(View.GONE);
        typeText = type.getText().toString();
        String result = "我在"+ ConceptApplication.getInstance().getResources().getString(R.string.app_name) +"APP\n完成了听力学习";
        type.setText(result);
        tips_cardView.setVisibility(View.GONE);

        Bitmap cacheBitmap;
        if (mView.getWidth() < 1 || layout.getHeight() < 1) {
            //防止崩溃出现
            new Handler().postDelayed(this::storeFile, 200);
            return;
        } else {
            cacheBitmap = Bitmap.createBitmap(mView.getWidth(), layout.getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas c = new Canvas(cacheBitmap);
        mView.draw(c);

        CardView tips_cardView = mView.findViewById(R.id.tips_cardView);
        boolean isCloseTips = ConfigManager.Instance().loadBoolean(TIPS_SWITCH, false);
        if (isCloseTips) {
            tips_cardView.setVisibility(View.GONE);
        } else {
            tips_cardView.setVisibility(View.VISIBLE);
        }
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


    public void showShareOnMoment(Context context) {

        OnekeyShare oks = new OnekeyShare();
        //这里直接关闭微博分享
        if (!InfoHelper.showWeiboShare()){
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }

        //设置分享是否显示
        if (!InfoHelper.getInstance().openQQShare()){
            oks.addHiddenPlatform(QQ.NAME);
            oks.addHiddenPlatform(QZone.NAME);
        }
        if (!InfoHelper.getInstance().openWeChatShare()){
            oks.addHiddenPlatform(Wechat.NAME);
            oks.addHiddenPlatform(WechatMoments.NAME);
            oks.addHiddenPlatform(WechatFavorite.NAME);
        }
        if (!InfoHelper.getInstance().openWeiboShare()){
            oks.addHiddenPlatform(SharePlatform.SinaWeibo);
        }

        //微博飞雷神
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        oks.setPlatform(WechatMoments.NAME);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setImagePath(mContext.getExternalFilesDir(null) + "/" + FILE_NAME);
        oks.setText(ConceptApplication.getInstance().getResources().getString(R.string.app_name));
        oks.setTitle(ConceptApplication.getInstance().getResources().getString(R.string.app_name));
        oks.setSilent(true);

        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (mCustomDialog != null) {
                    mCustomDialog.dismiss();
                }
                ToastUtil.showToast(mContext, "分享成功");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                ToastUtil.showToast(mContext, "分享失败");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                ToastUtil.showToast(mContext, "分享取消");
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
