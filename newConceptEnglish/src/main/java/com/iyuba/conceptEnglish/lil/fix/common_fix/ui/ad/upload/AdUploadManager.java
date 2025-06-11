package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.upload;

import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Ad_click_result;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.manager.AdRemoteManager;
import com.iyuba.core.lil.remote.util.LibRxUtil;
import com.iyuba.core.lil.user.UserInfoManager;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AdUploadManager {
    private static final String TAG = "AdUploadManager";

    private static AdUploadManager instance;

    public static AdUploadManager getInstance(){
        if (instance==null){
            synchronized (AdUploadManager.class){
                if (instance==null){
                    instance = new AdUploadManager();
                }
            }
        }
        return instance;
    }

    /**********************************************点击广告获取奖励*************************************/
    //点击广告
    private Disposable clickAdForRewardDis;
    public void clickAdForReward(String showType, String adType, OnAdClickCallBackListener onAdClickCallBackListener){
        LibRxUtil.unDisposable(clickAdForRewardDis);

        //如果没有登录，则不调用
        if (!UserInfoManager.getInstance().isLogin()){
            return;
        }

        //展示类型
        int showPositionType = AdUploadUtil.Util.getNetShowType(showType);
        //广告类型
        int adShowType = AdUploadUtil.Util.getNetAdType(adType);

        //判断哪些内容需要上传数据
        if (showPositionType<0 || adShowType <0){
            return;
        }

        AdRemoteManager.getAdClickReward(UserInfoManager.getInstance().getUserId(), showPositionType,adShowType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Ad_click_result>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        clickAdForRewardDis = d;
                    }

                    @Override
                    public void onNext(Ad_click_result bean) {
                        if (onAdClickCallBackListener!=null){
                            if (bean!=null){
                                if (bean.getResult()==200){
                                    onAdClickCallBackListener.showClickAdResult(true,bean.getMessage());
                                }else {
                                    onAdClickCallBackListener.showClickAdResult(false,bean.getMessage());
                                }
                            }else {
                                onAdClickCallBackListener.showClickAdResult(false,"点击广告结果失败");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (onAdClickCallBackListener!=null){
                            onAdClickCallBackListener.showClickAdResult(false,"点击广告结果异常("+e.getMessage()+")");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //点击广告接口
    public interface OnAdClickCallBackListener{
        void showClickAdResult(boolean isSuccess,String showMsg);
    }

    /********************************************提交广告数据****************************************/

}
