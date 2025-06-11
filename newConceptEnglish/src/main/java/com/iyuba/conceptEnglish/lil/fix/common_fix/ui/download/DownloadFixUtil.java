package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.download;

import android.util.Log;

import androidx.annotation.NonNull;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 下载功能操作类
 */
public class DownloadFixUtil {

    private static DownloadFixUtil instance;
    public static DownloadFixUtil getInstance(){
        if (instance==null){
            synchronized (DownloadFixUtil.class){
                if (instance==null){
                    instance = new DownloadFixUtil();
                }
            }
        }
        return instance;
    }

    //下载操作
    private Call downloadCall;
    //下载文件
    public void downloadFile(String fileUrl,String savePath,DownloadFixCallback callback){
        if (downloadCall!=null && !downloadCall.isCanceled()){
            callback.onDownloadState(TypeLibrary.FileDownloadStateType.file_otherDownload,0,100);
            return;
        }

        //开始下载
        callback.onDownloadState(TypeLibrary.FileDownloadStateType.file_isDownloading,0,100);

        /**************************使用okhttp******************************/
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .connectTimeout(10,TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(fileUrl)
                .build();
        downloadCall = client.newCall(request);
        downloadCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //关闭操作
                call.cancel();
                //回调数据
                if (callback!=null){
                    callback.onDownloadState(TypeLibrary.FileDownloadStateType.file_downloadFail,0,100);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //保存数据
                File saveFile = new File(savePath);
                try {
                    if (saveFile.exists()){
                        saveFile.delete();
                    }
                    if (!saveFile.getParentFile().exists()){
                        saveFile.getParentFile().mkdirs();
                    }
                    boolean isCreateFile = saveFile.createNewFile();
                    if (isCreateFile){
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        long progressLength = 0;

                        //文件长度
                        long fileSize = response.body().contentLength();

                        OutputStream outputStream = new FileOutputStream(saveFile);
                        InputStream inputStream = response.body().byteStream();

                        while ((len = inputStream.read(bytes))!=-1){
                            progressLength+=len;
                            outputStream.write(bytes,0,len);

                            Log.d("下载进度", "实际进度--"+progressLength+"--"+fileSize);

                            //刷新下载进度
                            if (callback!=null){
                                Log.d("下载进度", "回调进度--"+progressLength);

                                callback.onDownloadState(TypeLibrary.FileDownloadStateType.file_isDownloading,progressLength,fileSize);
                            }
                        }

                        outputStream.flush();
                        outputStream.close();

                        //保存在数据库中
                        if (callback!=null){
                            callback.onDownloadState(TypeLibrary.FileDownloadStateType.file_downloaded,0,100);
                        }
                    }else {
                        if (callback!=null){
                            callback.onDownloadState(TypeLibrary.FileDownloadStateType.file_downloadFail,0,100);
                        }
                    }
                }catch (Exception e){
                    if (saveFile.exists()){
                        saveFile.delete();
                    }
                    if (callback!=null){
                        callback.onDownloadState(TypeLibrary.FileDownloadStateType.file_downloadFail,0,100);
                    }
                }
                //关闭操作
                call.cancel();
            }
        });
    }

    public void stopDownload(){
        if (downloadCall!=null && !downloadCall.isCanceled()){
            downloadCall.cancel();
        }
    }
}
