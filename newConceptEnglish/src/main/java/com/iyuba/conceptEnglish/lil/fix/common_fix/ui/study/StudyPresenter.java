package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study;

import android.text.TextUtils;
import android.util.Pair;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.NovelDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.ChapterDetailEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.novel.ChapterDetailEntity_novel;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.ChapterEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.novel.ChapterEntity_novel;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.WordEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.util.DBTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_bookInfo_texts;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_voatext;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Collect_chapter;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Junior_chapter_detail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Novel_book;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Novel_chapter_detail;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.util.RemoteTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;
import com.iyuba.sdk.other.NetworkUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/5/22 16:03
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class StudyPresenter extends BasePresenter<StudyView> {

    //获取中小学章节详情数据
    private Disposable juniorChapterDetailDis;
    //获取小说的章节详情数据
    private Disposable novelChapterDetailDis;

    //收藏文章
    private Disposable collectArticleDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(novelChapterDetailDis);
        RxUtil.unDisposable(juniorChapterDetailDis);

        RxUtil.unDisposable(collectArticleDis);
    }

    //获取当前章节是否存在单词
    public boolean isExistWord(String types,String bookId,String voaId){
        if (TextUtils.isEmpty(types)){
            return false;
        }

        switch (types) {
            case TypeLibrary.BookType.junior_primary:
            case TypeLibrary.BookType.junior_middle:
                //中小学
                List<WordEntity_junior> juniorList = JuniorDataManager.searchWordByVoaIdFromDB(bookId,voaId);
                if (juniorList!=null&&juniorList.size()>0){
                    return true;
                }
                break;
            case TypeLibrary.BookType.conceptFour:
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptFourUK:
                //新概念全四册
                break;
            case TypeLibrary.BookType.conceptJunior:
                //新概念青少版
                break;
        }
        return false;
    }

    //获取当前章节单词所属的单元id
    public String getWordUnitId(String types,String bookId,String voaId){
        //中小学、新概念青少版-unitId，新概念全四册-voaId
        if (TextUtils.isEmpty(types)){
            return voaId;
        }

        switch (types) {
            case TypeLibrary.BookType.junior_primary:
            case TypeLibrary.BookType.junior_middle:
                //中小学
                return JuniorDataManager.searchWordUnitIdByVoaIdFromDB(bookId, voaId);
            case TypeLibrary.BookType.conceptFour:
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptFourUK:
                //新概念全四册
                break;
            case TypeLibrary.BookType.conceptJunior:
                //新概念青少版
                break;
        }
        return null;
    }

    //获取当前书籍的章节数据
    public List<BookChapterBean> getMultiChapterData(String types,String level,String bookId){
        List<BookChapterBean> list = new ArrayList<>();

        if (TextUtils.isEmpty(types)){
            return list;
        }

        switch (types){
            case TypeLibrary.BookType.junior_primary://小学
            case TypeLibrary.BookType.junior_middle://初中
                //中小学
                List<ChapterEntity_junior> junior = JuniorDataManager.getMultiChapterFromDB(bookId);
                if (junior!=null&&junior.size()>0){
                    list = DBTransUtil.transJuniorChapterData(types,junior);
                }
                break;
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                //小说
                List<ChapterEntity_novel> novel = NovelDataManager.searchMultiChapterFromDB(types, level, bookId);
                if (novel!=null&&novel.size()>0){
                    list = DBTransUtil.novelToChapterData(novel);
                }
                break;
        }
        return list;
    }

    //获取当前章节的数据
    private BookChapterBean getChapterData(String types, String voaId){
        if (TextUtils.isEmpty(types)){
            return null;
        }

        switch (types){
            case TypeLibrary.BookType.junior_primary://小学
            case TypeLibrary.BookType.junior_middle://初中
                //中小学
                ChapterEntity_junior junior = JuniorDataManager.getSingleChapterFromDB(voaId);
                return DBTransUtil.transJuniorSingleChapterData(types,junior);
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                //小说
                ChapterEntity_novel novel = NovelDataManager.searchSingleChapterFromDB(types, voaId);
                return DBTransUtil.novelToSingleChapterData(novel);
        }
        return null;
    }

    //获取当前章节详情数据
    public void getChapterDetail(String types,String bookId,String voaId){
        switch (types){
            case TypeLibrary.BookType.junior_primary://小学
            case TypeLibrary.BookType.junior_middle://初中
                //中小学
                List<ChapterDetailEntity_junior> juniorList = JuniorDataManager.getMultiChapterDetailFromDB(voaId);
                if (juniorList!=null&&juniorList.size()>0){
                    getMvpView().showData(DBTransUtil.transJuniorChapterDetailData(juniorList),false);
                }else {
                    if (!NetworkUtil.isConnected(ResUtil.getInstance().getContext())){
                        ToastUtil.showToast(ResUtil.getInstance().getContext(), "请链接网络后重试~");
                        return;
                    }

                    getMvpView().showLoading("正在加载详情内容~");
                    loadJuniorChapterDetail(types, bookId, voaId,false);
                }
                break;
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                //小说
                List<ChapterDetailEntity_novel> novelList = NovelDataManager.searchMultiChapterDetailFromDB(types,voaId);
                if (novelList!=null&&novelList.size()>0){
                    getMvpView().showData(DBTransUtil.novelToChapterDetailData(novelList),false);
                }else {
                    if (!NetworkUtil.isConnected(ResUtil.getInstance().getContext())){
                        ToastUtil.showToast(ResUtil.getInstance().getContext(), "请链接网络后重试~");
                        return;
                    }

                    getMvpView().showLoading("正在加载详情内容~");
                    loadNovelChapterDetailData(types, voaId,false);
                }
                break;
        }
    }

    //获取当前章节的详情数据(服务器)
    public void getChapterDetailFromRemote(String types,String bookId,String voaId){
        switch (types){
            case TypeLibrary.BookType.junior_primary://小学
            case TypeLibrary.BookType.junior_middle://初中
                //中小学
                if (!NetworkUtil.isConnected(ResUtil.getInstance().getContext())){
                    ToastUtil.showToast(ResUtil.getInstance().getContext(), "请链接网络后重试~");
                    return;
                }

                getMvpView().showLoading("正在加载详情内容~");
                loadJuniorChapterDetail(types, bookId, voaId,true);
                break;
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                //小说
                if (!NetworkUtil.isConnected(ResUtil.getInstance().getContext())){
                    ToastUtil.showToast(ResUtil.getInstance().getContext(), "请链接网络后重试~");
                    return;
                }

                getMvpView().showLoading("正在加载详情内容~");
                loadNovelChapterDetailData(types, voaId,true);
                break;
        }
    }

    /********************中小学***********************/
    //获取中小学的章节详情数据
    private void loadJuniorChapterDetail(String types,String bookId,String voaId,boolean isRefresh){
        checkViewAttach();
        RxUtil.unDisposable(juniorChapterDetailDis);
        JuniorDataManager.getJuniorChapterDetail(voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_voatext<List<Junior_chapter_detail>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        juniorChapterDetailDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_voatext<List<Junior_chapter_detail>> bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.getVoatext()!=null){
                                //保存在数据库
                                JuniorDataManager.saveChapterDetailToDB(RemoteTransUtil.transJuniorChapterDetailData(types,bookId,voaId,bean.getVoatext()));
                                //从数据库取出
                                List<ChapterDetailEntity_junior> list = JuniorDataManager.getMultiChapterDetailFromDB(voaId);
                                //展示数据
                                getMvpView().showData(DBTransUtil.transJuniorChapterDetailData(list),isRefresh);
                            }else {
                                getMvpView().showData(null,isRefresh);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showData(null,isRefresh);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /********************小说*************************/
    //获取小说的章节详情数据
    private void loadNovelChapterDetailData(String types,String voaId,boolean isRefresh){
        checkViewAttach();
        RxUtil.unDisposable(novelChapterDetailDis);
        NovelDataManager.getChapterDetailData(types, voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_bookInfo_texts<Novel_book, List<Novel_chapter_detail>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        novelChapterDetailDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_bookInfo_texts<Novel_book, List<Novel_chapter_detail>> bean) {
                        if (getMvpView()!=null) {
                            if (bean!=null&&bean.getResult() == 200) {
                                //保存在本地
                                NovelDataManager.saveChapterDetailToDB(RemoteTransUtil.transNovelChapterDetailToDB(types,bean.getTexts()));
                                //从本地获取数据
                                List<ChapterDetailEntity_novel> list = NovelDataManager.searchMultiChapterDetailFromDB(types, voaId);
                                //显示在界面上
                                getMvpView().showData(DBTransUtil.novelToChapterDetailData(list),isRefresh);
                            }else {
                                getMvpView().showData(null,isRefresh);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showData(null,isRefresh);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //合并章节数据，判断是否格外选项显示
    public BookChapterBean margeChapterData(String types,String voaId){
        BookChapterBean chapterBean = getChapterData(types, voaId);
        //判断单词是否显示
        if (chapterBean!=null){
            chapterBean.setShowWord(false);
        }
        return chapterBean;
    }


    /****************************************收藏/取消收藏****************************/
    //收藏/取消收藏文章
    public void collectArticle(String types,String voaId,String userId,boolean isCollect){
        if (TextUtils.isEmpty(types)){
            ToastUtil.showToast(ResUtil.getInstance().getContext(), "暂无该类型数据");
            return;
        }

        switch (types){
            case TypeLibrary.BookType.junior_primary:
            case TypeLibrary.BookType.junior_middle:
                //中小学
                collectJuniorArticle(types, voaId, userId, isCollect);
                break;
            case TypeLibrary.BookType.bookworm:
            case TypeLibrary.BookType.newCamstory:
            case TypeLibrary.BookType.newCamstoryColor:
                //小说
                collectNovelArticle(types, voaId, userId, isCollect);
                break;
        }
    }

    //中小学-收藏/取消收藏文章
    public void collectJuniorArticle(String types,String voaId,String userId,boolean isCollect){
        checkViewAttach();
        RxUtil.unDisposable(collectArticleDis);
        JuniorDataManager.collectArticle(types,userId,voaId,isCollect)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Collect_chapter>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        collectArticleDis = d;
                    }

                    @Override
                    public void onNext(Collect_chapter bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.msg.equals("Success")){
                                getMvpView().showCollectArticle(true,isCollect);
                                //刷新收藏界面回调
                                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.junior_lesson_collect));
                            }else {
                                getMvpView().showCollectArticle(false,isCollect);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showCollectArticle(false,false);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //小说-收藏/取消收藏文章
    public void collectNovelArticle(String types,String voaId,String userId,boolean isCollect){
        checkViewAttach();
        RxUtil.unDisposable(collectArticleDis);
        NovelDataManager.collectArticle(types, voaId, userId, isCollect)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Collect_chapter>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        collectArticleDis = d;
                    }

                    @Override
                    public void onNext(Collect_chapter bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.msg.equals("Success")){
                                getMvpView().showCollectArticle(true,isCollect);
                                //刷新收藏界面回调
                                EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.novel_lesson_collect));
                            }else {
                                getMvpView().showCollectArticle(false,isCollect);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showCollectArticle(false,false);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**********************************上下文切换和随机播放*****************************/
    //获取上下文数据位置
    //-1:只有一个，-2：最后一个
    public Pair<Integer, Pair<BookChapterBean,BookChapterBean>> getCurChapterIndex(String types, String level, String bookId, String voaId){
        if (TextUtils.isEmpty(types)){
            return new Pair<>(0,new Pair<>(null,null));
        }

        List<BookChapterBean> list = getMultiChapterData(types, level, bookId);
        if (list!=null&&list.size()>0){
            int index = 0;

            bookChapter:for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getVoaId().equals(voaId)){
                    index = i;
                    break bookChapter;
                }
            }

            //只有一个
            if (list.size()==1){
                return new Pair<>(-1,new Pair<>(null,null));
            }

            //第一个
            if (index==0){
                int nextIndex = index+1;
                return new Pair<>(index,new Pair<>(null,list.get(nextIndex)));
            }

            //最后一个
            if (index==list.size()-1){
                int preIndex = index-1;
                return new Pair<>(-2,new Pair<>(list.get(preIndex),null));
            }

            if (index>0&&index<list.size()-1){
                int preIndex = index-1;
                int nextIndex = index+1;

                return new Pair<>(index,new Pair<>(list.get(preIndex),list.get(nextIndex)));
            }


        }

        return new Pair<>(0,new Pair<>(null,null));
    }

    //获取随机的数据位置
    public BookChapterBean getRandomChapterData(String types, String level, String bookId){
        List<BookChapterBean> list = getMultiChapterData(types, level, bookId);
        if (list!=null&&list.size()>0){
            //使用随机数进行处理
            int randomInt = (int) (Math.random()*list.size())-1;
            return list.get(randomInt);
        }
        return null;
    }
}
