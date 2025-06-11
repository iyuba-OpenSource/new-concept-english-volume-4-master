//package com.iyuba.conceptEnglish.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.media.AudioManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.adapter.VoaAdapter;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
//import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
//import com.iyuba.conceptEnglish.manager.DownloadStateManager;
//import com.iyuba.conceptEnglish.manager.VoaDataManager;
//import com.iyuba.conceptEnglish.protocol.FavorSynRequest;
//import com.iyuba.conceptEnglish.protocol.FavorSynResponse;
//import com.iyuba.conceptEnglish.protocol.FavorUpdateRequest;
//import com.iyuba.conceptEnglish.sqlite.mode.Voa;
//import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
//import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
//import com.iyuba.conceptEnglish.study.StudyNewActivity;
//import com.iyuba.configation.Constant;
//import com.iyuba.core.common.activity.login.LoginUtil;
//import com.iyuba.core.common.base.BasisActivity;
//import com.iyuba.core.common.listener.ProtocolResponse;
//import com.iyuba.core.common.protocol.BaseHttpResponse;
//import com.iyuba.core.common.util.ExeProtocol;
//import com.iyuba.core.common.widget.dialog.CustomDialog;
//import com.iyuba.core.common.widget.dialog.CustomToast;
//import com.iyuba.core.common.widget.dialog.WaittingDialog;
//import com.iyuba.core.lil.user.UserInfoManager;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//
//
///**
// * 收藏至本地，本地的新概念 ，对应我的页面的
// * 本地篇目，最爱篇目，历史篇目 跳转的页面
// * 从本地数据库查询数据
// */
//public class LocalNews extends BasisActivity {
//    private Context mContext;
//    private int localType;// 0 local ; 1 love ; 2 heard
//    private TextView titleText;
//    private List<Voa> voaList;
//    private ListView voaListView;
//    private VoaAdapter voaAdapter;
//    private Button backButton, buttonEdit, buttonSyncho;
//    private boolean isDelStart = false;
//    private Voa voa;
//    private VoaOp voaOp;
//    private VoaDetailOp voaDetailOp;
//    private CustomDialog waittingDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.local_list);
//        setVolumeControlStream(AudioManager.STREAM_MUSIC);
//
//        mContext = this;
//        localType = getIntent().getIntExtra("localType", localType);
//
//        initWidget();
//        init();
//
//        handler.postDelayed(runnable, 1000);// 每兩秒執行一次runnable.
//    }
//
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            if (voaAdapter != null) {
//                handler.sendEmptyMessage(7);
//            }
//            handler.postDelayed(this, 1000);//2秒刷新
//        }
//    };
//
//    public void initWidget() {
//        waittingDialog = WaittingDialog.showDialog(mContext);
//
//        titleText = (TextView) findViewById(R.id.title);
//
//        backButton = (Button) findViewById(R.id.button_back);
//        backButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        buttonEdit = (Button) findViewById(R.id.button_edit);
//        buttonEdit.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isDelStart) {
//                    Iterator<Voa> iteratorVoa = voaList.iterator();
//                    while (iteratorVoa.hasNext()) {
//                        Voa voaTemp = iteratorVoa.next();
//
//                        if (voaTemp.isDelete) {
//                            deleteData(localType, voaTemp);
//                            iteratorVoa.remove();
//
//                            if (localType == 1) {
//                                voaOp.updateSynchro(voaTemp.voaId, 0);
//                                handler.sendEmptyMessage(3);
//                            }
//                        }
//                    }
//
//                    handler.sendEmptyMessage(7);
//
//                    isDelStart = false;
//                    buttonEdit.setBackgroundResource(R.drawable.button_edit);
//                    changeItemDeleteStart(false);
//                } else {
//                    buttonEdit.setBackgroundResource(R.drawable.button_edit_finished);
//                    if (voaAdapter != null) {
//                        isDelStart = true;
//                        changeItemDeleteStart(true);
//                    }
//                }
//            }
//        });
//
//        buttonSyncho = (Button) findViewById(R.id.button_syncho);
//        buttonSyncho.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (UserInfoManager.getInstance().isLogin()) {
//                    handler.sendEmptyMessage(2);
//                } else {
//                    LoginUtil.startToLogin(mContext);
//                }
//            }
//        });
//        if (localType != 1) {
//            buttonSyncho.setVisibility(View.GONE);
//        }
//
//        //下面都是两个LIST相关的event，动画等等。
//        voaListView = (ListView) findViewById(R.id.voa_list);
//        voaListView.setFastScrollStyle(R.style.local_list_listview_fastScroll_style);
//        voaListView.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1,
//                                    int arg2, long arg3) {
//                VoaDataManager.Instace().voaTemp = voaList.get(arg2);
//                if (isDelStart) {
//                    boolean isDelete = voaList.get(arg2).isDelete;
//                    voaList.get(arg2).isDelete = !isDelete;
//
//                    handler.sendEmptyMessage(7);
//                } else {
//                    voa = voaList.get(arg2);
//                    handler.sendEmptyMessage(6);
//                    handler.sendEmptyMessage(7);
//                }
//            }
//        });
//
//        setTop(localType);
//    }
//
//    public void setTop(int type) {
//        switch (type) {
//            case 0:
//                titleText.setText(R.string.local_title);
//                break;
//            case 1:
//                titleText.setText(R.string.favor_title);
//                break;
//            case 2:
//                titleText.setText(R.string.read_title);
//                break;
//        }
//    }
//
//    public void init() {
//        voaOp = new VoaOp(mContext);
//        voaDetailOp = new VoaDetailOp(mContext);
//
//        voaList = getData(localType);
//        voaAdapter = new VoaAdapter(mContext, voaList);
//        voaListView.setAdapter(voaAdapter);
//    }
//
//    public List<Voa> getData(int type) {
//        List<Voa> voaList = null;
//        switch (type) {
//            case 0:
//                if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptFourUS)) {
//                    voaList = voaOp.findDataFromDownload();
//                } else {
//                    voaList = searchLocalBritishFile();
//                }
//                Log.e("voaList", voaList.size() + "****");
//                break;
//            case 1:
//                voaList = voaOp.findDataFromCollection();
//                break;
//            case 2:
//                voaList = voaOp.findDataFromRead();
//                break;
//        }
//
//        return voaList;
//    }
//
//    private List<Voa> searchLocalBritishFile() {
//        List<Voa> voaList = new ArrayList<>();
//        List<Integer> voaIdList = new ArrayList<>();
//        File dir = new File(Constant.videoAddr);
//        File[] files = dir.listFiles();
//
//        if (files!=null){
//            for (File file : files) {
//                String fileName = file.getName();
//                /* 英音均携带下划线，以此区分 */
//                int removePoint = fileName.indexOf("_");
//                if (removePoint != -1) {
//                    fileName = fileName.substring(0, removePoint);
//                    voaIdList.add(Integer.parseInt(fileName));
//                }
//            }
//
//            Collections.sort(voaIdList, Integer::compareTo);
//        }
//
//        for (int voaId : voaIdList) {
//            Voa voa = voaOp.findDataById(voaId);
//            voaList.add(voa);
//        }
//
//        return voaList;
//    }
//
//    public void deleteData(int type, Voa voa) {
//        switch (type) {
//            case 0:
//                DownloadStateManager.instance().delete(voa.voaId);
//                voaOp.deleteDataInDownload(voa.voaId);
//                break;
//            case 1:
//                for (Voa voaTemp : VoaDataManager.Instace().voasTemp) {
//                    if (voaTemp.voaId == voa.voaId) {
//                        voaTemp.isCollect = "0";
//                    }
//                }
//                voaOp.deleteDataInCollection(voa.voaId);
//                break;
//            case 2:
//                for (Voa voaTemp : VoaDataManager.Instace().voasTemp) {
//                    if (voaTemp.voaId == voa.voaId) {
//                        voaTemp.isRead = "0";
//                    }
//                }
//                voaOp.deleteDataInRead(voa.voaId);
//                break;
//        }
//    }
//
//    //	以上均为两个个list切换及相关按键逻辑
//    public void changeItemDeleteStart(boolean isDelete) {
//        if (voaAdapter != null) {
//            voaAdapter.modeDelete = isDelete;
//            handler.sendEmptyMessage(7);
//        }
//    }
//
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            switch (msg.what) {
//                case 0:
//                    waittingDialog.dismiss();
//                    break;
//                case 1:
//                    waittingDialog.show();
//                    break;
//                case 2:
//                    handler.sendEmptyMessage(1);
//                    List<Voa> tempVoaList = voaOp.findUnSynchroData();
//                    if (tempVoaList != null && localType == 1) {
//                        Message message = null;
//                        for (Voa tempVoa : tempVoaList) {
//                            message = handler.obtainMessage();
//                            message.what = 3;
//                            message.arg1 = tempVoa.voaId;
//                            message.arg2 = Integer.valueOf(tempVoa.isCollect);
//                            handler.sendMessageDelayed(message, 1500);
//                        }
//                    }
//
//                    ExeProtocol.exe(
//                            new FavorSynRequest(
//                                    String.valueOf(UserInfoManager.getInstance().getUserId())),
//                            new ProtocolResponse() {
//                                @Override
//                                public void finish(BaseHttpResponse bhr) {
//                                    FavorSynResponse response = (FavorSynResponse) bhr;
//                                    if (response.list != null
//                                            && response.list.size() != 0) {
//                                        for (int voaid : response.list) {
//                                            voaOp.updateSynchro(voaid, 1);
//                                            voaOp.insertDataToCollection(voaid);
//                                        }
//                                        handler.sendEmptyMessage(9);
//                                        handler.sendEmptyMessageDelayed(0, 1000);
//                                    } else {
//                                        handler.sendEmptyMessage(0);
//                                        handler.sendEmptyMessage(5);
//                                        handler.sendEmptyMessage(7);
//                                    }
//                                }
//
//                                @Override
//                                public void error() {
//                                    handler.sendEmptyMessage(0);
//                                    handler.sendEmptyMessage(4);
//                                }
//                            });
//                    handler.sendEmptyMessage(0);
//                    break;
//                case 3:
//                    final int voaid = msg.arg1;
//                    final int typeId = msg.arg2;
//                    String type = (typeId == 1) ? "insert" : "del";
//                    ExeProtocol.exe(
//                            new FavorUpdateRequest(
//                                    String.valueOf(UserInfoManager.getInstance().getUserId()), voaid, type),
//                            new ProtocolResponse() {
//                                @Override
//                                public void finish(BaseHttpResponse bhr) {
//                                    voaOp.updateSynchro(voaid, 1);
//                                    handler.sendEmptyMessage(7);
//                                }
//
//                                @Override
//                                public void error() {
//                                }
//                            });
//                    break;
//                case 4:
//                    CustomToast.showToast(mContext, R.string.please_check_network, 1000);
//                    break;
//                case 5:
//                    CustomToast.showToast(mContext, R.string.newslist_synchro_success, 1000);
//                    break;
//                case 6:
//
//                    switch (localType) {
//                        case 0: //下载
//                            VoaDataManager.Instace().setPlayLocalType(1);
//                            VoaDataManager.Instace().voasTemp = voaList;
//                            break;
//                        case 1: //收藏
//                            VoaDataManager.Instace().setPlayLocalType(2);
//                            VoaDataManager.Instace().voasTemp = voaList;
//                            break;
//                        case 2://试听
//                            VoaDataManager.Instace().setPlayLocalType(3);
//                            VoaDataManager.Instace().voasTemp = voaList;
//                            break;
//                    }
//                    getTextDetail(voa);
//                    break;
//                case 7:
//                    voaAdapter.notifyDataSetChanged();
//                    break;
//                case 8:
//                    CustomToast.showToast(mContext, "删除成功", 1000);
//                    break;
//                case 9:
//                    voaList.clear();
//                    voaList.addAll(voaOp.findDataFromCollection());
//                    handler.sendEmptyMessage(7);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    public void setSyschro() {
//        if (localType == 1) {
//            buttonSyncho.setVisibility(View.VISIBLE);
//        } else {
//            buttonSyncho.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    protected void onResume() {
//
//        super.onResume();
//    }
//
//    public void getTextDetail(final Voa voa) {
//        handler.sendEmptyMessage(1);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //从本地数据库中查找
//                VoaDataManager.Instace().voaTemp = voa;
//                VoaDataManager.Instace().voaDetailsTemp = voaDetailOp.findDataByVoaId(voa.voaId);
//                if (VoaDataManager.Instace().voaDetailsTemp != null && VoaDataManager.Instace().voaDetailsTemp.size() != 0) {
//                    VoaDataManager.Instace().setSubtitleSum(voa, VoaDataManager.Instace().voaDetailsTemp);
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, StudyNewActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
//                    handler.sendEmptyMessage(0);
//                }
//            }
//        }).start();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (isDelStart) {
//            buttonEdit.setBackgroundResource(R.drawable.button_edit_finished);
//            if (voaAdapter != null) {
//                isDelStart = true;
//                changeItemDeleteStart(true);
//            }
//        } else {
//            finish();
//        }
//    }
//
//
//}
