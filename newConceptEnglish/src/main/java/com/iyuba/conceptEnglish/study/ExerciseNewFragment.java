package com.iyuba.conceptEnglish.study;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.ConstantNew;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.FragmentExerciseNewBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.NewLoginActivity;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.jn.yyz.practise.PractiseInit;
import com.jn.yyz.practise.fragment.PractiseFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 新版练习题界面
 */
public class ExerciseNewFragment extends BaseViewBindingFragment<FragmentExerciseNewBinding> {

    public static ExerciseNewFragment getInstance(String type,int voaId,int position){
        ExerciseNewFragment fragment = new ExerciseNewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.type,type);
        bundle.putInt(StrLibrary.voaId,voaId);
        bundle.putInt(StrLibrary.position,position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initClick();
        initFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initClick(){
        binding.startExercise.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                NewLoginActivity.start(getActivity(), ConstantNew.loginType);
                return;
            }

            //当前位置(第一个免费，后续付费)
            int position = getArguments().getInt(StrLibrary.position,0);

            //判断vip用户
            if (position>0 && !UserInfoManager.getInstance().isVip()){
                new AlertDialog.Builder(requireActivity())
                        .setTitle("会员购买")
                        .setMessage("非会员仅能练习第一课的内容，会员无限制。是否开通会员使用?")
                        .setPositiveButton("开通会员", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                NewVipCenterActivity.start(requireActivity(),NewVipCenterActivity.VIP_APP);
                            }
                        }).setNegativeButton("暂不使用",null)
                        .setCancelable(false)
                        .create().show();
                return;
            }

            PractiseInit.setUid(UserInfoManager.getInstance().getUserId());
            binding.startLayout.setVisibility(View.GONE);
        });
    }

    private void initFragment(){
        String type = getArguments().getString(StrLibrary.type);
        int voaId = getArguments().getInt(StrLibrary.voaId);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PractiseFragment practiseFragment = PractiseFragment.newInstance(false,false,"练习题",type,0,String.valueOf(voaId),PractiseFragment.page_exerciseOther);
        transaction.add(R.id.container,practiseFragment).show(practiseFragment).commitNowAllowingStateLoss();
    }
}
