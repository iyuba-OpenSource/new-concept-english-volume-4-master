package com.iyuba.conceptEnglish.widget.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.view.NoScrollLinearLayoutManager;
import com.iyuba.conceptEnglish.util.ConceptApplication;
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
 * @title: 新的听力学习报告弹窗界面
 * @date: 2023/11/13 16:34
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NewListenStudyReportDialog extends AlertDialog {

    private static final String TIPS_SWITCH="studynew_listen_dialog_no_longer";
    private static final String FILE_NAME = "reportDialogListen.jpg";

    private Context context;
    private String rewardPrice;
    private List<VoaWord2> allList;
    private List<VoaWord2> collectList;

    private Button shareBtn;
    private TextView type;
    private TextView rewardText;
    private CardView tips_cardView;
    private DialogCallBack mCallback;

    private LinearLayout layout;
    private View mView;

    private Consumer<Integer> onDialogTouchListener;

    public void setOnDialogTouchListener(Consumer<Integer> onDialogTouchListener) {
        this.onDialogTouchListener = onDialogTouchListener;
    }

    public NewListenStudyReportDialog(@NonNull Context context) {
        this(context,0);
    }

    public NewListenStudyReportDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public void setData(String reward, List<VoaWord2> allWordList, List<VoaWord2> collectWordList, DialogCallBack callBack){
        this.rewardPrice = reward;
        this.allList = allWordList;
        this.collectList = collectWordList;
        this.mCallback = callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_study_report_listen);

        mView = findViewById(R.id.mView);
        layout = findViewById(R.id.layoutView);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (onDialogTouchListener!=null){
                    onDialogTouchListener.accept(0);
                }
                return true;
            }
        });

        TextView userName = findViewById(R.id.dialog_report_username);
        type = findViewById(R.id.dialog_report_type_text);
        rewardText = findViewById(R.id.dialog_report_reward_text);
        initWords();
        shareBtn = findViewById(R.id.dialog_report_share_btn);
        ImageView close = findViewById(R.id.close);

        tips_cardView=findViewById(R.id.tips_cardView);
        Button no_longer_tips=findViewById(R.id.no_longer_tips);
        Button cancel_tips=findViewById(R.id.cancel_tips);
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
        if (!TextUtils.isEmpty(rewardPrice)){
            double rewardData = Double.parseDouble(rewardPrice);
            if (rewardData>0){
                String formatStr = "本次学习获得%1$s元红包奖励";
                String showStr = String.format(formatStr,rewardPrice);

                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(showStr);
                ForegroundColorSpan priceSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.btn_red));
                ssb.setSpan(priceSpan,showStr.indexOf(rewardPrice),showStr.indexOf(rewardPrice)+rewardPrice.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
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
                showShareOnMoment(context);
            }
        });
        close.setOnClickListener(v -> dismiss());

        ImageView headPortra = layout.findViewById(R.id.dialog_report_head_portrait);
        String imageUrl = "http://api."+ Constant.IYUBA_COM+"v2/api.iyuba?protocol=10005&uid=" + UserInfoManager.getInstance().getUserId() + "&size=big";
        /*Glide.with(context)
                .load(imageUrl)
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(headPortra) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        headPortra.setImageDrawable(circularBitmapDrawable);
                    }
                });*/
        LibGlide3Util.loadCircleImg(context,imageUrl,0,headPortra);
    }

    private void initWords() {
        CardView cardView = findViewById(R.id.cardView);
        LinearLayout relative = findViewById(R.id.relative);
        //比较好的句子
//        LinearLayout wordLinear = findViewById(R.id.words_linear);
        RecyclerView wordLinear = findViewById(R.id.words_linear);
        ImageView noWordImage = findViewById(R.id.no_word_img);

//        LinearLayout newWordLinear = findViewById(R.id.new_words_linear);
        RecyclerView newWordLinear = findViewById(R.id.new_words_linear);
        TextView noNewWordTxt = findViewById(R.id.no_new_word_text);

        //单词显示
        ListenStudyReportAllAdapter allAdapter = new ListenStudyReportAllAdapter(context,allList);
        wordLinear.setLayoutManager(new NoScrollLinearLayoutManager(context,false));
        wordLinear.setAdapter(allAdapter);

        if (allList == null || allList.size() < 1) {
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
        ListenStudyReportCollectAdapter collectAdapter = new ListenStudyReportCollectAdapter(context,collectList);
        newWordLinear.setLayoutManager(new NoScrollLinearLayoutManager(context,false));
        newWordLinear.setAdapter(collectAdapter);

        if (collectList == null || collectList.size() < 1) {
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

    @Override
    public void show() {
        super.show();

        setOnDismissListener(dialog -> {
            if (mCallback != null) {
                mCallback.callback();
            }
        });

        WindowManager.LayoutParams params = getWindow().getAttributes();
        WindowManager w=(WindowManager)getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        int windowHeight=w.getDefaultDisplay().getHeight();
        int windowWidth = w.getDefaultDisplay().getWidth();

        params.width = (int) (windowWidth*0.8f);
        params.height = (int) (windowHeight*0.8f);
        getWindow().setAttributes(params);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
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
        File newPNGFile = new File(context.getExternalFilesDir(null) + "/" + FILE_NAME);
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

    private void showShareOnMoment(Context context) {

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
        oks.setImagePath(context.getExternalFilesDir(null) + "/" + FILE_NAME);
        oks.setText(ConceptApplication.getInstance().getResources().getString(R.string.app_name));
        oks.setTitle(ConceptApplication.getInstance().getResources().getString(R.string.app_name));
        oks.setSilent(true);

        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                dismiss();
                ToastUtil.showToast(context, "分享成功");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                ToastUtil.showToast(context, "分享失败");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                ToastUtil.showToast(context, "分享取消");
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
