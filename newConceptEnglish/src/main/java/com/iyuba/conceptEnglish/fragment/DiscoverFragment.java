/*
 * 文件名
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.conceptEnglish.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.iyuba.conceptEnglish.lil.concept_other.word.wordNote.WordNoteActivity;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.discover.activity.AppGround;
import com.iyuba.core.discover.activity.MovieNewActivity;
import com.iyuba.core.discover.activity.Saying;
import com.iyuba.core.discover.activity.SearchFriend;
import com.iyuba.core.discover.activity.SearchWord;
import com.iyuba.core.discover.activity.mob.SimpleMobClassList;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.iyuba.core.talkshow.TalkShowActivity;
import com.iyuba.core.teacher.activity.FindTeacherActivity;
import com.iyuba.core.teacher.activity.QuestionNotice;
import com.iyuba.core.teacher.activity.TeacherActivity;
import com.iyuba.core.teacher.activity.TeacherBaseInfo;
import com.iyuba.core.teacher.activity.TheQuesListActivity;
import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.IHeadlineManager;
import com.iyuba.headlinelibrary.data.model.Headline;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.TextContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity;
import com.iyuba.imooclib.ui.content.ContentActivity;
import com.iyuba.lib.R;
import com.iyuba.lib.databinding.DiscoverBinding;
import com.iyuba.module.headlinesearch.event.HeadlineSearchItemEvent;
import com.iyuba.module.headlinesearch.ui.MSearchActivity;
import com.iyuba.module.movies.event.IMovieGoVipCenterEvent;
import com.iyuba.module.movies.ui.movie.MovieActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 类名
 *
 * @author 作者 <br/>
 * 实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 *
 * 发现界面
 */
public class DiscoverFragment extends Fragment {
    //界面样式
    private DiscoverBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DiscoverBinding.inflate(inflater,container,false);

        //设置view高度为statusbar的高度，并填充statusbar
        View mStatusBar = binding.fillStatusBarView;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mStatusBar.getLayoutParams();
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lp.height = getStatusHeight(getActivity());
        mStatusBar.setLayoutParams(lp);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //设置控件
        initWidget();
    }

    private void initWidget() {
        //青少版配音
        binding.rlTalk.setOnClickListener(v->{
            startActivity(new Intent(getActivity(),TalkShowActivity.class));
        });
        //返回按钮
        binding.buttonBack.setVisibility(View.VISIBLE);
        binding.buttonBack.setOnClickListener(v->{
            getActivity().finish();
        });
        //英语头条
        binding.headline.setOnClickListener(v->{
//            startActivity(new Intent(getActivity(),HeadlineActivity.class));
        });
        //听说系列应用
        binding.news.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), AppGround.class);
            intent.putExtra("title", R.string.discover_news);
            startActivity(intent);
        });
        //考试系列应用
        binding.exam.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), AppGround.class);
            intent.putExtra("title", R.string.discover_exam);
            startActivity(intent);
        });
        //移动课堂
        binding.mob.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), SimpleMobClassList.class);
            intent.putExtra("title", R.string.discover_mobclass);
            startActivity(intent);
        });
        //问一问
        binding.ansQues.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), TeacherActivity.class);
            startActivity(intent);
        });
        //看一看
        binding.watchWatch.setOnClickListener(v->{
            startActivity(new Intent(getActivity(), MovieActivity.class));
        });
        //搜一搜
        binding.searchSearch.setOnClickListener(v->{
            String[] types = new String[]{HeadlineType.NEWS, HeadlineType.SONG, HeadlineType.VOA, HeadlineType.BBC, HeadlineType.TED, HeadlineType.VIDEO, HeadlineType.CLASS,};
            startActivity(MSearchActivity.buildIntent(getContext(), types));
        });
        //爱语吧旗下安卓应用
        binding.all.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setClass(getActivity(), Web.class);
            intent.putExtra("url", "http://app." + Constant.IYUBA_CN + "android");
            intent.putExtra("title", getActivity().getString(R.string.discover_appall));
            startActivity(intent);
        });
        //查一查
        binding.searchWord.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setClass(getActivity(), SearchWord.class);
            startActivity(intent);
        });
        //经典谚语
        binding.saying.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setClass(getActivity(), Saying.class);
            startActivity(intent);
        });
        //生词本
        binding.collectWord.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                LoginUtil.startToLogin(getActivity());
                return;
            }

//            Intent intent = new Intent();
//            intent.setClass(getActivity(), WordCollection.class);
//            startActivity(intent);
            WordNoteActivity.start(getActivity());
        });
        //找朋友
        binding.discoverSearchFriend.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()) {
                LoginUtil.startToLogin(getActivity());
                return;
            }

            Intent intent = new Intent();
            intent.setClass(getActivity(), SearchFriend.class);
            startActivity(intent);
        });
        //教师认证
        binding.discoverSearchCerteacher.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()) {
                LoginUtil.startToLogin(getActivity());
                return;
            }

            Intent intent = new Intent();
            intent.setClass(getActivity(), TeacherBaseInfo.class);
            startActivity(intent);
        });
        //朋友圈
        binding.discoverSearchFriend.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
                LoginUtil.startToLogin(getActivity());
                return;
            }

            Intent intent = new Intent();
            intent.setClass(getActivity(), SearchFriend.class);
            startActivity(intent);
        });
        //找名师
        binding.discoverSearchTeacher.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                LoginUtil.startToLogin(getActivity());
                return;
            }

            Intent intent = new Intent();
            intent.setClass(getActivity(), FindTeacherActivity.class);
            startActivity(intent);
        });
        //摇一摇
        binding.discoverVibrate.setOnClickListener(v->{

        });
        //看视频
        binding.studybs.setOnSystemUiVisibilityChangeListener(v->{
            startActivity(new Intent(getActivity(), MovieNewActivity.class));
        });
        //我的回复
        binding.discoverMysub.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setClass(getActivity(), TheQuesListActivity.class);
            intent.putExtra("utype", "2");
            startActivity(intent);
        });
        //我的问题
        binding.discoverMyq.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                LoginUtil.startToLogin(getActivity());
                return;
            }

            Intent myq = new Intent();
            myq.setClass(getActivity(), TheQuesListActivity.class);
            myq.putExtra("utype", "4");
            startActivity(myq);
        });
        //问题动态
        binding.discoverQnotice.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                LoginUtil.startToLogin(getActivity());
                return;
            }

            Intent qnotice = new Intent();
            qnotice.setClass(getActivity(), QuestionNotice.class);
            startActivity(qnotice);
        });

        // TODO: 2024/1/31 根据要求，暂时关闭找朋友的功能(李涛-新概念群组)
        binding.friendLayout.setVisibility(View.GONE);

        //这里出于展示原音，只显示ai学习和名师堂，其他的关闭
        binding.myAnswerLayout.setVisibility(View.GONE);
        binding.moreAppLayout.setVisibility(View.GONE);
        binding.teacherLayout.setVisibility(View.GONE);
    }

    private void showNormalDialog(String content) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
        normalDialog.setIcon(R.drawable.iyubi_icon);
        normalDialog.setTitle("提示");
        normalDialog.setMessage(content);
        normalDialog.setPositiveButton("登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //...To-do
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), Login.class);
//                startActivity(intent);
                LoginUtil.startToLogin(getActivity());

            }
        });
        normalDialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //...To-do

            }
        });
        // 显示
        normalDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 美剧-下载开通会员
     *
     * @param event
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMovieGoVipCenterEvent event) {
        if (UserInfoManager.getInstance().isLogin()) {
            NewVipCenterActivity.start(getActivity(),NewVipCenterActivity.VIP_APP);
        } else {
            LoginUtil.startToLogin(getActivity());
        }
    }


    /**
     * 搜一搜列表点击
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HeadlineSearchItemEvent event) {
        Headline headline = event.headline;
        IHeadlineManager.appId = Constant.APPID;
        switch (headline.type) {
            case "news":
                startActivity(TextContentActivity.getIntent2Me(getContext(), headline));
                break;
            case "voa":
            case "csvoa":
            case "bbc":
                startActivity(AudioContentActivity.getIntent2Me(getContext(), headline));
                break;
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(getContext(), headline));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
                startActivity(VideoContentActivity.getIntent2Me(getContext(), headline));
                break;
            case "bbcwordvideo":
            case "topvideos":
                startActivity(VideoContentActivity.getIntent2Me(getContext(), headline));
                break;
            case "class": {
                int packId = Integer.parseInt(headline.id);
                Intent intent = ContentActivity.buildIntent(getContext(), packId,"class.jichu");
                startActivity(intent);
                break;
            }
        }
    }


    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

}
