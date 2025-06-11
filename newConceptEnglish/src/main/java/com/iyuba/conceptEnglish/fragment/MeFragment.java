/*
 * 文件名
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.conceptEnglish.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.AIStudyActivity;
import com.iyuba.conceptEnglish.activity.BookMarketActivity;
import com.iyuba.conceptEnglish.activity.CalendarActivity;
import com.iyuba.conceptEnglish.activity.ContainerActivity;
import com.iyuba.conceptEnglish.activity.InfoFullFillActivity;
import com.iyuba.conceptEnglish.activity.OfficialAccountListActivity;
import com.iyuba.conceptEnglish.activity.OralShowWrapperActivity;
import com.iyuba.conceptEnglish.activity.RankActivity;
import com.iyuba.conceptEnglish.activity.SendBookActivity;
import com.iyuba.conceptEnglish.activity.SetActivity;
import com.iyuba.conceptEnglish.activity.SignActivity;
import com.iyuba.conceptEnglish.ad.AdInitManager;
import com.iyuba.conceptEnglish.api.ApiRetrofit;
import com.iyuba.conceptEnglish.api.QQGroupApi;
import com.iyuba.conceptEnglish.databinding.MeBinding;
import com.iyuba.conceptEnglish.event.ChangeUsernameEvent;
import com.iyuba.conceptEnglish.event.MoneyChangeEvent;
import com.iyuba.conceptEnglish.han.utils.ExpandKt;
import com.iyuba.conceptEnglish.lil.concept_other.exercise_new.ExerciseNewShowActivity;
import com.iyuba.conceptEnglish.lil.concept_other.me_localNews.LocalNewsActivity;
import com.iyuba.conceptEnglish.lil.concept_other.me_wallet.WalletListActivity;
import com.iyuba.conceptEnglish.lil.concept_other.talkshow_fix.Fix_MyTalkActivity;
import com.iyuba.conceptEnglish.lil.concept_other.verify.AbilityControlManager;
import com.iyuba.conceptEnglish.lil.concept_other.word.wordNote.WordNoteActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.PractiseShowActivity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.line.PractiseLineShowBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.ui.video.VideoShowActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.protocol.SignRequest;
import com.iyuba.conceptEnglish.protocol.SignResponse;
import com.iyuba.conceptEnglish.sqlite.mode.StudyTimeBeanNew;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.study.StudyNewActivity;
import com.iyuba.conceptEnglish.util.QQUtil;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.message.RequestUserDetailInfo;
import com.iyuba.core.common.protocol.message.ResponseUserDetailInfo;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.discover.activity.MovieNewActivity;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.listener.UserinfoCallbackListener;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.core.me.activity.BuyIyubiActivity;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivityNew;
import com.iyuba.headlinelibrary.ui.content.TextContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivityNew;
import com.iyuba.headlinelibrary.ui.video.VideoMiniContentActivity;
import com.iyuba.imooclib.ui.record.PurchaseRecordActivity;
import com.iyuba.module.favor.BasicFavor;
import com.iyuba.module.favor.data.model.BasicFavorPart;
import com.iyuba.module.favor.event.FavorItemEvent;
import com.iyuba.module.favor.ui.BasicFavorActivity;
import com.iyuba.module.intelligence.ui.LearnResultActivity;
import com.iyuba.module.intelligence.ui.LearningGoalActivity;
import com.iyuba.module.intelligence.ui.TestResultActivity;
import com.iyuba.module.intelligence.ui.WordResultActivity;
import com.iyuba.module.movies.ui.series.SeriesActivity;
import com.iyuba.module.toolbox.MD5;
import com.jn.yyz.practise.PractiseInit;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;
import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.PersonalType;
import personal.iyuba.personalhomelibrary.data.model.GetGroup;
import personal.iyuba.personalhomelibrary.event.ArtDataSkipEvent;
import personal.iyuba.personalhomelibrary.event.UserNameChangeEvent;
import personal.iyuba.personalhomelibrary.ui.groupChat.GroupChatManageActivity;
import personal.iyuba.personalhomelibrary.ui.groupChat.getGroupInfo.AppGroupInfo;
import personal.iyuba.personalhomelibrary.ui.groupChat.getGroupInfo.GetGroupCallBack;
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;
import personal.iyuba.personalhomelibrary.ui.message.MessageActivity;
import personal.iyuba.personalhomelibrary.ui.my.MyCommentsActivity;
import personal.iyuba.personalhomelibrary.ui.my.MySpeechActivity;
import personal.iyuba.personalhomelibrary.ui.search.SearchGroupActivity;
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryActivity;
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


/**
 * 类名
 *
 * @author 作者 <br/>
 * 实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class MeFragment extends BaseViewBindingFragment<MeBinding> {
    private static final int HANLDER_QQ = 7;
    private Context mContext;

    private ResponseUserDetailInfo userDetailInfo;
    private boolean infoFlag = false, levelFlag = false;

    private int iyubaGroupId = Constant.GROUP_ID;
    private String iyubaGroupTitle = Constant.GROUP_NAME;

    private View rootView;

    private TextView meProtocol;

    private View reMessage,reAppGroup,collectWord,meQqGroup,personalhome,rlOfficialAccountList;
    private View tvLogout,meVip,rlClockInHistory,meOralshow,meSendBook,meCollectLayout;
    private View meLocal,meLove,meRead;
    private View meSummary;
    private View llMoney,llScore,llIyubi;
    private View reSearchGroup,meComment,discoverIyubaset,meRank,intelUserinfo,meMediaBook,meDiscover,meVideo,meTalk,meSpeakCircle;
    private View intelGoal,intelResult,intelWordResult,intelTestResult,intelAbilityTest;

    private View tvTipGroup,talkTips,talkLayout,vipMediaBookLayout,groupTitle,groupLayout;
    private View otherTitle,otherLayout,scorePic,moneyPic,iyubiPic,meSummaryLine;
    private TextView tvLogin,tvUserType,tvName,scoreTips,tvScore,tvIyubi,tvMoney,tvQqGroup;
    private TextView moneyTips,iyubiTips,vipCenter;
    private ImageView imgVip,mePic;

    private View mocPayMark;
    private View videoLayout,videoWatch,videoCollect,videoTalk;
    private View exerciseNewShow,exerciseNewRank,exerciseNewNote;

    //这里判断下是否是从视频模块出来的，因为视频模块点击广告后没有回调事件，手动触发
    private boolean isToVideo = false;

    //广告测试按钮
    private View adTestView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.me,null);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initLocal();
        initClick();
        getQQGroup();
        initProtocol();
    }

    @Override
    public void onResume() {
        super.onResume();

        MobclickAgent.onResume(mContext);
        if (UserInfoManager.getInstance().isLogin()){
            handler.sendEmptyMessage(6);
        }

        if (UserInfoManager.getInstance().isLogin()) {
            //个人中心面壁者
            int uid=UserInfoManager.getInstance().getUserId();
            String username=UserInfoManager.getInstance().getUserName();
            String vipState=UserInfoManager.getInstance().getVipStatus();
            PersonalHome.setSaveUserinfo(uid,username,vipState);
            PersonalHome.setCategoryType(PersonalType.NCE);
            AppGroupInfo.Instance().getGroupInfo(callBack);
        } else {
            Timber.d("setTextViewContent By resume");
        }

        setTextViewContent();

        //如果从视频模块中出来，则刷新数据
        if (isToVideo){
            isToVideo = false;

            if (UserInfoManager.getInstance().isLogin()){
                UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), null);
            }
        }

        //应用宝因为版权问题暂时关闭
        videoLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView(){
        meProtocol = rootView.findViewById(R.id.me_protocol);
        reMessage = rootView.findViewById(R.id.re_message);
        reAppGroup = rootView.findViewById(R.id.re_app_group);
        collectWord = rootView.findViewById(R.id.collect_word);
        meQqGroup = rootView.findViewById(R.id.me_qq_group);
        personalhome = rootView.findViewById(R.id.personalhome);
        rlOfficialAccountList = rootView.findViewById(R.id.rl_official_account_list);
        scorePic = rootView.findViewById(R.id.score_pic);
        moneyPic = rootView.findViewById(R.id.money_pic);
        iyubiPic = rootView.findViewById(R.id.iyubi_pic);
        meSummaryLine = rootView.findViewById(R.id.me_summary_line);

        tvTipGroup = rootView.findViewById(R.id.tv_tip_group);
        tvLogin = rootView.findViewById(R.id.tv_login);
        tvLogout = rootView.findViewById(R.id.tv_logout);
        tvUserType = rootView.findViewById(R.id.tv_user_type);
        tvName = rootView.findViewById(R.id.tv_name);
        imgVip = rootView.findViewById(R.id.img_vip);
        scoreTips = rootView.findViewById(R.id.score_tips);
        tvScore = rootView.findViewById(R.id.tv_score);
        tvIyubi = rootView.findViewById(R.id.tv_iyubi);
        tvMoney = rootView.findViewById(R.id.tv_money);
        mePic = rootView.findViewById(R.id.me_pic);
        tvQqGroup = rootView.findViewById(R.id.tv_qq_group);
        otherTitle = rootView.findViewById(R.id.other_title);

        meVip = rootView.findViewById(R.id.me_vip);
        rlClockInHistory = rootView.findViewById(R.id.rl_clock_in_history);
        meOralshow = rootView.findViewById(R.id.me_oralshow);
        meSendBook = rootView.findViewById(R.id.me_send_book);
        moneyTips = rootView.findViewById(R.id.money_tips);
        iyubiTips = rootView.findViewById(R.id.iyubi_tips);
        vipCenter = rootView.findViewById(R.id.vip_center);
        talkTips = rootView.findViewById(R.id.talk_tips);
        talkLayout = rootView.findViewById(R.id.talk_layout);
        vipMediaBookLayout = rootView.findViewById(R.id.vip_media_book_layout);
        groupTitle = rootView.findViewById(R.id.group_title);
        groupLayout = rootView.findViewById(R.id.group_layout);
        otherLayout = rootView.findViewById(R.id.other_layout);

        meLocal = rootView.findViewById(R.id.me_local);
        meLove = rootView.findViewById(R.id.me_love);
        meRead = rootView.findViewById(R.id.me_read);

        meSummary = rootView.findViewById(R.id.me_summary);

        llMoney = rootView.findViewById(R.id.ll_money);
        llScore = rootView.findViewById(R.id.ll_score);
        llIyubi = rootView.findViewById(R.id.ll_iyubi);

        reSearchGroup = rootView.findViewById(R.id.re_search_group);
        meComment = rootView.findViewById(R.id.me_comment);
        discoverIyubaset = rootView.findViewById(R.id.discover_iyubaset);
        meRank = rootView.findViewById(R.id.me_rank);
        intelUserinfo = rootView.findViewById(R.id.intel_userinfo);
        meMediaBook = rootView.findViewById(R.id.me_media_book);
        meDiscover = rootView.findViewById(R.id.me_discover);
        meVideo = rootView.findViewById(R.id.me_video);
        meTalk = rootView.findViewById(R.id.me_talk);
        meSpeakCircle = rootView.findViewById(R.id.me_speak_circle);

        intelGoal = rootView.findViewById(R.id.intel_goal);
        intelResult = rootView.findViewById(R.id.intel_result);
        intelWordResult = rootView.findViewById(R.id.intel_word_result);
        intelTestResult = rootView.findViewById(R.id.intel_test_result);
        intelAbilityTest = rootView.findViewById(R.id.intel_ability_test);

        //微课
//        mocLayout = rootView.findViewById(R.id.mocLayout);
//        mocLearn = rootView.findViewById(R.id.mocLearn);
        mocPayMark = rootView.findViewById(R.id.mocPayMark);
        //视频
        videoLayout = rootView.findViewById(R.id.videoLayout);
        videoWatch = rootView.findViewById(R.id.videoWatch);
        videoCollect = rootView.findViewById(R.id.videoCollect);
        videoTalk = rootView.findViewById(R.id.videoTalk);

        adTestView = rootView.findViewById(R.id.adLayout);

        exerciseNewShow = rootView.findViewById(R.id.exerciseShow);
        exerciseNewRank = rootView.findViewById(R.id.exerciseRank);
        exerciseNewNote = rootView.findViewById(R.id.exerciseNote);
    }

    private void initProtocol() {
        meProtocol.setText(ExpandKt.getProtocolText(mContext));
        meProtocol.setMovementMethod(ScrollingMovementMethod.getInstance());
        meProtocol.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initClick(){
        //消息中心
        reMessage.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
                startActivity(new Intent(getContext(), MessageActivity.class));
            } else {
                gotoLoginActivity();
            }
        });
        //新概念英语官方群
        reAppGroup.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
                GroupChatManageActivity.start(getContext(), iyubaGroupId,
                        iyubaGroupTitle, true);
                if (ConfigManager.Instance().loadBoolean("showTV", true)) {
                    ConfigManager.Instance().putBoolean("showTV", false);
                }
            } else {
                gotoLoginActivity();
            }
        });
        //生词本
        collectWord.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
//                Intent intent = new Intent();
//                intent.setClass(mContext, WordCollection.class);
//                startActivity(intent);

                WordNoteActivity.start(getActivity());
            } else {
                gotoLoginActivity();
            }
        });
        //qq交流群
        meQqGroup.setOnClickListener(v->{
            QQUtil.startQQGroup(mContext, ConfigManager.Instance().getQQKey());
        });
        //个人中心
        personalhome.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
                startActivity(PersonalHomeActivity.buildIntent(getActivity(), UserInfoManager.getInstance().getUserId(), UserInfoManager.getInstance().getUserName(), 0));
            } else {
                gotoLoginActivity();
            }
        });
        //公众号列表
        rlOfficialAccountList.setOnClickListener(v->{
            startActivity(new Intent(mContext, OfficialAccountListActivity.class));
        });
        //打卡或者登录
        tvLogin.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                gotoLoginActivity();
            }else {
                clockIn();
            }
        });
        //登出
        tvLogout.setOnClickListener(v->{
            new AlertDialog.Builder(mContext)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.alert_title)
                    .setMessage(R.string.logout_alert)
                    .setPositiveButton(R.string.alert_btn_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.sendEmptyMessage(4);
                        }
                    })
                    .setNeutralButton(R.string.alert_btn_cancel, null)
                    .show();
        });
        //会员中心
        meVip.setOnClickListener(v->{
            NewVipCenterActivity.start(getActivity(),NewVipCenterActivity.VIP_APP);
        });
        //打卡记录
        rlClockInHistory.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
                startActivity(new Intent(mContext, CalendarActivity.class));
            } else {
                gotoLoginActivity();
            }
        });
        //口语秀
        meOralshow.setOnClickListener(v->{
            startActivity(new Intent(mContext, OralShowWrapperActivity.class));
        });
        //点我送书
        meSendBook.setOnClickListener(v->{
            startActivity(new Intent(mContext, SendBookActivity.class));
        });
        //视频
        videoWatch.setOnClickListener(v->{
            isToVideo = true;

            VideoShowActivity.start(getActivity());
        });
        //视频收藏
        videoCollect.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
                List<String> types = new ArrayList<>();
                types.add(HeadlineType.SMALLVIDEO);
                BasicFavor.setTypeFilter(types);

                Intent intents = BasicFavorActivity.buildIntent(mContext);
                startActivity(intents);
            } else {
                gotoLoginActivity();
            }
        });
        //视频配音
        videoTalk.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
                ArrayList<String> types = new ArrayList<>();
                types.add(HeadlineType.SMALLVIDEO);


                startActivity(com.iyuba.module.headlinetalk.ui.mytalk.MyTalkActivity.buildIntent(getActivity(),types));
            } else {
                gotoLoginActivity();
            }
        });
        //本地篇目
        meLocal.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()){
                LocalNewsActivity.start(getActivity(),0);
            }else {
                gotoLoginActivity();
            }
        });
        //最爱篇目
        meLove.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()){
                LocalNewsActivity.start(getActivity(),1);
            }else {
                gotoLoginActivity();
            }
        });
        //历史篇目
        meRead.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()){
                LocalNewsActivity.start(getActivity(),2);
            }else {
                gotoLoginActivity();
            }
        });
        //学习报告
        meSummary.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
//                AccountManager.getInstance().setIyuUser();

                String[] types = null;
                String showType = "all";
                if (AbilityControlManager.getInstance().isLimitMoc()){
                    types = new String[]{
                            SummaryType.LISTEN,
                            SummaryType.EVALUATE,
                            SummaryType.WORD,
                            //SummaryType.TEST,//这里李涛在新概念群里说暂时屏蔽，数据对不起来
                            SummaryType.READ
                    };
                }else {
                    types = new String[]{
                            SummaryType.LISTEN,
                            SummaryType.EVALUATE,
                            SummaryType.WORD,
                            //SummaryType.TEST,//这里李涛在新概念群里说暂时屏蔽，数据对不起来
                            SummaryType.MOOC,
                            SummaryType.READ
                    };
                }

                startActivity(SummaryActivity.getIntent(mContext,showType,types, 0));
            } else {
                gotoLoginActivity();
            }
        });
        //钱包
        llMoney.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()){
//                initWallet();

                //进入钱包的列表界面
                WalletListActivity.start(getActivity());
            }else {
                gotoLoginActivity();
            }
        });
        //积分
        llScore.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {

                String url = "http://m."+Constant.IYUBA_CN+"mall/index.jsp?"
                        + "&uid=" + UserInfoManager.getInstance().getUserId()
                        + "&sign=" + MD5.getMD5ofStr("iyuba" + UserInfoManager.getInstance().getUserId() + "camstory")
                        + "&username=" + UserInfoManager.getInstance().getUserName()
                        + "&platform=android&appid="
                        + Constant.APPID;
                Intent intent = new Intent(mContext, Web.class);
                intent.putExtra("title", "积分商城");
                intent.putExtra("url", url);
                startActivity(intent);
            } else {
                gotoLoginActivity();
            }
        });
        //爱语币
        llIyubi.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
                Intent intent = new Intent();
                intent.setClass(mContext, BuyIyubiActivity.class);
                intent.putExtra("title", "爱语币充值");
                startActivity(intent);
            } else {
                gotoLoginActivity();
            }
        });
        //搜索群
        reSearchGroup.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
                startActivity(new Intent(getContext(), SearchGroupActivity.class));
            } else {
//                startActivity(new Intent(mContext, Login.class));
                LoginUtil.startToLogin(mContext);
            }
        });
        //评论圈
        meComment.setOnClickListener(v->{
            startActivity(new Intent(mContext, MyCommentsActivity.class));
        });
        //应用设置
        discoverIyubaset.setOnClickListener(v->{
            Intent intent = new Intent();
            intent.setClass(mContext, SetActivity.class);
            startActivity(intent);
        });
        //排行榜
        meRank.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()){
                startActivity(new Intent(mContext, RankActivity.class));
            }else {
                gotoLoginActivity();
            }
        });
        //智能化学习目标
        intelGoal.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                gotoLoginActivity();
                return;
            }

            Intent intent = LearningGoalActivity.buildIntent(mContext);
            startActivity(intent);
        });
        //个人信息完善
        intelUserinfo.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                gotoLoginActivity();
                return;
            }

            Intent intent = new Intent();
            intent.setClass(mContext, InfoFullFillActivity.class);
            startActivity(intent);
        });
        //智能化学习成果
        intelResult.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                gotoLoginActivity();
                return;
            }

            if (infoFlag) {
                Toast toast = Toast.makeText(mContext, "请先完善个人信息", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else if (levelFlag) {
                Toast toast = Toast.makeText(mContext, "请先完善学习目标", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                Intent intent = LearnResultActivity.buildIntent(mContext);
                startActivity(intent);
            }
        });
        //单词大数据
        intelWordResult.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                gotoLoginActivity();
                return;
            }

            if (infoFlag) {
                Toast toast = Toast.makeText(mContext, "请先完善个人信息", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else if (levelFlag) {
                Toast toast = Toast.makeText(mContext, "请先完善学习目标信息", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                Intent intent = WordResultActivity.buildIntent(mContext);
                startActivity(intent);
            }
        });
        //智能化测试成果
        intelTestResult.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                gotoLoginActivity();
                return;
            }

            if (infoFlag) {
                Toast toast = Toast.makeText(mContext, "请先完善个人信息", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else if (levelFlag) {
                Toast toast = Toast.makeText(mContext, "请先完善学习目标信息", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                Intent intent = TestResultActivity.buildIntent(mContext);
                startActivity(intent);
            }
        });
        //我的智慧化评测
        intelAbilityTest.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()){
                AIStudyActivity.buildIntent(mContext, infoFlag, levelFlag);
            }else {
                gotoLoginActivity();
            }
        });
        //全媒体图书
        meMediaBook.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()){
                startActivity(new Intent(mContext, BookMarketActivity.class));
            }else {
                gotoLoginActivity();
            }
        });
        //发现
        meDiscover.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()){
                startActivity(new Intent(mContext, ContainerActivity.class));
            }else {
                gotoLoginActivity();
            }
        });
        //口语秀
        meVideo.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                gotoLoginActivity();
                return;
            }

            startActivity(new Intent(mContext, MovieNewActivity.class));
        });
        //我的配音
        meTalk.setOnClickListener(v->{
            if (UserInfoManager.getInstance().isLogin()) {
                startActivity(new Intent(mContext, Fix_MyTalkActivity.class));
            } else {
                gotoLoginActivity();
            }
        });
        //口语圈
        meSpeakCircle.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                gotoLoginActivity();
                return;
            }
            mContext.startActivity(new Intent(mContext, MySpeechActivity.class));
        });
        //微课学习
//        mocLearn.setOnClickListener(v->{
//            if (!AccountUtil.isLogin()){
//                gotoLoginActivity();
//                return;
//            }
//
//            MocShowActivity.start(getActivity());
//        });
        //购买记录
        mocPayMark.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                gotoLoginActivity();
                return;
            }

//            AccountManager.getInstance().setIyuUser();
            startActivity(PurchaseRecordActivity.buildIntent(getActivity()));
        });
        //练习题首页
        exerciseNewShow.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                gotoLoginActivity();
                return;
            }

            PractiseInit.setUid(UserInfoManager.getInstance().getUserId());
            PractiseShowActivity.start(getActivity(),PractiseShowActivity.showType_line);
        });
        //练习题排行
        exerciseNewRank.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                gotoLoginActivity();
                return;
            }

            PractiseInit.setUid(UserInfoManager.getInstance().getUserId());
            PractiseShowActivity.start(getActivity(),TypeLibrary.ExerciseNewShowType.type_rank);
        });
        //练习错题本
        exerciseNewNote.setOnClickListener(v->{
            if (!UserInfoManager.getInstance().isLogin()){
                gotoLoginActivity();
                return;
            }

            PractiseInit.setUid(UserInfoManager.getInstance().getUserId());
            ExerciseNewShowActivity.start(getActivity(),TypeLibrary.ExerciseNewShowType.type_note,TypeLibrary.ExerciseNewDataType.type_concept);
        });
    }

    private GetGroupCallBack callBack = new GetGroupCallBack() {
        @Override
        public void getSuccess(GetGroup group) {
            iyubaGroupId = group.groupId;
            iyubaGroupTitle = group.gptitle;
        }

        @Override
        public void getError() {
            //骚扰的场合远远多于实际作用，封存至理解意思为止。单例引用似乎使得生效范围过大
            //ToastFactory.showShort(mContext, "获取群信息失败");f
        }
    };

    private void initLogin() {
        if (!UserInfoManager.getInstance().isLogin()) {
            tvLogin.setText("登录");
            tvLogout.setVisibility(View.GONE);

            tvUserType.setText("未登录");
            tvName.setText("未登录");
            imgVip.setImageResource(R.drawable.ic_me_novip);
        } else {
            tvLogin.setText("打卡");
            tvLogout.setVisibility(View.VISIBLE);
            tvName.setText(UserInfoManager.getInstance().getUserName());
            if (UserInfoManager.getInstance().isVip()) {
                tvUserType.setText("VIP用户");
                imgVip.setImageResource(R.drawable.ic_me_isvip);
            } else {
                tvUserType.setText("普通用户");
                imgVip.setImageResource(R.drawable.ic_me_novip);
            }
        }
    }

    /**
     * 统一接口，跳转至登录页面
     */
    private void gotoLoginActivity() {
        LoginUtil.startToLogin(mContext);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    private void initLocal() {
        // TODO: 2023/11/22 李涛在新概念群众说关闭公众号列表的显示，李沁瑞确认小程序中已经没有公众号文章跳转界面 
        rlOfficialAccountList.setVisibility(View.GONE);

        //这里为什么在屏蔽广告的时候进行屏蔽，不清楚，暂不处理
        if (!AdInitManager.isShowAd()){
            reAppGroup.setVisibility(View.GONE);
            reMessage.setVisibility(View.GONE);
            reSearchGroup.setVisibility(View.GONE);
            meComment.setVisibility(View.GONE);
        }

        if (UserInfoManager.getInstance().isLogin()) {
            Timber.d("setTextViewContent By create");
            setTextViewContent();
        }

        if (!ConfigManager.Instance().loadBoolean("showTV", true)) {
            tvTipGroup.setVisibility(View.GONE);
        }

        //视频
        if (!AbilityControlManager.getInstance().isLimitVideo()){
            videoLayout.setVisibility(View.VISIBLE);
        }else {
            videoLayout.setVisibility(View.GONE);
        }

        //微课
        if (!AbilityControlManager.getInstance().isLimitMoc()){
            mocPayMark.setVisibility(View.VISIBLE);
        }else {
            mocPayMark.setVisibility(View.GONE);
        }

        //根据包名进行判断
        if (getActivity().getPackageName().equals(Constant.package_learnNewEnglish)){
            //新概念英语微课

            //全媒体图书
            vipMediaBookLayout.setVisibility(View.VISIBLE);
            vipMediaBookLayout.setOnClickListener(v->{
                startActivity(new Intent(mContext, BookMarketActivity.class));
            });

            //生词本
//            collectWordLayout.setVisibility(View.GONE);

            //口语、配音
//            talkTips.setVisibility(View.GONE);
//            talkLayout.setVisibility(View.GONE);

            //视频
            videoWatch.setVisibility(View.VISIBLE);

            //消息、聊天
            groupTitle.setVisibility(View.GONE);
            groupLayout.setVisibility(View.GONE);

            //其他
            otherTitle.setVisibility(View.GONE);
            otherLayout.setVisibility(View.GONE);

            //图标
            scoreTips.setVisibility(View.GONE);
            scorePic.setVisibility(View.VISIBLE);
            moneyTips.setVisibility(View.GONE);
            moneyPic.setVisibility(View.VISIBLE);
            iyubiTips.setVisibility(View.GONE);
            iyubiPic.setVisibility(View.VISIBLE);
        }else if (getActivity().getPackageName().equals(Constant.package_conceptStory)){
            //新概念人工智能学外语
            scoreTips.setText("score");
            moneyTips.setText("money");
            iyubiTips.setText("coin");
            vipCenter.setText("VIP Center");

            talkTips.setVisibility(View.GONE);
            talkLayout.setVisibility(View.GONE);
        }else if (getActivity().getPackageName().equals(Constant.package_concept2)
                ||getActivity().getPackageName().equals(Constant.package_englishfm)){
            //新概念英语全四册

            //视频
            videoWatch.setVisibility(View.VISIBLE);
        }

        //设置学习报告
        if (AbilityControlManager.getInstance().isLimitConcept()
                &&AbilityControlManager.getInstance().isLimitJunior()
                &&AbilityControlManager.getInstance().isLimitNovel()){
            meSummaryLine.setVisibility(View.GONE);
            meSummary.setVisibility(View.GONE);
        }else {
            meSummaryLine.setVisibility(View.VISIBLE);
            meSummary.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     */
    private void setTextViewContent() {
        if (!UserInfoManager.getInstance().isLogin()) {
            mePic.setImageResource(R.drawable.ic_w_head);

            tvLogin.setText("登录");
            tvLogout.setVisibility(View.GONE);

            tvUserType.setText("未登录");
            tvName.setText("未登录");
            imgVip.setImageResource(R.drawable.ic_me_novip);
        } else {
            String imageUrl = "http://api."+Constant.IYUBA_COM+"v2/api.iyuba?protocol=10005&uid=" + UserInfoManager.getInstance().getUserId() + "&size=big";
            LibGlide3Util.loadHeadImg(mContext,imageUrl,  R.drawable.ic_w_head, mePic);

            tvLogin.setText("打卡");
            tvLogout.setVisibility(View.VISIBLE);
            tvName.setText(UserInfoManager.getInstance().getUserName());
            if (UserInfoManager.getInstance().isVip()) {
                tvUserType.setText("VIP用户");
                imgVip.setImageResource(R.drawable.ic_me_isvip);
            } else {
                tvUserType.setText("普通用户");
                imgVip.setImageResource(R.drawable.ic_me_novip);
            }
        }
        if (UserInfoManager.getInstance().isLogin()) {
            tvMoney.setText(String.valueOf(UserInfoManager.getInstance().getMoney()));
            tvIyubi.setText(String.valueOf(UserInfoManager.getInstance().getIyuIcon()));
            tvScore.setText(String.valueOf(UserInfoManager.getInstance().getJiFen())); //积分
        }else {
            mePic.setImageResource(R.drawable.ic_w_head);
            tvName.setText("未登录");
            tvUserType.setText("未登录");
            tvLogin.setText("登录");
            tvMoney.setText("0.00");
            tvScore.setText("0");
            tvIyubi.setText("0");
            imgVip.setImageResource(R.drawable.ic_me_novip);
            tvLogout.setVisibility(View.GONE);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    CustomToast.showToast(mContext, R.string.check_network);
                    break;
                case 1:
                    CustomToast.showToast(mContext, R.string.action_fail);
                    break;
                case 2:
                    break;
                case 3:
                    try {
                        setTextViewContent();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    tvScore.setText(String.valueOf(UserInfoManager.getInstance().getJiFen()));
                    tvName.setText(UserInfoManager.getInstance().getUserName());
                    break;
                case 4:
                    //用户退出登录
//                    InitPush.getInstance().unRegisterToken(mContext, Integer.parseInt(ConfigManager.Instance().getUserId()));
                    UserInfoManager.getInstance().clearUserInfo();

                    CustomToast.showToast(mContext, R.string.loginout_success);
//                    SettingConfig.Instance().setHighSpeed(false);
                    EventBus.getDefault().post(new VipChangeEvent());
                    onResume();
                    break;
                case 6:
                    infoFlag = false;
                    levelFlag = false;
                    ExeProtocol.exe(
                            new RequestUserDetailInfo(String.valueOf(UserInfoManager.getInstance().getUserId())),
                            new ProtocolResponse() {
                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    ResponseUserDetailInfo responseUserDetailInfo =
                                            (ResponseUserDetailInfo) bhr;

                                    if (responseUserDetailInfo.result.equals("211")) {
                                        userDetailInfo = responseUserDetailInfo;
                                        if (userDetailInfo.gender.isEmpty() || userDetailInfo.birthday.isEmpty()
                                                || userDetailInfo.resideLocation
                                                .isEmpty()
                                                || userDetailInfo.occupation
                                                .isEmpty()
                                                || userDetailInfo.education
                                                .isEmpty()
                                                || userDetailInfo.graduateschool
                                                .isEmpty()) {
                                            infoFlag = true;// 有为空的用户信息

                                        }
                                        if (userDetailInfo.editUserInfo.getPlevel()
                                                .isEmpty()
                                                || Integer
                                                .parseInt(userDetailInfo.editUserInfo
                                                        .getPlevel()) <= 0
                                                || userDetailInfo.editUserInfo
                                                .getPtalklevel().isEmpty()
                                                || Integer
                                                .parseInt(userDetailInfo.editUserInfo
                                                        .getPtalklevel()) <= 0
                                                || userDetailInfo.editUserInfo
                                                .getPreadlevel().isEmpty()
                                                || Integer
                                                .parseInt(userDetailInfo.editUserInfo
                                                        .getPreadlevel()) <= 0
                                                || userDetailInfo.editUserInfo
                                                .getGlevel().isEmpty()
                                                || Integer
                                                .parseInt(userDetailInfo.editUserInfo
                                                        .getGlevel()) <= 0
                                                || userDetailInfo.editUserInfo
                                                .getGtalklevel().isEmpty()
                                                || Integer
                                                .parseInt(userDetailInfo.editUserInfo
                                                        .getGtalklevel()) <= 0
                                                || userDetailInfo.editUserInfo
                                                .getGreadlevel().isEmpty()
                                                || Integer
                                                .parseInt(userDetailInfo.editUserInfo
                                                        .getGreadlevel()) <= 0) {
                                            levelFlag = true;// 有为空的用户等级信息
                                        }
                                    }
                                }

                                @Override
                                public void error() {
                                }
                            });
                    break;
                case HANLDER_QQ:
                    try {
                        tvQqGroup.setText(String.format("%s：%s", getString(R.string.qq_group), ConfigManager.Instance().getQQGroup()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };


    private void showNormalDialog(String content) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        normalDialog.setIcon(com.iyuba.lib.R.drawable.iyubi_icon);
        normalDialog.setTitle("提示");
        normalDialog.setMessage(content);
        normalDialog.setPositiveButton("登录",
                (dialog, which) -> {

                    gotoLoginActivity();

                });
        normalDialog.setNegativeButton("确定",
                (dialog, which) -> {
                    //...To-do

                });
        // 显示
        normalDialog.show();
    }


    CustomDialog mWaittingDialog;
    private final int signStudyTime = 3 * 60;
    private String loadFiledHint = "打卡加载失败";

    //打卡
    private void clockIn() {
        mWaittingDialog = WaittingDialog.showDialog(getActivity());
        mWaittingDialog.setTitle("请稍后");
        mWaittingDialog.show();
        String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        final String url = String.format(Locale.CHINA, "http://daxue." + Constant.IYUBA_CN + "ecollege/getMyTime.jsp?uid=%s&day=%s&flg=1", uid, getDays());

        ExeProtocol.exe(
                new SignRequest(String.valueOf(UserInfoManager.getInstance().getUserId())),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        SignResponse response = (SignResponse) bhr;
                        try {
                            if (null != mWaittingDialog) {
                                if (mWaittingDialog.isShowing()) {
                                    mWaittingDialog.dismiss();
                                }
                            }
                            final StudyTimeBeanNew bean = new Gson().fromJson(response.jsonObjectRoot.toString(), StudyTimeBeanNew.class);
                            Log.d("dddd", response.jsonObjectRoot.toString());
                            if ("1".equals(bean.getResult())) {
                                final int time = Integer.parseInt(bean.getTotalTime());
                                if (time < signStudyTime) {
                                    toast(getStudyTime(time));
                                } else {
                                    //跳转打卡页面
                                    Intent intent1 = new Intent(getActivity(), SignActivity.class);
                                    intent1.putExtra("bean", bean);
                                    startActivity(intent1);
                                }
                            } else {
                                toast(loadFiledHint + bean.getResult());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("异常", e.toString());
                            toast(loadFiledHint + "！！");
                        }

                    }

                    @Override
                    public void error() {

                    }
                });

    }

    private void toast(String msg) {
        Looper.prepare();
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        Looper.loop();

    }

    private long getDays() {
        //东八区;
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.set(1970, 0, 1, 0, 0, 0);
        Calendar now = Calendar.getInstance(Locale.CHINA);
        long intervalMilli = now.getTimeInMillis() - cal.getTimeInMillis();
        long xcts = intervalMilli / (24 * 60 * 60 * 1000);
        return xcts;
    }


    private String floatToString(float fNumber) {
        fNumber = (float) (fNumber * 0.01);
        DecimalFormat myformat = new java.text.DecimalFormat("0.00");
        String str = myformat.format(fNumber);
        return str;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FavorItemEvent fEvent) {
        //收藏页面点击
        BasicFavorPart fPart = fEvent.items.get(fEvent.position);
        goFavorItem(fPart);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChangeUsernameEvent event) {
        tvName.setText(event.newUsername);
    }

    private void goFavorItem(BasicFavorPart part) {

        switch (part.getType()) {
            case "news":
                startActivity(TextContentActivity.getIntent2Me(mContext, part.getId(), part.getTitle(), part.getTitleCn(), part.getType()
                        , part.getCategoryName(), part.getCreateTime(), part.getPic(), part.getSource()));
                break;
            case "voa":
            case "csvoa":
            case "bbc":
                startActivity(AudioContentActivityNew.getIntent2Me(mContext,
                        part.getCategoryName(), part.getTitle(), part.getTitleCn(),
                        part.getPic(), part.getType(), part.getId(), part.getSound()));
                break;
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(mContext,
                        part.getCategoryName(), part.getTitle(), part.getTitleCn(),
                        part.getPic(), part.getType(), part.getId(), part.getSound()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivityNew.getIntent2Me(mContext,
                        part.getCategoryName(), part.getTitle(), part.getTitleCn(), part.getPic(),
                        part.getType(), part.getId(), part.getSound()));
                break;
            case "series":

                Intent intent = SeriesActivity.buildIntent(mContext, part.getSeriesId(), part.getId());
                startActivity(intent);
                break;
            case HeadlineType.SMALLVIDEO:
                int code=1;
                int pageCount=1;
                int dataPage=0;
                Intent forOne = VideoMiniContentActivity.buildIntentForOne(requireContext(), part.getId(), dataPage, pageCount, code);
                startActivity(forOne);
                break;
        }
    }

    private void getQQGroup() {

        ConfigManager.Instance().setQQGroup(QQUtil.getQQGroupNumber(QQUtil.getBrandName()));


        ConfigManager.Instance().setQQKey(QQUtil.getQQGroupKey(QQUtil.getBrandName()));
        QQGroupApi qqGroupApi = ApiRetrofit.getInstance().getQqGroupApi();
        qqGroupApi.getQQGroup(QQGroupApi.URL, QQUtil.getBrandName()).enqueue(new Callback<QQGroupApi.QQGroupBean>() {


            @Override
            public void onResponse(@NonNull Call<QQGroupApi.QQGroupBean> call, @NonNull Response<QQGroupApi.QQGroupBean> response) {

                try {
                    QQGroupApi.QQGroupBean bean = response.body();
                    if (bean != null && "true".equals(bean.message)) {
                        ConfigManager.Instance().setQQGroup(bean.QQ);
                        ConfigManager.Instance().setQQKey(bean.key);
                        handler.sendEmptyMessage(HANLDER_QQ);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(@NonNull Call<QQGroupApi.QQGroupBean> call, @NonNull Throwable t) {

            }
        });
    }

    private void initWallet() {
        DecimalFormat df = new DecimalFormat("0.00");
        String tag = "爱语吧";
        //根据包名进行判断(样式)
        if ("com.iyuba.conceptStory".equals(mContext.getPackageName())){
            tag = "爱语言";
        }
        String content = "当前钱包金额:" + UserInfoManager.getInstance().getMoney() + "元,满10元可在["+tag+"]微信公众号提现(关注绑定爱语吧账号),每天坚持打卡分享,获得更多红包吧!";
        //当天再次打卡成功后显示
        final MaterialDialog dialog_share = new MaterialDialog(mContext);
        dialog_share.setTitle("提示");
        dialog_share.setMessage(content);
        dialog_share.setPositiveButton("确定", v -> dialog_share.dismiss());
        dialog_share.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(MoneyChangeEvent event) {
        try {
            tvMoney.setText(String.valueOf(UserInfoManager.getInstance().getMoney()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(VipChangeEvent event) {
        //登录
        setTextViewContent();
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(ArtDataSkipEvent event) {
        //新版个人中心点击列表项
        BasicFavorPart fPart = new BasicFavorPart();
        if ("news".equals(event.type)) {
            fPart.setTitle(event.headline.Title);
            fPart.setTitleCn(event.headline.TitleCn);
            fPart.setPic(event.headline.getPic());
            fPart.setType(event.type);
            fPart.setId(event.headline.id + "");
            goFavorItem(fPart);
        } else if (Constant.EVAL_TYPE.equals(event.type)) {
            int topicId = Integer.parseInt(event.exam.topicId);
            if (topicId > 10000) {
                topicId = topicId / 10;
            }
            getVoaDetail(topicId);
        } else {
            fPart.setTitle(event.voa.title);
            fPart.setTitleCn(event.voa.title_cn);
            fPart.setPic(event.voa.pic);
            fPart.setType(event.type);
            fPart.setId(event.voa.voaid + "");
            fPart.setSound(event.voa.sound);
            goFavorItem(fPart);
        }
    }


    public void getVoaDetail(final int voaId) {
        new Thread(() -> {
            // 从本地数据库中查找
            VoaDataManager.Instace().voaTemp = new VoaOp(mContext).findDataById(voaId);
            VoaDataManager.Instace().voaDetailsTemp = new VoaDetailOp(mContext).findDataByVoaId(voaId);
            if (VoaDataManager.Instace().voaDetailsTemp != null && VoaDataManager.Instace().voaDetailsTemp.size() != 0) {
                VoaDataManager.Instace().setSubtitleSum(VoaDataManager.Instace().voaTemp, VoaDataManager.Instace().voaDetailsTemp);
                VoaDataManager.Instace().setPlayLocalType(0);
                Intent intent = new Intent();
                intent.setClass(mContext, StudyNewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                intent.putExtra("curVoaId", voaId + "");
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(
                        R.anim.slide_in_right, android.R.anim.fade_out);
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(UserNameChangeEvent event) {
        //这里重新获取用户信息
//        ConfigManager.Instance().setNickName(event.newName);
//        AccountManager.getInstance().setUserName(event.newName);
//        EventBus.getDefault().post(new VipChangeEvent());
        UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().getUserId(), new UserinfoCallbackListener() {
            @Override
            public void onSuccess() {
                setTextViewContent();
            }

            @Override
            public void onFail(String errorMsg) {

            }
        });
    }

    private String getStudyTime(int time){
        StringBuilder builder=new StringBuilder()
                .append("当前已学习");
        if (time>60){
            int minute=time/60;
            builder.append(minute)
                    .append("分")
                    .append(time-minute*60)
                    .append("秒");
        }else {
            builder.append(time)
                    .append("秒");
        }
        builder.append("\n满").append(signStudyTime / 60).append("分钟可打卡");
        return builder.toString();
    }

    /***********************************测试广告显示***************************/
    //显示测试广告按钮
    /*private void showAdTestView() {
        //设置广告测试按钮显示
        int userId = UserInfoManager.getInstance().getUserId();
        int[] showAdArray = new int[]{
                14044990,//房
                469532,//峰
                7215434,//展
                2888726,//国
                14829872,//彭
        };

        //固定账号校验
        boolean isShowAdTest = false;
        showAd:for (int i = 0; i < showAdArray.length; i++) {
            if (showAdArray[i] == userId) {
                isShowAdTest = true;
                break showAd;
            }
        }
        //使用vip进行校验
        if (!isShowAdTest && UserInfoManager.getInstance().getVipStatus().equals("99")){
            isShowAdTest = true;
        }

        if (isShowAdTest) {
            setAdTestInit();
            adTestView.setVisibility(View.INVISIBLE);
            adTestView.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), AdActivity.class));
            });
        } else {
            adTestView.setVisibility(View.INVISIBLE);
        }
    }*/

    //初始化设置广告测试功能
    /*private void setAdTestInit() {
        //初始化数据
        AdListLibInit.init(getActivity(), Constant.APP_ID, OAIDHelper.getInstance().getOAID(), String.valueOf(UserInfoManager.getInstance().getUserId()), ConceptApplication.getInstance().getPackageName());
        AdListLibInit.setPriority(15);
        AdListLibInit.setUid(String.valueOf(UserInfoManager.getInstance().getUserId()));
        //设置key参数
        Ad[] spreadKeyArray = AdNewKeyData.getSpreadKeyArray;
        Ad[] bannerKeyArray = AdNewKeyData.getBannerKeyArray;
        Ad[] templateKeyArray = AdNewKeyData.getTemplateKeyArray;
        Ad[] interstitialKeyArray = AdNewKeyData.getInterstitialKeyArray;
        Ad[] drawVideoKeyArray = AdNewKeyData.getDrawVideoKeyArray;
        Ad[] rewardKeyArray = AdNewKeyData.getRewardVideoKeyArray;
        //设置需要显示的数据
        AdListLibInit.setAdKeys(bannerKeyArray, templateKeyArray, interstitialKeyArray, drawVideoKeyArray, rewardKeyArray, spreadKeyArray);
    }*/
}
