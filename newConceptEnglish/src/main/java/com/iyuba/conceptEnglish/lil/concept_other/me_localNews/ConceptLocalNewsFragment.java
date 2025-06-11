package com.iyuba.conceptEnglish.lil.concept_other.me_localNews;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.hjq.permissions.XXPermissions;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.VoaAdapter;
import com.iyuba.conceptEnglish.databinding.FragmentLocalNewsConceptBinding;
import com.iyuba.conceptEnglish.event.RefreshBookEvent;
import com.iyuba.conceptEnglish.lil.concept_other.download.FilePathUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_concept;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_conceptDownload;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Collect_chapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Junior_chapter_collect;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlayEvent;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlaySession;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.study.StudyNewActivity;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @title: 新概念的本地内容列表
 * @date: 2023/6/20 14:05
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ConceptLocalNewsFragment extends BaseViewBindingFragment<FragmentLocalNewsConceptBinding> {

    private Context mContext;
    private int localType;// 0 local ; 1 love ; 2 heard
    private List<Voa> voaList;
    private VoaAdapter voaAdapter;
    public boolean isDelStart = false;
    private Voa voa;
    private VoaOp voaOp;
    private VoaDetailOp voaDetailOp;
    private CustomDialog waittingDialog;

    public static ConceptLocalNewsFragment getInstance(int localType){
        ConceptLocalNewsFragment fragment = new ConceptLocalNewsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("localType",localType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();
        localType = getArguments().getInt("localType", localType);

        initWidget();
        init();

        handler.postDelayed(runnable, 1000);// 每兩秒執行一次runnable.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (voaAdapter != null) {
                handler.sendEmptyMessage(7);
            }
            handler.postDelayed(this, 1000);//2秒刷新
        }
    };

    public void initWidget() {
        waittingDialog = WaittingDialog.showDialog(mContext);

        //下面都是两个列表相关的event，动画等等。
        binding.voaList.setFastScrollStyle(R.style.local_list_listview_fastScroll_style);
        binding.voaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                VoaDataManager.Instace().voaTemp = voaList.get(arg2);
                if (isDelStart) {
                    boolean isDelete = voaList.get(arg2).isDelete;
                    voaList.get(arg2).isDelete = !isDelete;

                    handler.sendEmptyMessage(7);
                } else {
                    voa = voaList.get(arg2);
                    handler.sendEmptyMessage(6);
                    handler.sendEmptyMessage(7);
                }
            }
        });

        //设置长按
        if (localType == 1){
            binding.voaList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("取消收藏")
                            .setMessage("是否取消收藏当前文章?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Voa selectVoa = voaList.get(position);
                                    //删除数据并且刷新数据
                                    collectData(selectVoa);
                                }
                            }).setNegativeButton("取消",null)
                            .setCancelable(false)
                            .show();

                    return true;
                }
            });
        }
    }

    public void init() {
        voaOp = new VoaOp(mContext);
        voaDetailOp = new VoaDetailOp(mContext);

        voaList = getData(localType);
        voaAdapter = new VoaAdapter(mContext, voaList);
        binding.voaList.setAdapter(voaAdapter);
    }

    public List<Voa> getData(int type) {
        List<Voa> voaList = null;
        switch (type) {
            case 0://下载
//                if (ConfigManager.Instance().isAmercan()) {
//                    voaList = voaOp.findDataFromDownload();
//                } else {
//                    voaList = searchLocalBritishFile();
//                }
                voaList = getDownloadList();
                break;
            case 1://收藏
//                voaList = voaOp.findDataFromCollection();
                voaList = getCollectList();
                break;
            case 2://阅读
//                voaList = voaOp.findDataFromRead();
                voaList = getReadList();
                break;
        }

        return voaList;
    }

    /*********************************不同类型数据的获取方式***********************************/
    //获取阅读数据
    private List<Voa> getReadList(){
        List<Voa> voaList = new ArrayList<>();

        List<LocalMarkEntity_concept> allReadList = ConceptDataManager.getLocalMarkReadData(UserInfoManager.getInstance().getUserId());
        if (allReadList!=null&&allReadList.size()>0){
            for (int i = 0; i < allReadList.size(); i++) {
                //当前的阅读数据
                LocalMarkEntity_concept curRead = allReadList.get(i);
                //只有阅读为1的才行
                if (curRead.isRead.equals("0")){
                    continue;
                }
                //获取当前的voa数据
                Voa curVoa = voaOp.findDataById(curRead.voaId);
                //合并数据显示
                curVoa.lessonType = curRead.lessonType;
                curVoa.isRead = "1";
                curVoa.position = curRead.position;

                //同时处理下载状态
                String downloadStatus = getVoaDownloadStatus(curVoa);
                String localPath = getLocalSoundPath(curVoa);
                if (!TextUtils.isEmpty(downloadStatus)&&!TextUtils.isEmpty(localPath)){
                    curVoa.isDownload = downloadStatus;
                }else {
                    curVoa.isDownload = "0";
                }

                //处理下收藏状态
                curVoa.isCollect = getVoaCollectStatus(curVoa);

                voaList.add(curVoa);
            }
        }

        return voaList;
    }

    //获取收藏数据
    private List<Voa> getCollectList(){
        List<Voa> tempList = new ArrayList<>();

        //查询数据
        List<LocalMarkEntity_concept> collectList = ConceptDataManager.getLocalMarkCollectData(UserInfoManager.getInstance().getUserId());
        if (collectList!=null&&collectList.size()>0){
            for (int i = 0; i < collectList.size(); i++) {
                //收藏的数据
                LocalMarkEntity_concept curCollectData = collectList.get(i);
                //只有收藏为1的才行
                if (curCollectData.isCollect.equals("0")){
                    continue;
                }
                //当前的数据
                Voa curVoa = voaOp.findDataById(curCollectData.voaId);
                curVoa.lessonType = curCollectData.lessonType;
                curVoa.isCollect = "1";
                curVoa.position = curCollectData.position;

                //同时处理下载状态
                String downloadStatus = getVoaDownloadStatus(curVoa);
                String localPath = getLocalSoundPath(curVoa);
                if (!TextUtils.isEmpty(downloadStatus)&&!TextUtils.isEmpty(localPath)){
                    curVoa.isDownload = downloadStatus;
                }else {
                    curVoa.isDownload = "0";
                }

                //处理下收藏状态
                curVoa.isRead = getVoaReadStatus(curVoa);

                tempList.add(curVoa);
            }
        }

        return tempList;
    }

    //获取下载数据
    private List<Voa> getDownloadList(){
        List<Voa> tempList = new ArrayList<>();

        //获取下载数据
        List<LocalMarkEntity_conceptDownload> downloadList = ConceptDataManager.getLocalMarkDownloadData(UserInfoManager.getInstance().getUserId());
        if (downloadList!=null&&downloadList.size()>0){
            for (int i = 0; i < downloadList.size(); i++) {
                LocalMarkEntity_conceptDownload curDownData = downloadList.get(i);
                //只有下载为1的才行
                if (curDownData.isDownload.equals("0")){
                    continue;
                }
                Voa curVoa = voaOp.findDataById(curDownData.voaId);
                //设置数据
                curVoa.lessonType = curDownData.lessonType;
                curVoa.position = curDownData.position;
                //这里需要判断文件是否存在
                String localPath = getLocalSoundPath(curVoa);
                if (!TextUtils.isEmpty(localPath)){
                    curVoa.isDownload  = curDownData.isDownload;
                }else {
                    //没有文件的就不显示，哪怕在数据库中存在
                    //顺便重置下数据
                    ConceptDataManager.updateLocalMarkDownloadStatus(curVoa.voaId,curVoa.lessonType,UserInfoManager.getInstance().getUserId(), "0",curVoa.position);
                    continue;
                }

                //处理下收藏和阅读状态
                curVoa.isRead = getVoaReadStatus(curVoa);
                curVoa.isCollect = getVoaCollectStatus(curVoa);

                tempList.add(curVoa);
            }
        }
        return tempList;
    }

    //获取单个数据的下载状态
    private String getVoaDownloadStatus(Voa curVoa){
        if (curVoa==null){
            return "0";
        }

        LocalMarkEntity_conceptDownload download = ConceptDataManager.getLocalMarkSingleDownload(curVoa.voaId,curVoa.lessonType,UserInfoManager.getInstance().getUserId());
        if (download!=null&&!TextUtils.isEmpty(download.isDownload)){
            return download.isDownload;
        }else {
            return "0";
        }
    }

    //获取单个数据的收藏状态
    private String getVoaCollectStatus(Voa curVoa){
        if (curVoa==null){
            return "0";
        }

        LocalMarkEntity_concept concept = ConceptDataManager.getLocalMarkSingle(curVoa.voaId,curVoa.lessonType,UserInfoManager.getInstance().getUserId());
        if (concept!=null&&!TextUtils.isEmpty(concept.isCollect)){
            return concept.isCollect;
        }else {
            return "0";
        }
    }

    //获取单个数据的阅读状态
    private String getVoaReadStatus(Voa curVoa){
        if (curVoa==null){
            return "0";
        }

        LocalMarkEntity_concept concept = ConceptDataManager.getLocalMarkSingle(curVoa.voaId,curVoa.lessonType,UserInfoManager.getInstance().getUserId());
        if (concept!=null&&!TextUtils.isEmpty(concept.isRead)){
            return concept.isRead;
        }else {
            return "0";
        }
    }

    //获取当前章节的音频本地路径
    private String getLocalSoundPath(Voa curVoa) {
        String localPath = "";

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            return localPath;
        }

        //这里不获取当前的数据，而是获取数据中的类型
        /*switch (curVoa.lessonType) {
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptJunior:
            default:
                // 美音原文音频的存放路径
                String pathString = Constant.videoAddr + curVoa.voaId + Constant.append;
                File fileTemp = new File(pathString);
                if (fileTemp.exists()) {
                    localPath =  pathString;
                }
                break;
            case TypeLibrary.BookType.conceptFourUK:
                // 英音原文音频的存放路径
                String pathStringEng = Constant.videoAddr + curVoa.voaId + "_B" + Constant.append;
                File fileTempEng = new File(pathStringEng);
                if (fileTempEng.exists()) {
                    localPath = pathStringEng;
                }
                break;
        }*/
        //更换地址
        String pathString = FilePathUtil.getHomeAudioPath(curVoa.voaId,curVoa.lessonType);
        File file = new File(pathString);
        if (file.exists()){
            localPath = pathString;
        }

        return localPath;
    }

    //保存云端收藏数据到本地
    private void collectRemoteCollectToLocal(Junior_chapter_collect collect){
        //这里保存在本地数据库中
        //部分数据需要处理下
        String lessonType = TypeLibrary.BookType.conceptFourUS;
        int collectVoaId = Integer.parseInt(collect.getVoaid());
        if (collectVoaId>40000){
            //青少版
            lessonType = TypeLibrary.BookType.conceptJunior;
        }else if (collectVoaId>10000){
            //英音
            collect.setVoaid(String.valueOf(collectVoaId/10));
            lessonType = TypeLibrary.BookType.conceptFourUK;
        }else {
            lessonType = TypeLibrary.BookType.conceptFourUS;
        }
        ConceptDataManager.updateLocalMarkCollectStatus(Integer.parseInt(collect.getVoaid()),lessonType,UserInfoManager.getInstance().getUserId(),"1",getCurVoaInPosition(Integer.parseInt(collect.getVoaid())));
    }

    //获取当前数据在对应数据中到位置
    private int getCurVoaInPosition(int voaId){
        //先从数据库中查询所有的数据
        //tnnd的，返回的数据中好没有bookId信息，还需要从本地查询出来
        Voa tempVoa = voaOp.findDataById(voaId);
        //获取当前书籍下的集合数据
        List<Voa> tempList = voaOp.findDataByBook(tempVoa.category);
        for (int i = 0; i < tempList.size(); i++) {
            if (tempList.get(i).voaId == voaId){
                return i;
            }
        }
        return 0;
    }

    /*private List<Voa> searchLocalBritishFile() {
        List<Voa> voaList = new ArrayList<>();
        List<Integer> voaIdList = new ArrayList<>();
        File dir = new File(Constant.videoAddr);
        File[] files = dir.listFiles();

        if (files!=null){
            for (File file : files) {
                String fileName = file.getName();
                *//* 英音均携带下划线，以此区分 *//*
                int removePoint = fileName.indexOf("_");
                if (removePoint != -1) {
                    fileName = fileName.substring(0, removePoint);
                    voaIdList.add(Integer.parseInt(fileName));
                }
            }

            Collections.sort(voaIdList, Integer::compareTo);
        }

        for (int voaId : voaIdList) {
            Voa voa = voaOp.findDataById(voaId);
            voaList.add(voa);
        }

        return voaList;
    }*/

    public void deleteData(int type, Voa voa) {
        switch (type) {
            case 0:
//                DownloadStateManager.instance().delete(voa.voaId);
//                voaOp.deleteDataInDownload(voa.voaId);
                ConceptDataManager.updateLocalMarkDownloadStatus(voa.voaId,voa.lessonType,UserInfoManager.getInstance().getUserId(), "0",voa.position);
                //删除对应的本地数据
                String localPath = getLocalSoundPath(voa);
                if (XXPermissions.isGranted(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE
//                        ,Manifest.permission.MANAGE_EXTERNAL_STORAGE
                )){
                    File file = new File(localPath);
                    file.delete();
                }
                break;
            case 1:
//                for (Voa voaTemp : VoaDataManager.Instace().voasTemp) {
//                    if (voaTemp.voaId == voa.voaId) {
//                        voaTemp.isCollect = "0";
//                    }
//                }
//                voaOp.deleteDataInCollection(voa.voaId);
                ConceptDataManager.updateLocalMarkCollectStatus(voa.voaId,voa.lessonType,UserInfoManager.getInstance().getUserId(),"0",voa.position);
                break;
            case 2:
//                for (Voa voaTemp : VoaDataManager.Instace().voasTemp) {
//                    if (voaTemp.voaId == voa.voaId) {
//                        voaTemp.isRead = "0";
//                    }
//                }
//                voaOp.deleteDataInRead(voa.voaId);
                ConceptDataManager.updateLocalMarkReadStatus(voa.voaId,voa.lessonType,UserInfoManager.getInstance().getUserId(),"0",voa.position);
                break;
        }
    }

    //	以上均为两个个list切换及相关按键逻辑
    public void changeItemDeleteStart(boolean isDelete) {
        if (voaAdapter != null) {
            voaAdapter.modeDelete = isDelete;
            handler.sendEmptyMessage(7);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    waittingDialog.dismiss();
                    break;
                case 1:
                    waittingDialog.show();
                    break;
                case 2:
                    handler.sendEmptyMessage(1);
                    List<Voa> tempVoaList = voaOp.findUnSynchroData();
                    if (tempVoaList != null && localType == 1) {
                        Message message = null;
                        for (Voa tempVoa : tempVoaList) {
                            message = handler.obtainMessage();
                            message.what = 3;
                            message.arg1 = tempVoa.voaId;
                            message.arg2 = Integer.valueOf(tempVoa.isCollect);
                            handler.sendMessageDelayed(message, 1500);
                        }
                    }

                    // TODO: 2023/6/21 修改成下面的接口
                    /*ExeProtocol.exe(
                            new FavorSynRequest(
                                    AccountManager.Instance(mContext).userId),
                            new ProtocolResponse() {
                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    FavorSynResponse response = (FavorSynResponse) bhr;
                                    if (response.list != null
                                            && response.list.size() != 0) {
                                        for (int voaid : response.list) {
                                            voaOp.updateSynchro(voaid, 1);
                                            voaOp.insertDataToCollection(voaid);
                                        }
                                        handler.sendEmptyMessage(9);
                                        handler.sendEmptyMessageDelayed(0, 1000);
                                    } else {
                                        handler.sendEmptyMessage(0);
                                        handler.sendEmptyMessage(5);
                                        handler.sendEmptyMessage(7);
                                    }
                                }

                                @Override
                                public void error() {
                                    handler.sendEmptyMessage(0);
                                    handler.sendEmptyMessage(4);
                                }
                            });*/

                    ConceptDataManager.getArticleCollect(TypeLibrary.BookType.conceptFour,UserInfoManager.getInstance().getUserId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<BaseBean_data<List<Junior_chapter_collect>>>() {
                                @Override
                                public void accept(BaseBean_data<List<Junior_chapter_collect>> bean) throws Exception {
                                    if (bean!=null&&bean.getResult().equals("1")&&bean.getData()!=null&&bean.getData().size()>0){
                                        for (int i = 0; i < bean.getData().size(); i++) {
                                            Junior_chapter_collect collect = bean.getData().get(i);
                                            int voaId = Integer.parseInt(collect.getVoaid());
                                            voaOp.updateSynchro(voaId, 1);
                                            voaOp.insertDataToCollection(voaId);

                                            //保存在本地
                                            collectRemoteCollectToLocal(collect);
                                        }
                                        handler.sendEmptyMessage(9);
                                        handler.sendEmptyMessageDelayed(0, 1000);
                                    }else {
                                        handler.sendEmptyMessage(0);
                                        handler.sendEmptyMessage(5);
                                        handler.sendEmptyMessage(7);
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    handler.sendEmptyMessage(0);
                                    handler.sendEmptyMessage(4);
                                }
                            });
                    
                    handler.sendEmptyMessage(0);
                    break;
                case 3:
                    final int voaid = msg.arg1;
                    final int typeId = msg.arg2;

                    // TODO: 2023/6/21 替换下面的方法
                    /*String type = (typeId == 1) ? "insert" : "del";
                    ExeProtocol.exe(
                            new FavorUpdateRequest(
                                    AccountManager.Instance(mContext).userId, voaid, type),
                            new ProtocolResponse() {
                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    voaOp.updateSynchro(voaid, 1);
                                    handler.sendEmptyMessage(7);
                                }

                                @Override
                                public void error() {
                                }
                            });*/

                    boolean isCollect = typeId == 1;
                    ConceptDataManager.collectArticle(TypeLibrary.BookType.conceptFour,String.valueOf(UserInfoManager.getInstance().getUserId()),String.valueOf(voaid),isCollect)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Collect_chapter>() {
                                @Override
                                public void accept(Collect_chapter collect_chapter) throws Exception {
                                    voaOp.updateSynchro(voaid, 1);
                                    handler.sendEmptyMessage(7);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {

                                }
                            });
                    break;
                case 4:
                    CustomToast.showToast(mContext, R.string.please_check_network, 1000);
                    break;
                case 5:
                    CustomToast.showToast(mContext, R.string.newslist_synchro_success, 1000);
                    break;
                case 6:
                    switch (localType) {
                        case 0: //下载
                            VoaDataManager.Instace().setPlayLocalType(1);
                            VoaDataManager.Instace().voasTemp = voaList;
                            break;
                        case 1: //收藏
                            VoaDataManager.Instace().setPlayLocalType(2);
                            VoaDataManager.Instace().voasTemp = voaList;
                            break;
                        case 2://试听
                            VoaDataManager.Instace().setPlayLocalType(3);
                            VoaDataManager.Instace().voasTemp = voaList;
                            break;
                    }
                    getTextDetail(voa);
                    break;
                case 7:
                    voaAdapter.notifyDataSetChanged();
                    break;
                case 8:
                    CustomToast.showToast(mContext, "删除成功", 1000);
                    break;
                case 9:
                    voaList.clear();
                    voaList.addAll(voaOp.findDataFromCollection());
                    handler.sendEmptyMessage(7);
                    break;
                default:
                    break;
            }
        }
    };

    public void getTextDetail(final Voa voa) {
        handler.sendEmptyMessage(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //从本地数据库中查找
                VoaDataManager.Instace().voaTemp = voa;
                VoaDataManager.Instace().voaDetailsTemp = voaDetailOp.findDataByVoaId(voa.voaId);

                //设置为临时数据
                ConceptBgPlaySession.getInstance().setTempData(true);
                //关闭底部的控制栏
                EventBus.getDefault().post(new ConceptBgPlayEvent(ConceptBgPlayEvent.event_control_hide));

                if (VoaDataManager.Instace().voaDetailsTemp != null && VoaDataManager.Instace().voaDetailsTemp.size() != 0) {
                    VoaDataManager.Instace().setSubtitleSum(voa, VoaDataManager.Instace().voaDetailsTemp);
                    Intent intent = new Intent();
                    intent.setClass(mContext, StudyNewActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }

    /*****************回退操作*******************/
    public void onBackShowDialog(){
        ((LocalNewsActivity)getActivity()).setEditBtn(true);
        if (voaAdapter != null) {
            isDelStart = true;
            changeItemDeleteStart(true);
        }
    }

    /*****************辅助功能*****************/
    //设置编辑操作
    public void setEdit(boolean isDelete){
        if (!isDelete){
            changeItemDeleteStart(false);
            return;
        }

        if (isDelStart) {
            Iterator<Voa> iteratorVoa = voaList.iterator();
            while (iteratorVoa.hasNext()) {
                Voa voaTemp = iteratorVoa.next();

                if (voaTemp.isDelete) {
                    iteratorVoa.remove();
                    deleteData(localType, voaTemp);

                    if (localType == 1) {
                        voaOp.updateSynchro(voaTemp.voaId, 0);
                        handler.sendEmptyMessage(3);
                    }
                }
            }

            //如果是0，则刷新下首页的显示
            if (localType == 0){
                EventBus.getDefault().post(new RefreshBookEvent());
            }

            handler.sendEmptyMessage(7);

            isDelStart = false;
            ((LocalNewsActivity)getActivity()).setEditBtn(false);
            changeItemDeleteStart(false);
        } else {
            ((LocalNewsActivity)getActivity()).setEditBtn(true);
            if (voaAdapter != null) {
                isDelStart = true;
                changeItemDeleteStart(true);
            }
        }
    }

    //设置同步操作
    public void setSync(){
        if (UserInfoManager.getInstance().isLogin()) {
            handler.sendEmptyMessage(2);
        } else {
            LoginUtil.startToLogin(mContext);
        }
    }

    /**********************************接口**************************/
    //收藏/取消收藏
    private void collectData(Voa selectVoa){
        ConceptDataManager.collectArticle(TypeLibrary.BookType.conceptFour,String.valueOf(UserInfoManager.getInstance().getUserId()),String.valueOf(selectVoa.voaId),false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Collect_chapter>() {
                    @Override
                    public void accept(Collect_chapter bean) throws Exception {
                        if (bean!=null&&bean.msg.equals("Success")){
                            //删除数据
                            voaOp.deleteDataInCollection(selectVoa.voaId);
                            //删除对应的数据
                            deleteData(1,selectVoa);
                            //刷新数据
                            voaList = getData(localType);
                            voaAdapter.setmList(voaList);
                            voaAdapter.notifyDataSetChanged();
                        }else {
                            ToastUtil.showToast(getActivity(),"操作失败，请重试～");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtil.showToast(getActivity(),"操作失败，请重试～");
                    }
                });
    }
}
