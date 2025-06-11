package com.iyuba.conceptEnglish.lil.fix.junior.choose;

import android.util.Pair;

import com.iyuba.ConstantNew;
import com.iyuba.conceptEnglish.lil.concept_other.verify.AbilityControlManager;
import com.iyuba.conceptEnglish.lil.concept_other.verify.AppCheckResponse;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.BookEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base.BaseBean_data_primary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Junior_book;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Junior_type;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.util.RemoteTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil;
import com.tencent.vasdolly.helper.ChannelReaderUtil;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title:
 * @date: 2023/5/22 09:31
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class JuniorChoosePresenter extends BasePresenter<JuniorChooseView> {

    //小学的出版社类型
    private Disposable primaryTypeDis;
    //初中的出版社类型
    private Disposable middleTypeDis;
    //中小学的书籍数据
    private Disposable juniorBookDis;
    //审核接口
    private Disposable renVerifyDis;

    @Override
    public void detachView() {
        super.detachView();

        RxUtil.unDisposable(primaryTypeDis);
        RxUtil.unDisposable(middleTypeDis);
        RxUtil.unDisposable(juniorBookDis);
        RxUtil.unDisposable(renVerifyDis);
    }

    //获取出版社数据
    public void getTypeData(String types){
        if (types.equals(TypeLibrary.BookType.junior_primary)){
            //小学
            getPrimaryTypeData();
        }else if (types.equals(TypeLibrary.BookType.junior_middle)){
            //初中
            getMiddleTypeData();
        }
    }

    //获取书籍数据
    public void getBookData(String category){
        List<BookEntity_junior> list = JuniorDataManager.getBookFromDB(category);
        if (list!=null&&list.size()>0){
            getMvpView().showBookData(list);
        }else {
            getMvpView().refreshBookData();
        }
    }

    /*************远程接口************/
    //获取人教版审核接口数据
    public void getRenVerifyData(){
        checkViewAttach();
        RxUtil.unDisposable(renVerifyDis);

        int verifyId = ConstantNew.getRenLimitChannelId(ChannelReaderUtil.getChannel(ResUtil.getInstance().getContext()));
        CommonDataManager.getVerifyData(verifyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppCheckResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AppCheckResponse response) {
                        if (getMvpView()!=null){
                            if (response.getResult().equals("0")){
                                AbilityControlManager.getInstance().setLimitPep(false);
                            }else {
                                AbilityControlManager.getInstance().setLimitPep(true);
                            }

                            getMvpView().showPepVerifyData(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            AbilityControlManager.getInstance().setLimitPep(true);
                            getMvpView().showPepVerifyData(false);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取小学的出版社数据
    private void getPrimaryTypeData(){
        checkViewAttach();
        RxUtil.unDisposable(primaryTypeDis);
        JuniorDataManager.getPrimaryType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<BaseBean_data_primary<List<Junior_type>>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        primaryTypeDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<BaseBean_data_primary<List<Junior_type>>> bean) {
                        if (getMvpView()!=null){
                            if (bean!=null
                                    &&bean.getResult()!=null
                                    &&bean.getResult().equals("200")){
                                if (bean.getData().getPrimary()!=null){
                                    //转化数据
                                    List<Pair<String,List<Pair<String,String>>>> list = RemoteTransUtil.transJuniorTypeData(bean.getData().getPrimary());
                                    //展示数据
                                    getMvpView().showTypeData(list);
                                }else {
                                    getMvpView().showTypeData(null);
                                }
                            }else {
                                getMvpView().showTypeData(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showTypeData(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取初中的出版社数据
    private void getMiddleTypeData(){
        checkViewAttach();
        RxUtil.unDisposable(middleTypeDis);
        JuniorDataManager.getMiddleType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<BaseBean_data_junior<List<Junior_type>>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        middleTypeDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<BaseBean_data_junior<List<Junior_type>>> bean) {
                        if (getMvpView()!=null){
                            if (bean!=null&&bean.getResult().equals("200")){
                                if (bean.getData().getJunior()!=null){
                                    //转化数据
                                    List<Pair<String,List<Pair<String,String>>>> list = RemoteTransUtil.transJuniorTypeData(bean.getData().getJunior());
                                    //展示数据
                                    getMvpView().showTypeData(list);
                                }else {
                                    getMvpView().showTypeData(null);
                                }
                            }else {
                                getMvpView().showTypeData(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showTypeData(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取中小学的书籍数据
    public void getJuniorNetBookData(String types,String category){
        checkViewAttach();
        RxUtil.unDisposable(juniorBookDis);
        JuniorDataManager.getJuniorBook(category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<List<Junior_book>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        juniorBookDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<List<Junior_book>> bean) {
                        if (getMvpView()!=null){
                            if (bean.getResult().equals("1")){
                                //保存在数据库
                                JuniorDataManager.saveBookToDB(RemoteTransUtil.transJuniorBookData(types,bean.getData()));
                                //从数据库取出
                                List<BookEntity_junior> list = JuniorDataManager.getBookFromDB(category);
                                //展示数据
                                getMvpView().showBookData(list);
                            }else {
                                getMvpView().showBookData(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getMvpView()!=null){
                            getMvpView().showBookData(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
