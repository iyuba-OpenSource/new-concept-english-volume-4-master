package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.download;

import android.content.Context;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.core.common.util.StorageUtil;
import com.iyuba.core.common.util.VoaMediaUtil;
import com.iyuba.core.event.DownloadEvent;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.dlex.bizs.DLTaskInfo;
import com.iyuba.dlex.interfaces.IDListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * @title: 口语秀下载功能
 * @date: 2023/6/6 19:08
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingDownloadPresenter {

    public static final String AUDIO_CATEGORY_AND_TAG = "talkshow_audio_";
    public static final String VIDEO_CATEGORY_AND_TAG = "talkshow_video_";
    public static final String ERROR_CATEGORY_AND_TAG = "talkshow_error_";

    private static final long SHOW_PEROID = 500;

    //本地的存储路径
    private String mDir;
    private String mVideoUrl;
    private String mMediaUrl;
    private DLTaskInfo mVideoTask;
    private DLTaskInfo mMediaTask;
    private BookChapterBean chapterBean;

    private Timer mMsgTimer;
    private String mMsg;
    private Context mContext;

    public void init(Context context,BookChapterBean chapterBean){
        this.mContext = context;
        this.chapterBean = chapterBean;
    }

    //设置下载链接和文件夹路径
    public void setUrlAndFolder(String audioUrl,String videoUrl){
        this.mMediaUrl = audioUrl;
        this.mVideoUrl = videoUrl;

        mDir = StorageUtil
                .getMediaDir(mContext, Integer.parseInt(chapterBean.getVoaId()))
                .getAbsolutePath()+"/"+chapterBean.getTypes();
    }

    /*************************音视频下载回调*********************/
    private IDListener mVideoListener = new IDListener() {
        private int mFileLength = 0;

        @Override
        public void onPrepare() {

        }

        @Override
        public void onStart(String fileName, String realUrl, int fileLength) {
            this.mFileLength = fileLength;
        }

        @Override
        public void onProgress(int progress) {
            if (mFileLength != 0) {
                Double v = ((double) progress / (double) mFileLength) * 100;
                DecimalFormat df = new DecimalFormat("#.00");
//                Timber.e("下载字节数____________"+progress+"总字节数____________"+mFileLength);
                long percent = (long) progress / mFileLength;
                if (mContext != null) {
                    mMsg = MessageFormat.format(mContext.getString(com.iyuba.lib.R.string.video_loading_tip), df.format(v));
                    Timber.e("下载字" + mMsg + "percent" + percent);
                }
            }
        }

        @Override
        public void onStop(int progress) {
            mMsgTimer.cancel();
        }

        @Override
        public void onFinish(File file) {
            StorageUtil.renameVideoFile(mDir, Integer.parseInt(chapterBean.getVoaId()));
            if (StorageUtil.checkFileExist(mDir, Integer.parseInt(chapterBean.getVoaId()))) {
                mMsgTimer.cancel();
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, Integer.parseInt(chapterBean.getVoaId())));
                //addDownload(); 下载数据库记录
            }
        }

        @Override
        public void onError(int status, String error) {
            mMsgTimer.cancel();
        }
    };
    private IDListener mMediaListener = new IDListener() {
        private int mFileLength = 0;

        @Override
        public void onPrepare() {

        }

        @Override
        public void onStart(String fileName, String realUrl, int fileLength) {
            this.mFileLength = fileLength;
        }

        @Override
        public void onProgress(int progress) {
            if (mFileLength != 0) {
                int percent = progress * 100 / mFileLength;
                if (mContext != null) {
                    mMsg = MessageFormat.format(mContext.getString(com.iyuba.lib.R.string.media_loading_tip), percent);
                }
            }
        }

        @Override
        public void onStop(int progress) {
            mMsgTimer.cancel();
        }

        @Override
        public void onFinish(File file) {
            StorageUtil.renameAudioFile(mDir, Integer.parseInt(chapterBean.getVoaId()));
            if (StorageUtil.checkFileExist(mDir, Integer.parseInt(chapterBean.getVoaId()))) {
                mMsgTimer.cancel();
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, Integer.parseInt(chapterBean.getVoaId())));
                //addDownload();
            }

            //下载视频
            if (!StorageUtil.isVideoExist(mDir, Integer.parseInt(chapterBean.getVoaId()))) {
                downloadVideo();
            }
        }

        @Override
        public void onError(int status, String error) {
            String mUrl;
            mUrl = VoaMediaUtil.getAudioErrorUrl("/202002/313116.mp3");

            DLTaskInfo taskv = new DLTaskInfo();
            taskv.tag = ERROR_CATEGORY_AND_TAG + chapterBean.getVoaId();
            taskv.filePath = mDir;
            taskv.fileName = mUrl.substring(mUrl.lastIndexOf("/"));
            taskv.initalizeUrl(mUrl);
            taskv.category = ERROR_CATEGORY_AND_TAG;
            taskv.setDListener(mMediaListener);
            DLManager.getInstance().addDownloadTask(taskv);
            mMediaTask=taskv;

//            mDLManager.dlStart(mUrl, mDir, StorageUtil.getAudioTmpFilename(mVoa.voaId()), mMediaListener);

            mMsgTimer.cancel();
        }
    };

    /****************************下载操作**********************/
    public void download() {
        mMsgTimer = new Timer();
        mMsgTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mMsg != null) {
                    Timber.e("下载进度" + mMsg);
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.DOWNLOADING, mMsg, Integer.parseInt(chapterBean.getVoaId())));
                }else {
                    int a=0;
                }
            }
        }, 0, SHOW_PEROID);

        if (!StorageUtil.isAudioExist(mDir, Integer.parseInt(chapterBean.getVoaId()))) {
            downloadAudio();
        } else if (!StorageUtil.isVideoExist(mDir, Integer.parseInt(chapterBean.getVoaId()))) {
            downloadVideo();
        }
    }

    private void downloadVideo() {
        //mVideoUrl= "http://"+Constant.staticStr+Constant.IYUBA_CN+"video/voa/321/321001.mp4";
        DLTaskInfo taskv = new DLTaskInfo();
        taskv.tag = VIDEO_CATEGORY_AND_TAG + chapterBean.getVoaId();
        taskv.filePath = mDir;
        taskv.fileName = mVideoUrl.substring(mVideoUrl.lastIndexOf("/"));
        taskv.initalizeUrl(mVideoUrl);
        taskv.category = VIDEO_CATEGORY_AND_TAG;
        taskv.setDListener(mVideoListener);
        DLManager.getInstance().addDownloadTask(taskv);
        mVideoTask=taskv;

//        mDLManager.dlStart(mVideoUrl, mDir,
//                StorageUtil.getVideoTmpFilename(mVoa.voaId()), mVideoListener);
    }

    private void downloadAudio() {
        mMediaUrl = chapterBean.getBgAudioUrl();//"/202002/313116.mp3"
        //http://staticvip."+Constant.IYUBA_CN+"sounds/voa/202005/321001.mp3
        DLTaskInfo taskv = new DLTaskInfo();
        taskv.tag = AUDIO_CATEGORY_AND_TAG + chapterBean.getVoaId();
        taskv.filePath = mDir;
        taskv.fileName = mMediaUrl.substring(mMediaUrl.lastIndexOf("/"));
        taskv.initalizeUrl(mMediaUrl);
        taskv.category = AUDIO_CATEGORY_AND_TAG;
        taskv.setDListener(mMediaListener);
        DLManager.getInstance().addDownloadTask(taskv);
        mMediaTask=taskv;
//        mDLManager.dlStart(mMediaUrl, mDir,
//                StorageUtil.getAudioTmpFilename(mVoa.voaId()), mMediaListener);
    }

    public void cancelDownload() {
        if (mVideoUrl != null) {
            DLManager.getInstance().cancelTask(mVideoTask);
        }

        if (mMediaUrl != null) {
            DLManager.getInstance().cancelTask(mMediaTask);
        }
    }

    /*********************其他方法**************************/
    public boolean checkFileExist() {
        return StorageUtil.checkFileExist(mDir, Integer.parseInt(chapterBean.getVoaId()));
    }

    //音频链接
    public String getAudioPath(){
        String audioName = mMediaUrl.substring(mMediaUrl.lastIndexOf("/"));
        return mDir+audioName;
    }

    //视频链接
    public String getVideoPath(){
        String videoName = mVideoUrl.substring(mVideoUrl.lastIndexOf("/"));
        return mDir+videoName;
    }
}
