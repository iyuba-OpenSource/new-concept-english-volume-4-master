package com.iyuba.conceptEnglish.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.protocol.DictRequest;
import com.iyuba.conceptEnglish.protocol.DictResponse;
import com.iyuba.conceptEnglish.protocol.WordUpdateRequest;
import com.iyuba.conceptEnglish.sqlite.mode.NewWord;
import com.iyuba.conceptEnglish.sqlite.op.WordOp;
import com.iyuba.conceptEnglish.util.NetWorkState;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.sqlite.mode.Word;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.event.WordSearchEvent;
import com.iyuba.core.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;

public class WordCard extends LinearLayout {
    private Context mContext;
    LayoutInflater layoutInflater;
    private Button add_word, close_word;
    private ProgressBar progressBar_translate;
    private String selectText;
    private TextView key, pron, def, examples;
    private Typeface mFace;
    private NewWord selectCurrWordTemp;
    private ImageView speaker;
    private WordOp wordOp;
    private Handler wordHandler;

    private Player player;
    private View main;

    public WordCard(Context context) {
        super(context);

        mContext = context;
        wordOp = new WordOp(context);

        ((Activity) mContext).getLayoutInflater().inflate(R.layout.wordcard, this);
        initGetWordMenu();
    }

    public WordCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        ((Activity) mContext).getLayoutInflater().inflate(R.layout.wordcard,
                this);
        initGetWordMenu();
    }

    private void initGetWordMenu() {
        main = findViewById(com.iyuba.lib.R.id.word);
        progressBar_translate = (ProgressBar) findViewById(R.id.progressBar_get_Interperatatior);
        key = (TextView) findViewById(R.id.word_key);
        pron = (TextView) findViewById(R.id.word_pron);
        def = (TextView) findViewById(R.id.word_def);
        examples = (TextView) findViewById(R.id.examples);
        speaker = (ImageView) findViewById(R.id.word_speaker);

        Typeface mFace = Typeface.createFromAsset(mContext.getAssets(), "fonts/segoeui.ttf");
        pron.setTypeface(mFace);

        add_word = (Button) findViewById(R.id.word_add);
        // 添加到生词本
        add_word.setOnClickListener(v -> saveNewWords(selectCurrWordTemp));
        close_word = (Button) findViewById(R.id.word_close);
        close_word.setOnClickListener(arg0 -> WordCard.this.setVisibility(View.GONE));
    }

    /**
     * 获取单词释义
     */
    private void getNetworkInterpretation() {
        if (selectText != null && selectText.length() != 0) {
            ClientSession.Instace().asynGetResponse(
                    new DictRequest(selectText), new IResponseReceiver() {

                        @Override
                        public void onResponse(BaseHttpResponse response,
                                               BaseHttpRequest request, int rspCookie) {
                            DictResponse dictResponse = (DictResponse) response;

                            selectCurrWordTemp = dictResponse.newWord;
                            selectCurrWordTemp.id = String.valueOf(UserInfoManager.getInstance().getUserId());

                            if (selectCurrWordTemp != null) {
                                if (selectCurrWordTemp.def != null
                                        && selectCurrWordTemp.def.length() != 0) {
                                    handler.sendEmptyMessage(1);
                                } else {
                                    handler.sendEmptyMessage(2);
                                }
                            } else {
                            }
                        }
                    }, null, null);
        } else {
            CustomToast.showToast(mContext, R.string.play_please_take_the_word,
                    1000);
        }
    }

    public void showWordDefInfo(Handler handler) {
        if (selectCurrWordTemp != null) {
            key.setText(selectCurrWordTemp.word);

            if (selectCurrWordTemp.pron != null
                    && selectCurrWordTemp.pron.length() != 0) {
                pron.setText(Html.fromHtml("[" + selectCurrWordTemp.pron + "]"));
            }else {
                pron.setText("");
            }

            def.setText(selectCurrWordTemp.def);

            examples.setText(Html.fromHtml(selectCurrWordTemp.examples));
            examples.setMovementMethod(ScrollingMovementMethod.getInstance());
            examples.setText(Html.fromHtml(selectCurrWordTemp.examples));

            if (selectCurrWordTemp.audio != null && selectCurrWordTemp.audio.length() != 0) {
                speaker.setVisibility(View.VISIBLE);
            } else {
                speaker.setVisibility(View.GONE);
            }

            speaker.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    if (player == null) {
                        player = new Player(mContext, null);
                    }
                    String url = selectCurrWordTemp.audio;
                    player.playUrl(url);
                }
            });

            main.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (selectCurrWordTemp == null || selectCurrWordTemp.word == null) return;
                    EventBus.getDefault().post(new WordSearchEvent(selectCurrWordTemp.word));

                }
            });
            add_word.setVisibility(View.VISIBLE); // 选词的同事隐藏加入生词本功能

            handler.sendEmptyMessage(0);
        } else {
            CustomToast.showToast(mContext,
                    R.string.no_search_word, 1000);
        }

        progressBar_translate.setVisibility(View.GONE); // 显示等待
    }

    private void saveNewWords(NewWord wordTemp) {
        if (!UserInfoManager.getInstance().isLogin()) {
            LoginUtil.startToLogin(mContext);
        } else {
            try {
                com.iyuba.core.common.sqlite.op.WordOp wordOpLib = new com.iyuba.core.common.sqlite.op.WordOp(mContext);

                Word word = new Word();
                word.userid = String.valueOf(UserInfoManager.getInstance().getUserId());
                word.key = wordTemp.word;
                word.def = wordTemp.def;
                word.pron= wordTemp.pron;
                word.audioUrl = wordTemp.audio;
                wordOpLib.saveData(word);
                CustomToast.showToast(mContext, R.string.play_ins_new_word_success, 1000);
                WordCard.this.setVisibility(View.GONE);
                addNetwordWord(wordTemp.word);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addNetwordWord(String wordTemp) {
        ClientSession.Instace().asynGetResponse(
                new WordUpdateRequest(String.valueOf(UserInfoManager.getInstance().getUserId()),
                        WordUpdateRequest.MODE_INSERT, wordTemp),
                new IResponseReceiver() {
                    @Override
                    public void onResponse(BaseHttpResponse response,
                                           BaseHttpRequest request, int rspCookie) {
                    }
                }, null, null);
    }

    public void searchWord(String word, Context mContext, Handler handler) {
        selectText = word;
        wordHandler = handler;

        int isConnect = NetWorkState.getAPNType();

        if (isConnect == 0) {

            if (ConfigManager.Instance().loadInt("isvip") >= 1) {
                if (wordOp == null) {
                    wordOp = new WordOp(mContext);
                }

                selectCurrWordTemp = wordOp.findData(selectText);
                showWordDefInfo(wordHandler);
            }

            // 请检查网络
            CustomToast.showToast(mContext, R.string.category_check_network, 1000);

        } else {
            getNetworkInterpretation();
        }
    }

    public void setWordCard(Context context) {
        mContext = context;
        wordOp = new WordOp(mContext);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    showWordDefInfo(wordHandler);
                    break;
                case 2:
                    CustomToast.showToast(mContext,
                            R.string.play_no_word_interpretation, 1000);
                    WordCard.this.setVisibility(View.GONE);
                    break;
            }
        }
    };

    public void setWordPlayerStop() {

        if (player != null) {
            player.stop();
        }

    }

    public String getKeyStirng(){
        return key.getText().toString();
    }
    public String getDefStirng(){
        return def.getText().toString();
    }
}
