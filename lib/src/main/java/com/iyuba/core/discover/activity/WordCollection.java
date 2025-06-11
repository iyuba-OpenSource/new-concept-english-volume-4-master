//package com.iyuba.core.discover.activity;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.iyuba.configation.ConfigManager;
//import com.iyuba.core.common.base.BasisActivity;
//import com.iyuba.core.common.base.CrashApplication;
//import com.iyuba.core.common.listener.ProtocolResponse;
//import com.iyuba.core.common.network.ClientSession;
//import com.iyuba.core.common.network.IResponseReceiver;
//import com.iyuba.core.common.protocol.BaseHttpRequest;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.news.WordSynRequest;
//import com.iyuba.core.common.protocol.news.WordSynResponse;
//import com.iyuba.core.common.protocol.news.WordUpdateRequest;
//import com.iyuba.core.common.sqlite.mode.Word;
//import com.iyuba.core.common.sqlite.op.WordOp;
//import com.iyuba.core.common.util.ExeProtocol;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.discover.adapter.WordListAdapter;
//import com.iyuba.core.event.WordSearchEvent;
//import com.iyuba.core.lil.user.UserInfoManager;
//import com.iyuba.lib.R;
//import com.iyuba.lib.R2;
//import com.scwang.smartrefresh.layout.SmartRefreshLayout;
//import com.scwang.smartrefresh.layout.api.RefreshLayout;
//import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
//import com.scwang.smartrefresh.layout.header.ClassicsHeader;
//import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.util.ArrayList;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
///**
// * 单词本界面
// *
// * @author chentong
// * @version 1.0
// */
//
//public class WordCollection extends BasisActivity {
//    private Context mContext;
//    private ArrayList<Word> words;
//    private WordOp wo;
//    private WordListAdapter nla;
//    private ImageView back;
//
//    private Boolean isLastPage = false;
//    private CustomDialog wettingDialog;
//
//    @BindView(R2.id.relativeLayout_title)
//    RelativeLayout reTitle;
//
//
//    @BindView(R2.id.tv_word_statistic)
//    TextView tvWordNum;
//
//    @BindView(R2.id.tv_word_edit)
//    TextView tvWordEdit;
//
//    //设置操作
//    @BindView(R2.id.tv_word_set)
//    TextView tvWordSet;
//    //pdf下载
//    @BindView(R2.id.tv_word_pdf)
//    TextView tvPdfDownload;
//
//    private ListView wordList;
//    private SmartRefreshLayout refreshLayout;
//
//
//    private int page = 1, pageCounts = 30;
//
//    private WordPdfExport wordPdfExport;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.word_collection_list);
//        CrashApplication.getInstance().addActivity(this);
//        mContext = this;
//        wordPdfExport = new WordPdfExport(mContext);
//        wettingDialog = WaittingDialog.showDialog(mContext);
//
//        ButterKnife.bind(this);
//
//        wordList = findViewById(R.id.list);
//        back = findViewById(R.id.button_back);
//        back.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                onBackPressed();
//            }
//        });
//
//
//        wo = new WordOp(this);
//        getWords();
//        nla = new WordListAdapter(this);
//        wordList.setAdapter(nla);
//        if (words != null) {
//            nla.setData(words);
//            handler.sendEmptyMessage(0);
//        } else {
//            words = new ArrayList<>();
//        }
//
//        //更新单词功能显示
//        updateWordSettingVisible();
//
//        wordList.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1,
//                                    int arg2, long arg3) {
//                if (nla != null) {
//                    if (!nla.modeDelete)
//                        EventBus.getDefault().post(new WordSearchEvent(words.get(arg2).key));
//                    else {
//                        words.get(arg2).isDelete = !words.get(arg2).isDelete;
//                        nla.notifyDataSetChanged();
//                    }
//                }
//            }
//
//        });
//        wordList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
//                if (nla != null && !nla.modeDelete) {
//
//                    new AlertDialog.Builder(mContext)
//                            .setTitle("提示")
//                            .setMessage("确认删除\"" + words.get(position).key + "\"吗？")
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    dialogInterface.dismiss();
//                                }
//                            })
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    dialogInterface.dismiss();
//                                    wo.tryToDeleteItemWord(words.get(position).key, String.valueOf(UserInfoManager.getInstance().getUserId()));
//                                    Message message = new Message();
//                                    message.what = 9;
//                                    message.obj = words.get(position).key;
//                                    handler.sendMessage(message);
//                                }
//                            })
//                            .create().show();
//
//                }
//                return true;
//            }
//        });
//        if (nla != null && nla.modeDelete) {
//            return;
//        }
//        handler.sendEmptyMessage(5);
//
//        //根据包名进行判断(样式)
//        if (getPackageName().equals("com.iyuba.conceptStory")
//                ||getPackageName().equals("com.iyuba.nce")){
//            tvPdfDownload.setTextColor(Color.parseColor("#5468FF"));
//            tvWordSet.setTextColor(Color.parseColor("#5468FF"));
//        }
//    }
//
//    @OnClick(R2.id.tv_word_set)
//    void setClick() {
//        startActivityForResult(new Intent(WordCollection.this, WordSetActivity.class), 1);
//    }
//
//    @OnClick(R2.id.tv_word_edit)
//    void deleteWords() {
//        if (nla != null) {
//            if (nla.modeDelete) {
//                //删除按钮
//                for (Word word : words) {
//                    if (word.isDelete) {
//                        wo.tryToDeleteItemWord(word.key, String.valueOf(UserInfoManager.getInstance().getUserId()));
//                        Message message = new Message();
//                        message.what = 9;
//                        message.obj = word.key;
//                        handler.sendMessage(message);
//                    }
//                }
//                nla.modeDelete = false;
//                tvWordEdit.setText("编辑");
//                nla.notifyDataSetChanged();
//            } else {
//                //编辑
//                nla.modeDelete = true;
//                nla.notifyDataSetInvalidated();
//                tvWordEdit.setText("删除");
//            }
//        }
//
//    }
//
//
//    @OnClick(R2.id.tv_word_pdf)
//    void pdfExportClick() {
//        if (words.size() > 0) {
//            wordPdfExport.getPDFResult(String.valueOf(UserInfoManager.getInstance().getUserId()), page, pageCounts);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        handler.removeCallbacksAndMessages(null);
//    }
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    tvWordNum.setText(String.format("共%d个单词", words.size()));
//                    nla.notifyDataSetChanged();
//                    break;
//                case 1:
//                    wettingDialog.show();
//                    break;
//                case 2:
//                    wettingDialog.dismiss();
//                    break;
//                case 3:
//                    CustomToast.showToast(mContext, R.string.check_network);
//                    break;
//                case 4:
//                    page = 1;
//                    if (words != null) {
//                        words.clear();
//                    }
//                    handler.sendEmptyMessage(5);
//                    handler.sendEmptyMessage(1);
//                    break;
//                case 5:
//                    handler.sendEmptyMessage(1);
//                    ExeProtocol.exe(new WordSynRequest(String.valueOf(UserInfoManager.getInstance().getUserId()), pageCounts, page),
//                            new ProtocolResponse() {
//                                @Override
//                                public void finish(BaseHttpResponse bhr) {
//                                    // TODO Auto-generated method stub
//                                    WordSynResponse wsr = (WordSynResponse) bhr;
//                                    words.clear();
//                                    words.addAll(wsr.wordList);
//                                    if (words != null && words.size() > 0) {
//                                        isLastPage = (page == wsr.lastPage);
//                                        wo.saveData(wsr.wordList);
//                                        getWords();
//                                        nla.setData(words);
//                                        handler.sendEmptyMessage(0);
//                                        handler.sendEmptyMessage(2);
//                                    } else {
//                                        handler.sendEmptyMessage(2);
//                                        handler.sendEmptyMessage(7);
//                                    }
//
//
//                                    //显示功能
//                                    updateWordSettingVisible();
//                                }
//
//                                @Override
//                                public void error() {
//                                    // TODO Auto-generated method stub
//                                    handler.sendEmptyMessage(3);
//                                    handler.sendEmptyMessage(2);
//                                }
//                            });
//                    break;
////                case 6:
////                    handler.sendEmptyMessage(2);
////                    break;
//                case 7:
//                    CustomToast.showToast(mContext, R.string.word_no_data);
//                    nla.notifyDataSetChanged();
//                    break;
//                case 8:
//                    CustomToast.showToast(mContext, R.string.word_add_all);
//                    break;
//                case 9:
//                    if (msg.obj != null) {
//                        wettingDialog.show();
//
//                        ClientSession.Instace().asynGetResponse(
//                                new WordUpdateRequest(String.valueOf(UserInfoManager.getInstance().getUserId()),
//                                        WordUpdateRequest.MODE_DELETE,
//                                        msg.obj.toString()), new IResponseReceiver() {
//                                    @Override
//                                    public void onResponse(BaseHttpResponse response,
//                                                           BaseHttpRequest request, int rspCookie) {
//                                        handler.sendEmptyMessage(2);
//                                        wo.deleteItemWord(String.valueOf(UserInfoManager.getInstance().getUserId()));
//                                        words = (ArrayList<Word>) wo.findDataByAll(String.valueOf(UserInfoManager.getInstance().getUserId()));
//                                        //老哥，这里没数据就不要刷新了，咋没有进行空值判断呢
//                                        if (words==null){
//                                            words = new ArrayList<>();
//                                        }
//                                        nla.setData(words);
//                                        handler.sendEmptyMessage(0);
//
//                                        //这里没有数据的话，就把按钮和设置给隐藏掉
//                                        updateWordSettingVisible();
//                                    }
//                                });
//                    }
//                    break;
//            }
//        }
//    };
//
//    @Override
//    public void onBackPressed() {
//        if (nla != null && nla.modeDelete) {
//            for (Word word : words) {
//                word.isDelete = false;
//            }
//            nla.modeDelete = false;
//            tvWordEdit.setText("编辑");
//            nla.notifyDataSetChanged();
//        } else {
//            super.onBackPressed();
//        }
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            getWords();
//
//            if (words!=null&&words.size()>0){
//                nla.setData(words);
//                nla.notifyDataSetChanged();
//            }
//        }
//    }
//
//    private void getWords() {
//        int sortFlag = ConfigManager.Instance().getWordSort();
//        switch (sortFlag) {
//            case 0:
//                words = (ArrayList<Word>) wo.findDataByAll(String.valueOf(UserInfoManager.getInstance().getUserId()));
//                break;
//            case 1:
//                words = (ArrayList<Word>) wo.findDataByTime(String.valueOf(UserInfoManager.getInstance().getUserId()));
//                break;
//        }
//    }
//
//    //更新单词功能显示
//    private void updateWordSettingVisible(){
//        //判断单词数据，无数据则不显示功能
//        if (words.size()>0){
//            tvPdfDownload.setVisibility(View.VISIBLE);
//            tvWordSet.setVisibility(View.VISIBLE);
//        }else {
//            tvPdfDownload.setVisibility(View.INVISIBLE);
//            tvWordSet.setVisibility(View.INVISIBLE);
//        }
//    }
//}
