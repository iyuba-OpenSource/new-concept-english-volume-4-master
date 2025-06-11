package com.iyuba.conceptEnglish.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.beizi.fusion.BeiZis;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.iyuba.ConstantNew;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.api.ApiRetrofit;
import com.iyuba.conceptEnglish.api.UnitTitle;
import com.iyuba.conceptEnglish.api.UpdateTestAPI;
import com.iyuba.conceptEnglish.api.UpdateTitleAPI;
import com.iyuba.conceptEnglish.api.UpdateUnitTitleAPI;
import com.iyuba.conceptEnglish.api.UpdateVoaDetailAPI;
import com.iyuba.conceptEnglish.api.UpdateWordDetailAPI;
import com.iyuba.conceptEnglish.databinding.SettingBinding;
import com.iyuba.conceptEnglish.event.PullLastLessonDataEvent;
import com.iyuba.conceptEnglish.event.RefreshBookEvent;
import com.iyuba.conceptEnglish.han.utils.ExpandKt;
import com.iyuba.conceptEnglish.lil.concept_other.book_choose.ConceptBookChooseActivity;
import com.iyuba.conceptEnglish.lil.concept_other.download.FilePathUtil;
import com.iyuba.conceptEnglish.lil.concept_other.remote.FixDataManager;
import com.iyuba.conceptEnglish.lil.concept_other.util.SendBookUtil;
import com.iyuba.conceptEnglish.lil.concept_other.verify.AbilityControlManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.NetHostManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.StudySettingManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.FileManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.view.dialog.LoadingDialog;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingActivity;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxTimer;
import com.iyuba.conceptEnglish.manager.DownloadStateManager;
import com.iyuba.conceptEnglish.manager.sharedpreferences.InfoHelper;
import com.iyuba.conceptEnglish.model.SaveWordDataModel;
import com.iyuba.conceptEnglish.protocol.NewInfoRequest;
import com.iyuba.conceptEnglish.protocol.NewInfoResponse;
import com.iyuba.conceptEnglish.sqlite.mode.Book;
import com.iyuba.conceptEnglish.sqlite.mode.DownloadInfo;
import com.iyuba.conceptEnglish.sqlite.mode.MultipleChoice;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.mode.VoaAnnotation;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDiffcultyExercise;
import com.iyuba.conceptEnglish.sqlite.mode.VoaStructure;
import com.iyuba.conceptEnglish.sqlite.mode.VoaStructureExercise;
import com.iyuba.conceptEnglish.sqlite.mode.VoaWord;
import com.iyuba.conceptEnglish.sqlite.op.AnnotationOp;
import com.iyuba.conceptEnglish.sqlite.op.BookOp;
import com.iyuba.conceptEnglish.sqlite.op.DownloadInfoOp;
import com.iyuba.conceptEnglish.sqlite.op.MultipleChoiceOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailYouthOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaDiffcultyExerciseOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaStructureExerciseOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaStructureOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
import com.iyuba.conceptEnglish.util.PullHistoryDetailUtil;
import com.iyuba.conceptEnglish.util.SendBookPop;
import com.iyuba.conceptEnglish.util.TransUtil;
import com.iyuba.conceptEnglish.util.UtilFile;
import com.iyuba.conceptEnglish.widget.SleepDialog;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.data.DataManager;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.common.data.model.VoaTextYouthByBook;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.setting.SettingConfig;
import com.iyuba.core.common.util.FileSize;
import com.iyuba.core.common.util.PrivacyUtil;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.common.util.ToastUtil;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.view.PermissionMsgDialog;
import com.iyuba.core.microclass.activity.DelCourseDataActivity;
import com.iyuba.module.toolbox.RxUtil;
import com.umeng.analytics.MobclickAgent;
import com.yd.saas.common.util.MediaUrlUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * 设置界面
 */
public class SetActivity extends BaseViewBindingActivity<SettingBinding> implements SetMvpView {

    private Context mContext;

    private static int hour, minute = 0, totaltime, volume;// total用于计算时间，volume用于调整音量,睡眠模式用到的
    private static boolean isSleep = false;// 睡眠模式是否开启
    private ArrayList<Voa> voaList = new ArrayList<Voa>();

    private Disposable mDispose_SyncVoaSub, mDisposable_YouthUnitTitle;
    private String lastSavingPath, nowSavingPath;

    private CustomDialog waittingDialog;
    private SetPresenter mPresenter;

    private boolean updateTitleDone = false;
    private boolean updateUnitTitleDone = false;

    //权限弹窗
    private PermissionMsgDialog permissionDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        mContext = this;
        waittingDialog = WaittingDialog.showDialog(mContext);

        /*binding.showPersonal.setChecked(InfoHelper.getInstance().agreePersonal());*/

        mPresenter = new SetPresenter();
        mPresenter.attachView(this);
        changeServiceName();

        //根据包名设置数据
        if (getPackageName().equals(Constant.package_learnNewEnglish)) {
            TextView aboutText = findViewById(R.id.about_iyuba);
            aboutText.setText("关于");
        }

        initStudyHome();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initWidget();
        initCheckBox();
        initSleep();

        binding.savingpathPath.setText(ConfigManager.Instance().loadString("media_saving_path"));// 显示路径
        if (UserInfoManager.getInstance().isLogin()) {
            ClientSession.Instace()
                    .asynGetResponse(
                            new NewInfoRequest(
                                    String.valueOf(UserInfoManager.getInstance().getUserId())),
                            new IResponseReceiver() {
                                @Override
                                public void onResponse(
                                        BaseHttpResponse response,
                                        BaseHttpRequest request, int rspCookie) {
                                    NewInfoResponse rs = (NewInfoResponse) response;
                                    if (rs.letter > 0) {
                                        handler.sendEmptyMessage(4);
                                    }
                                }
                            });
        }
        MobclickAgent.onResume(mContext);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //这里没必要单独处理回调，直接刷新显示即可
        //显示课文选择后的操作
        String bookType = ConceptBookChooseManager.getInstance().getBookType();
        switch (bookType) {
            case TypeLibrary.BookType.conceptFourUS:
            default:
                binding.tvSoundType.setText("美音");
                break;
            case TypeLibrary.BookType.conceptFourUK:
                binding.tvSoundType.setText("英音");
                break;
            case TypeLibrary.BookType.conceptJunior:
                binding.tvSoundType.setText("青少版");
                break;
        }
    }

    //学习界面的首页设置
    private void initStudyHome() {
        String[] typeTextArray = new String[]{"听力", "阅读"};
        String[] typeArray = new String[]{TypeLibrary.StudyPageType.read, TypeLibrary.StudyPageType.section};

        //新概念的类型
        String conceptType = StudySettingManager.getInstance().getStudyConceptHome();
        if (conceptType.equals(TypeLibrary.StudyPageType.read)) {
            binding.conceptStudyHomeShowText.setText(typeTextArray[0]);
        } else if (conceptType.equals(TypeLibrary.StudyPageType.section)) {
            binding.conceptStudyHomeShowText.setText(typeTextArray[1]);
        }
        binding.conceptStudyHomeShow.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("新概念课文首页设置")
                    .setItems(typeTextArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            binding.conceptStudyHomeShowText.setText(typeTextArray[which]);
                            StudySettingManager.getInstance().setStudyConceptHome(typeArray[which]);
                        }
                    }).create().show();
        });

        //中小学类型
        String juniorType = StudySettingManager.getInstance().getStudyJuniorHome();
        if (juniorType.equals(TypeLibrary.StudyPageType.read)) {
            binding.juniorStudyHomeShowText.setText(typeTextArray[0]);
        } else if (juniorType.equals(TypeLibrary.StudyPageType.section)) {
            binding.juniorStudyHomeShowText.setText(typeTextArray[1]);
        }
        binding.juniorStudyHomeShow.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("中小学课文首页设置")
                    .setItems(typeTextArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            binding.juniorStudyHomeShowText.setText(typeTextArray[which]);
                            StudySettingManager.getInstance().setStudyJuniorHome(typeArray[which]);
                        }
                    }).create().show();
        });

        //小说类型
        String novelType = StudySettingManager.getInstance().getStudyNovelHome();
        if (novelType.equals(TypeLibrary.StudyPageType.read)) {
            binding.readStudyHomeShowText.setText(typeTextArray[0]);
        } else if (novelType.equals(TypeLibrary.StudyPageType.section)) {
            binding.readStudyHomeShowText.setText(typeTextArray[1]);
        }
        binding.readStudyHomeShow.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("阅读课文首页设置")
                    .setItems(typeTextArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            binding.readStudyHomeShowText.setText(typeTextArray[which]);
                            StudySettingManager.getInstance().setStudyNovelHome(typeArray[which]);
                        }
                    }).create().show();
        });

        //接口审核数据控制
        if (AbilityControlManager.getInstance().isLimitConcept()
                && AbilityControlManager.getInstance().isLimitNovel()
                && AbilityControlManager.getInstance().isLimitJunior()) {
            binding.studyHomeTitle.setVisibility(View.GONE);
            binding.studyHomeLayout.setVisibility(View.GONE);
        } else {
            binding.studyHomeTitle.setVisibility(View.VISIBLE);

            if (ConstantNew.isShowConcept
                    && !AbilityControlManager.getInstance().isLimitConcept()) {
                binding.conceptStudyHomeShow.setVisibility(View.VISIBLE);
            } else {
                binding.conceptStudyHomeShow.setVisibility(View.GONE);
            }

            if (ConstantNew.isShowJunior
                    && !AbilityControlManager.getInstance().isLimitJunior()) {
                binding.juniorStudyHomeShow.setVisibility(View.VISIBLE);
            } else {
                binding.juniorStudyHomeShow.setVisibility(View.GONE);
            }

            if (ConstantNew.isShowNovel
                    && !AbilityControlManager.getInstance().isLimitNovel()) {
                binding.readStudyHomeShow.setVisibility(View.VISIBLE);
            } else {
                binding.readStudyHomeShow.setVisibility(View.GONE);
            }
        }
    }

    private void changeServiceName() {
        RelativeLayout changeService = findViewById(R.id.change_service);
        changeService.setOnClickListener(v -> {
            String url = "http://111.198.52.105:8085/api/getDomain.jsp?appId=" + Constant.APPID + "&short1=iyuba.cn&short2=iyuba.com.cn";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    String resp = response.body().string();
                    try {
                        JSONObject object = new JSONObject(resp);
                        if ("200".equals(object.getString("result"))) {
                            Message message = new Message();
                            message.obj = resp;
                            message.what = 10056;
                            handler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void chooseBookRefresh(PullLastLessonDataEvent pullLastLessonDataEvent) {
        getUpdateContentVersion(pullLastLessonDataEvent.getCurrentBook(),
                pullLastLessonDataEvent.isBoolAmerican());
    }

    /**
     * 更新青少版的课文的标题详情
     *
     * @param bookId
     */
    private void insertOrUpdateYouthUnitTitles(int bookId) {
        if (waittingDialog != null) {
            waittingDialog.show();
        }
        VoaOp voaOp = new VoaOp(mContext);
        RxUtil.dispose(mDisposable_YouthUnitTitle);
        mDisposable_YouthUnitTitle = DataManager.getInstance().getTalkLesson(bookId + "")
                .compose(RxUtil.<List<TalkLesson>>applySingleIoScheduler())
                .subscribe(new Consumer<List<TalkLesson>>() {
                    @Override
                    public void accept(List<TalkLesson> list) throws Exception {
                        if (waittingDialog.isShowing()) {
                            waittingDialog.dismiss();
                        }
                        if (list != null && list.size() > 0) {
                            List<Voa> listVoa = new ArrayList<>();
                            for (TalkLesson talkLesson : list) {
                                Voa voa = new Voa();
                                voa.voaId = talkLesson.voaId();
                                voa.title = talkLesson.Title;
                                voa.titleCn = talkLesson.TitleCn;
                                voa.category = Integer.parseInt(talkLesson.series);
                                voa.clickRead = talkLesson.clickRead;
                                voa.pic = talkLesson.Pic;
                                voa.sound = talkLesson.Sound;
                                voa.lessonType = TypeLibrary.BookType.conceptJunior;
                                listVoa.add(voa);
                            }
                            voaOp.insertOrUpdate(listVoa);
                            EventBus.getDefault().post(new RefreshBookEvent());
                        } else {
                            ToastUtil.showToast(mContext, "服务器异常,更新课文标题失败");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        ToastUtil.showToast(mContext, "更新课文标题失败,请检查网络");
                        if (waittingDialog.isShowing()) {
                            waittingDialog.dismiss();
                        }

                    }
                });
    }

    /**
     * 青少版单词数据的 更新插入
     *
     * @param bookId
     */
    private void getYouthWordData(int bookId) {
        if (waittingDialog != null) {
            waittingDialog.show();
        }

        RxUtil.dispose(mDispose_SyncVoaSub);
        mDispose_SyncVoaSub = DataManager.getInstance().getChildWords(bookId + "")
                .compose(com.iyuba.module.toolbox.RxUtil.<List<VoaWord2>>applySingleIoScheduler())
                .subscribe(new Consumer<List<VoaWord2>>() {
                    @Override
                    public void accept(List<VoaWord2> list) throws Exception {

                        if (waittingDialog.isShowing()) {
                            waittingDialog.dismiss();
                        }
                        if (list != null && list.size() > 0) {
                            SaveWordDataModel saveWordDataModel = new SaveWordDataModel(mContext);
                            saveWordDataModel.saveWordData(list);
                        } else {
                            ToastUtil.showToast(mContext, "服务器异常,更新课文单词失败");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        if (waittingDialog.isShowing()) {
                            waittingDialog.dismiss();
                        }
                        Timber.e(throwable);
                        ToastUtil.showToast(mContext, "更新课文单词失败,请检查网络");
                    }
                });
    }

    /**
     * 拉取青少版课文详情 ，按课本拉取
     *
     * @param bookId
     */
    public void getVoaDetailBySeries(int bookId) {

        if (waittingDialog != null) {
            waittingDialog.show();
        }
        //从服务器获取并保存到本地
        VoaDetailYouthOp voaDetailYouthOp = new VoaDetailYouthOp(mContext);
        RxUtil.dispose(mDispose_SyncVoaSub);
        mDispose_SyncVoaSub = DataManager.getInstance().getVoaTextsBySeries(bookId, UserInfoManager.getInstance().getUserId(), 222)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<VoaTextYouthByBook>>() {
                    @Override
                    public void accept(List<VoaTextYouthByBook> voaTexts) throws Exception {
                        if (waittingDialog.isShowing()) {
                            waittingDialog.dismiss();
                        }
                        if (voaTexts.size() != 0) {
                            voaDetailYouthOp.insertOrReplaceDataBySeries(voaTexts);
                        } else {
                            ToastUtil.showToast(mContext, "更新失败");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (waittingDialog.isShowing()) {
                            waittingDialog.dismiss();
                        }
                        ToastUtil.showToast(mContext, "网络错误，无法获取课文详情");
                    }
                });
    }

    public void initCheckBox() {
//        CheckBoxHighSpeedDwonload = (CheckBox) findViewById(R.id.CheckBox_high_speed_download);
        if (UserInfoManager.getInstance().isVip()){
            binding.CheckBoxHighSpeedDownload.setChecked(SettingConfig.Instance().isHighSpeed());
        }else {
            binding.CheckBoxHighSpeedDownload.setChecked(false);
        }
        binding.CheckBoxHighSpeedDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setHighSpeedDownload();
            }
        });
        binding.btnHighSpeedDownload.setOnClickListener(v -> {
            setHighSpeedDownload();
        });
//        checkBoxPushMessage = (CheckBox) findViewById(R.id.CheckBox_PushMessage);
        binding.CheckBoxPushMessage.setChecked(SettingConfig.Instance().isPush());
        binding.CheckBoxPushMessage
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        setPush();
                    }
                });
//        CheckBoxScreenLit = (CheckBox) findViewById(R.id.CheckBox_ScreenLit);
        binding.CheckBoxScreenLit.setChecked(SettingConfig.Instance().isLight());
        binding.CheckBoxScreenLit.setOnCheckedChangeListener((buttonView, isChecked) -> SettingConfig.Instance().setLight(isChecked));
//        checkSpeedPlayer = (CheckBox) findViewById(R.id.CheckBox_speed_player);
        /*binding.CheckBoxSpeedPlayer.setChecked(SettingConfig.Instance().isSpeedPlayer());
        binding.CheckBoxSpeedPlayer.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                SettingConfig.Instance().setSpeedPlayer(isChecked);
                if (UserInfoManager.getInstance().isVip()) {
                    if (SettingConfig.Instance().isSpeedPlayer()) {
                        SettingConfig.Instance().setSpeedPlayer(false);
                        MediaPlayer videoView = BackgroundManager.Instace().bindService.getPlayer();
                        if (videoView != null && videoView.isPlaying()) {
                            videoView.pause();
                        }

                    } else {
                        SettingConfig.Instance().setSpeedPlayer(true);
                    }
                    binding.CheckBoxSpeedPlayer.setChecked(SettingConfig
                            .Instance().isSpeedPlayer());
                } else {
                    binding.CheckBoxSpeedPlayer.setChecked(false);
                    AlertDialog.Builder builder = new Builder(mContext);
                    builder.setTitle("提示");
                    builder.setPositiveButton("确定", null);
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                    builder.setMessage("您好，只有vip用户，才能拥有调速权限！");
                    builder.show();
                }
            }
        });*/
    }

    public void initWidget() {
        if (com.iyuba.core.InfoHelper.getInstance().openShare()) {
            binding.recommendBtn.setVisibility(View.VISIBLE);
        } else {
            binding.recommendBtn.setVisibility(View.GONE);
        }
        //根据包名处理下
        if (getPackageName().equals(Constant.package_nce)) {
            binding.praiseAndSendBooks.setVisibility(View.GONE);
        }else {
            Pair<Boolean,String> showBookPair = SendBookUtil.showSendBookNew(getPackageName());
            if (showBookPair.first){
                binding.praiseAndSendBooks.setVisibility(View.VISIBLE);
            }else {
                binding.praiseAndSendBooks.setVisibility(View.GONE);
            }
        }

        initCacheSize();
        initListener();
        initProtocol();
    }

    public void initCacheSize() {
        new Thread(new Runnable() {
            // 获取图片大小
            @Override
            public void run() {
                String strings;
                try {
                    strings = getSize(0);

                    handler.obtainMessage(0, strings).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 获取音频大小
        new Thread(() -> {
            String strings;
            try {
                strings = getSize(1);
                handler.obtainMessage(1, strings).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }).start();
    }

    private void initProtocol() {
//        tv_protocol = findViewById(R.id.tv_protocol);
        binding.tvProtocol.setText(ExpandKt.getProtocolText(this));
        binding.tvProtocol.setMovementMethod(ScrollingMovementMethod.getInstance());
        binding.tvProtocol.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void initListener() {
        binding.btnScreenLit.setOnClickListener(v -> {
            setScreenLit();
        });
        binding.btnPushMessage.setOnClickListener(v -> {
            setPush();
        });
        binding.sleepMod.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, SleepDialog.class);
            startActivityForResult(intent, 23);// 第二个参数requestcode随便写的，应该定义个static比较好
        });
//        binding.settingSavingPath.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                lastSavingPath = ConfigManager.Instance().loadString("media_saving_path");
//                Intent intent = new Intent(mContext, FileBrowserActivity.class);
//                startActivityForResult(intent, 25);
//            }
//        });

        //暂时关闭显示
        binding.picSize.setVisibility(View.INVISIBLE);
        binding.clearPic.setOnClickListener(v -> {
            /*if (!XXPermissions.isGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                XXPermissions.with(this)
                        .permission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .request(new OnPermissionCallback() {
                            @Override
                            public void onGranted(List<String> permissions, boolean all) {
                                ToastUtil.showToast(SetActivity.this, "已授权，请执行操作");
                            }

                            @Override
                            public void onDenied(List<String> permissions, boolean never) {
                                ToastUtil.showToast(SetActivity.this, "请授权必要的存储权限");
                            }
                        });
                return;
            }
            CustomToast.showToast(mContext, R.string.setting_deleting, 2000);// 这里可以改为引用资源文件
            new CleanBufferAsyncTask("image").execute();*/

            // TODO: 2025/4/11 判断android版本进行不同区分
            if (Build.VERSION.SDK_INT >= 35){
                startLoading("正在清理缓存文件");

                //延迟2s后关闭
                RxTimer.getInstance().timerInMain(timer_clearPic, 2 * 1000, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        stopLoading();
                        RxTimer.getInstance().cancelTimer(timer_clearPic);
                        ToastUtil.showToast(SetActivity.this,"清理完成");
                    }
                });
            }else {
                //原来的不对，现在重新处理下
                //图片没有缓存，延迟2s后显示清理完成
                List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
                pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","获取缓存数据，根据缓存数据位置进行清理")));
                if (permissionDialog==null){
                    permissionDialog = new PermissionMsgDialog(this);
                }
                permissionDialog.showDialog(null, pairList, true, new PermissionMsgDialog.OnPermissionApplyListener() {
                    @Override
                    public void onApplyResult(boolean isSuccess) {
                        if (isSuccess){
                            startLoading("正在清理缓存文件");

                            //延迟2s后关闭
                            RxTimer.getInstance().timerInMain(timer_clearPic, 2 * 1000, new RxTimer.RxActionListener() {
                                @Override
                                public void onAction(long number) {
                                    stopLoading();
                                    RxTimer.getInstance().cancelTimer(timer_clearPic);
                                    ToastUtil.showToast(SetActivity.this,"清理完成");
                                }
                            });
                        }
                    }
                });
            }
        });

        //暂时关闭显示
        binding.soundSize.setVisibility(View.INVISIBLE);
        binding.clearVideo.setOnClickListener(v -> {
            /*if (!XXPermissions.isGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                XXPermissions.with(this)
                        .permission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .request(new OnPermissionCallback() {
                            @Override
                            public void onGranted(List<String> permissions, boolean all) {
                                ToastUtil.showToast(SetActivity.this, "已授权，请执行操作");
                            }

                            @Override
                            public void onDenied(List<String> permissions, boolean never) {
                                ToastUtil.showToast(SetActivity.this, "请授权必要的存储权限");
                            }
                        });
                return;
            }

            new AlertDialog.Builder(mContext)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getResources().getString(R.string.alert_title))
                    .setMessage(getResources().getString(R.string.setting_alert))
                    .setPositiveButton(
                            getResources().getString(R.string.alert_btn_ok),
                            (dialog1, whichButton) -> {
                                CustomToast.showToast(mContext, R.string.setting_deleting, 2000);// 这里可以改为引用资源文件
                                DownloadInfoOp downloadInfoOp = new DownloadInfoOp(mContext);
                                downloadInfoOp.deleteAll();
                                new CleanBufferAsyncTask("video").execute();
                            })
                    .setNeutralButton(getResources().getString(R.string.alert_btn_cancel), null)
                    .create().show();*/

            //使用新的方法处理
            List<Pair<String, Pair<String,String>>> pairList = new ArrayList<>();
            pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE,new Pair<>("存储权限","获取缓存数据，根据缓存数据位置进行清理")));
            if (permissionDialog==null){
                permissionDialog = new PermissionMsgDialog(this);
            }
            permissionDialog.showDialog(null, pairList, true, new PermissionMsgDialog.OnPermissionApplyListener() {
                @Override
                public void onApplyResult(boolean isSuccess) {
                    if (isSuccess){
                        new AlertDialog.Builder(mContext)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(getResources().getString(R.string.alert_title))
                                .setMessage(getResources().getString(R.string.setting_alert))
                                .setPositiveButton(
                                        getResources().getString(R.string.alert_btn_ok),
                                        (dialog1, whichButton) -> {
                                            CustomToast.showToast(mContext, R.string.setting_deleting, 2000);// 这里可以改为引用资源文件
                                            DownloadInfoOp downloadInfoOp = new DownloadInfoOp(mContext);
                                            downloadInfoOp.deleteAll();
                                            new CleanBufferAsyncTask("video").execute();
                                        })
                                .setNeutralButton(getResources().getString(R.string.alert_btn_cancel), null)
                                .create().show();
                    }
                }
            });
        });
        binding.recommendBtn.setOnClickListener(v -> {
            prepareMessage();
        });
        binding.aboutBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(mContext, AboutActivity.class);
            startActivity(intent);
        });
        binding.praiseAndSendBooks.setOnClickListener(v -> {
            //好评送书
            SendBookPop sendBookPop = new SendBookPop(SetActivity.this,
                    SetActivity.this.getWindow().getDecorView());
        });
        binding.feedbackBtn.setOnClickListener(v -> {
            //意见反馈
            Intent intent = new Intent();
            intent.setClass(mContext, FeedbackActivity.class);
            startActivity(intent);
        });
        binding.clearMicroclass.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(mContext, DelCourseDataActivity.class);
            startActivity(intent);
        });
        binding.reSyncVoaTest.setOnClickListener(v -> {
//            int bookID = ConfigManager.Instance().loadInt("curBook", 1);// 1，2，3，4
            int bookID = ConceptBookChooseManager.getInstance().getBookId();
            getUpdateCheck(bookID);
        });
        binding.reSyncVoaKnowledge.setOnClickListener(v -> {
//            int bookId = ConfigManager.Instance().loadInt("curBook", 1);// 1，2，3，4
            int bookId = ConceptBookChooseManager.getInstance().getBookId();
            updateKnowledge(bookId);
        });
        /*binding.showPersonal.setOnClickListener(v -> {
            boolean checked = binding.showPersonal.isChecked();
            BeiZis.setSupportPersonalized(checked);
            InfoHelper.getInstance().changeAgreePersonal(checked);
        });*/
        binding.meAlarm.setOnClickListener(v -> {
            setAlarmDialog();
        });

        //隐私协议和用户政策
        binding.privacyBtn.setOnClickListener(v -> {
            Intent privacyIntent = new Intent();
            privacyIntent.setClass(SetActivity.this, Web.class);
            privacyIntent.putExtra("url", PrivacyUtil.getSeparatedSecretUrl());
            privacyIntent.putExtra("title", "隐私政策");
            startActivity(privacyIntent);
        });
        binding.termBtn.setOnClickListener(v -> {
            Intent termIntent = new Intent();
            termIntent.setClass(SetActivity.this, Web.class);
            termIntent.putExtra("url", PrivacyUtil.getSeparatedProtocolUrl());
            termIntent.putExtra("title", "用户协议");
            startActivity(termIntent);
        });

        //同步课程标题
        binding.reSyncVoaTitle.setOnClickListener(v -> {
            /*int bookId = ConfigManager.Instance().loadInt("curBook", 1);
            switch (ConfigManager.Instance().getBookType()) {
                case ENGLISH:
                    getUnitTitles(bookId, false);
                    break;
                case AMERICA:
                    getUnitTitles(bookId, true);
                default:
                    break;
                case YOUTH:
                    insertOrUpdateYouthUnitTitles(bookId);
                    break;
            }*/
            int bookId = ConceptBookChooseManager.getInstance().getBookId();
            switch (ConceptBookChooseManager.getInstance().getBookType()) {
                case TypeLibrary.BookType.conceptFourUK:
                    getUnitTitles(bookId, false);
                    break;
                case TypeLibrary.BookType.conceptFourUS:
                    getUnitTitles(bookId, true);
                default:
                    break;
                case TypeLibrary.BookType.conceptJunior:
                    insertOrUpdateYouthUnitTitles(bookId);
                    break;
            }
        });
        binding.reSyncVoa.setOnClickListener(v -> {
            /*int bookId = ConfigManager.Instance().loadInt("curBook", 1);
            switch (ConfigManager.Instance().getBookType()) {
                case ENGLISH:
                    getUpdateContentVersion(bookId, false);
                    break;
                case AMERICA:
                default:
                    getUpdateContentVersion(bookId, true);
                    break;
                case YOUTH:
                    getVoaDetailBySeries(bookId);
                    break;
            }*/
            int bookId = ConceptBookChooseManager.getInstance().getBookId();
            switch (ConceptBookChooseManager.getInstance().getBookType()) {
                case TypeLibrary.BookType.conceptFourUK:
                    getUpdateContentVersion(bookId, false);
                    break;
                case TypeLibrary.BookType.conceptFourUS:
                default:
                    getUpdateContentVersion(bookId, true);
                    break;
                case TypeLibrary.BookType.conceptJunior:
                    getVoaDetailBySeries(bookId);
                    break;
            }
        });
        binding.reSyncWord.setOnClickListener(v -> {
            /*int bookId = ConfigManager.Instance().loadInt("curBook", 1);
            switch (ConfigManager.Instance().getBookType()) {
                case ENGLISH:
                case AMERICA:
                default:
                    getWordTitles(bookId);
                    break;
                case YOUTH:
                    getYouthWordData(bookId);
                    break;
            }*/
            int bookId = ConceptBookChooseManager.getInstance().getBookId();
            switch (ConceptBookChooseManager.getInstance().getBookType()) {
                case TypeLibrary.BookType.conceptFourUK:
                case TypeLibrary.BookType.conceptFourUS:
                default:
                    getWordTitles(bookId);
                    break;
                case TypeLibrary.BookType.conceptJunior:
                    getYouthWordData(bookId);
                    break;
            }
        });
        binding.reSyncRecord.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                ToastUtil.showToast(mContext, "请登录后再执行此操作");
            } else {
                if (!waittingDialog.isShowing()) {
                    AlertDialog dialog = new AlertDialog.Builder(mContext)
                            .setTitle(R.string.alert_title)
                            .setMessage("此操作会将服务器各类统计数据同步到本地，耗时较长，请耐心等待")
                            .setPositiveButton("开始同步",
                                    (dialog1, whichButton) -> updateArticleRecord())
                            .setNeutralButton("暂不同步",
                                    (dialog12, which) -> {
                                    }).create();
                    dialog.show();
                }
            }
        });
        binding.report.setOnClickListener(v -> {
            //举报功能
            try {
                String url = "mqqwpa://im/chat?chat_type=wpa&uin=";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url + "572828703")));
            } catch (Exception e) {
                ToastUtil.showToast(mContext, "您的设备尚未安装QQ客户端，举报功能需要使用QQ");
                e.printStackTrace();
            }
        });

        binding.reSoundType.setOnClickListener(v -> {
//            Intent intent = new Intent();
//            intent.setClass(mContext, BookChooseActivity.class);
//            intent.putExtra("isFirstInfo", 1);
//            startActivity(intent);
            ConceptBookChooseActivity.start(mContext, 1);
        });
        binding.reHotLine.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToCall();
            }
        });
        binding.buttonBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d("数据集合", "这里结束操作了3");
                finish();
            }
        });
    }

    private void setAlarmDialog() {
        String[] speeds = new String[]{"不开启", "10分钟后", "20分钟后", "30分钟后", "45分钟后", "60分钟后"};

        int cPosition = ConfigManager.Instance().getAlarmItem();
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        builder.setTitle("计时结束将停止播放");
        builder.setSingleChoiceItems(speeds, cPosition, (dialog, index) -> {
            if (index == cPosition) {
                return;
            }
            switch (index) {
                case 0:
                    try {
                        ConfigManager.Instance().setAlarmItem(index);
                        ToastUtil.showToast(mContext, "定时播放已取消");
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    minute = 10;
                    break;
                case 2:
                    minute = 20;
                    break;
                case 3:
                    minute = 30;
                    break;
                case 4:
                    minute = 45;
                    break;
                case 5:
                    minute = 60;
                    break;
            }
            if (index != 0) {
                try {
                    ConfigManager.Instance().setAlarmItem(index);
                    if (minute == 60) {
                        ToastUtil.showToast(mContext, "设置成功，将于1小时后关闭");
                    } else {
                        ToastUtil.showToast(mContext, "设置成功，将于" + minute + "分钟后关闭");
                    }
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        builder.create().show();
    }

    public void setHighSpeedDownload() {
        if (UserInfoManager.getInstance().isVip()) {
            if (SettingConfig.Instance().isHighSpeed()) {
                SettingConfig.Instance().setHighSpeed(false);
            } else {
                SettingConfig.Instance().setHighSpeed(true);
            }

            binding.CheckBoxHighSpeedDownload.setChecked(SettingConfig.Instance()
                    .isHighSpeed());
        } else {
            binding.CheckBoxHighSpeedDownload.setChecked(false);

            AlertDialog.Builder builder = new Builder(mContext);
            builder.setTitle("提示");
            builder.setPositiveButton("确定", null);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setMessage(getResources().getString(
                    R.string.high_speed_download_toast));
            builder.show();
        }
    }

    public void setScreenLit() {
        if (SettingConfig.Instance().isLight()) {
            SettingConfig.Instance().setLight(false);
        } else {
            SettingConfig.Instance().setLight(true);
        }
        binding.CheckBoxScreenLit.setChecked(SettingConfig.Instance().isLight());
    }

    private void setPush() {
        if (binding.CheckBoxPushMessage.isChecked()) {
            SettingConfig.Instance().setPush(true);
//            PushAgent.getInstance(mContext).enable();
        } else {
            SettingConfig.Instance().setPush(false);
//            PushAgent.getInstance(mContext).disable();
        }
    }

    public void initSleep() {
        if (!isSleep) {
            ((TextView) findViewById(R.id.sleep_state))
                    .setText(R.string.setting_sleep_state_off);
        } else {
            ((TextView) findViewById(R.id.sleep_state)).setText(String.format("%02d:%02d", hour, minute));
        }
    }

    Handler sleepHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int count = 0;
            AudioManager am = (AudioManager) mContext
                    .getSystemService(Context.AUDIO_SERVICE);
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (hour + minute != 0) {// 时间没结束
                        count++;
                        if (count % 10 == 0) {
                            if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > 2) {
                                am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_LOWER, 0);// 第三参数为0代表不弹出提示。
                            }
                        }
                        totaltime--;
                        ((TextView) findViewById(R.id.sleep_state)).setText(String
                                .format("%02d:%02d", hour, minute));
                        hour = totaltime / 60;
                        minute = totaltime % 60;
                        sleepHandler.sendEmptyMessageDelayed(0, 60000);
                    } else {// 到结束时间
                        isSleep = false;
                        ((TextView) findViewById(R.id.sleep_state))
                                .setText(R.string.setting_sleep_state_off);
                        Intent intent = new Intent();
                        intent.setAction("gotosleep");
                        mContext.sendBroadcast(intent);
                        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

                        Log.d("数据集合", "这里结束操作了1");

                        finish();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    String strings = (String) msg.obj;
                    binding.picSize.setText(strings);
                    break;
                case 1:
                    String string = (String) msg.obj;
                    binding.soundSize.setText(string);
                    break;
                case 2:
                    CustomToast.showToast(mContext,
                            R.string.file_path_move_success, 1000);
                    break;
                case 3:
                    CustomToast.showToast(mContext,
                            R.string.file_path_move_exception, 1000);
                    break;
                case 6:
                    initWidget();
                    break;
                case 7:
                    initCheckBox();
                    break;
                case 8:
                    initSleep();
                    break;
                case 10056:
                    JSONObject object;
                    try {
                        object = new JSONObject(msg.obj.toString());
                        if ("1".equals(object.getString("updateflg"))) {
                            Constant.IYUBA_CN = object.getString("short1");
                            Constant.IYUBA_COM = object.getString("short2");
                            InfoHelper.init(SetActivity.this);
                            InfoHelper.getInstance().putDomain(Constant.IYUBA_CN + "/").putShort(Constant.IYUBA_COM + "/");

                            //这里将分支-网络数据给更新
                            NetHostManager.getInstance().setDomainShort(object.getString("short1"));
                            NetHostManager.getInstance().setDomainLong(object.getString("short2"));

                            //这里顺便把广告sdk的更新给处理了
                            MediaUrlUtils.setBaseUrl("http://new.domain/endpoint");
                        }
                        Toast.makeText(SetActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 23 && resultCode == 1) {// 睡眠模式设置的返回结果
            hour = data.getExtras().getInt("hour");
            minute = data.getExtras().getInt("minute");
            if (hour + minute == 0) {
                isSleep = false;
                hour = 0;
                minute = 0;
                totaltime = 0;
                sleepHandler.removeMessages(0);
                ((TextView) findViewById(R.id.sleep_state))
                        .setText(R.string.setting_sleep_state_off);
            } else {
                sleepHandler.removeMessages(0);
                isSleep = true;
                totaltime = hour * 60 + minute;
                AudioManager amAudioManager = (AudioManager) mContext
                        .getSystemService(Context.AUDIO_SERVICE);
                volume = amAudioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC);
                sleepHandler.sendEmptyMessage(0);
            }

        } else if (requestCode == 25 && resultCode == 2) {// 改变存储路径的结果
            nowSavingPath = data.getExtras().getString("nowSavingPath");
            if (!nowSavingPath.equals(lastSavingPath)) {
                Dialog dialog = new AlertDialog.Builder(mContext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(
                                getResources().getString(R.string.alert_title))
                        .setMessage(
                                getResources().getString(
                                        R.string.setting_file_path_ask))
                        .setPositiveButton(
                                getResources().getString(R.string.alert_btn_ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        if (lastSavingPath == null
                                                || nowSavingPath == null) {
                                            return;
                                        }
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (movePath(lastSavingPath,
                                                        nowSavingPath)) {
                                                    handler.sendEmptyMessage(2);
                                                } else {
                                                    handler.sendEmptyMessage(3);
                                                }

                                            }
                                        }).start();
                                        CustomToast
                                                .showToast(
                                                        mContext,
                                                        R.string.setting_file_path_moving,
                                                        2000);// 这里可以改为引用资源文件
                                    }
                                })
                        .setNeutralButton(
                                getResources().getString(
                                        R.string.alert_btn_cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                }).create();
                dialog.show();

            }
        }
    }

    private boolean movePath(String oldPath, String newPath) {
        boolean isok = true;
        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
//				if (temp.isFile() && temp.getName().startsWith("temp_audio_")) {
                boolean aa = temp.isFile();
                String bb = temp.getName().substring(temp.getName().indexOf("."));
                if (temp.isFile() && temp.getName().substring(temp.getName().indexOf(".")).equals(".mp3")) {
                    if (!UtilFile.copyFile(temp.getPath(), newPath + "/"
                            + (temp.getName()).toString())) {
                        isok = false;
                    } else {
                        // 移动成功，删除原来的
                        if (temp.delete()) {
                            Log.d("shanchu", "chenggong");
                        } else {
                            Log.d("shanchu", "shibai");
                        }
                    }

                }
                if (temp.isDirectory()) {// 如果是子文件夹
                }
            }
        } catch (Exception e) {
            isok = false;
        }
        return isok;
    }

    private void prepareMessage() {

//        String text = getRes、ources().getString(R.string.setting_share1)
//                + Constant.APPName
//                + getResources().getString(R.string.setting_share2)
//                + "：http://app." + Constant.IYUBA_CN + "android/androidDetail.jsp?id=222";
//        Intent shareInt = new Intent(Intent.ACTION_SEND);
//        shareInt.setType("text/*");
//        shareInt.putExtra(Intent.EXTRA_TEXT, text);
//        shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        shareInt.putExtra("sms_body", text);
//        startActivity(Intent.createChooser(shareInt,
//                getResources().getString(R.string.setting_share_ways)));
//        getIyubi("qq");

//        showShare();

        String siteUrl = "http://voa." + Constant.IYUBA_CN + "voa/shareApp.jsp?appType=concept";
        showShare("http://app.iyuba.cn/", "");

//        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_share_app, null, false);
//        AlertDialog dialog = new AlertDialog.Builder(mContext)
//                .setView(dialogView)
//                .create();
//        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);// 一句话搞定
//        TextView tvAndroid = dialogView.findViewById(R.id.tv_android);
//        TextView tvIOSQuick = dialogView.findViewById(R.id.tv_ios_quick);
//        TextView tvIOS = dialogView.findViewById(R.id.tv_ios);
//        ImageView imgClose = dialogView.findViewById(R.id.image_close);
//        imgClose.setOnClickListener(v -> dialog.dismiss());
//        tvAndroid.setOnClickListener(v -> {
//            dialog.dismiss();
//            String siteUrl = "http://voa."+Constant.IYUBA_CN+"voa/shareApp.jsp?appType=concept";
//            showShare(siteUrl, "");
//        });
//        tvIOSQuick.setOnClickListener(v -> {
//            dialog.dismiss();
//            String siteUrl = "https://itunes.apple.com/cn/app/id1455340455?l=zh&ls=1&mt=8";
//            showShare(siteUrl, "-ios");
//
//        });
//        tvIOS.setOnClickListener(v -> {
//            dialog.dismiss();
//            String siteUrl = "https://itunes.apple.com/cn/app/id1164310129?l=zh&ls=1&mt=8";
//            showShare(siteUrl, "-ios");
//        });
//        dialog.show();
//        dialog.setCanceledOnTouchOutside(true);

    }

    private void showShare(String siteUrl, String type) {
        String text = "讲真、你与学霸只有一个" + Constant.APPName + "的距离";
        String imageUrl = "http://app." + Constant.IYUBA_CN + "android/images/newconcept/newconcept.png";
//        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
//        weibo.removeAccount(true);
        ShareSDK.removeCookieOnAuthorize(true);
        OnekeyShare oks = new OnekeyShare();
        if (!com.iyuba.core.InfoHelper.showWeiboShare()) {
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }

        //设置分享是否显示
        if (!com.iyuba.core.InfoHelper.getInstance().openQQShare()) {
            oks.addHiddenPlatform(QQ.NAME);
            oks.addHiddenPlatform(QZone.NAME);
        }
        if (!com.iyuba.core.InfoHelper.getInstance().openWeChatShare()) {
            oks.addHiddenPlatform(Wechat.NAME);
            oks.addHiddenPlatform(WechatMoments.NAME);
            oks.addHiddenPlatform(WechatFavorite.NAME);
        }
        if (!com.iyuba.core.InfoHelper.getInstance().openWeiboShare()) {
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }

        //微博飞雷神
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(Constant.APPName + type);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(siteUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        // oks.setImagePath("/sdcard/test.jpg");
        // imageUrl是Web图片路径，sina需要开通权限
        oks.setImageUrl(imageUrl);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(siteUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("这款应用" + Constant.APPName + "真的很不错啊~推荐！");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(Constant.APPName);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(siteUrl);
        // oks.setDialogMode();
        // oks.setSilent(false);
        oks.setCallback(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Log.e("okCallbackonError", "onError");
                Log.e("--分享失败===", arg2.toString() + "------------------" + arg2.getMessage());

            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                Log.e("okCallbackonComplete", "onComplete");
                if (UserInfoManager.getInstance().isLogin()) {
                    Message msg = new Message();
                    msg.obj = arg0.getName();
                    if (arg0.getName().equals("QQ")
                            || arg0.getName().equals("Wechat")
                            || arg0.getName().equals("WechatFavorite")) {
                        msg.what = 49;
                    } else if (arg0.getName().equals("QZone")
                            || arg0.getName().equals("WechatMoments")
                            || arg0.getName().equals("SinaWeibo")
                            || arg0.getName().equals("TencentWeibo")) {
                        msg.what = 19;
                    }
                    handler.sendMessage(msg);
                } else {
                    handler.sendEmptyMessage(13);
                }
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                Log.e("okCallbackonCancel", "onCancel");
            }
        });
        // 启动分享GUI
        oks.show(this);
    }

    @Override
    public void clearSuccess() {
        //注销账号后退出登录
//        InitPush.getInstance().unRegisterToken(mContext, Integer.parseInt(ConfigManager.Instance().getUserId()));
        UserInfoManager.getInstance().clearUserInfo();
//        SettingConfig.Instance().setHighSpeed(false);
        CustomToast.showToast(mContext, R.string.account_cancellation_success);
        new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage("账户注销成功")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Log.d("数据集合", "这里结束操作了2");
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void showMessage(String msg) {
        ToastUtil.showToast(mContext, msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (waittingDialog!=null&&waittingDialog.isShowing()){
            waittingDialog.dismiss();
        }
        com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil.unDisposable(syncExerciseDis);
        mPresenter.detachView();
        EventBus.getDefault().unregister(this);
    }

    class CleanBufferAsyncTask extends AsyncTask<Void, Void, Void> {
        private String filepath = Constant.picAddr;
        public String result;
        private String cachetype;
        private VoaOp voaOp;
        private BookOp bookOp;
        private List<Book> bookList;
        private List<DownloadInfo> infoList;

        public CleanBufferAsyncTask(String type) {
            this.cachetype = type;
            if (type.equals("image")) {
                filepath = Constant.picAddr;// 此处在voa常速英语中改为filepath=Constant.Instance().getPicPos();Constant文件不一致
            } else if (type.equals("video")) {
                DownloadStateManager manager = DownloadStateManager.instance();
                bookOp = manager.bookOp;
                infoList = manager.downloadList;
                bookList = manager.bookList;

                manager.downloadList.clear();
                filepath = ConfigManager.Instance().loadString("media_saving_path");
            }
        }

        public boolean Delete() {
            File file = new File(filepath);
            if (file.isFile()) {
                file.delete();
                return true;
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                if (files != null && files.length == 0) {
                    return false;
                } else {
                    for (int i = 0; i < files.length; i++) {
                        files[i].delete();
                    }
                    return true;
                }
            } else {
                return false;
            }
        }

        public DownloadInfo getDownloadInfo(int voaId) {
            for (DownloadInfo info : infoList) {
                if (info.voaId == voaId) {
                    return info;
                }
            }

            return null;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Delete()) {
                if (cachetype.equals("image")) {
                    binding.picSize.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.picSize.setText("OK");
                        }
                    });
                } else if (cachetype.equals("video")) {
                    for (Book book : bookList) {
                        book.downloadNum = 0;
                        book.downloadState = 0;

                        bookOp.updateDownloadNum(book);
                    }

                    voaOp = new VoaOp(mContext);
                    voaList = (ArrayList<Voa>) voaOp.findDataFromDownload();
                    if (voaList != null) {
                        Iterator<Voa> iteratorVoa = voaList.iterator();
                        while (iteratorVoa.hasNext()) {
                            Voa voaTemp = iteratorVoa.next();
                            voaOp.deleteDataInDownload(voaTemp.voaId);
                            DownloadStateManager.instance().delete(
                                    voaTemp.voaId);
                        }
                        binding.soundSize.post(new Runnable() {
                            @Override
                            public void run() {
                                binding.soundSize.setText("OK");
                            }
                        });
                    }
                }
            } else
                RxTimer.getInstance().timerInMain("showToast", 0, new RxTimer.RxActionListener() {
                    @Override
                    public void onAction(long number) {
                        RxTimer.getInstance().cancelTimer("showToast");
//                        CustomToast.showToast(mContext, R.string.setting_del_fail, 1000);
                        CustomToast.showToast(mContext, "删除完成", 1000);
                    }
                });
            return null;
        }
    }

    private String getSize(int type) throws Exception {
        if (type == 0) {
            return FileSize.getInstance().getFormatFolderSize(
                    new File(Constant.envir + "/image"));
        } else {

            long audioSize = FileSize.getInstance().getFolderSize(
                    new File(ConfigManager.Instance().loadString("media_saving_path")));

            File soundFile = new File(ConfigManager.Instance().loadString("media_saving_path") + "/sound");

            long soundSize;
            if (soundFile.exists()) {
                soundSize = FileSize.getInstance().getFolderSize(
                        new File(ConfigManager.Instance().loadString("media_saving_path") + "/sound"));
            } else {
                soundSize = 0;
            }


            return FileSize.getInstance().FormetFileSize(audioSize - soundSize);
        }
    }

    //跳转拨号界面
    private void jumpToCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:4008881905"));
        startActivity(intent);
    }

    /**
     * 修改用户名。
     * <p>
     * 主要进行确认密码操作
     */
//    @OnClick(R.id.rl_refactor_username)
//    public void refactorUsername() {
//        if (AccountManager.Instance(mContext).checkUserLogin()) {
//            Intent intent = new Intent(SetActivity.this, RefactorUsernameActivity.class);
//            startActivity(intent);
//        } else {
//            ToastUtil.showToast(mContext, "未登录，请先登录");
//        }
//    面壁者
//    }
    private void updateArticleRecord() {
        //同步听力数据
        Log.e("开始时间", new Date() + "");
        if (!waittingDialog.isShowing()) {
            waittingDialog.show();
        }

        PullHistoryDetailUtil util = new PullHistoryDetailUtil(mContext, new PullHistoryDetailUtil.Callback() {
            @Override
            public void callback() {
                waittingDialog.dismiss();
                EventBus.getDefault().post(new VipChangeEvent());
            }
        });
        util.startPull();
    }

    //检查是否要更新
    private void getUpdateCheck(int bookId) {
        waittingDialog.show();
        UpdateTestAPI updateTestAPI = ApiRetrofit.getInstance().updateTestAPI;
        updateTestAPI.getTestUpDataForBook(UpdateTestAPI.urlCheck, bookId).enqueue(new Callback<UpdateTestAPI.TestBookUpData>() {

            @Override
            public void onResponse(Call<UpdateTestAPI.TestBookUpData> call, Response<UpdateTestAPI.TestBookUpData> response) {
//                UpdateTestAPI.TestBookUpData bean = response.body();
//                List<UpdateTestAPI.TestBookUpData.DataBean> list = bean.data;
//                String voaList = "";
//                for (UpdateTestAPI.TestBookUpData.DataBean bean1 : list) {
//                    int version = ConfigManager.Instance().loadInt(String.valueOf(bean1.voaId), 0);
//                    if (version < bean1.versionExercise) {
//                        ConfigManager.Instance().putInt(String.valueOf(bean1.voaId), bean1.versionExercise);
//                        voaList = voaList + bean1.voaId + ",";
//                    }
//                }
//
//                if (TextUtils.isEmpty(voaList)) {
//                    waittingDialog.dismiss();
//                    ToastUtil.showToast(mContext, "无更新数据！");
//                } else {
//                    Timber.d("需要更新的 数据列表" + voaList.substring(0, voaList.length() - 1));
//                    getUpdateTest(voaList.substring(0, voaList.length() - 1));
//                }

                String voaList = "";
                for (int i = 0; i < 150; i++) {
                    voaList = voaList + (1000 + i) + ",";
                }

                for (int i = 0; i < 150; i++) {
                    voaList = voaList + (2000 + i) + ",";
                }

                getUpdateTest(voaList.substring(0, voaList.length() - 1));
            }

            @Override
            public void onFailure(Call<UpdateTestAPI.TestBookUpData> call, Throwable t) {
                if (!isFinishing()||!isDestroyed()){
                    ToastUtil.showToast(mContext, "更新数据请求失败！");
                    waittingDialog.dismiss();
                }
            }
        });
    }

    //更新习题
    private Disposable syncExerciseDis;
    private void getUpdateTest(String voaList) {
        FixDataManager.syncExercise(voaList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateTestAPI.UpdateTestBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        syncExerciseDis = d;
                    }

                    @Override
                    public void onNext(UpdateTestAPI.UpdateTestBean bean) {
                        List<MultipleChoice> list = bean.multipleChoice;
                        List<VoaStructureExercise> list2 = bean.VoaStructureExercise;
                        if (list != null && list.size() > 0) {
                            MultipleChoiceOp multipleChoiceOp = new MultipleChoiceOp(mContext);
                            multipleChoiceOp.deleteData(list);
                            multipleChoiceOp.saveData(list);
                        }

                        if (list2 != null && list2.size() > 0) {
                            VoaStructureExerciseOp voaStructureExerciseOp = new VoaStructureExerciseOp(mContext);
                            voaStructureExerciseOp.deleteData(list2);
                            voaStructureExerciseOp.saveData(list2);
                        }

                        //这里增加重点难点的习题更新（不知道之前为啥没有，应该是没数据）
                        List<VoaDiffcultyExercise> list3 = TransUtil.transDiffExercise(bean.VoaDiffcultyExercise);
                        if (list3 != null && list3.size() > 0) {
                            VoaDiffcultyExerciseOp voaDiffcultyExerciseOp = new VoaDiffcultyExerciseOp(mContext);
                            voaDiffcultyExerciseOp.deleteData(list3);
                            voaDiffcultyExerciseOp.saveData(list3);
                        }

                        if (!isFinishing()||!isDestroyed()){
                            ToastUtil.showToast(mContext, "更新习题完成");
                            Timber.d("更新习题完成!");
                            waittingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast(mContext, "更新习题失败");
                        waittingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        com.iyuba.conceptEnglish.lil.fix.common_mvp.util.rxjava2.RxUtil.unDisposable(syncExerciseDis);
                    }
                });

        /*UpdateTestAPI updateTestAPI = ApiRetrofit.getInstance().updateTestAPI;
        updateTestAPI.getData(UpdateTestAPI.url, voaList).enqueue(new Callback<UpdateTestAPI.UpdateTestBean>() {
            @Override
            public void onResponse(Call<UpdateTestAPI.UpdateTestBean> call, Response<UpdateTestAPI.UpdateTestBean> response) {
                UpdateTestAPI.UpdateTestBean bean = response.body();
                List<MultipleChoice> list = bean.multipleChoice;
                List<VoaStructureExercise> list2 = bean.VoaStructureExercise;
                if (list != null && list.size() > 0) {
                    MultipleChoiceOp multipleChoiceOp = new MultipleChoiceOp(mContext);
                    multipleChoiceOp.deleteData(list);
                    multipleChoiceOp.saveData(list);
                }

                if (list2 != null && list2.size() > 0) {
                    VoaStructureExerciseOp voaStructureExerciseOp = new VoaStructureExerciseOp(mContext);
                    voaStructureExerciseOp.deleteData(list2);
                    voaStructureExerciseOp.saveData(list2);
                }

                //这里增加重点难点的习题更新（不知道之前为啥没有，应该是没数据）
                List<VoaDiffcultyExercise> list3 = TransUtil.transDiffExercise(bean.VoaDiffcultyExercise);
                if (list3 != null && list3.size() > 0) {
                    VoaDiffcultyExerciseOp voaDiffcultyExerciseOp = new VoaDiffcultyExerciseOp(mContext);
                    voaDiffcultyExerciseOp.deleteData(list3);
                    voaDiffcultyExerciseOp.saveData(list3);
                }

                if (!isFinishing()||!isDestroyed()){
                    ToastUtil.showToast(mContext, "更新习题完成");
                    Timber.d("更新习题完成!");
                    waittingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<UpdateTestAPI.UpdateTestBean> call, Throwable t) {
                if (!isFinishing()||!isDestroyed()){
                    Timber.d("更新习题失败!");
                    ToastUtil.showToast(mContext, "更新习题失败");
                    t.printStackTrace();
                    waittingDialog.dismiss();
                }
            }
        });*/
    }

    //检查知识的跟新版本
    private void updateKnowledge(int booKId) {
        waittingDialog.show();
        UpdateTestAPI updateTestAPI = ApiRetrofit.getInstance().updateTestAPI;
        updateTestAPI.getKnowUpDataForBook(UpdateTestAPI.urlCheckKnow, booKId).enqueue(new Callback<UpdateTestAPI.KnowLedgeVersion>() {

            @Override
            public void onResponse(Call<UpdateTestAPI.KnowLedgeVersion> call, Response<UpdateTestAPI.KnowLedgeVersion> response) {
                UpdateTestAPI.KnowLedgeVersion bean = response.body();
                List<UpdateTestAPI.KnowLedgeVersion.DataBean> list = bean.data;
                String voaList = "";
                for (UpdateTestAPI.KnowLedgeVersion.DataBean bean1 : list) {
                    int version = ConfigManager.Instance().loadInt(String.valueOf(bean1.voaId) + "knowledge", 0);
                    if (version < bean1.versionKnowledge) {
                        ConfigManager.Instance().putInt(String.valueOf(bean1.voaId) + "knowledge", bean1.versionKnowledge);
                        voaList = voaList + bean1.voaId + ",";
                    }
                }

                if (TextUtils.isEmpty(voaList)) {
                    waittingDialog.dismiss();
                    ToastUtil.showToast(mContext, "无更新数据！");
                } else {
                    Timber.d("需要更新的 数据列表" + voaList.substring(0, voaList.length() - 1));
                    getUpdateKnowledge(voaList.substring(0, voaList.length() - 1));
                }
            }

            @Override
            public void onFailure(Call<UpdateTestAPI.KnowLedgeVersion> call, Throwable t) {
                ToastUtil.showToast(mContext, "更新数据请求失败！");
                waittingDialog.dismiss();
            }
        });
    }

    //更新知识
    private void getUpdateKnowledge(String voaList) {
        Timber.d("getUpdateKnowledge: %s", voaList);
        UpdateTestAPI updateTestAPI = ApiRetrofit.getInstance().updateTestAPI;
        updateTestAPI.getKnowData(UpdateTestAPI.urlKnow, voaList).enqueue(new Callback<UpdateTestAPI.knowLedgeData>() {
            @Override
            public void onResponse(Call<UpdateTestAPI.knowLedgeData> call, Response<UpdateTestAPI.knowLedgeData> response) {
                UpdateTestAPI.knowLedgeData bean = response.body();
                List<VoaAnnotation> list = bean.VoaAnnotation;
                List<VoaStructure> list2 = bean.VoaStructure;

                if (list2 != null && list2.size() != 0) {
                    for (VoaStructure item : list2) {
                        Timber.d("item: %s", item.note);
                    }
                } else {
                    Timber.d("null!");
                }

                if (list != null && list.size() > 0) {
                    AnnotationOp annotationOp = new AnnotationOp(mContext);
                    annotationOp.deleteData(list);
                    annotationOp.saveData(list);
                }

                if (list2 != null && list2.size() > 0) {
                    VoaStructureOp voaStructureOp = new VoaStructureOp(mContext);
                    voaStructureOp.deleteData(list2);
                    voaStructureOp.saveData(list2);
                }
                ToastUtil.showToast(mContext, "更新知识完成");
                Timber.d("更新知识完成!");
                waittingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<UpdateTestAPI.knowLedgeData> call, Throwable t) {
                Timber.d("更新知识失败!");
                ToastUtil.showToast(mContext, "更新知识失败");
                t.printStackTrace();
                waittingDialog.dismiss();
            }
        });
    }

    String voaIdStr;

    private void getUpdateContentVersion(int bookId, boolean isAmerican) {
        updateTitleDone = false;
        updateUnitTitleDone = false;

        voaIdStr = "";
        VoaOp voaOp = new VoaOp(mContext);
        String type;
        if (isAmerican) {
            type = UpdateTitleAPI.TYPE_US;
        } else {
            type = UpdateTitleAPI.TYPE_UK;
        }
        waittingDialog.show();


        UpdateTitleAPI updateTitleAPI = ApiRetrofit.getInstance().getUpdateTitleAPI();
        updateTitleAPI.getData(UpdateTitleAPI.url, bookId, type).enqueue(new Callback<UpdateTitleAPI.UpdateTitleBean>() {
            @Override
            public void onResponse(Call<UpdateTitleAPI.UpdateTitleBean> call, Response<UpdateTitleAPI.UpdateTitleBean> response) {
                try {
                    Log.e("ccad--获取更新课文标题", call.request().url().toString());
                    UpdateTitleAPI.UpdateTitleBean bean = response.body();
                    if (bean != null && bean.getSize() > 0) {
                        for (UpdateTitleAPI.UpdateTitleBean.DataBean child : bean.getData()) {
                            int version = Integer.parseInt(child.getVersion());
                            Log.e("ccad-", "----");
                            if (version > voaOp.getCourseVersion(child.getVoa_id(), isAmerican)) {
                                Voa voa = voaOp.findDataById(child.getVoa_id());
                                int id;
                                if (isAmerican) {
                                    id = child.getVoa_id();
                                    voa.version_us = Integer.parseInt(child.getVersion());
                                } else {
                                    id = child.getVoa_id() * 10;
                                    voa.version_uk = Integer.parseInt(child.getVersion());
                                }
                                voaOp.updateDataVersion(voa, isAmerican);
                                if (TextUtils.isEmpty(voaIdStr)) {
                                    voaIdStr += id;
                                } else {
                                    voaIdStr += "," + id;
                                }

                            }
                        }

                        if (!TextUtils.isEmpty(voaIdStr)) {
                            //请求修改的数据
                            Log.e("ccad--", voaIdStr);
                            getVoaDetail(TextAttr.decode(voaIdStr), isAmerican);
                        } else {
                            updateTitleDone = true;
                            if (updateTitleDone && updateUnitTitleDone) {
                                waittingDialog.dismiss();
                            }
                            ToastUtil.showToast(mContext, "本册暂无更新内容");
                        }
                    } else {
                        updateTitleDone = true;
                        if (updateTitleDone && updateUnitTitleDone) {
                            waittingDialog.dismiss();
                        }
                        ToastUtil.showToast(mContext, "本册暂无更新内容");

                    }
                } catch (Exception e) {
                    waittingDialog.dismiss();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<UpdateTitleAPI.UpdateTitleBean> call, Throwable t) {
                updateTitleDone = true;
                if (updateTitleDone && updateUnitTitleDone) {
                    waittingDialog.dismiss();
                }
            }
        });
    }

    /**
     * 更新微课跳转数据
     *
     * @param bookId
     * @param isAmerican
     */
    private void getUnitTitles(int bookId, boolean isAmerican) {
        VoaOp voaOp = new VoaOp(mContext);
        String type;
        if (isAmerican) {
            type = UpdateUnitTitleAPI.TYPE_US;
        } else {
            type = UpdateUnitTitleAPI.TYPE_UK;
        }
        if (!waittingDialog.isShowing()) {
            waittingDialog.show();
        }

        UpdateUnitTitleAPI updateUnitTitleAPI = ApiRetrofit.getInstance().getUnitTitleAPI();
        updateUnitTitleAPI.getData(UpdateUnitTitleAPI.url, bookId, type, UpdateUnitTitleAPI.flgContainNew).enqueue(new Callback<UnitTitle>() {

            @Override
            public void onResponse(Call<UnitTitle> call, Response<UnitTitle> response) {
                Log.e("ccad--获取更新课文标题字符", call.request().url().toString());
                UnitTitle unitTitle = response.body();
                if (unitTitle != null && unitTitle.getSize() > 0) {
                    for (UnitTitle.DataBean dataBean : unitTitle.getData()) {
                        int voaId = dataBean.getVoa_id();
                        voaOp.updateTitle(voaId, dataBean.getTitle());
                        voaOp.updateTitleCn(voaId, dataBean.getTitle_cn());
                        //更新微课需要的数据
                        voaOp.updateMiacroLessonData(voaId, dataBean.getCategoryid(), dataBean.getTitleid(),
                                dataBean.getTotalTime());
                    }

                    if (waittingDialog.isShowing()) {
                        waittingDialog.dismiss();
                    }
                    ConfigManager.Instance().putBoolean("unitTitleChange", true);
                    ToastUtil.showToast(mContext, "更新成功");
                } else {
                    if (waittingDialog.isShowing()) {
                        waittingDialog.dismiss();
                    }
                    ToastUtil.showToast(mContext, "本册暂无更新内容");
                }
            }

            @Override
            public void onFailure(Call<UnitTitle> call, Throwable t) {
                ToastUtil.showToast(mContext, "更新失败，请检查网络");
                if (waittingDialog.isShowing()) {
                    waittingDialog.dismiss();
                }
            }
        });
    }

    private void getVoaDetail(String voaIdStr, boolean isAmerican) {
        UpdateVoaDetailAPI updateVoaDetailAPI = ApiRetrofit.getInstance().getUpdateVoaDetailAPI();
        updateVoaDetailAPI.getData(UpdateVoaDetailAPI.url, UpdateVoaDetailAPI.type, voaIdStr).enqueue(new Callback<UpdateVoaDetailAPI.UpdateVoaDetailBean>() {
            @Override
            public void onResponse(Call<UpdateVoaDetailAPI.UpdateVoaDetailBean> call, Response<UpdateVoaDetailAPI.UpdateVoaDetailBean> response) {
                try {
                    waittingDialog.dismiss();
                    ToastUtil.showToast(mContext, "数据更新完成");
                    UpdateVoaDetailAPI.UpdateVoaDetailBean bean = response.body();
                    if (bean != null && bean.getSize() > 0) {
                        List<VoaDetail> details = new ArrayList<>();
                        for (UpdateVoaDetailAPI.UpdateVoaDetailBean.DataBean child : bean.getData()) {
                            VoaDetail detail = new VoaDetail();
                            if (child.getVoaid() > 5000) {
                                detail.voaId = child.getVoaid() / 10;
                            } else {
                                detail.voaId = child.getVoaid();
                            }
                            detail.paraId = child.getParaid();
                            detail.lineN = child.getIdIndex();
                            detail.startTime = Double.parseDouble(child.getTiming());
                            detail.endTime = Double.parseDouble(child.getEndTiming());
                            detail.sentence = child.getSentence();
                            detail.sentenceCn = child.getSentence_cn();
                            details.add(detail);
                        }
                        VoaDetailOp voaDetailOp = new VoaDetailOp(mContext);
                        voaDetailOp.saveData(details, isAmerican);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<UpdateVoaDetailAPI.UpdateVoaDetailBean> call, Throwable t) {
                waittingDialog.dismiss();
            }
        });

    }

    //单词更新
    String voaIdStrWord;

    private void getWordTitles(int bookId) {
        voaIdStrWord = "";
        waittingDialog.show();
        VoaOp voaOp = new VoaOp(mContext);
        UpdateTitleAPI updateTitleAPI = ApiRetrofit.getInstance().getUpdateTitleAPI();
        updateTitleAPI.getWordData(UpdateTitleAPI.word_url, bookId).enqueue(new Callback<UpdateTitleAPI.UpdateTitleBean>() {
            @Override
            public void onResponse(Call<UpdateTitleAPI.UpdateTitleBean> call, Response<UpdateTitleAPI.UpdateTitleBean> response) {

                try {
                    Log.e("ccad--获取更新课文标题", call.request().url().toString());
                    UpdateTitleAPI.UpdateTitleBean bean = response.body();
                    if (bean != null && bean.getSize() > 0) {
                        for (UpdateTitleAPI.UpdateTitleBean.DataBean child : bean.getData()) {
                            int version = Integer.parseInt(child.getVersion());
                            if (version > voaOp.getWordVersion(child.getVoa_id())) {
                                Voa voa = voaOp.findDataById(child.getVoa_id());
                                voa.version_word = Integer.parseInt(child.getVersion());
                                voaOp.updateWordVersion(voa);
                                if (TextUtils.isEmpty(voaIdStrWord)) {
                                    voaIdStrWord += child.getVoa_id();
                                } else {
                                    voaIdStrWord += "," + child.getVoa_id();
                                }

                            }
                        }
                        if (!TextUtils.isEmpty(voaIdStrWord)) {
                            //请求修改的数据
                            Log.e("ccad--", voaIdStrWord);
                            getWordDetail(TextAttr.decode(voaIdStrWord));
                        } else {
                            waittingDialog.dismiss();
                            ToastUtil.showToast(mContext, "本册暂无更新内容");
                        }
                    } else {
                        waittingDialog.dismiss();
                        ToastUtil.showToast(mContext, "本册暂无更新内容");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    waittingDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<UpdateTitleAPI.UpdateTitleBean> call, Throwable t) {
                waittingDialog.dismiss();
            }
        });
    }

    private void getWordDetail(String voaIdStrWord) {
        UpdateWordDetailAPI updateWordDetailAPI = ApiRetrofit.getInstance().getUpdateWordDetailAPI();
        updateWordDetailAPI.getData(UpdateWordDetailAPI.url, UpdateWordDetailAPI.type, voaIdStrWord).enqueue(new Callback<UpdateWordDetailAPI.WordBean>() {
            @Override
            public void onResponse(Call<UpdateWordDetailAPI.WordBean> call, Response<UpdateWordDetailAPI.WordBean> response) {

                Log.e("ccad--更新课文详情", call.request().url().toString());

                try {
                    waittingDialog.dismiss();
                    ToastUtil.showToast(mContext, "数据更新完成");
                    UpdateWordDetailAPI.WordBean bean = response.body();
                    if (bean != null && bean.getSize() > 0) {
                        List<VoaWord> wordList = new ArrayList<>();
                        for (UpdateWordDetailAPI.WordBean.DataBean child : bean.getData()) {
                            VoaWord word = new VoaWord();
                            word.position = Integer.parseInt(child.getPosition());
                            word.voaId = child.getVoa_id() + "";
                            word.word = child.getWord();
                            word.def = child.getDef();
                            word.pron = child.getPron();
                            word.audio = child.getAudio();
                            word.examples = Integer.parseInt(child.getExamples());
                            wordList.add(word);
                        }
                        VoaWordOp voaWordOp = new VoaWordOp(mContext);
                        voaWordOp.saveData(wordList);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<UpdateWordDetailAPI.WordBean> call, Throwable t) {
                waittingDialog.dismiss();
            }
        });

    }

    //加载弹窗
    private LoadingDialog loadingDialog;

    private void startLoading(String showMsg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
            loadingDialog.create();
        }
        loadingDialog.setMsg(showMsg);
        loadingDialog.show();
    }

    private void stopLoading(){
        if (loadingDialog!=null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    //清理图片缓存的计时器
    private static String timer_clearPic = "clearPicTimer";
    
    //删除本地的音视频文件
    private void deleteLocalAudioAndVideo(){
        startLoading("正在删除缓存文件～");
        
        //音频文件夹路径
        String audioDirectoryPath = FilePathUtil.getAudioDirectoryPath();
        //评测文件路径
        String evalDirectoryPath = FileManager.getInstance().dirPath();
        //准备删除
        
    }
    
    //循环删除文件
    private void deleteFileByRecycle(String filePath){
        try {
            File newFile = new File(filePath);
            if (newFile.isFile()){
                newFile.deleteOnExit();
            }else {
                File[] fileArray = newFile.listFiles();
                if (fileArray!=null && fileArray.length>0){
                    for (int i = 0; i < fileArray.length; i++) {
                        fileArray[i].delete();
                    }
                }

            }
        }catch (Exception e){
            stopLoading();
            ToastUtil.showToast(this,"删除缓存完成");
        }
    }
}
