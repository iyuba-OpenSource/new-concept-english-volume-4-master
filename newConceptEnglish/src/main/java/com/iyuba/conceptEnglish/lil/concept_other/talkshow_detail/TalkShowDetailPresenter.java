package com.iyuba.conceptEnglish.lil.concept_other.talkshow_detail;

import static okhttp3.MultipartBody.FORM;

import android.content.Context;
import android.util.Log;

import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.util.ConceptApplication;
import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.model.SendEvaluateResponse;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.data.model.VoaText;
import com.iyuba.core.common.data.remote.WordResponse;
import com.iyuba.core.common.util.NetStateUtil;
import com.iyuba.core.common.util.RxUtil;
import com.iyuba.core.common.util.StorageUtil;
import com.iyuba.core.common.util.VoaMediaUtil;
import com.iyuba.core.event.DownloadEvent;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.dlex.bizs.DLTaskInfo;
import com.iyuba.dlex.interfaces.IDListener;
import com.iyuba.module.mvp.BasePresenter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

/**
 * @title:
 * @date: 2023/5/17 10:02
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class TalkShowDetailPresenter extends BasePresenter<TalkShowDetailView> {

    public static final String AUDIO_CATEGORY_AND_TAG = "talkshow_audio_";
    public static final String VIDEO_CATEGORY_AND_TAG = "talkshow_video_";
    public static final String ERROR_CATEGORY_AND_TAG = "talkshow_error_";

    private static final long SHOW_PEROID = 500;

    private final DataManager mDataManager;
    private final DLManager mDLManager;

    /**
     * 绑定着需要的信息数据
     */
    private TalkLesson mVoa;
    //本地的存储路径
    private String mDir;
    private String mVideoUrl;
    private String mMediaUrl;
    private DLTaskInfo mVideoTask;
    private DLTaskInfo mMediaTask;


    private Timer mMsgTimer;
    private String mMsg;
    private Context mContext;

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
                DecimalFormat df = new DecimalFormat("0.00");
//                Timber.e("下载字节数____________"+progress+"总字节数____________"+mFileLength);
                long percent = (long) progress / mFileLength;
                if (getMvpView() != null) {
                    mMsg = MessageFormat.format("正在下载视频{0}%", df.format(v));
                    Timber.e("下载字" + mMsg + "percent" + percent);
                }
                if (mContext != null) {
                    mMsg = MessageFormat.format(mContext.getString(com.iyuba.lib.R.string.video_loading_tip), df.format(v));
                    Timber.e("下载字" + mMsg + "percent" + percent);
                }

                Log.d("下载文件信息", "视频--"+mMsg+"---"+df.format(v));
            }
        }

        @Override
        public void onStop(int progress) {
            mMsgTimer.cancel();
        }

        @Override
        public void onFinish(File file) {
            StorageUtil.renameVideoFile(mDir, mVoa.voaId());
            if (StorageUtil.checkFileExist(mDir, mVoa.voaId())) {
                mMsgTimer.cancel();
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId()));
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
                if (getMvpView() != null) {
                    mMsg = MessageFormat.format("正在下载音频{0}%", percent);
                }
                if (mContext != null) {
                    mMsg = MessageFormat.format(mContext.getString(com.iyuba.lib.R.string.media_loading_tip), percent);
                }

                Log.d("下载文件信息", "音频--"+mMsg+"---"+percent);
            }
        }

        @Override
        public void onStop(int progress) {
            mMsgTimer.cancel();
        }

        @Override
        public void onFinish(File file) {
            StorageUtil.renameAudioFile(mDir, mVoa.voaId());
            if (StorageUtil.checkFileExist(mDir, mVoa.voaId())) {
                mMsgTimer.cancel();
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.FINISH, mVoa.voaId()));
                //addDownload();
            }

            //下载视频
            if (!StorageUtil.isVideoExist(mDir, mVoa.voaId())) {
                downloadVideo();
            }
        }

        @Override
        public void onError(int status, String error) {
            String mUrl;
            mUrl = VoaMediaUtil.getAudioErrorUrl("/202002/313116.mp3");

            DLTaskInfo taskv = new DLTaskInfo();
            taskv.tag = ERROR_CATEGORY_AND_TAG + mVoa.Id;
            taskv.filePath = mDir;
            taskv.fileName = mUrl.substring(mUrl.lastIndexOf("/"));
            taskv.initalizeUrl(mUrl);
            taskv.category = ERROR_CATEGORY_AND_TAG;
            taskv.setDListener(mMediaListener);
            mDLManager.addDownloadTask(taskv);
            mMediaTask=taskv;

//            mDLManager.dlStart(mUrl, mDir, StorageUtil.getAudioTmpFilename(mVoa.voaId()), mMediaListener);

            mMsgTimer.cancel();
        }
    };
    private Disposable mSearchSub;
    private Disposable mDeleteDisposable;
    private Disposable mInsertDisposable;
    private Disposable mSyncVoaSub;
    private Disposable mMergeRecordSub;

    public TalkShowDetailPresenter(DLManager dlManager) {
        this.mDataManager = DataManager.getInstance();
        this.mDLManager = dlManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSyncVoaSub);
        RxUtil.unsubscribe(mMergeRecordSub);
        RxUtil.unsubscribe(mSearchSub);
//        RxUtil.unsubscribe(mDeleteRecordSub);
//        RxUtil.unsubscribe(mDelete1RecordSub);
    }

    public void init(Context context, TalkLesson voa) {
        this.mVoa = voa;
        mContext = context;
        mDir = StorageUtil
                .getMediaDir(context, voa.voaId())
                .getAbsolutePath();
        Timber.e("下载路径" + mDir);
    }

    public void download() {
        mMsgTimer = new Timer();
        mMsgTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mMsg != null) {
                    Timber.e("下载进度" + mMsg);
                    EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Status.DOWNLOADING, mMsg, mVoa.voaId()));
                }else {
                    int a=0;
                }
            }
        }, 0, SHOW_PEROID);

        if (!StorageUtil.isAudioExist(mDir, mVoa.voaId())) {
            downloadAudio();
        } else if (!StorageUtil.isVideoExist(mDir, mVoa.voaId())) {
            downloadVideo();
        }
    }

    private void downloadVideo() {
        //mVideoUrl= "http://"+Constant.staticStr+Constant.IYUBA_CN+"video/voa/321/321001.mp4";
        mVideoUrl = VoaMediaUtil.getVideoUrl(mVoa.category(), mVoa.voaId());

        DLTaskInfo taskv = new DLTaskInfo();
        taskv.tag = VIDEO_CATEGORY_AND_TAG + mVoa.Id;
        taskv.filePath = mDir;
        taskv.fileName = mVideoUrl.substring(mVideoUrl.lastIndexOf("/"));
        taskv.initalizeUrl(mVideoUrl);
        taskv.category = VIDEO_CATEGORY_AND_TAG;
        taskv.setDListener(mVideoListener);
        mDLManager.addDownloadTask(taskv);
        mVideoTask=taskv;

//        mDLManager.dlStart(mVideoUrl, mDir,
//                StorageUtil.getVideoTmpFilename(mVoa.voaId()), mVideoListener);
    }

    private void downloadAudio() {
        mMediaUrl = mVoa.Sound;//"/202002/313116.mp3"
        //http://staticvip."+Constant.IYUBA_CN+"sounds/voa/202005/321001.mp3
        DLTaskInfo taskv = new DLTaskInfo();
        taskv.tag = AUDIO_CATEGORY_AND_TAG + mVoa.Id;
        taskv.filePath = mDir;
        taskv.fileName = mMediaUrl.substring(mMediaUrl.lastIndexOf("/"));
        taskv.initalizeUrl(mMediaUrl);
        taskv.category = AUDIO_CATEGORY_AND_TAG;
        taskv.setDListener(mMediaListener);
        mDLManager.addDownloadTask(taskv);
        mMediaTask=taskv;
//        mDLManager.dlStart(mMediaUrl, mDir,
//                StorageUtil.getAudioTmpFilename(mVoa.voaId()), mMediaListener);
    }

    public void cancelDownload() {
        if (mVideoUrl != null) {
            mDLManager.cancelTask(mVideoTask);
        }

        if (mMediaUrl != null) {
            mDLManager.cancelTask(mMediaTask);
        }
    }

    public void syncVoaTexts(final int voaId) {
        checkViewAttached();
        RxUtil.unsubscribe(mSyncVoaSub);
        mSyncVoaSub = mDataManager.syncVoaTexts(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<VoaText>>() {
                    @Override
                    public void accept(List<VoaText> voaTexts) throws Exception {
                        if (voaTexts.size() == 0) {
                            getMvpView().showEmptyTexts();
                        } else {
                            getMvpView().showVoaTexts(voaTexts);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (!NetStateUtil.isConnected(ResUtil.getInstance().getContext())) {
                            getMvpView().showToast(com.iyuba.lib.R.string.please_check_network);
                        } else {
                            getMvpView().showToast(com.iyuba.lib.R.string.request_fail);
                        }
                        getMvpView().showEmptyTexts();
                    }
                });
    }

    public boolean checkFileExist() {
        return StorageUtil.checkFileExist(mDir, mVoa.voaId());
    }

    //删除音频和视频文件
    public void deleteAudioAndVideo(){
        File audioFile = StorageUtil.getAudioFile(ConceptApplication.getContext(),mVoa.voaId());
        File videoFile = StorageUtil.getVideoFile(ConceptApplication.getContext(),mVoa.voaId());

        if (audioFile.exists()){
            audioFile.delete();
        }
        if (videoFile.exists()){
            videoFile.delete();
        }
    }


    public int getFinishNum(int voaId, long timestamp) {
        return StorageUtil.getRecordNum(ConceptApplication.getContext(), voaId, timestamp);
    }

    /**
     * 如果存在草稿，取数据，读取分数
     */
//    void checkDraftExist(long mTimeStamp) {
//        mDataManager.getDraftRecord(mTimeStamp)
//                .subscribe(new Subscriber<List<Record>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//
//                    @Override
//                    public void onNext(List<Record> records) {
//                        if (records != null && records.size() > 0) {
//                            getMvpView().onDraftRecordExist(records.get(0));
//                        }
//                    }
//                });
//    }

    //评测接口 ！！
    public void uploadSentence(String sentence, int index, int newsId, final int paraId,
                               String type, String uid, final File file, String filePath, final int progress,
                               final int secondaryProgress) {
        checkViewAttached();
        RxUtil.unsubscribe(mMergeRecordSub);
        String TYPE = "type";
        String SENTENCE = "sentence";
        String USERID = "userId";
        String NEWSID = "newsId";
        String PARAID = "paraId";
        String IDINDEX = "IdIndex";
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(FORM);

        builder.addFormDataPart(SENTENCE, sentence)
                .addFormDataPart(IDINDEX, String.valueOf(index))
                .addFormDataPart(NEWSID, String.valueOf(newsId))
                .addFormDataPart(PARAID, String.valueOf(paraId))
                .addFormDataPart(TYPE, type)
                .addFormDataPart(USERID, uid);


        //String path = EnDecodeUtils.encode(file.getPath());
        //RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        builder.addPart(
                Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + filePath + "\""),
                RequestBody.create(MediaType.parse("application/octet-stream"), file));

        RequestBody requestBody = builder.build();
        mMergeRecordSub = mDataManager.uploadSentence(requestBody)
                .compose(com.iyuba.module.toolbox.RxUtil.<SendEvaluateResponse>applySingleIoScheduler())
                .subscribe(new Consumer<SendEvaluateResponse>() {
                    @Override
                    public void accept(SendEvaluateResponse pair) throws Exception {
                        if (isViewAttached()) {
                            //getMvpView().showToast("评测请求成功");
                            getMvpView().getEvaluateResponse(pair, paraId, file, progress, secondaryProgress);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        Timber.e("evaluate request fail.");
                        getMvpView().evaluateError("evaluate request fail.");
                    }
                });
    }


//    public void deleteWords(final int userId, List<String> words, final ActionMode mode) {
//        com.iyuba.module.toolbox.RxUtil.dispose(mDeleteDisposable);
//        mDeleteDisposable = mDataManager.deleteWords(userId, words)
//                .compose(com.iyuba.module.toolbox.RxUtil.<Boolean>applySingleIoSchedulerWith(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Exception {
//                        if (isViewAttached()) {
//                        }
//                    }
//                }))
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean result) throws Exception {
//                        if (isViewAttached()) {
//                            if (result) {
//                            } else {
//                                getMvpView().showToast("删除失败，请稍后重试!");
//                                mode.finish();
//                            }
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Timber.e(throwable);
//                        if (isViewAttached()) {
//                            getMvpView().showToast("删除失败，请稍后重试!");
//                            mode.finish();
//                        }
//                    }
//                });
//    }

    public void getNetworkInterpretation(String selectText) {
        if (mSearchSub != null)
            mSearchSub.dispose();
        mSearchSub = mDataManager.getWordOnNet(selectText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WordResponse>() {
                    @Override
                    public void accept(WordResponse wordBean) throws Exception {
                        getMvpView().showWord(wordBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                    }
                });
    }


    public void insertWords(int userId, List<String> words) {
        com.iyuba.module.toolbox.RxUtil.dispose(mInsertDisposable);
        mInsertDisposable = mDataManager.insertWords(userId, words)
                .compose(com.iyuba.module.toolbox.RxUtil.<Boolean>applySingleIoScheduler())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (isViewAttached()) {
                            if (result) {
                                getMvpView().showToast(com.iyuba.lib.R.string.play_ins_new_word_success);
                            } else {
                                getMvpView().showToast("添加生词未成功");
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            getMvpView().showToast("添加生词未成功");
                        }
                    }
                });
    }
}
