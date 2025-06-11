//package com.iyuba.conceptEnglish.activity;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.inputmethod.EditorInfo;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.FragmentActivity;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.adapter.SearchWordAdapter;
//import com.iyuba.conceptEnglish.sqlite.mode.RecycleViewItemData;
//import com.iyuba.conceptEnglish.sqlite.mode.Voa;
//import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
//import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
//import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
//import com.iyuba.conceptEnglish.widget.cdialog.CustomToast;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.listener.ProtocolResponse;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.protocol.base.DictRequest;
//import com.iyuba.core.common.protocol.base.DictResponse;
//import com.iyuba.core.common.sqlite.mode.Word;
//import com.iyuba.core.common.util.ToastUtil;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.lil.user.UserInfoManager;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import permissions.dispatcher.NeedsPermission;
//import permissions.dispatcher.OnPermissionDenied;
//import permissions.dispatcher.RuntimePermissions;
//
///**
// * 单词查询界面
// * Created by ivotsm on 2017/2/22.
// */
//@RuntimePermissions
//public class SearchActivity extends FragmentActivity {
//    private Context mContext;
//    private static boolean isNight = false;
//
//    @BindView(R.id.iv_title_back)
//    Button iv_title_back;
//
//    @BindView(R.id.edit_search)
//    EditText edit_search;
//
//    @BindView(R.id.recycleview)
//    RecyclerView recycleview;
//
//    @BindView(R.id.clear)
//    ImageView clear;
//
//    @BindView(R.id.re_empty)
//    RelativeLayout re_empty;
//
//    @BindView(R.id.empty_text)
//    TextView empty_text;
//
//
//    private String keyWord;
//    private List<RecycleViewItemData> list = new ArrayList<>();
//    private SearchWordAdapter searchWordAdapter;
//
//    private CustomDialog mWaittingDialog;
//
//
//    private int searchCurrPages = 1;
//    private Voa curVoa;
//    VoaDetailOp voaDetailOp;
//    private String bellVoaId;
//
//    private List<Voa> searchVoaList = new ArrayList<>();
//    private VoaOp voaOp;
//    private int pageNum = 3;
//
//
//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    try {
//                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    handler.sendEmptyMessage(1);
//
//                    String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
//                    String testmode = "2";
//                    String appid = String.valueOf(Constant.APP_ID);
//
//                    com.iyuba.core.common.util.ExeProtocol.exe(new DictRequest(keyWord, uid, testmode, appid), new ProtocolResponse() {
//                        @Override
//                        public void finish(BaseHttpResponse bhr) {
//                            // TODO Auto-generated method stub
//                            handler.sendEmptyMessage(2);
//                            DictResponse dictResponse = (DictResponse) bhr;
//                            list.clear();
//
//                            Word curWord = dictResponse.word;
//                            if (!"".equals(curWord.key)) {
//                                RecycleViewItemData<Word> itemData0 = new RecycleViewItemData<>();
//                                itemData0.setT(curWord);
//                                itemData0.setDataType(SearchWordAdapter.ITEM_TYPE_WORD);
//                                list.add(itemData0);
//                            }
//
//                            handler.sendEmptyMessage(3);
//                        }
//
//                        @Override
//                        public void error() {
//                            // TODO Auto-generated method stub
//                            list.clear();
//                            handler.sendEmptyMessage(2);
//                            handler.sendEmptyMessage(3);
//                        }
//                    });
//                    break;
//                case 1:
//                    if (mWaittingDialog != null && !mWaittingDialog.isShowing()) {
//                        mWaittingDialog.show();
//                    }
//                    break;
//                case 2:
//                    if (mWaittingDialog != null && mWaittingDialog.isShowing()) {
//                        mWaittingDialog.dismiss();
//                    }
//                    break;
//                case 3:
//                    searchAppointText();
//                    searchSentence();
//                    if (list.size() > 0) {
//                        re_empty.setVisibility(View.GONE);
//                    } else {
//                        re_empty.setVisibility(View.VISIBLE);
//                        empty_text.setText("搜索不到任何内容");
//                    }
//                    searchWordAdapter.notifyDataSetChanged();
//                    break;
//                case 4:
//                    ToastUtil.showToast(mContext, "与服务器连接异常，请稍后再试");
//                    break;
//
//            }
//        }
//    };
//
//    public static void start(Context context,String word){
//        Intent intent = new Intent();
//        intent.setClass(context,SearchActivity.class);
//        intent.putExtra("word",word);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search);
//        mContext = this;
//        ButterKnife.bind(this);
//        mWaittingDialog = WaittingDialog.showDialog(mContext);
//
//        voaOp = new VoaOp(mContext);
//        iv_title_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                //以下方法防止两次发送请求
//                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//                    switch (event.getAction()) {
//                        case KeyEvent.ACTION_UP:
//                            //发送请求
//                            keyWord = edit_search.getText().toString().trim();
//                            if (null == keyWord || "".equals(keyWord)) {
//                                ToastUtil.showToast(mContext, "查询内容不能为空");
//                            } else {
//                                handler.sendEmptyMessage(0);
//                                searchWordAdapter.setKeyWord(keyWord);
//                            }
//
//                            return true;
//                        default:
//                            return true;
//                    }
//
//                }
//                return false;
//            }
//        });
//        recycleview.setLayoutManager(new LinearLayoutManager(mContext));
//        recycleview.addItemDecoration(new DividerItemDecoration(mContext, 1));
//        searchWordAdapter = new SearchWordAdapter(mContext, list);
//        recycleview.setAdapter(searchWordAdapter);
//
//        clear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                edit_search.setText("");
//            }
//        });
//
//        keyWord = getIntent().getStringExtra("word");
//        edit_search.setText(keyWord);
//        if (null == keyWord || "".equals(keyWord)) {
//
//        } else {
//            searchWordAdapter.setKeyWord(keyWord);
//            handler.sendEmptyMessage(0);
//        }
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (searchWordAdapter != null) searchWordAdapter.stopAllPlayer();
//    }
//
//
//    @SuppressLint("NeedOnRequestPermissionsResult")
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        SearchActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
//    }
//
//    public void requstPerssiion() {
//        //录音以及存储权限
//        SearchActivityPermissionsDispatcher.initLocationWithPermissionCheck(this);
//    }
//
//    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO})
//    public void initLocation() {
//    }
//
//    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO})
//    public void locationDenied() {
//        CustomToast.showToast(SearchActivity.this, "录音或存储权限未开启，开启后可正常使用此功能", 1000);
//    }
//
//
//    /**
//     * 查询文章
//     */
//    private void searchAppointText() {
//        List<Voa> searchResult = voaOp.getSearchResult(" " + keyWord + " ");
//        for (int i = 0; i < searchResult.size(); i++) {
////            if (searchResult.get(i).voaId < 2000) {
//            searchVoaList.add(searchResult.get(i));
////            }
//        }
//
//        if (searchVoaList != null && searchVoaList.size() != 0) {
//
//            RecycleViewItemData<Integer> itemDatamore1 = new RecycleViewItemData<>();
//            if (searchVoaList.size() >= 3) {
//                itemDatamore1.setT(SearchWordAdapter.articleMore);
//            } else {
//                itemDatamore1.setT(SearchWordAdapter.article);
//            }
//            itemDatamore1.setDataType(SearchWordAdapter.ITEM_TYPE_MORE);
//            list.add(itemDatamore1);
//            for (int i = 0; i < searchVoaList.size(); i++) {
//                if (i == 3) {
//                    break;
//                }
//                RecycleViewItemData<Voa> itemData0 = new RecycleViewItemData<>();
//                itemData0.setT(searchVoaList.get(i));
//                itemData0.setDataType(SearchWordAdapter.ITEM_TYPE_ARTICLE);
//                list.add(itemData0);
//            }
//        }
//    }
//
//    /**
//     * 查询句子
//     */
//    private void searchSentence() {
//        VoaDetailOp voaDetailOp = new VoaDetailOp(mContext);
//        List<VoaDetail> voaDetailList = voaDetailOp.findDataByKey(" " + keyWord + " ");
//
//        if (voaDetailList != null && voaDetailList.size() > 0) {
//            RecycleViewItemData<Integer> itemDatamore1 = new RecycleViewItemData<>();
//            if (voaDetailList.size() >= 3) {
//                itemDatamore1.setT(SearchWordAdapter.sentencMore);
//            } else {
//                itemDatamore1.setT(SearchWordAdapter.sentence);
//            }
//            itemDatamore1.setDataType(SearchWordAdapter.ITEM_TYPE_MORE);
//            list.add(itemDatamore1);
//
//            for (int i = 0; i < voaDetailList.size(); i++) {
//                if (i == 3) {
//                    break;
//                }
//                RecycleViewItemData<VoaDetail> itemData0 = new RecycleViewItemData<>();
//                VoaDetail voaDetail = voaDetailList.get(i);
//                voaDetail.setRealIndex(i + 1);
//                itemData0.setT(voaDetail);
//                itemData0.setDataType(SearchWordAdapter.ITEM_TYPE_SENTENCE);
//                list.add(itemData0);
//            }
//        }
//    }
//}
