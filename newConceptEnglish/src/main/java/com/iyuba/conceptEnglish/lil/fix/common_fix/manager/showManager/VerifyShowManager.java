package com.iyuba.conceptEnglish.lil.fix.common_fix.manager.showManager;

import android.content.Context;

import com.iyuba.ConstantNew;
import com.iyuba.conceptEnglish.api.AiyubaAdvApi;
import com.iyuba.conceptEnglish.lil.concept_other.verify.AbilityControlManager;
import com.iyuba.conceptEnglish.lil.concept_other.verify.AppCheckResponse;
import com.iyuba.conceptEnglish.lil.concept_other.verify.HelpUtil;
import com.iyuba.conceptEnglish.lil.concept_other.verify.OtherUtil;
import com.iyuba.conceptEnglish.lil.concept_other.verify.VerifyApi;
import com.iyuba.core.common.util.RxUtil;
import com.iyuba.core.lil.remote.util.LibRxUtil;
import com.tencent.vasdolly.helper.ChannelReaderUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 审核显示管理
 */
public class VerifyShowManager {
    private static VerifyShowManager instance;

    public static VerifyShowManager getInstance(){
        if (instance==null){
            synchronized (VerifyShowManager.class){
                if (instance==null){
                    instance = new VerifyShowManager();
                }
            }
        }
        return instance;
    }

    //获取新概念内容控制
    private Disposable verifyConceptDis;

    public void checkConceptVerify(Context context){
        LibRxUtil.unDisposable(verifyConceptDis);

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(AiyubaAdvApi.BASEURL)
                .build();
        VerifyApi verifyApi = retrofit.create(VerifyApi.class);

        String version = HelpUtil.getAppVersion(context, context.getPackageName());
        if (OtherUtil.isBelongToOppoPhone()) {
            version = "oppo_" + version;
        }

        String channel = ChannelReaderUtil.getChannel(context);
        verifyApi.verifyMoc(VerifyApi.BASEURL_1, ConstantNew.getConceptLimitChannelId(channel), version)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppCheckResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        verifyConceptDis = d;
                    }

                    @Override
                    public void onNext(AppCheckResponse response) {
                        if (response.getResult().equals("0")) {
                            AbilityControlManager.getInstance().setLimitConcept(false);
                        } else {
                            AbilityControlManager.getInstance().setLimitConcept(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        AbilityControlManager.getInstance().setLimitConcept(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取中小学内容控制
    private Disposable verifyJuniorDis;

    public void checkJuniorVerify(Context context){
        RxUtil.unsubscribe(verifyJuniorDis);

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(AiyubaAdvApi.BASEURL)
                .build();
        VerifyApi verifyApi = retrofit.create(VerifyApi.class);

        String version = HelpUtil.getAppVersion(context, context.getPackageName());
        if (OtherUtil.isBelongToOppoPhone()) {
            version = "oppo_" + version;
        }

        String channel = ChannelReaderUtil.getChannel(context);
        verifyApi.verifyMoc(VerifyApi.BASEURL_1, ConstantNew.getJuniorLimitChannelId(channel), version)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppCheckResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        verifyJuniorDis = d;
                    }

                    @Override
                    public void onNext(AppCheckResponse response) {
                        if (response.getResult().equals("0")) {
                            AbilityControlManager.getInstance().setLimitJunior(false);
                        } else {
                            AbilityControlManager.getInstance().setLimitJunior(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        AbilityControlManager.getInstance().setLimitJunior(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取小说内容控制
    private Disposable verifyNovelDis;

    public void checkNovelVerify(Context context){
        RxUtil.unsubscribe(verifyNovelDis);

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(AiyubaAdvApi.BASEURL)
                .build();
        VerifyApi verifyApi = retrofit.create(VerifyApi.class);

        String version = HelpUtil.getAppVersion(context, context.getPackageName());
        if (OtherUtil.isBelongToOppoPhone()) {
            version = "oppo_" + version;
        }

        String channel = ChannelReaderUtil.getChannel(context);
        verifyApi.verifyMoc(VerifyApi.BASEURL_1, ConstantNew.getNovelLimitChannelId(channel), version)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppCheckResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        verifyNovelDis = d;
                    }

                    @Override
                    public void onNext(AppCheckResponse response) {
                        if (response.getResult().equals("0")) {
                            AbilityControlManager.getInstance().setLimitNovel(false);
                        } else {
                            AbilityControlManager.getInstance().setLimitNovel(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        AbilityControlManager.getInstance().setLimitNovel(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取人教版内容控制
    private Disposable verifyPepDis;

    public void checkPepVerify(Context context){
        RxUtil.unsubscribe(verifyPepDis);

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(AiyubaAdvApi.BASEURL)
                .build();
        VerifyApi verifyApi = retrofit.create(VerifyApi.class);

        String version = HelpUtil.getAppVersion(context, context.getPackageName());
        if (OtherUtil.isBelongToOppoPhone()) {
            version = "oppo_" + version;
        }

        String channel = ChannelReaderUtil.getChannel(context);
        verifyApi.verifyMoc(VerifyApi.BASEURL_1, ConstantNew.getRenLimitChannelId(channel), version)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppCheckResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        verifyPepDis = d;
                    }

                    @Override
                    public void onNext(AppCheckResponse response) {
                        if (response.getResult().equals("0")) {
                            AbilityControlManager.getInstance().setLimitPep(false);
                        } else {
                            AbilityControlManager.getInstance().setLimitPep(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        AbilityControlManager.getInstance().setLimitPep(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取微课内容控制
    private Disposable verifyMocDis;

    public void checkMocVerify(Context context){
        RxUtil.unsubscribe(verifyMocDis);

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(AiyubaAdvApi.BASEURL)
                .build();
        VerifyApi verifyApi = retrofit.create(VerifyApi.class);

        String version = HelpUtil.getAppVersion(context, context.getPackageName());
        if (OtherUtil.isBelongToOppoPhone()) {
            version = "oppo_" + version;
        }

        String channel = ChannelReaderUtil.getChannel(context);
        verifyApi.verifyMoc(VerifyApi.BASEURL_1, ConstantNew.getMocLimitChannelId(channel), version)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppCheckResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        verifyMocDis = d;
                    }

                    @Override
                    public void onNext(AppCheckResponse response) {
                        if (response.getResult().equals("0")) {
                            AbilityControlManager.getInstance().setLimitMoc(false);
                        } else {
                            AbilityControlManager.getInstance().setLimitMoc(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        AbilityControlManager.getInstance().setLimitMoc(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取视频内容控制
    private Disposable verifyVideoDis;

    public void checkVideoVerify(Context context){
        RxUtil.unsubscribe(verifyVideoDis);

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(AiyubaAdvApi.BASEURL)
                .build();
        VerifyApi verifyApi = retrofit.create(VerifyApi.class);

        String version = HelpUtil.getAppVersion(context, context.getPackageName());
        if (OtherUtil.isBelongToOppoPhone()) {
            version = "oppo_" + version;
        }

        String channel = ChannelReaderUtil.getChannel(context);
        verifyApi.verifyMoc(VerifyApi.BASEURL_1, ConstantNew.getVideoLimitChannelId(channel), version)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppCheckResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        verifyVideoDis = d;
                    }

                    @Override
                    public void onNext(AppCheckResponse response) {
                        if (response.getResult().equals("0")) {
                            AbilityControlManager.getInstance().setLimitVideo(false);
                        } else {
                            AbilityControlManager.getInstance().setLimitVideo(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        AbilityControlManager.getInstance().setLimitVideo(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //关闭审核操作
    public void stopVerify(){
        LibRxUtil.unDisposable(verifyConceptDis);
        LibRxUtil.unDisposable(verifyPepDis);
        LibRxUtil.unDisposable(verifyMocDis);
        LibRxUtil.unDisposable(verifyVideoDis);
        LibRxUtil.unDisposable(verifyJuniorDis);
        LibRxUtil.unDisposable(verifyNovelDis);
    }
}
