package com.iyuba.conceptEnglish.util.download;

import com.blankj.utilcode.util.ZipUtils;

import java.io.File;
import java.util.List;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.IDListener;
import timber.log.Timber;

public class DownloadTask {

    public static final int TYPE_VIDEO = 100 ;
    public static final int TYPE_AUDIO_SENTENCE = 200 ;
    public static final int TYPE_AUDIO_WORD = 300 ;
    public static final int TYPE_PIC = 400;
    public int type ;

    public boolean isFinish;
    onDownloadFinishCallBack callBack ;
    private IDListener idlListener = new  IDListener() {

        @Override
        public void onPrepare() {

        }

        @Override
        public void onStart(String fileName, String realUrl, int fileLength) {
        }

        @Override
        public void onProgress(int progress) {
            Timber.tag("TAG").d("onProgress: %s", mVideoUrl);
        }

        @Override
        public void onStop(int progress) {
            isQuit =true ;
            callBack.finish();
            isFinish = true ;
        }

        @Override
        public void onFinish(File file) {
            // 压缩包 则解压
            if (file.getName().endsWith(".zip")){
                try {
//                    FileUtils.unZipFiles(file.getAbsolutePath(),file.getParentFile().getAbsolutePath());
                    ZipUtils.unzipFile(file,file.getParentFile());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                BaseStorageUtil.renameFile(mDir, file.getName(),file.getName().substring(3));
            }
            Timber.tag("TAG").d("onFinish: %s", mVideoUrl);
            if (callBack!=null)
            callBack.finish();
            isFinish =true ;
            synchronized (downloadManager){
                downloadManager.notifyAll();
            }
        }

        /* 可能是问题所在，导致99%卡住，初步怀疑是notifyAll概率失灵 */
        @Override
        public void onError(int status, String error) {
            Timber.tag("errormessage").d(error);
            Timber.tag("errormessage").d(mVideoUrl);
            synchronized (downloadManager){
                downloadManager.notifyAll();
            }
            isFinish = true ;
        }
    };
    DLManager dlManager ;
    public int voaId ;
    public String mDir;
    public String mVideoUrl;
    public String tmp;
    public  boolean isQuit;
    private List<DownloadTask> downloadManager = null;


    public void dlInit(int type , String mVideoUrl, String mDir, String videoTmpFilename, List<DownloadTask> manager, DLManager dlManager){
        this. type = type ;
        this.mDir = mDir ;
        this.mVideoUrl = mVideoUrl ;
        this.tmp = videoTmpFilename;
        this.downloadManager = manager ;
        this.dlManager = dlManager ;
    }

    public  void  dlStart(onDownloadFinishCallBack callBack){
        this.callBack = callBack ;
        File file = new File(mDir,tmp);
        if (file.exists()) file.delete();
        try {
            dlManager.dlStart(mVideoUrl, mDir, tmp, idlListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface  onDownloadFinishCallBack{
        void finish();
    }

}
