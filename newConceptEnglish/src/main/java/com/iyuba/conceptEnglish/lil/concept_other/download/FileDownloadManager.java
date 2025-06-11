package com.iyuba.conceptEnglish.lil.concept_other.download;

import android.util.Log;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_conceptDownload;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @title: 文件下载管理器（仅用于首页刷新数据显示）
 * @date: 2023/11/8 14:26
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class FileDownloadManager {
    private static final String TAG = "FileDownloadManager";

    private static FileDownloadManager instance;

    public static FileDownloadManager getInstance(){
        if (instance==null){
            synchronized (FileDownloadManager.class){
                if (instance==null){
                    instance = new FileDownloadManager();
                }
            }
        }
        return instance;
    }

    public FileDownloadManager(){
        downloadList = new ArrayList<>();
    }

    //文件暂存列表
    private List<FileDownloadBean> downloadList;
    //当前下载文件的位置
    private int downloadFileIndex = -1;
    //文件下载
    private Disposable downFileDis;
    //是否下载完成
    private boolean isDownloading = false;

    //正在下载的文件存储数据
    private Map<String,FileDownloadBean> downingFileMap = new HashMap<>();

    //下载操作
    public void downloadFile(FileDownloadBean downloadBean){
        //保存需要下载的文件信息
        downingFileMap.put(downloadBean.getFilePath(),downloadBean);

        //先保存
        downloadList.add(downloadBean);
        if (!isDownloading){
            Log.d(TAG, "开始下载");
            keepDownload();
        }
    }

    //关闭下载
    public void stopDownload(){
        RxUtil.unDisposable(downFileDis);
        //删除当前的音频文件
        /*if (downloadFileIndex>=0&&downloadList.size()>downloadFileIndex){
            setFileDownloadStatus(TypeLibrary.FileDownloadStateType.file_no);
            FileDownloadBean downloadBean = downloadList.get(downloadFileIndex);
            File file = new File(downloadBean.getFilePath());
            if (file.exists()){
                file.delete();

                Log.d("文件删除", file.getPath());
            }
        }*/

        //原来的貌似不太对，重新处理下
        for (String key:downingFileMap.keySet()){
            //保存的数据
            FileDownloadBean bean = downingFileMap.get(key);
            //删除数据
            File downloadFile = new File(key);
            if (downloadFile.exists()){
                downloadFile.delete();
            }
            //设置未下载状态
            ConceptDataManager.updateLocalMarkDownloadStatus(bean.getVoaId(),bean.getBookType(),UserInfoManager.getInstance().getUserId(), TypeLibrary.FileDownloadStateType.file_no,bean.getPosition());
        }
    }

    private void keepDownload(){
        Log.d(TAG, "正式下载--"+downloadFileIndex+"---"+downloadList.size());
        RxUtil.unDisposable(downFileDis);
        downloadFileIndex++;
        isDownloading = true;

        if (downloadFileIndex>=downloadList.size()){
            Log.d(TAG, "下载完成--"+downloadFileIndex+"--"+downloadList.size());
            downloadReset();
            return;
        }

        //获取需要的数据
        FileDownloadBean downloadBean = downloadList.get(downloadFileIndex);
        Log.d(TAG, "下载接口--"+downloadBean.getFileUrl());
        //然后开始下载
        CommonDataManager.downloadFile(downloadBean.getFileUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        downFileDis = d;
                    }

                    @Override
                    public void onNext(ResponseBody body) {
                        Log.d(TAG, "保存文件--"+downloadBean.getVoaId());
                        saveFile(body.contentLength(),body.byteStream(),downloadBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "下载错误"+downloadBean.getVoaId());
                        //下一个
                        isDownloading = false;
                        setFileDownloadStatus(TypeLibrary.FileDownloadStateType.file_no);
                        keepDownload();
                    }

                    @Override
                    public void onComplete() {
                        RxUtil.unDisposable(downFileDis);
                    }
                });
    }

    //文件保存操作
    private void saveFile(long fileLength,InputStream inputStream, FileDownloadBean downloadBean){
        try {
            //先判断文件是否存在
            File saveFile = new File(downloadBean.getFilePath());
            if (saveFile.exists()){
                saveFile.delete();
            }
            if (!saveFile.getParentFile().exists()){
                saveFile.getParentFile().mkdirs();
            }
            boolean isFileCreated = saveFile.createNewFile();

            if (isFileCreated){
                byte[] bytes = new byte[1024];
                int len = 0;
                long progressLength = 0;

                OutputStream outputStream = new FileOutputStream(saveFile);

                while ((len = inputStream.read(bytes))!=-1){
                    progressLength+=len;
                    outputStream.write(bytes,0,len);
                    Log.d(TAG, "下载进度--"+progressLength+"/"+fileLength);
                }

                outputStream.flush();
                outputStream.close();
                //保存在数据库中
                setFileDownloadStatus(TypeLibrary.FileDownloadStateType.file_downloaded);
                //从待下载的列表中删除
                if (downingFileMap.get(downloadBean.getFilePath())!=null){
                    downingFileMap.remove(downloadBean.getFilePath());
                }
                //发送信息
                EventBus.getDefault().post(new FileDownloadEvent(FileDownloadEvent.home,downloadBean.getBookType(),downloadBean.getVoaId(),downloadBean.getPosition()));
            }else {
                setFileDownloadStatus(TypeLibrary.FileDownloadStateType.file_no);
            }

            //下一个
            Log.d(TAG, "下载完成，下一个");
        }catch (Exception e){
            //下一个
            Log.d(TAG, "出现错误--"+e.getMessage());
            setFileDownloadStatus(TypeLibrary.FileDownloadStateType.file_no);
        }finally {
            isDownloading = false;
            keepDownload();
        }
    }

    // 最终下载操作
    private void downloadReset(){
        downloadList = new ArrayList<>();
        downloadFileIndex = -1;
        isDownloading = false;
        RxUtil.unDisposable(downFileDis);
    }

    //设置文件下载状态
    public void setFileDownloadStatus(String downloadStatus){
        if (downloadFileIndex>=0 && downloadList.size()>downloadFileIndex){
            FileDownloadBean bean = downloadList.get(downloadFileIndex);
            ConceptDataManager.updateLocalMarkDownloadStatus(bean.getVoaId(),bean.getBookType(),UserInfoManager.getInstance().getUserId(), downloadStatus,bean.getPosition());
        }
    }
}
