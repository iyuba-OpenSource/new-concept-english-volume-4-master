package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingPreview;

import android.util.Pair;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.ChapterDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.DubbingPreviewShowBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.DubbingPreviewSubmitBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.EvalChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.EvalEntity_chapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.ChapterDetailEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.ChapterEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.util.DBTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Publish_preview;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.FileManager;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;
import com.iyuba.conceptEnglish.util.ConceptApplication;
import com.iyuba.core.common.util.StorageUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/6/7 16:11
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingPreviewPresenter extends BasePresenter<DubbingPreviewView> {

    //提交预览的发布
    private Disposable publishDubbingPreviewDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(publishDubbingPreviewDis);
    }

    //加载章节数据
    public BookChapterBean getChapterData(String types, String voaId){
        if (types.equals(TypeLibrary.BookType.junior_primary)
                ||types.equals(TypeLibrary.BookType.junior_middle)){
            //中小学
            ChapterEntity_junior junior = JuniorDataManager.getSingleChapterFromDB(voaId);
            return DBTransUtil.transJuniorSingleChapterData(types,junior);
        }
        return null;
    }

    //获取章节详情数据
    public List<ChapterDetailBean> getChapterDetail(String types, String voaId){
        List<ChapterDetailBean> detailList = new ArrayList<>();

        if (types.equals(TypeLibrary.BookType.junior_primary)
                ||types.equals(TypeLibrary.BookType.junior_middle)){
            //中小学
            List<ChapterDetailEntity_junior> list = JuniorDataManager.getMultiChapterDetailFromDB(voaId);
            if (list!=null&&list.size()>0){
                detailList = DBTransUtil.transJuniorChapterDetailData(list);
            }
        }
        return detailList;
    }

    //获取评测结果数据
    public List<EvalChapterBean> getEvalDetail(String types,String voaId){
        List<EvalEntity_chapter> evalList = CommonDataManager.getEvalChapterByVoaIdFromDB(types, voaId, String.valueOf(UserInfoManager.getInstance().getUserId()));
        List<EvalChapterBean> tempList = new ArrayList<>();
        if (evalList!=null&&evalList.size()>0){
            for (int i = 0; i < evalList.size(); i++) {
                EvalEntity_chapter chapter = evalList.get(i);
                tempList.add(DBTransUtil.transEvalSingleChapterData(chapter));
            }
        }
        return tempList;
    }

    //提交发布
    public void submitDubbingPreview(DubbingPreviewSubmitBean submitBean){
        checkViewAttach();
        RxUtil.unDisposable(publishDubbingPreviewDis);
        JuniorDataManager.publishTalkPreview(submitBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Publish_preview>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        publishDubbingPreviewDis = d;
                    }

                    @Override
                    public void onNext(Publish_preview bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.getMessage().toLowerCase().equals("ok")){
                                getMvpView().showPublishData(String.valueOf(bean.getShuoShuoId()));
                            }else {
                                getMvpView().showPublishData(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showPublishData(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /******************************合并数据*************************/
    //获取音频的长度

    //合并数据为一个样式
    public DubbingPreviewShowBean margeDubbingShowData(String types,String voaId){
        //获取章节数据
        BookChapterBean chapterBean = getChapterData(types, voaId);
        if (chapterBean==null){
            return null;
        }

        //获取章节详情数据
        List<ChapterDetailBean> detailBeanList = getChapterDetail(types, voaId);
        if (detailBeanList==null||detailBeanList.size()==0){
            return null;
        }

        //获取评测数据
        List<EvalChapterBean> evalList = getEvalDetail(types, voaId);
        if (evalList.size()<1){
            return null;
        }

        //音视频数据
        String folderPath = StorageUtil
                .getMediaDir(ConceptApplication.getContext(), Integer.parseInt(chapterBean.getVoaId()))
                .getAbsolutePath()+"/"+chapterBean.getTypes();


        String videoPath = getFilePath(folderPath,chapterBean.getVideoUrl());
        String bgAudioPath = getFilePath(folderPath,chapterBean.getBgAudioUrl());
        if (!FileManager.getInstance().isFileExist(videoPath)){
            return null;
        }

        if (!FileManager.getInstance().isFileExist(bgAudioPath)){
            return null;
        }

        //必要的数据
        double totalScore = 0;//总分
        double wordScore = 0;//单词总分
        int wordCount = 0;//单词数量

        for (int i = 0; i < evalList.size(); i++) {
            EvalChapterBean evalBean = evalList.get(i);
            totalScore+=evalBean.getTotalScore();

            if (evalBean.getWordList()!=null&&evalBean.getWordList().size()>0){
                for (int j = 0; j < evalBean.getWordList().size(); j++) {
                    EvalChapterBean.WordBean wordBean = evalBean.getWordList().get(j);

                    wordScore+=trans2Data(Double.parseDouble(wordBean.getScore()));
                }
                wordCount+=evalBean.getWordList().size();
            }
        }

        //准确度--总分/评测的句子数量
        double rightScore = trans2Data(totalScore*1.0f/evalList.size());
        //完成度--评测数量/总数量
        double completeScore = trans2Data(evalList.size()*1.0f/detailBeanList.size());
        //流畅度--单词总分/单词总量
        double fluentScore = trans2Data(wordScore*1.0f/wordCount);


        //合并数据
        String bookId = chapterBean.getBookId();
        String videoUrl = chapterBean.getVideoUrl();
        String bgAudioUrl = chapterBean.getBgAudioUrl();

        List<DubbingPreviewShowBean.DubbingBean> showList = new ArrayList<>();
        for (int i = 0; i < evalList.size(); i++) {
            //评测数据
            EvalChapterBean evalBean = evalList.get(i);
            //详情数据
            Pair<Integer,ChapterDetailBean> detailBean = getCurDataIndex(detailBeanList,evalBean.getParaId(),evalBean.getIndexId());
            //合并数据
            showList.add(new DubbingPreviewShowBean.DubbingBean(
                    evalBean.getSentence(),
                    trans2Data(detailBean.second.getTiming()),
                    trans2Data(detailBean.second.getEndTiming()),
                    evalBean.getFilepath(),
                    evalBean.getUrl(),
                    false,
                    detailBean.first
            ));
        }

        return new DubbingPreviewShowBean(
                chapterBean.getTypes(),
                chapterBean.getBookId(),
                chapterBean.getVoaId(),
                videoPath,
                videoUrl,
                bgAudioPath,
                bgAudioUrl,

                rightScore,
                completeScore,
                fluentScore,
                showList
        );
    }

    //这里从所有的数据中查询出当前数据的位置
    private Pair<Integer,ChapterDetailBean> getCurDataIndex(List<ChapterDetailBean> detailList, String paraId, String idIndex){
        for (int i = 0; i < detailList.size(); i++) {
            ChapterDetailBean detailBean = detailList.get(i);
            if (detailBean.getParaId().equals(paraId)&&detailBean.getIndexId().equals(idIndex)){
                return new Pair<>(i,detailBean);
            }
        }
        return null;
    }

    //将数据获取后两位
    public double trans2Data(double timeData){
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");//构造方法的字符格式这里如果小数不足1位,会以0补足.
        String timeStr = decimalFormat.format(timeData);
        return Double.parseDouble(timeStr);
    }

    //将数据获取后一位
    public double trans1Data(double timeData){
        DecimalFormat decimalFormat = new DecimalFormat("##0.0");//构造方法的字符格式这里如果小数不足1位,会以0补足.
        String timeStr = decimalFormat.format(timeData);
        return Double.parseDouble(timeStr);
    }

    //文件地址
    public String getFilePath(String folderPath,String fileUrl){
        String audioName = fileUrl.substring(fileUrl.lastIndexOf("/"));
        return folderPath+audioName;
    }
}
