package com.iyuba.conceptEnglish.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.ad.AdInitManager;
import com.iyuba.conceptEnglish.han.utils.AdvertisingKey;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.HomeMocProgressEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.AdShowUtil;
import com.iyuba.conceptEnglish.util.ScreenUtils;
import com.iyuba.config.AdTestKeyData;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.activity.BuyIyubiActivity;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.iyuba.core.me.pay.PayOrderActivity;
import com.iyuba.headlinelibrary.ui.title.DropdownTitleFragment;
import com.iyuba.imooclib.IMooc;
import com.iyuba.imooclib.ImoocManager;
import com.iyuba.imooclib.event.ImoocBuyIyubiEvent;
import com.iyuba.imooclib.event.ImoocBuyVIPEvent;
import com.iyuba.imooclib.event.ImoocPayCourseEvent;
import com.iyuba.imooclib.event.ImoocPlayEvent;
import com.iyuba.imooclib.ui.mobclass.MobClassFragment;
import com.iyuba.module.privacy.PrivacyAgreeEvent;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.module.user.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


/**
 * 微课的界面(两个界面，其中之一)
 * Created by iyuba on 2017/7/27.
 */

public class CourseFragment extends Fragment {
    private Context mContext;

    private DropdownTitleFragment mExtraFragment;

    private WindowManager mWindowManager;

    private MobClassFragment mobClassFragment;

    private View root;

    //是否跳转微课购买
    private boolean isJumpMocBuy = false;


    /**
     * 标记已加载完成，保证懒加载只能加载一次
     */
    private boolean hasLoaded = false;
    /**
     * 标记Fragment是否已经onCreate
     */
    private boolean isCreated = false;
    /**
     * 界面对于用户是否可见
     */
    private boolean isVisibleToUser = false;

    //是否已经播放过微课
    private boolean isPlayMoc = false;


    private Handler handler = new Handler();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
    }


    protected void initVariables() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_layout_video, null);

        /**
         * 设置view高度为statusbar的高度，并填充statusbar
         */
        View mStatusBar = root.findViewById(R.id.fillStatusBarView);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mStatusBar.getLayoutParams();
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lp.height = ScreenUtils.getStatusHeight(mContext);
        mStatusBar.setLayoutParams(lp);

        lazyLoad();
        isCreated = true;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return root;
    }


    /**
     * 监听界面是否展示给用户，实现懒加载
     * 这个方法也是网上的一些方法用的最多的一个，我的思路也是这个，不过把整体思路完善了一下
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.i("TAG", "setUserVisibleHint: ");
        //注：关键步骤
        this.isVisibleToUser = isVisibleToUser;
        lazyLoad();
    }

    /**
     * 懒加载方法，获取数据什么的放到这边来使用，在切换到这个界面时才进行网络请求
     */
    private void lazyLoad() {

        //如果该界面不对用户显示、已经加载、fragment还没有创建，
        //三种情况任意一种，不获取数据
        if (!isVisibleToUser || hasLoaded || !isCreated) {
            return;
        }
        initStudy();
        //注：关键步骤，确保数据只加载一次
        hasLoaded = true;
    }



    public void initStudy() {
        //设置appId
        ImoocManager.appId = Constant.APPID;
        //根据要求显示广告
        IMooc.setAdAppId(String.valueOf(AdShowUtil.NetParam.getAdId()));
        IMooc.setStreamAdPosition(AdShowUtil.NetParam.SteamAd_startIndex,AdShowUtil.NetParam.SteamAd_intervalIndex);
        IMooc.setYoudaoId(AdTestKeyData.KeyData.TemplateAdKey.template_youdao);
        IMooc.setYdsdkTemplateKey(AdTestKeyData.KeyData.TemplateAdKey.template_csj,AdTestKeyData.KeyData.TemplateAdKey.template_ylh,AdTestKeyData.KeyData.TemplateAdKey.template_ks,AdTestKeyData.KeyData.TemplateAdKey.template_baidu,AdTestKeyData.KeyData.TemplateAdKey.template_vlion);
        //设置用户信息
        setUser();

        ArrayList<Integer> list = new ArrayList<>();
        if (mContext.getPackageName().equals(AdvertisingKey.releasePackage)) {
            list.add(-2); //全部课程
            list.add(-1); //最新课程
            list.add(2); //英语四级
            list.add(3); //VOA英语
            list.add(4); //英语六级
            list.add(7); //托福TOEFL
            list.add(8); //考研英语一
            list.add(9); //BBC英语
            list.add(21); //新概念英语
            list.add(22); //走遍美国
//            list.add(28); //学位英语
//            list.add(52); //考研英语二
//            list.add(52); //雅思
            list.add(91); //中职英语

        } else {
            list.add(3); //VOA英语
//            list.add(9); //BBC英语
            list.add(21); //新概念英语
//            list.add(22); //走遍美国
        }

        Bundle args = MobClassFragment.buildArguments(21, false, list);
        mobClassFragment = MobClassFragment.newInstance(args);
        getChildFragmentManager().beginTransaction().add(R.id.content_video, mobClassFragment).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    //微课购买vip
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(ImoocBuyVIPEvent event) {
        if (!UserInfoManager.getInstance().isLogin()) {
            LoginUtil.startToLogin(mContext);
            return;
        }
        NewVipCenterActivity.start(getActivity(),NewVipCenterActivity.VIP_GOLD);
    }

    //微课直购
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocPayCourseEvent event){
        if (!UserInfoManager.getInstance().isLogin()) {
            LoginUtil.startToLogin(mContext);
            return;
        }

        isJumpMocBuy = true;

        String vipDesc = event.body;
        String vipBody = "花费"+event.price+"元购买微课("+event.body+")";
        String vipSubject = "微课直购";
        String vipPrice = event.price;

        Intent intent = PayOrderActivity.buildIntent(getActivity(),vipPrice,event.courseId,event.productId,vipSubject,vipBody,vipDesc,PayOrderActivity.Order_moc);
        startActivity(intent);
    }

    //微课购买爱语币
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocBuyIyubiEvent event){
        if (!UserInfoManager.getInstance().isLogin()) {
            LoginUtil.startToLogin(mContext);
            return;
        }

        Intent intent = new Intent();
        intent.setClass(mContext, BuyIyubiActivity.class);
        intent.putExtra("title", "爱语币充值");
        startActivity(intent);
    }

    //vip购买后的回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(VipChangeEvent vipChangeEvent) {
        setUser();

        //测试下操作
        if (mobClassFragment!=null){
            mobClassFragment.onResume();
        }
    }

    //微课播放的回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocPlayEvent event){
        isPlayMoc = true;
    }

    private void setUser() {
        User user = new User();
        user.vipStatus = UserInfoManager.getInstance().getVipStatus();
        user.name = UserInfoManager.getInstance().getUserName();
        user.uid = UserInfoManager.getInstance().getUserId();
        IyuUserManager.getInstance().setCurrentUser(user);
        if (user.uid==0){
            IyuUserManager.getInstance().logout();
        }

        //刷新微课购买
        if (isJumpMocBuy){
            IMooc.notifyCoursePurchased();
            isJumpMocBuy = false;
        }
    }

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        Random r = new Random();
        key = key + Math.abs(r.nextInt());
        key = key.substring(0, 15);
        return key;
    }

    @Override
    public void onResume() {
        super.onResume();

        //刷新首页的微课进度
        if (isPlayMoc){
            isPlayMoc = false;
            EventBus.getDefault().post(new HomeMocProgressEvent());
        }
    }
}
