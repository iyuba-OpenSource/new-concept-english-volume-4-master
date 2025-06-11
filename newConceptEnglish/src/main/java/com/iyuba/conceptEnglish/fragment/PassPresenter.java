package com.iyuba.conceptEnglish.fragment;

import android.content.Context;
import android.text.TextUtils;

import com.iyuba.conceptEnglish.sqlite.db.WordChildDBManager;
import com.iyuba.conceptEnglish.sqlite.mode.SentenceAudio;
import com.iyuba.conceptEnglish.util.download.DownloadTask;
import com.iyuba.conceptEnglish.util.download.StorageUtil;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.common.util.RxUtil;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.event.DownloadEvent;
import com.iyuba.module.mvp.BasePresenter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;
import cn.aigestudio.downloader.bizs.DLManager;

public class PassPresenter extends BasePresenter<PassMvpView> {
    private final int timeOutFlag=3;
    private DataManager mDataManager;

    private Disposable mGetWords;

    private Executor executor;
    private ExecutorService executor1;
    private DLManager mDLManager;
    private List<DownloadTask> downloadManager = new ArrayList<>();
    private Subscription downloadObSub;
    private Context mContext;

    private final int DEADLINE = 20;
    private int finalWaitTimes = 0;

    PassPresenter(Context context) {
        mDataManager = DataManager.getInstance();
        executor = Executors.newSingleThreadExecutor();
        executor1 = Executors.newSingleThreadExecutor();
        mContext = context;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetWords);
        RxUtil.unsubscribe(downloadObSub);
    }

    void getChildWords(String bookId, String text) {
        RxUtil.unsubscribe(mGetWords);
        mGetWords = mDataManager.getChildWords(bookId)
                .compose(com.iyuba.module.toolbox.RxUtil.<List<VoaWord2>>applySingleIoScheduler())
                .subscribe(new Consumer<List<VoaWord2>>() {
                    @Override
                    public void accept(List<VoaWord2> list) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().getChildWordList(list, text);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            throwable.printStackTrace();
                            getMvpView().showMessage("网络请求失败");
                        }
                    }
                });
    }

    void upDataDownload(String bookId, int version) {
        RxUtil.unsubscribe(mGetWords);
        Timber.d("获取接口：" + bookId + " " + version);
        mGetWords = mDataManager.upDataDownload(bookId, version)
                .compose(com.iyuba.module.toolbox.RxUtil.<List<VoaWord2>>applySingleIoScheduler())
                .subscribe(list -> {
                    if (isViewAttached()) {
                        getMvpView().upDataWordList(list);
                    }
                }, throwable -> {
                    Timber.e(throwable);
                    if (isViewAttached()) {
                        throwable.printStackTrace();
                        getMvpView().showMessage(throwable.getMessage());
                    }
                });
    }

    void startDownload(String bookId) {
        getMvpView().startDownload();
        if (mDLManager == null)
            mDLManager = DLManager.getInstance(mContext);
        if (executor1.isShutdown()) {
            executor1 = Executors.newSingleThreadExecutor();
            executor = Executors.newSingleThreadExecutor();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                initDownloadInfo(bookId);
                if (downloadManager.isEmpty()) {
                    //全都下载过
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, "文件已经下载！", 0));
                } else {
                    if (mDLManager!=null){
                        mDLManager.setMaxTask(10);
                    }
                    Timber.tag("TAG").d("startDownload: %s", downloadManager.size());
                    startOb();
                    for (int i = 0; i < downloadManager.size(); i++) {
                        DownloadTask task = downloadManager.get(i);
                        DownloadJob job = new DownloadJob(task);
                        synchronized (downloadManager) {
                            if (!executor1.isShutdown()) {
                                try {
                                    if (i >= 10)
                                        downloadManager.wait();
                                    executor1.submit(job);//提交要进行的线程（任务）
                                } catch (InterruptedException | RejectedExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    //下载更新的方法
    void startDownloadUpData(String bookId, List<VoaWord2> list) {
        getMvpView().startDownload();
        if (mDLManager == null)
            mDLManager = DLManager.getInstance(mContext);
        if (executor1.isShutdown()) {
            executor1 = Executors.newSingleThreadExecutor();
            executor = Executors.newSingleThreadExecutor();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                PassPresenter.this.initDownloadInfoUpData(bookId, list);
                if (downloadManager.isEmpty()) {
                    //全都下载过
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, "文件已经下载！", 0));
                } else {
                    mDLManager.setMaxTask(10);
                    Timber.tag("TAG").d("startDownload: %s", downloadManager.size());
                    PassPresenter.this.startOb();
                    for (int i = 0; i < downloadManager.size(); i++) {
                        DownloadTask task = downloadManager.get(i);
                        DownloadJob job = new DownloadJob(task);
                        synchronized (downloadManager) {
                            if (!executor1.isShutdown()) {
                                try {
                                    if (i >= 10)
                                        downloadManager.wait();
                                    executor1.submit(job);//提交要进行的线程（任务）
                                } catch (InterruptedException | RejectedExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    void cancelDownload() {
        executor1.shutdownNow();
        mDLManager = null;
        stopOb();
    }


    //定义了一个线程接口
    static class DownloadJob implements Runnable {
        DownloadTask task;

        DownloadJob(DownloadTask task) {
            this.task = task;
        }

        @Override
        public void run() {
            task.dlStart(new DownloadTask.onDownloadFinishCallBack() {
                @Override
                public void finish() {
                    Timber.d("下载=完成");
                }
            });
        }
    }

    private void initDownloadInfo(String bookId) {
        //需要下载 单词音频(整体压缩包) 句子音频  视频 图片
        downloadManager.clear();
        List<String> videoUrls = WordChildDBManager.getInstance().findVideoList(bookId);//视频路径
        videoUrls.remove("");
        List<SentenceAudio> sentenceSounds = WordChildDBManager.getInstance().findSentenceAudios(bookId);
        addWordDownloadList();//下载单词 所有的不分书
        addAudioDownloadList(sentenceSounds, bookId, false);//下载句子音频
        addImageDownloadList(bookId);//下载图片
        addVideoClipDownloadList(videoUrls, false);// 也是视频  例句视频
        //addVideoDownloadList(voaids); ???
    }

    private void initDownloadInfoUpData(String bookId, List<VoaWord2> list) {
        //需要下载 单词音频(整体压缩包) 句子音频  视频 图片
        downloadManager.clear();
        List<String> videoUrls = new ArrayList<>();//视频路径
        List<String> wordAudios = new ArrayList<>();//单词音频
        List<String> wordImages = new ArrayList<>();//单词图片
        List<SentenceAudio> sentenceSounds = new ArrayList<>();
        for (VoaWord2 word2 : list) {
            videoUrls.add(word2.videoUrl);
            wordAudios.add(word2.audio);
            wordImages.add(word2.picUrl);
            SentenceAudio sentenceAudio = new SentenceAudio();
            sentenceAudio.unitId = word2.unitId;
            sentenceAudio.position = String.valueOf(word2.position);
            sentenceAudio.mSentenceAudio = word2.SentenceAudio;
            sentenceSounds.add(sentenceAudio);
        }
        videoUrls.remove("");
        //addWordDownloadList();//下载单词 所有的不分书
        addUpDataWord(wordAudios);
        addAudioDownloadList(sentenceSounds, bookId, true);//下载句子音频
        //addImageDownloadList(bookId);//下载图片
        addImageUpData(wordImages, bookId);
        addVideoClipDownloadList(videoUrls, true);// 也是视频  例句视频
    }

    private void addWordDownloadList() {
        String wordUrl = "http://static2."+Constant.IYUBA_CN+"aiciaudio/newconceptword.zip";
        String wordDirPath = StorageUtil
                .getWordZipDir(mContext)//路径修改
                .getAbsolutePath();
        File wordDir = new File(wordDirPath);

        if (!new File(wordDir, "words.zip").exists()) {
            Timber.d("下载=单词音频未完成");
            DownloadTask task1 = new DownloadTask();
            task1.dlInit(DownloadTask.TYPE_AUDIO_WORD, wordUrl, wordDirPath, "words.zip", downloadManager, mDLManager);
            downloadManager.add(task1);
        }
    }

    private void addUpDataWord(List<String> wordAudios) {
        String dir = StorageUtil.getWordDir(mContext).getAbsolutePath();
        for (String audio : wordAudios) {
            if (TextUtils.isEmpty(audio)) continue;
            File file = new File(dir, StorageUtil.getWordName(audio));
            if (file.exists()) {
                file.delete();
            }
            DownloadTask task1 = new DownloadTask();
            task1.dlInit(DownloadTask.TYPE_AUDIO_WORD, audio, dir, StorageUtil.getWordName(audio), downloadManager, mDLManager);
            downloadManager.add(task1);
            Timber.d("dir" + dir + "单词更新音频名称" + StorageUtil.getWordName(audio));
        }
    }

    private void addAudioDownloadList(List<SentenceAudio> voas, String bookId, boolean isRefresh) {
        for (SentenceAudio sounds : voas) {
            if (TextUtils.isEmpty(sounds.mSentenceAudio)) {
                continue;
            }
            String mDir = StorageUtil
                    .getMediaDir(mContext, bookId, sounds.unitId)
                    .getAbsolutePath();
            String audioUrl = sounds.mSentenceAudio;
            if (isRefresh && StorageUtil.isAudioExist(mDir, sounds.position)) {
                StorageUtil.isAudioExistDel(mDir, sounds.position);
            }
            if (!StorageUtil.isAudioExist(mDir, sounds.position)) {
                Timber.d("下载=句子音频未完成");
                DownloadTask task = new DownloadTask();
                task.dlInit(DownloadTask.TYPE_AUDIO_SENTENCE, audioUrl, mDir, StorageUtil.getAudioFilename(sounds.position), downloadManager, mDLManager);
                downloadManager.add(task);
            }
        }
    }

    private void addImageDownloadList(String book_id) {
        //http://static2."+Constant.IYUBA_CN+"images/words/zip/280.zip
        String imageHeaderUrl = "http://static2."+ Constant.IYUBA_CN+"images/words/zip/";
        String mPicUrl = imageHeaderUrl + book_id + ".zip";

        String mImageDirPath = StorageUtil
                .getMediaDir(mContext, book_id)
                .getAbsolutePath();
        File mImageDir = new File(mImageDirPath);
        File imageFile = new File(mImageDir, StorageUtil.getImageZipName(book_id));//String.valueOf(book_id)
        Timber.d("下载=图片判断文件" + imageFile.getPath());
        if (!imageFile.exists()) {
            Timber.d("下载=图片未完成");
            DownloadTask task2 = new DownloadTask();
            downloadManager.add(task2);
            task2.dlInit(DownloadTask.TYPE_PIC, mPicUrl, mImageDirPath, StorageUtil.getImageZipName(book_id), downloadManager, mDLManager);
        }
    }

    private void addImageUpData(List<String> images, String bookId) {
        String mImageDirPath = StorageUtil.getImageUnzipDir(mContext, bookId).getAbsolutePath();
        String imageHeaderUrl = "http://static2."+Constant.IYUBA_CN+"images/words/";

        for (String image : images) {
            File file = new File(mImageDirPath, image);
            Timber.d("更新图片原本是否存在" + file.exists() + "路径" + file.getAbsolutePath());
            if (file.exists()) {
                file.delete();
            }
            DownloadTask task1 = new DownloadTask();
            task1.dlInit(DownloadTask.TYPE_PIC, imageHeaderUrl + image, mImageDirPath, image, downloadManager, mDLManager);
            downloadManager.add(task1);
            Timber.d("单词更新图片名称" + mImageDirPath + images);
        }
    }

    private void addVideoClipDownloadList(List<String> urls, boolean isRefresh) {
        for (String url : urls) {
            if (TextUtils.isEmpty(url.trim())) {
                continue;
            }
            String mDir = StorageUtil
                    .getMediaDir(mContext, url)
                    .getAbsolutePath();

            if (isRefresh && StorageUtil.isVideoClipExist(mDir, url)) {
                StorageUtil.delVideoClipExist(mDir, url);
            }
            if (!StorageUtil.isVideoClipExist(mDir, url)) {
                Timber.d("下载=视频未完成" + mDir + "==" + url);
                DownloadTask task = new DownloadTask();
                task.dlInit(DownloadTask.TYPE_VIDEO, url, mDir, StorageUtil.getVideoClipTempilename(url), downloadManager, mDLManager);
                downloadManager.add(task);
            }
        }
    }

    Subscription getObObject() {
        return downloadObSub;
    }

    void stopOb() {
        downloadObSub.unsubscribe();
    }

    private void startOb() {
        if (downloadObSub != null){
            downloadObSub.unsubscribe();
        }
        //如何用一个合适的变量来判断下载进度卡在了99%?
        AtomicInteger timeCount= new AtomicInteger();
        downloadObSub = Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(l -> {
                    int count = 0;
                    /* 被清理了？导致无法触发isFinish？ */
                    for (DownloadTask task : downloadManager) {
                        if (task.isFinish) {
                            count++;
                            finalWaitTimes = 0;
                        } else if (downloadManager.size() - count <=5) {//防止卡在99、只能强退的情况，现在彻底卡住后
                            //此处可以移出for循环
                            finalWaitTimes = finalWaitTimes + 1;
                            if (finalWaitTimes >= DEADLINE) {
                                Timber.tag("errormessage").e("Out of time!");
                                ToastUtil.showToast(mContext,"链接超时，可能有部分资源缺少，建议重试或到信号更好的地方重试");//0为LENGTH_SHORT的含义
                                count = downloadManager.size();
                            }
                        }
                    }
                    if (downloadManager.size()-count<=timeOutFlag){
                        timeCount.getAndIncrement();
                    }
                    if (timeCount.get()==timeOutFlag){
                        EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, "", 0));
                        downloadObSub.unsubscribe();
                    }
//                    int size= (int) downloadManager.stream().filter(d -> d.isFinish).count();
                    String mMsg = count * 100 / downloadManager.size() + "";
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.DOWNLOADING, mMsg, 1000));
                    Timber.d("下载进度count" + count + "总进度" + downloadManager.size());
                    if (count >= downloadManager.size()) {
                        EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, "", 0));
                        downloadObSub.unsubscribe();
                    }
                }, Throwable::printStackTrace);

    }
}
