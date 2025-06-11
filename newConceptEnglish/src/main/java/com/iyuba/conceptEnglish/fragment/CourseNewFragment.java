package com.iyuba.conceptEnglish.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.han.utils.AdvertisingKey;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.ad.show.AdShowUtil;
import com.iyuba.config.AdTestKeyData;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.activity.BuyIyubiActivity;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.iyuba.core.me.pay.PayOrderActivity;
import com.iyuba.imooclib.IMooc;
import com.iyuba.imooclib.ImoocManager;
import com.iyuba.imooclib.event.ImoocBuyIyubiEvent;
import com.iyuba.imooclib.event.ImoocBuyVIPEvent;
import com.iyuba.imooclib.event.ImoocPayCourseEvent;
import com.iyuba.imooclib.ui.mobclass.MobClassFragment;
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
 * 非懒加载的界面操作
 */

public class CourseNewFragment extends Fragment {
    private MobClassFragment mobClassFragment;
    private View root;

    private Handler handler = new Handler();

    public static CourseNewFragment getInstance(){
        CourseNewFragment fragment = new CourseNewFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = View.inflate(getActivity(),R.layout.layout_container,null);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initStudy();
    }

    public void initStudy() {
        //学一学
        ImoocManager.appId = Constant.APPID;
        setUser();
        //根据要求显示广告
        IMooc.setAdAppId(String.valueOf(AdShowUtil.NetParam.getAdId()));
        IMooc.setStreamAdPosition(AdShowUtil.NetParam.SteamAd_startIndex,AdShowUtil.NetParam.SteamAd_intervalIndex);
        IMooc.setYoudaoId(AdTestKeyData.KeyData.TemplateAdKey.template_youdao);
        IMooc.setYdsdkTemplateKey(AdTestKeyData.KeyData.TemplateAdKey.template_csj,AdTestKeyData.KeyData.TemplateAdKey.template_ylh,AdTestKeyData.KeyData.TemplateAdKey.template_ks,AdTestKeyData.KeyData.TemplateAdKey.template_baidu,AdTestKeyData.KeyData.TemplateAdKey.template_vlion);

        ArrayList<Integer> list = new ArrayList<>();

        if (getActivity().getPackageName().equals(AdvertisingKey.releasePackage)) {
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
        getChildFragmentManager().beginTransaction().add(R.id.container, mobClassFragment).show(mobClassFragment).commitAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }

    /*********************回调******************/
    //微课购买vip
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(ImoocBuyVIPEvent event) {
        if (!UserInfoManager.getInstance().isLogin()) {
            LoginUtil.startToLogin(getActivity());
            return;
        }
        NewVipCenterActivity.start(getActivity(),NewVipCenterActivity.VIP_GOLD);
    }

    //微课直购
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocPayCourseEvent event){
        if (!UserInfoManager.getInstance().isLogin()) {
            LoginUtil.startToLogin(getActivity());
            return;
        }

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
            LoginUtil.startToLogin(getActivity());
            return;
        }

        Intent intent = new Intent();
        intent.setClass(getActivity(), BuyIyubiActivity.class);
        intent.putExtra("title", "爱语币充值");
        startActivity(intent);
    }

    //会员购买回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(VipChangeEvent vipChangeEvent) {
        setUser();
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
        IMooc.notifyCoursePurchased();
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
}
