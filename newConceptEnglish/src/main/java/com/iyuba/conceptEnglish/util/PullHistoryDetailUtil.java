package com.iyuba.conceptEnglish.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.iyuba.conceptEnglish.api.ApiRetrofit;
import com.iyuba.conceptEnglish.api.UpdateEvalDataApi;
import com.iyuba.conceptEnglish.api.UpdateImoocRecordAPI;
import com.iyuba.conceptEnglish.api.UpdateTestDataApi;
import com.iyuba.conceptEnglish.api.data.ImoocRecordBean;
import com.iyuba.conceptEnglish.model.PullHistoryThread;
import com.iyuba.conceptEnglish.protocol.ListenDetailRequest;
import com.iyuba.conceptEnglish.protocol.ListenDetailResponse;
import com.iyuba.conceptEnglish.sqlite.mode.ArticleRecordBean;
import com.iyuba.conceptEnglish.sqlite.mode.ListenWordDetail;
import com.iyuba.conceptEnglish.sqlite.mode.TestRecordBean;
import com.iyuba.conceptEnglish.sqlite.mode.UpdateEvalDataBean;
import com.iyuba.conceptEnglish.sqlite.mode.UpdateTestDataBean;
import com.iyuba.conceptEnglish.sqlite.mode.VoaSound;
import com.iyuba.conceptEnglish.sqlite.op.ArticleRecordOp;
import com.iyuba.conceptEnglish.sqlite.op.TestRecordOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaSoundOp;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.imooclib.data.local.IMoocDBManager;
import com.iyuba.imooclib.data.model.StudyProgress;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class PullHistoryDetailUtil {
    private Context mContext;
    private Callback mCallback;
    private Handler mHandler;

    public PullHistoryDetailUtil(Context context, Callback callback) {
        mContext = context;
        mCallback = callback;
        mHandler = new Handler();
    }

    public void startPull() {
        new UpdateLsDetailThread().start();
    }


    /**
     * 同步听力数据
     */
    private class UpdateLsDetailThread extends Thread {
        @Override
        public void run() {
            Log.e("开始", new Date().toString());
            String uid = ConfigManager.Instance().loadString("userId");
            ArticleRecordOp articleRecordOp = new ArticleRecordOp(mContext);

            ClientSession.Instace().asynGetResponse(
                    new ListenDetailRequest(uid, "1", "20000", "1"), (response, request, rspCookie) -> {
                        //先删除本地数据 再下载数据
                        ListenDetailResponse tr = (ListenDetailResponse) response;
                        if (tr != null && tr.result.equals("1")) {
                            for (ListenWordDetail detail : tr.mList) {
                                if (!detail.lesson.equals("NEWCONCEPT") && !detail.lesson.equals("concept")) {
                                    continue;
                                }
                                //区分英音和美音和青少版
                                int lessonId = Integer.parseInt(detail.lessonId);
                                /**
                                 * 0：美音
                                 * 1：英音
                                 * 2：青少版
                                 */
                                int type = 0;
                                if (lessonId > 5000) {
                                    if (lessonId / 1000 == 321) {
                                        //青少版
                                        type = 2;
                                    } else {
                                        //英音
                                        type = 1;
                                        lessonId = lessonId / 10;
                                    }
                                } else {
                                    //美音
                                    type = 0;
                                }

                                int percent = Integer.parseInt(detail.testNum);
                                int flag = Integer.parseInt(detail.endFlag);

                                ArticleRecordBean bean = articleRecordOp.getData(lessonId, type);
                                if (bean == null) {
                                    bean = new ArticleRecordBean();
                                    bean.voa_id = lessonId;
                                    bean.is_finish = flag;
                                    bean.type = type;
                                    bean.percent = percent;
                                    bean.curr_time = 0;
                                    bean.total_time = 0;
                                    if (percent != 0 || flag == 1) {
                                        articleRecordOp.updateDataWeb(bean);
                                    }
                                } else {
                                    if (bean.is_finish != 1) {
                                        if (flag == 1 || percent > bean.percent) {
                                            bean.type = type;
                                            bean.is_finish = flag;
                                            bean.percent = percent;
                                            articleRecordOp.updateDataWeb(bean);
                                        }
                                    }
                                }
                            }
                        }
//                        new Handler().postDelayed(() -> updateEvalData(), 100);


                    }, (errorResponse, request, rspCookie) -> {
                        updateEvalData();
                    }, null);
        }
    }


    /**
     * 同步评测数据
     */
    private void updateEvalData() {
        VoaSoundOp voaSoundOp = new VoaSoundOp(mContext);
        UpdateEvalDataApi updateEvalDataApi = ApiRetrofit.getInstance().getUpdateEvalDataApi();
        updateEvalDataApi.getData(UpdateEvalDataApi.url, String.valueOf(UserInfoManager.getInstance().getUserId()), Constant.EVAL_TYPE).enqueue(new retrofit2.Callback<UpdateEvalDataBean>() {
            @Override
            public void onResponse(Call<UpdateEvalDataBean> call, Response<UpdateEvalDataBean> response) {
                UpdateEvalDataBean bean = response.body();
                Log.e("评测url: ", call.request().url().toString());
                if (bean != null && "1".equals(bean.getResult())) {
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        for (UpdateEvalDataBean.DataBean child : bean.getData()) {
                            int voaId = child.getNewsid();
                            if (child.getNewsid() > 5000) {
                                if (child.getNewsid() / 1000 == 321) {
                                    //青少版
                                    voaId = child.getNewsid();
                                } else {
                                    //英音
                                    voaId = child.getNewsid() / 10;
                                }
                            } else {
                                //美音
                                voaId = child.getNewsid();
                            }

                            VoaSound voaSound = voaSoundOp.findDataByIdWeb(Integer.parseInt(voaId + "" + child.getIdindex()), child.getNewsid());
                            if (voaSound == null) {
                                voaSound = new VoaSound();
                                voaSound.voa_id = voaId;
                                voaSound.itemId = Integer.parseInt(voaId + "" + child.getIdindex());
                                voaSound.totalScore = (int) (Float.parseFloat(child.getScore()) * 20);
                                voaSound.sound_url = child.getUrl();
                                Log.e("插入数据", child.getNewsid() + "===" + voaSound.itemId + "===");
                                voaSoundOp.updateWordScoreWeb(voaSound, child.getNewsid());
                            }
                        }
                    }

                }
//                new Handler().postDelayed(() -> updateTestData(), 100);
                updateTestData();

            }

            @Override
            public void onFailure(Call<UpdateEvalDataBean> call, Throwable t) {
                Log.e("评测数据更新失败", t.getMessage().toString());
                updateTestData();
            }
        });

    }


    /**
     * 同步练习题数据
     */
    private void updateTestData() {
        Log.e("test数据下载", "开始");

        TestRecordOp testRecordOp = new TestRecordOp(mContext);
        UpdateTestDataApi updateTestDataApi = ApiRetrofit.getInstance().getUpdateTestDataApi();
        String sign = com.iyuba.conceptEnglish.util.MD5.getMD5ofStr(String.valueOf(UserInfoManager.getInstance().getUserId()) + getCurTime());
        updateTestDataApi.getData(UpdateTestDataApi.url, Constant.APPID, String.valueOf(UserInfoManager.getInstance().getUserId()), "10", sign, "json", 1, 1000).enqueue(new retrofit2.Callback<UpdateTestDataBean>() {
            @Override
            public void onResponse(Call<UpdateTestDataBean> call, Response<UpdateTestDataBean> response) {
                UpdateTestDataBean testBean = response.body();

                Log.e("结束时间", new Date().toString());
                Log.e("test数据下载", call.request().url().toString());

                if (testBean != null && "1".equals(testBean.getResult())) {
                    if (testBean.getData() != null && testBean.getData().size() > 0) {
                        for (UpdateTestDataBean.DataBean child : testBean.getData()) {

                            int lessonId = Integer.parseInt(child.getLessonId());
                            int testNumber = Integer.parseInt(child.getTestNumber());

                            if (testRecordOp.isExits(lessonId, testNumber)) {
                                continue;
                            }
                            if (Constant.AppName.equals(child.getAppName())
                                    || Constant.APPID.equals(child.getAppId()) || "146".equals(child.getAppId())) {
                                //146 苹果新概念id
                                TestRecordBean record = new TestRecordBean();
                                record.BeginTime = child.getBeginTime();
                                record.LessonId = Integer.parseInt(child.getLessonId());
                                record.UserAnswer = child.getUserAnswer();
                                record.RightAnswer = child.getRightAnswer();
                                record.uid = UserInfoManager.getInstance().getUserId();
                                record.TestNumber = Integer.parseInt(child.getTestNumber());
                                record.AnswerResult = 0;
                                if (child.getUserAnswer().trim().equals(child.getRightAnswer().trim())) {
                                    record.AnswerResult = 1;
                                }
                                testRecordOp.updateData(record);
                            }
                        }

                    }
                }
                updateImoocData();

            }

            @Override
            public void onFailure(Call<UpdateTestDataBean> call, Throwable t) {
                updateImoocData();

            }
        });

    }


    /**
     * 同步微课数据
     */
    private void updateImoocData() {
        Log.e("Imooc数据下载", "开始");

        UpdateImoocRecordAPI updateImoocRecordAPI = ApiRetrofit.getInstance().getUpdateImoocRecordAPI();
        String sign = com.iyuba.conceptEnglish.util.MD5.getMD5ofStr(String.valueOf(UserInfoManager.getInstance().getUserId()) + getCurTime());
        Timber.d("wangwenyang:" + sign);
        updateImoocRecordAPI.getData(UpdateImoocRecordAPI.url,
                UserInfoManager.getInstance().getUserId() + "",
                UpdateImoocRecordAPI.lesson,
                UpdateImoocRecordAPI.Pageth,
                UpdateImoocRecordAPI.NumPerPage,
                sign)
                .enqueue(new retrofit2.Callback<ImoocRecordBean>() {
                    @Override
                    public void onResponse(Call<ImoocRecordBean> call, Response<ImoocRecordBean> response) {
                        if (response.body() != null
                                && response.body().result == 1
                                && response.body().data != null
                                && response.body().data.size() > 0) {
                            List<ImoocRecordBean.DataBean> list = response.body().data;
                            List<StudyProgress> insertList=new ArrayList<>();
                            for (ImoocRecordBean.DataBean bean:list){
                                StudyProgress studyProgress=new StudyProgress();
                                studyProgress.uid= UserInfoManager.getInstance().getUserId();
                                studyProgress.lessonId= bean.LessonId;
                                studyProgress.lesson= UpdateImoocRecordAPI.lesson;
                                studyProgress.startTime= bean.BeginTime;
                                studyProgress.endTime= bean.EndTime;
                                studyProgress.percentage= 0;
                                studyProgress.endFlag= bean.EndFlg;
                                insertList.add(studyProgress);
                            }
                            IMoocDBManager.getInstance().saveStudyProgressList(insertList);
                        }

                        updatePASSData();
                    }

                    @Override
                    public void onFailure(Call<ImoocRecordBean> call, Throwable t) {
                        updatePASSData();

                    }
                });

    }

    /**
     * 同步闯关数据
     */
    private void updatePASSData() {
        String[] lessonNameList = new String[]{"1", "2",
                "3", "4", "278", "279", "280",
                "281", "282", "283", "284", "285", "286", "287", "288", "289"};
        PullHistoryThread thread = new PullHistoryThread(mContext,
                lessonNameList, new PullHistoryThread.CallBack() {
            @Override
            public void callback(boolean isSuccess) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null) {
                            mCallback.callback();
                        }
                    }
                });
            }
        });
        thread.start();
    }

    private String getCurTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(System.currentTimeMillis());
    }

    public interface Callback {
        void callback();
    }
}
