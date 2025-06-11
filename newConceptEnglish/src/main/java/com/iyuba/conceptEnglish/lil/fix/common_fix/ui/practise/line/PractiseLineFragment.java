package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.line;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.BuildConfig;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.pass.WordExerciseActivity;
import com.iyuba.conceptEnglish.databinding.FragmentPractiseLineBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.event.RefreshDataEvent;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.StrLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.ScreenUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.base.BaseViewBindingFragment;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.DateUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.EncodeUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;
import com.iyuba.conceptEnglish.lil.fix.concept.bgService.ConceptBgPlaySession;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.ArticleRecordBean;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.conceptEnglish.sqlite.mode.VoaSound;
import com.iyuba.conceptEnglish.sqlite.op.ArticleRecordOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaDetailOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaSoundOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
import com.iyuba.conceptEnglish.sqlite.op.WordPassUserOp;
import com.iyuba.conceptEnglish.study.StudyNewActivity;
import com.iyuba.conceptEnglish.study.StudyTempActivity;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.jn.yyz.practise.activity.PractiseActivity;
import com.jn.yyz.practise.entity.TestBean;
import com.jn.yyz.practise.event.BookChooseEventsbus;
import com.jn.yyz.practise.event.FinishPageEventBus;
import com.jn.yyz.practise.event.TestFinishEventbus;
import com.jn.yyz.practise.fragment.PractiseFragment;
import com.jn.yyz.practise.model.bean.ExpBean;
import com.jn.yyz.practise.model.bean.HomeTestTitleBean;
import com.jn.yyz.practise.model.bean.UploadTestBean;
import com.jn.yyz.practise.vm.HomeViewModel;

import org.cybergarage.upnp.std.av.server.object.format.ImageIOFormat;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 新版练习题的连线界面
 */
public class PractiseLineFragment extends BaseViewBindingFragment<FragmentPractiseLineBinding> {

    private String bookType = "concept";
    private int bookId = 1;

    //选择的书籍数据
    private Pair<Integer, String> selectConceptPair = new Pair<>(1, "新概念第一册");
    //当前已经进行的关卡
    private int curStagePosition = 0;

    //数据
    private HomeViewModel homeViewModel;
    //适配器
    private PractiseLineAdapter adapter;
    //弹窗
    private PopupWindow levelWindow;

    public static PractiseLineFragment getInstance(String type, int bookId) {
        PractiseLineFragment fragment = new PractiseLineFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.type, type);
        bundle.putInt(StrLibrary.bookId, bookId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        bookType = getArguments().getString(StrLibrary.type);
        bookId = getArguments().getInt(StrLibrary.bookId);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initList();
        setCallback();

        showTitleByBookId(bookId);
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initToolbar() {
        binding.toolbar.btnBack.setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setBackgroundResource(R.drawable.back_button_normal);
        binding.toolbar.btnBack.setOnClickListener(v -> {
            requireActivity().finish();
        });
        binding.toolbar.title.setText("习题练习");
        binding.toolbar.btnRight.setVisibility(View.VISIBLE);
        binding.toolbar.btnRight.setBackgroundResource(R.drawable.textbook_category);
        binding.toolbar.btnRight.setOnClickListener(v -> {
            showConceptBookDialog();
        });
    }

    private void initList() {
        adapter = new PractiseLineAdapter(requireActivity(), new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new PractiseLineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int voaId, int unitIndex, String title, PractiseLineShowBean.PractisePassBean passBean, boolean isNextLevelExist, int nextLevelPosition) {
                if (passBean.getShowType() == PractiseLineShowBean.box) {
                    if (passBean.isPass()) {
                        ToastUtil.showToast(requireActivity(), "当前宝箱已经开启");
                        return;
                    }

                    if (passBean.isClick()) {
                        submitBoxMark(voaId);
                        return;
                    }

                    ToastUtil.showToast(requireActivity(), "请完成当前课程的全部练习后开启宝箱");
                } else {
                    if (passBean.isClick()) {
                        showStageView(view, voaId, unitIndex, title, passBean, isNextLevelExist, nextLevelPosition);
                    } else {
                        ToastUtil.showToast(requireActivity(), "请先完成上个类型的内容");
                    }
                }

            }
        });
        adapter.setOnPositionJumpClickListener(new PractiseLineAdapter.OnPositionJumpClickListener() {
            @Override
            public void onClick(Pair<Boolean, Integer> lastPair, Pair<Boolean, Integer> nextPair) {
                if (!BuildConfig.DEBUG) {
                    return;
                }

                new AlertDialog.Builder(requireActivity())
                        .setTitle("跳转位置")
                        .setMessage("是否跳转到上一个或者下一个关卡?\n(点击空白关闭弹窗)")
                        .setPositiveButton("上一个", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (lastPair.first) {
                                    binding.recyclerView.scrollToPosition(lastPair.second);
                                } else {
                                    binding.recyclerView.smoothScrollToPosition(lastPair.second);
                                }
                            }
                        }).setNegativeButton("回到顶部", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                binding.recyclerView.scrollToPosition(0);
                            }
                        }).setNeutralButton("下一个", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (nextPair.first) {
                                    binding.recyclerView.scrollToPosition(nextPair.second);
                                } else {
                                    binding.recyclerView.smoothScrollToPosition(nextPair.second);
                                }
                            }
                        }).create().show();
            }
        });

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                //滑动停止(判断第一个或者第一个完整的数据)
                int showFirstPosition = manager.findFirstVisibleItemPosition();
                int showFirstCompletePosition = manager.findFirstCompletelyVisibleItemPosition();

                if (showFirstPosition == curStagePosition || showFirstCompletePosition == curStagePosition) {
                    binding.jumpStage.setVisibility(View.GONE);
                } else {
                    binding.jumpStage.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.jumpStage.setVisibility(View.GONE);
        binding.jumpStage.setOnClickListener(v -> {
            binding.recyclerView.scrollToPosition(curStagePosition);
            binding.jumpStage.setVisibility(View.GONE);
        });
    }

    //加载数据
    private void loadData() {
        homeViewModel.requestExamTitleList(bookId, bookType, "", String.valueOf(UserInfoManager.getInstance().getUserId()));
    }

    //提交宝箱的做题记录
    private void submitBoxMark(int voaId) {
        String date = DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMD);
        String signMd5 = EncodeUtil.md5("iyubaExam" + String.valueOf(UserInfoManager.getInstance().getUserId()) + String.valueOf(Constant.APPID) + String.valueOf(voaId) + date);

        String endTime = String.valueOf(System.currentTimeMillis() / 1000);
        String startTime = String.valueOf(Integer.parseInt(endTime) - 50);

        //需要提交的数据
        String boxTime = DateUtil.toDateStr(System.currentTimeMillis(), DateUtil.YMDHMS);
        List<TestBean> boxList = new ArrayList<>();
        boxList.add(new TestBean(0, 0, 1, "", boxTime, String.valueOf(voaId)));

        homeViewModel.requestUpdateEnglishTestRecord(1, String.valueOf(voaId), bookType, "box", signMd5, startTime, endTime, boxList);
    }

    //转换数据为显示数据
    private void transDataToShow(HomeTestTitleBean titleBean) {
        //没有数据的话就重置为空
        if (titleBean == null || titleBean.getData() == null || titleBean.getData().isEmpty()) {
            adapter.refreshData(new ArrayList<>());
            ToastUtil.showToast(requireActivity(), "未查询到当前书籍的数据");
            binding.jumpStage.setVisibility(View.GONE);
            return;
        }

        //转换后的数据
        List<PractiseLineShowBean> showList = new ArrayList<>();

        for (int i = 0; i < titleBean.getData().size(); i++) {
            List<PractiseLineShowBean.PractisePassBean> passList = new ArrayList<>();

            HomeTestTitleBean.Unity unitData = titleBean.getData().get(i);

            //转换数据
            //单词
            if (unitData.getData().size() > 0) {
                HomeTestTitleBean.Unity.Level tempData = unitData.getData().get(0);

                VoaWordOp wordOp = new VoaWordOp(requireActivity());
                List<VoaWord2> wordList = wordOp.findDataByVoaId(tempData.getVoaid());
                WordPassUserOp passOp = new WordPassUserOp(requireActivity());
                int rightCount = passOp.getRightNum(tempData.getVoaid());
                int totalCount = wordList.size();

                //计算
                boolean isPass = (rightCount * 1.0f / totalCount) > 0.8f;
                boolean isClick = isShowClick(isPass, showList, showList.size(), 0, passList);

                passList.add(new PractiseLineShowBean.PractisePassBean(PractiseLineShowBean.word, isPass, isClick, rightCount, totalCount));
            } else {
                boolean isPass = false;
                boolean isClick = isShowClick(isPass, showList, showList.size(), 0, passList);
                passList.add(new PractiseLineShowBean.PractisePassBean(PractiseLineShowBean.word, isPass, isClick, 0, 1));
            }
            //原文
            if (unitData.getData().size() > 0) {
                HomeTestTitleBean.Unity.Level tempData = unitData.getData().get(0);

                ArticleRecordOp recordOp = new ArticleRecordOp(requireActivity());
                ArticleRecordBean recordBean = recordOp.getData(tempData.getVoaid());

                if (recordBean != null) {
                    boolean isPass = recordBean.is_finish == 1;
                    int curTime = isPass ? recordBean.total_time : recordBean.curr_time;
                    int totalTime = recordBean.total_time;
                    boolean isClick = isShowClick(isPass, showList, showList.size(), 1, passList);

                    passList.add(new PractiseLineShowBean.PractisePassBean(PractiseLineShowBean.listen, isPass, isClick, curTime, totalTime));
                } else {
                    boolean isPass = false;
                    boolean isClick = isShowClick(isPass, showList, showList.size(), 1, passList);
                    passList.add(new PractiseLineShowBean.PractisePassBean(PractiseLineShowBean.listen, isPass, isClick, 0, 1));
                }
            } else {
                boolean isPass = false;
                boolean isClick = isShowClick(isPass, showList, showList.size(), 0, passList);
                passList.add(new PractiseLineShowBean.PractisePassBean(PractiseLineShowBean.listen, isPass, isClick, 0, 1));
            }
            //评测
            if (unitData.getData().size() > 0) {
                HomeTestTitleBean.Unity.Level tempData = unitData.getData().get(0);

                VoaDetailOp detailOp = new VoaDetailOp(requireActivity());
                List<VoaDetail> evalList = detailOp.findDataByVoaId(tempData.getVoaid());
                //循环查询数据，判断是否存在正确数据
                List<VoaSound> rightList = new ArrayList<>();
                VoaSoundOp soundOp = new VoaSoundOp(requireActivity());
                for (int j = 0; j < evalList.size(); j++) {
                    VoaDetail detail = evalList.get(j);
                    int itemId = Integer.parseInt(tempData.getVoaid() + "" + detail.paraId + "" + detail.lineN);
                    VoaSound evalData = soundOp.findDataById(itemId);
                    if (evalData != null) {
                        rightList.add(evalData);
                    }
                }
                //计算
                boolean isPass = rightList.size() == evalList.size();
                boolean isClick = isShowClick(isPass, showList, showList.size(), 2, passList);
                passList.add(new PractiseLineShowBean.PractisePassBean(PractiseLineShowBean.eval, isPass, isClick, rightList.size(), evalList.size()));
            } else {
                boolean isPass = false;
                boolean isClick = isShowClick(isPass, showList, showList.size(), 2, passList);
                passList.add(new PractiseLineShowBean.PractisePassBean(PractiseLineShowBean.eval, isPass, isClick, 0, 1));
            }
            //练习题和宝箱
            if (unitData.getData().size() > 0) {
                HomeTestTitleBean.Unity.Level practiseData = unitData.getData().get(0);
                boolean isPractisePass = practiseData.getIsPass() == 1;
                boolean isPractiseClick = isShowClick(isPractisePass, showList, showList.size(), 3, passList);
                passList.add(new PractiseLineShowBean.PractisePassBean(PractiseLineShowBean.practise, isPractisePass, isPractiseClick, isPractisePass ? 1 : 0, 1));

                if (unitData.getData().size() > 1) {
                    HomeTestTitleBean.Unity.Level boxData = unitData.getData().get(1);
                    boolean isBoxPass = boxData.getIsPass() == 1;
                    boolean isBoxClick = isBoxClick(isBoxPass, passList);
                    passList.add(new PractiseLineShowBean.PractisePassBean(PractiseLineShowBean.box, isBoxPass, isBoxClick, 0, 1));
                } else {
                    boolean isPass = false;
                    boolean isClick = isBoxClick(isPass, passList);
                    passList.add(new PractiseLineShowBean.PractisePassBean(PractiseLineShowBean.box, isPass, isClick, 0, 1));
                }
            } else {
                boolean isPractisePass = false;
                boolean isPractiseClick = isShowClick(isPractisePass, showList, showList.size(), 3, passList);
                passList.add(new PractiseLineShowBean.PractisePassBean(PractiseLineShowBean.practise, isPractisePass, isPractiseClick, 0, 1));

                boolean isBoxPass = false;
                boolean isBoxClick = isBoxClick(isBoxPass, passList);
                passList.add(new PractiseLineShowBean.PractisePassBean(PractiseLineShowBean.box, isBoxPass, isBoxClick, 0, 1));
            }

            //组合数据
            if (unitData.getData().size() > 0) {
                HomeTestTitleBean.Unity.Level tempData = unitData.getData().get(0);

                showList.add(new PractiseLineShowBean(
                        tempData.getVoaid(),
                        unitData.getUnit(),
                        tempData.getTitle(),
                        passList
                ));
            }
        }

        //刷新数据
        adapter.refreshData(showList);
        //判断跳转数据
        curStagePosition = checkCurStagePosition(showList);
        binding.recyclerView.scrollToPosition(curStagePosition);
        //关闭按钮显示
        binding.jumpStage.setVisibility(View.GONE);
    }

    //显示弹窗
    private void showStageView(View view, int voaId, int unitIndex, String title, PractiseLineShowBean.PractisePassBean passBean, boolean isNextLevelExist, int nextLevelPosition) {
        //设置布局样式
        View showView = LayoutInflater.from(requireActivity()).inflate(R.layout.layout_practise_window, null);
        TextView lessonView = showView.findViewById(R.id.lessonText);
        lessonView.setText("Lesson " + unitIndex + ":" + title);

        TextView typeView = showView.findViewById(R.id.typeText);
        String showType = "";
        switch (passBean.getShowType()) {
            case PractiseLineShowBean.word:
                showType = "单词进度(" + passBean.getRightCount() + "/" + passBean.getTotalCount() + ")";
                break;
            case PractiseLineShowBean.listen:
                showType = "听力进度";
                break;
            case PractiseLineShowBean.eval:
                showType = "口语进度(" + passBean.getRightCount() + "/" + passBean.getTotalCount() + ")";
                break;
            case PractiseLineShowBean.practise:
                showType = "练习进度";
                break;
            case PractiseLineShowBean.box:
                showType = "宝箱进度";
                break;
        }
        typeView.setText(showType);
        TextView progressView = showView.findViewById(R.id.progressText);
        int progressInt = 0;
        if (passBean.getTotalCount() > 0) {
            progressInt = (int) (passBean.getRightCount() * 100 / passBean.getTotalCount());
        }
        progressView.setText(progressInt + "%");

        ProgressBar showProgress = showView.findViewById(R.id.showProgress);
        showProgress.setMax(100);
        showProgress.setProgress(progressInt);

        TextView nextLevel = showView.findViewById(R.id.nextLevel);
        if (isNextLevelExist) {
            nextLevel.setVisibility(View.VISIBLE);
        } else {
            nextLevel.setVisibility(View.GONE);
        }
        nextLevel.setOnClickListener(v -> {
            //关闭弹窗
            levelWindow.dismiss();
            //根据数据判断
            binding.recyclerView.smoothScrollToPosition(nextLevelPosition);
        });
        TextView startLevel = showView.findViewById(R.id.startLevel);
        startLevel.setOnClickListener(v -> {
            //关闭弹窗
            levelWindow.dismiss();

            //根据类型判断
            switch (passBean.getShowType()) {
                case PractiseLineShowBean.word:
                    //单词
                    VoaWordOp wordOp = new VoaWordOp(requireActivity());
                    List<VoaWord2> wordList = wordOp.findDataByVoaId(voaId);
                    if (wordList == null || wordList.isEmpty()) {
                        ToastUtil.showToast(requireActivity(), "当前课程不存在单词数据");
                        return;
                    }

                    WordExerciseActivity.start(requireActivity(), (Serializable) wordList);
                    break;
                case PractiseLineShowBean.listen:
                    //听力
                    VoaOp voaOp = new VoaOp(requireActivity());
                    Voa curVoa = voaOp.findDataById(voaId);
                    if (curVoa != null) {
                        VoaDataManager.Instace().voaTemp = curVoa;
                        VoaDataManager.Instace().voaDetailsTemp = new VoaDetailOp(requireActivity()).findDataByVoaId(voaId);

                        //设置为临时数据
                        ConceptBgPlaySession.getInstance().setTempData(true);

                        //跳转界面
                        StudyTempActivity.start(requireActivity(), TypeLibrary.StudyPageType.read, voaId);
                    } else {
                        ToastUtil.showToast(requireActivity(), "未查询到当前课程数据");
                    }
                    break;
                case PractiseLineShowBean.eval:
                    //评测
                    //取消临时数据
                    ConceptBgPlaySession.getInstance().setTempData(false);
                    //获取当前的voa数据
                    VoaOp tempOp = new VoaOp(requireActivity());
                    Voa tempVoa = tempOp.findDataById(voaId);
                    if (tempOp != null) {
                        VoaDataManager.Instace().voaTemp = tempVoa;
                        VoaDataManager.getInstance().voaDetailsTemp = new VoaDetailOp(requireActivity()).findDataByVoaId(voaId);
                        Constant.category = tempVoa.categoryid;
                        //配置数据
                        VoaDataManager.Instace().setSubtitleSum(tempVoa, VoaDataManager.Instace().voaDetailsTemp);
                        VoaDataManager.Instace().setPlayLocalType(0);
                        //跳转界面
                        StudyTempActivity.start(requireActivity(), TypeLibrary.StudyPageType.eval, voaId);
                    } else {
                        ToastUtil.showToast(requireActivity(), "未查询到当前课程数据");
                    }
                    break;
                case PractiseLineShowBean.practise:
                    //判断是否为第一个（第一个免费）
                    int position = nextLevelPosition-1;

                    //增加会员校验
                    if (position>0 && !UserInfoManager.getInstance().isVip()) {
                        new AlertDialog.Builder(requireActivity())
                                .setTitle("会员购买")
                                .setMessage("非会员仅能练习第一课的内容，会员无限制。是否开通会员使用?")
                                .setPositiveButton("开通会员", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        NewVipCenterActivity.start(requireActivity(), NewVipCenterActivity.VIP_APP);
                                    }
                                }).setNegativeButton("暂不使用", null)
                                .setCancelable(false)
                                .create().show();
                        return;
                    }

                    //练习题
                    PractiseActivity.startActivity(requireActivity(), true, bookType, bookId, String.valueOf(voaId), PractiseFragment.page_exerciseLine);
                    break;
                case PractiseLineShowBean.box:
                    //判断前边是否完成，并且获取积分数据
                    //暂时用不到了，因为直接在前面拦截了
                    break;
            }
        });
        //判断学习按钮的字符
        if (passBean.getRightCount() > 0) {
            startLevel.setText("继续学习");
        } else {
            startLevel.setText("开始学习");
        }

        //放在popWindow上
        levelWindow = new PopupWindow(requireActivity());
        levelWindow.setContentView(showView);
        levelWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//        levelWindow.setWidth((int) (ScreenUtil.getScreenW(requireActivity()) * 0.6));
        levelWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        levelWindow.setOutsideTouchable(true);
        levelWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //计算中间显示
        showView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int popWidth = showView.getMeasuredWidth();
        int popHeight = showView.getMeasuredHeight();
        int xOffset = (view.getWidth() - popWidth) / 2;
        //设置居中显示
        levelWindow.showAsDropDown(view, xOffset, 0);
    }

    //新概念的书籍弹窗
    private void showConceptBookDialog() {
        List<Pair<Integer, String>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(1, "新概念第一册"));
        pairList.add(new Pair<>(2, "新概念第二册"));
        pairList.add(new Pair<>(3, "新概念第三册"));
        pairList.add(new Pair<>(4, "新概念第四册"));

        List<String> showList = new ArrayList<>();
        for (int i = 0; i < pairList.size(); i++) {
            showList.add(pairList.get(i).second);
        }

        //显示数据
        String[] showArray = new String[showList.size()];
        showList.toArray(showArray);

        new AlertDialog.Builder(requireActivity())
                .setTitle("选择书籍")
                .setItems(showArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Pair<Integer, String> curPair = pairList.get(which);
                        if (curPair.first == selectConceptPair.first) {
                            return;
                        }

                        selectConceptPair = curPair;
                        EventBus.getDefault().post(new BookChooseEventsbus(bookType, selectConceptPair.first));
                        binding.toolbar.title.setText(selectConceptPair.second);
                        dialog.dismiss();
                    }
                }).create().show();
    }

    /******************************其他方法*****************************************/
    //判断是否符合点击显示操作(根据pass状态和上下文处理)
    private boolean isShowClick(boolean isPass, List<PractiseLineShowBean> showList, int curGroup, int curIndex, List<PractiseLineShowBean.PractisePassBean> curGroupPassList) {
        if (isPass) {
            return true;
        }

        if (curGroup <= 0 && curIndex <= 0) {
            return true;
        }

        if (curIndex > 0) {
            //获取上一个的数据进行判断
            PractiseLineShowBean.PractisePassBean lastPassData = curGroupPassList.get(curIndex - 1);
            return lastPassData.isPass();
        } else {
            //获取上一组的最后一个
            List<PractiseLineShowBean.PractisePassBean> lastPassList = showList.get(curGroup - 1).getPassList();
            PractiseLineShowBean.PractisePassBean lastPassData = lastPassList.get(lastPassList.size() - 1);
            return lastPassData.isPass();
        }
    }

    //判断宝箱是否符合点击操作(根据pass状态和前边内容的操作)
    private boolean isBoxClick(boolean isPass, List<PractiseLineShowBean.PractisePassBean> passList) {
        if (isPass) {
            return true;
        }

        int passCount = 0;
        for (int i = 0; i < passList.size(); i++) {
            PractiseLineShowBean.PractisePassBean passBean = passList.get(i);
            if (passBean.getShowType() != PractiseLineShowBean.box) {
                if (passBean.isPass()) {
                    passCount++;
                }
            }
        }

        if (passCount == passList.size()) {
            return true;
        } else {
            return false;
        }
    }

    //根据书籍判断显示
    private void showTitleByBookId(int bookId) {
        if (bookId <= 0) {
            binding.stageIndex.setText("");
            binding.stageTitle.setText("未知书籍");
            return;
        }

        switch (bookId) {
            case 1:
                binding.stageIndex.setText("第一阶段");
                binding.stageTitle.setText("英语初阶");
                break;
            case 2:
                binding.stageIndex.setText("第二阶段");
                binding.stageTitle.setText("实践进步");
                break;
            case 3:
                binding.stageIndex.setText("第三阶段");
                binding.stageTitle.setText("培养技能");
                break;
            case 4:
                binding.stageIndex.setText("第四阶段");
                binding.stageTitle.setText("流利英语");
                break;
        }
    }

    //判断当前数据已经练习的位置
    private int checkCurStagePosition(List<PractiseLineShowBean> list) {
        int curShowPosition = 0;

        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                PractiseLineShowBean showBean = list.get(i);
                //判断是否当前已经全部完成

                for (int j = 0; j < showBean.getPassList().size(); j++) {
                    PractiseLineShowBean.PractisePassBean passBean = showBean.getPassList().get(j);
                    if (!passBean.isPass()) {
                        //直接返回数据
                        return i;
                    }
                }

                //判断是否为最后一个
                if (i >= list.size() - 1) {
                    return i;
                }
            }
        }
        return curShowPosition;
    }

    /*********************************回调数据**************************************/
    private void setCallback() {
        //获取列表数据
        homeViewModel.getHomeTestTitleBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<HomeTestTitleBean>() {
                    @Override
                    public void onChanged(HomeTestTitleBean titleBean) {
                        //转换数据显示
                        transDataToShow(titleBean);
                    }
                });
        //获取积分数据
        homeViewModel.getExpIntBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<ExpBean>() {
                    @Override
                    public void onChanged(ExpBean expBean) {
                        if (expBean.getResult() == 200) {
                            ToastUtil.showToast(requireActivity(), "恭喜，您已获取" + expBean.getScore() + "积分");
                            loadData();
                        }
                    }
                });
        //上传试题数据(宝箱)
        homeViewModel.getUploadTestBeanMutableLiveData()
                .observe(getViewLifecycleOwner(), new Observer<UploadTestBean>() {
                    @Override
                    public void onChanged(UploadTestBean uploadTestBean) {
                        if (uploadTestBean.getResult() == 200) {
                            homeViewModel.requestUpdateEXP(403, uploadTestBean.getId(), 25);
                        }
                    }
                });
    }

    //刷新数据显示
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PractiseLineEvent event) {
        //单词
        if (event.getType().equals(PractiseLineEvent.event_word)) {
            loadData();
        }

        //听力
        if (event.getType().equals(PractiseLineEvent.event_listen)) {
            loadData();
        }

        //评测
        if (event.getType().equals(PractiseLineEvent.event_eval)) {
            loadData();
        }
    }

    //刷新练习题完成后的数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TestFinishEventbus event) {
        bookId = Integer.parseInt(event.getId());
        bookType = event.getType();

        loadData();
    }

    //切换书籍显示
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BookChooseEventsbus event) {
        bookType = event.getType();
        bookId = event.getBookId();

        //显示标题
        showTitleByBookId(bookId);
        //加载数据
        loadData();
    }
}