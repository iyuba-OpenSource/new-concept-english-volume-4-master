package com.iyuba.conceptEnglish.study;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.VoaWordAdapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search.NewSearchActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.word.WordShowBottomAdapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.word.conceptWordTrain.ConceptWordTrainActivity;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlaySession;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.protocol.DictRequest;
import com.iyuba.conceptEnglish.protocol.DictResponse;
import com.iyuba.conceptEnglish.protocol.WordUpdateRequest;
import com.iyuba.conceptEnglish.sqlite.mode.NewWord;
import com.iyuba.conceptEnglish.sqlite.op.NewWordOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
import com.iyuba.conceptEnglish.widget.MyListView;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.event.WordSearchEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.activity.NewVipCenterActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class VoaWordFragment extends Fragment {
    public static VoaWordFragment instance;

    private CustomDialog waittingDialog;
    private TextView wordSelf, wordDef, wordPron, wordExamples;
    private ImageView wordSpeaker, wordCollect;
    private Button backTolist, formerWord, nextWord;
    private RelativeLayout wordDetail;

    private MyListView wordsListView = null;
    private List<VoaWord2> voaWords, list_2;
    private VoaWordAdapter wordsAdapter;

    private int curWord = 0;
    private MediaPlayer voiceMediaPlayer;
    private Context mContext;
    private int voaId;
    private int position;//当前在列表中的位置

    private VoaWordOp voaWordOp = new VoaWordOp(mContext);
    private View rootView;

    private RelativeLayout noWordView;
    private RecyclerView bottomView;

    public static VoaWordFragment getInstance(int position){
        VoaWordFragment wordFragment = new VoaWordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(StrLibrary.position,position);
        wordFragment.setArguments(bundle);
        return wordFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.voa_word_list, container, false);
        }

        mContext = getActivity();
        instance = this;
        waittingDialog = WaittingDialog.showDialog(mContext);

        initVoaWords();
        return rootView;
    }



    // 初始化单词生词
    private void initVoaWords() {
        voaId = VoaDataManager.Instace().voaTemp.voaId;
        position = ConceptBgPlaySession.getInstance().getPlayPosition();

        voaWords = voaWordOp.findDataByVoaId(voaId);

        voiceMediaPlayer = new MediaPlayer();
        voiceMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        voiceMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                voiceMediaPlayer.start();
            }
        });

        noWordView = rootView.findViewById(R.id.no_word_view);
        bottomView = rootView.findViewById(R.id.bottomView);
        wordDetail = (RelativeLayout) rootView.findViewById(R.id.word_detail);

        wordSelf = (TextView) rootView.findViewById(R.id.word_self);
        wordDef = (TextView) rootView.findViewById(R.id.word_def);
        wordPron = (TextView) rootView.findViewById(R.id.word_pron);
        wordExamples = (TextView) rootView.findViewById(R.id.word_example);

        wordSpeaker = (ImageView) rootView.findViewById(R.id.word_speak);
        wordCollect = (ImageView) rootView.findViewById(R.id.word_collect);

        backTolist = (Button) rootView.findViewById(R.id.back_tolist);
        formerWord = (Button) rootView.findViewById(R.id.former_voaword);
        nextWord = (Button) rootView.findViewById(R.id.next_voaword);

        wordsListView = rootView.findViewById(R.id.wordslist);

        if (voaWords != null && voaWords.size() > 0) {
            wordsAdapter = new VoaWordAdapter(mContext, voaWords);
            wordsListView.setAdapter(wordsAdapter);

            wordsListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    EventBus.getDefault().post(new WordSearchEvent(voaWords.get(position).word));
                }
            });

            wordSpeaker.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        VoaWord2 word = voaWords.get(curWord);
                        if (word == null || "".equals(word.audio) || word.audio == null)
                            return;
                        voiceMediaPlayer.reset();
                        // 可以是网址
                        voiceMediaPlayer.setDataSource(word.audio);
                        voiceMediaPlayer.prepareAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            wordCollect.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//					CustomToast.showToast(mContext, "正在添加", 1000);

                    VoaWord2 tempVoaWord = voaWords.get(curWord);
                    NewWord word = new NewWord();
                    word.word = tempVoaWord.word;
                    word.audio = tempVoaWord.audio;
                    word.pron = tempVoaWord.pron;
                    word.def = tempVoaWord.def;

                    saveNewWords(word);
                }
            });

            //TODO
            backTolist.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeWordState();
                }
            });

            formerWord.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (curWord == 0) {
                        CustomToast.showToast(mContext, "已是第一个单词", 1000);
                    } else {
                        curWord--;
                        showVoaWordById(curWord);
                    }
                }
            });

            nextWord.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (curWord == voaWords.size() - 1) {
                        CustomToast.showToast(mContext, "已是最后一个单词", 1000);
                    } else {
                        curWord++;
                        showVoaWordById(curWord);
                    }
                }
            });

            wordsListView.setVisibility(View.VISIBLE);
            noWordView.setVisibility(View.GONE);
        } else {
            wordsListView.setVisibility(View.GONE);
            noWordView.setVisibility(View.VISIBLE);
        }

        //增加练习界面
        if (position!=-1){
            initBottomView();
        }
    }

    public boolean changeWordState() {
        if (wordDetail.getVisibility() == View.VISIBLE) {
            wordsListView.setVisibility(View.VISIBLE);
            wordDetail.setVisibility(View.GONE);
            return true;
        } else {
            return false;
        }
    }

    private void saveNewWords(NewWord wordTemp) {
        if (!UserInfoManager.getInstance().isLogin()) {
            LoginUtil.startToLogin(getActivity());
        } else {
            try {
//                wordTemp.id = ConfigManager.Instance().loadString("userId");
                wordTemp.id = String.valueOf(UserInfoManager.getInstance().getUserId());
                NewWordOp newWordOp = new NewWordOp(mContext);
                newWordOp.saveData(wordTemp);

                CustomToast.showToast(mContext, R.string.play_ins_new_word_success, 1000);

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

    private final void showVoaWordById(int order) {
        waittingDialog.show();

        wordSelf.setTextSize(Constant.textSize + 1);
        wordPron.setTextSize(Constant.textSize);
        wordDef.setTextSize(Constant.textSize);
        wordExamples.setTextSize(Constant.textSize);

        try {
            ClientSession.Instace().asynGetResponse(
                    new DictRequest(URLEncoder.encode(
                            voaWords.get(curWord).word,
                            "UTF-8")), new IResponseReceiver() {
                        @Override
                        public void onResponse(BaseHttpResponse response,
                                               BaseHttpRequest request, int rspCookie) {
                            waittingDialog.dismiss();

                            DictResponse rs = (DictResponse) response;
                            if (rs.result != null && rs.result.equals("1")) {
                                voaWords.get(curWord).audio = rs.newWord.audio;
                                voaWords.get(curWord).pron = rs.newWord.pron;

                                handler.sendEmptyMessage(0);
                            }
                        }
                    }, null, null);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    show(curWord);
                    break;
                default:
                    break;
            }
        }
    };

    public final void show(int order) {
        VoaWord2 voaWord = voaWords.get(curWord);

        if (voaWord.pron != null) {
            wordPron.setText(Html.fromHtml("[" + voaWord.pron + "]"));
        }

        if (voaWord.word != null) {
            wordSelf.setText(voaWord.word);
        }

        if (voaWord.def != null) {
            wordDef.setText(voaWord.def);
        }



        if (order == 0) {
            formerWord.setBackgroundResource(R.drawable.former_word_none);
        } else {
            formerWord.setBackgroundResource(R.drawable.former_word);
        }

        if (order == voaWords.size() - 1) {
            nextWord.setBackgroundResource(R.drawable.next_word_none);
        } else {
            nextWord.setBackgroundResource(R.drawable.next_word);
        }
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //增加底部功能
    private void initBottomView(){
        List<Pair<String, Pair<Integer,String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(TypeLibrary.WordTrainType.Train_enToCn,new Pair<>(R.drawable.vector_en2cn,"英汉训练")));
        pairList.add(new Pair<>(TypeLibrary.WordTrainType.Train_cnToEn,new Pair<>(R.drawable.vector_cn2en,"汉英训练")));
        pairList.add(new Pair<>(TypeLibrary.WordTrainType.Word_spell,new Pair<>(R.drawable.vector_spelling,"单词拼写")));
        pairList.add(new Pair<>(TypeLibrary.WordTrainType.Train_listen,new Pair<>(R.drawable.vector_listen,"听力训练")));
        WordShowBottomAdapter bottomAdapter = new WordShowBottomAdapter(getActivity(),pairList);
        GridLayoutManager bottomManager = new GridLayoutManager(getActivity(),pairList.size());
        bottomView.setLayoutManager(bottomManager);
        bottomView.setAdapter(bottomAdapter);
        bottomAdapter.setListener(new OnSimpleClickListener<String>() {
            @Override
            public void onClick(String showType) {
                if (voiceMediaPlayer!=null&&voiceMediaPlayer.isPlaying()){
                    voiceMediaPlayer.pause();
                }

                if (!UserInfoManager.getInstance().isLogin()){
                    LoginUtil.startToLogin(getActivity());
                    return;
                }

                int bookId = ConceptBookChooseManager.getInstance().getBookId();

                //这里需要判断当前章节的位置，前三个章节免费，后面的需要开通会员收费
                if (position<3){
                    ConceptWordTrainActivity.start(getActivity(),showType,VoaDataManager.getInstance().voaTemp.lessonType,String.valueOf(bookId),String.valueOf(voaId));
                }else {
                    if (!UserInfoManager.getInstance().isVip()){
                        new AlertDialog.Builder(getActivity())
                                .setTitle("使用说明")
                                .setMessage("课程前三个为免费课程，后续课程需要开通会员后使用，是否继续使用？")
                                .setPositiveButton("继续使用", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        NewVipCenterActivity.start(getActivity(),NewVipCenterActivity.VIP_APP);
                                    }
                                }).setNegativeButton("暂不使用",null)
                                .setCancelable(false)
                                .show();
                        return;
                    }

                    ConceptWordTrainActivity.start(getActivity(),showType,VoaDataManager.getInstance().voaTemp.lessonType,String.valueOf(bookId),String.valueOf(voaId));
                }
            }
        });
    }

    //刷新单词数据
    public void refreshData(){
        voaId = VoaDataManager.Instace().voaTemp.voaId;
        position = ConceptBgPlaySession.getInstance().getPlayPosition();
        voaWords = voaWordOp.findDataByVoaId(voaId);
        if (voaWords != null && voaWords.size() > 0) {
            wordsAdapter = new VoaWordAdapter(mContext, voaWords);
            wordsListView.setAdapter(wordsAdapter);

            wordsListView.setVisibility(View.VISIBLE);
            noWordView.setVisibility(View.GONE);
        }else {
            wordsListView.setVisibility(View.GONE);
            noWordView.setVisibility(View.VISIBLE);
        }
    }
}
