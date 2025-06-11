package com.iyuba.conceptEnglish.study;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.adapter.MultipleResultAdapter;
import com.iyuba.conceptEnglish.lil.concept_other.util.ConceptHomeRefreshUtil;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.protocol.UpdateExerciseRecordRequest;
import com.iyuba.conceptEnglish.protocol.UpdateExerciseRecordResponse;
import com.iyuba.conceptEnglish.sqlite.mode.ExerciseRecord;
import com.iyuba.conceptEnglish.sqlite.mode.MultipleChoice;
import com.iyuba.conceptEnglish.sqlite.op.MultipleChoiceOp;
import com.iyuba.conceptEnglish.sqlite.op.MultipleRecordOp;
import com.iyuba.conceptEnglish.util.JSONFIleUtils;
import com.iyuba.conceptEnglish.widget.components.ResultTextViews;
import com.iyuba.conceptEnglish.widget.indicator.ExercisePageIndicator;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.INetStateReceiver;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.common.widget.RoundProgressBar;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单项选择题
 */
public class MultipleChoiceFragment extends Fragment {
    public static MultipleChoiceFragment instance;

    private List<MultipleChoice> multipleChoiceMap = new ArrayList<>();
    private Map<Integer, ExerciseRecord> exerciseRecordMap;
    private MultipleChoiceOp multipleChoiceOp;
    private int curQuestionId;
    private int questionNum;
    private ExercisePageIndicator pageIndicator;

    private RoundProgressBar circleProgress;
    private TextView questionNumValueText, rightRateValueText,
            spendTimeValueText;
    private TextView questionNumText, rightRateText, spendTimeText;

    private TextView question_index;
    private TextView testTypeTextView, questionTextView;
    private TextView answerAIcon, answerBIcon, answerCIcon, answerDIcon;
    private TextView answerAText, answerBText, answerCText, answerDText;
    private View answerA, answerB, answerC, answerD, select_view;
    private LinearLayout question;
    private TextView formerQuestionButton, nextQuestionButton;
    private TextView exerciseResult;
    private ImageView backButton;
    // private Button checkButton;
    private View startExerciseView, noExerciseView, exerciseView, resultView;
    private ImageView startExercise;

    private int exerciseStatus;// 0 表示做练习 1表示查看结果

    private long startTime, endTime;
    private String startDate, endDate;
    private List<String> date = new ArrayList<String>();
    private Context mContext;
    private int voaId;

    private ResultTextViews resultTextViews;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

    private Chronometer timer; //计时器
    private ImageView image_result;

    private ListView recyclerView;
    private MultipleResultAdapter multipleResultAdapter;
    private List<ExerciseRecord> list_multipleResult = new ArrayList<>();

    private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {
        @Override
        public void onStartSend(BaseHttpRequest request, int rspCookie,
                                int totalLen) {
        }

        @Override
        public void onStartRecv(BaseHttpRequest request, int rspCookie,
                                int totalLen) {
        }

        @Override
        public void onStartConnect(BaseHttpRequest request, int rspCookie) {
        }

        @Override
        public void onSendFinish(BaseHttpRequest request, int rspCookie) {
        }

        @Override
        public void onSend(BaseHttpRequest request, int rspCookie, int len) {
        }

        @Override
        public void onRecvFinish(BaseHttpRequest request, int rspCookie) {
        }

        @Override
        public void onRecv(BaseHttpRequest request, int rspCookie, int len) {
        }

        @Override
        public void onNetError(BaseHttpRequest request, int rspCookie,
                               ErrorResponse errorInfo) {

        }

        @Override
        public void onConnected(BaseHttpRequest request, int rspCookie) {
        }

        @Override
        public void onCancel(BaseHttpRequest request, int rspCookie) {
        }
    };

    private View rootView;

    private MultipleRecordOp multipleRecordOp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.multiple_choice, null);

        mContext = getActivity();
        instance = this;
        multipleRecordOp = new MultipleRecordOp(mContext);
        initMultipleChoice();

        return rootView;
    }


    private void timerStart() {
        timer.setBase(SystemClock.elapsedRealtime());//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
        timer.setFormat("0" + String.valueOf(hour) + ":%s");
        timer.start();

    }

    private void initMultipleChoice() {
        //开始计时
        timer = (Chronometer) rootView.findViewById(R.id.timer);
        image_result = (ImageView) rootView.findViewById(R.id.image_result);
        recyclerView = (ListView) rootView.findViewById(R.id.result_listview);//成绩列表
        //设置RecyclerView管理器
        multipleResultAdapter = new MultipleResultAdapter(mContext, list_multipleResult);
        recyclerView.setAdapter(multipleResultAdapter);
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setQuestion(position);
            }
        });

        voaId = VoaDataManager.Instace().voaTemp.voaId;

        startExercise = (ImageView) rootView.findViewById(R.id.start_exercise);

        circleProgress = (RoundProgressBar) rootView.findViewById(R.id.result_round_bar);

        rightRateValueText = (TextView) rootView.findViewById(R.id.right_rate_value);
        spendTimeValueText = (TextView) rootView.findViewById(R.id.test_spendtime_value);
        questionNumValueText = (TextView) rootView.findViewById(R.id.question_sum_value);

        rightRateText = (TextView) rootView.findViewById(R.id.right_rate);
        spendTimeText = (TextView) rootView.findViewById(R.id.test_spendtime);
        questionNumText = (TextView) rootView.findViewById(R.id.question_sum);
        question_index = (TextView) rootView.findViewById(R.id.question_index); //题号

        pageIndicator = (ExercisePageIndicator) rootView.findViewById(R.id.pageIndicator);
        testTypeTextView = (TextView) rootView.findViewById(R.id.test_type);
        questionTextView = (TextView) rootView.findViewById(R.id.question_body);
        answerAIcon = (TextView) rootView.findViewById(R.id.answer_a_icon);
        answerBIcon = (TextView) rootView.findViewById(R.id.answer_b_icon);
        answerCIcon = (TextView) rootView.findViewById(R.id.answer_c_icon);
        answerDIcon = (TextView) rootView.findViewById(R.id.answer_d_icon);
        answerAText = (TextView) rootView.findViewById(R.id.answer_a_text);
        answerBText = (TextView) rootView.findViewById(R.id.answer_b_text);
        answerCText = (TextView) rootView.findViewById(R.id.answer_c_text);
        answerDText = (TextView) rootView.findViewById(R.id.answer_d_text);
        answerA = rootView.findViewById(R.id.answer_a);
        answerB = rootView.findViewById(R.id.answer_b);
        answerC = rootView.findViewById(R.id.answer_c);
        answerD = rootView.findViewById(R.id.answer_d);
        select_view = rootView.findViewById(R.id.select_question);
        question = (LinearLayout) rootView.findViewById(R.id.question);
        formerQuestionButton = (TextView) rootView.findViewById(R.id.former_question_button);
        nextQuestionButton = (TextView) rootView.findViewById(R.id.next_question_button);
        exerciseResult = (TextView) rootView.findViewById(R.id.result);
        backButton = (ImageView) rootView.findViewById(R.id.back);
        resultTextViews = (ResultTextViews) rootView.findViewById(R.id.result_textviews);

        startExerciseView = rootView.findViewById(R.id.start_exercise_view);
        noExerciseView = rootView.findViewById(R.id.no_exercise_view);
        exerciseView = rootView.findViewById(R.id.exercise_view);
        resultView = rootView.findViewById(R.id.result_view);

        multipleChoiceOp = new MultipleChoiceOp(mContext);

        multipleChoiceMap = multipleChoiceOp.findData(voaId);

        if (voaId == 1003&&multipleChoiceMap.size()>6){
            for (int  i =0;i<5;i++) {
                multipleChoiceMap.remove(0);
            }
        }
//        getData();

        questionNum = multipleChoiceMap.size();

        image_result.setOnClickListener(olc);
        startExercise.setOnClickListener(olc);
        formerQuestionButton.setOnClickListener(olc);
        nextQuestionButton.setOnClickListener(olc);
        exerciseResult.setOnClickListener(olc);
        backButton.setOnClickListener(olc);
        answerA.setOnClickListener(olc);
        answerB.setOnClickListener(olc);
        answerC.setOnClickListener(olc);
        answerD.setOnClickListener(olc);
        pageIndicator.setOnClickListener(olc);
        pageIndicator.setIndicator(questionNum);
        pageIndicator.setCurrIndicator(0);

        exerciseResult.setVisibility(View.INVISIBLE);

        exerciseView.setVisibility(View.GONE);
        resultView.setVisibility(View.GONE);

        if (questionNum == 0) {
            noExerciseView.setVisibility(View.VISIBLE);
            startExerciseView.setVisibility(View.GONE);
        } else {
            startExerciseView.setVisibility(View.VISIBLE);
            noExerciseView.setVisibility(View.GONE);
        }
    }

    public void setMultiplechoice() {
        exerciseRecordMap = new HashMap<Integer, ExerciseRecord>();

        if (exerciseRecordMap.size() != 0) {
            exerciseStatus = 1;
        }

        curQuestionId = 0;
        refreshQuestion();
    }

    OnClickListener olc = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_exercise:
                    startExerciseView.setVisibility(View.GONE);
                    exerciseView.setVisibility(View.VISIBLE);
                    timerStart();

                    setMultiplechoice();
                    startTime = System.currentTimeMillis();
                    startDate = df.format(new Date());
                    date.add(df.format(new Date()));
                    break;
                // 点击上一题
                case R.id.former_question_button:
                case R.id.former_question:
                    nextQuestionButton.setVisibility(View.VISIBLE);

                    if (curQuestionId != 0) {
                        pageIndicator.setCurrIndicator(--curQuestionId);
                        refreshQuestion();
                        date.remove(date.size() - 1);
                    } else {
                        CustomToast.showToast(mContext, "已是第一题", 1000);
                    }
                    break;
                case R.id.next_question_button:
                case R.id.next_question:
                    if (curQuestionId < questionNum - 1) {
                        pageIndicator.setCurrIndicator(++curQuestionId);
                        refreshQuestion();
                        date.add(df.format(new Date()));
                    } else {
                        CustomToast.showToast(mContext, "已是最后一题", 1000);
                    }
                    break;
                case R.id.result:// 点击成绩按钮
                    if (exerciseStatus == 0) {
                        date.add(df.format(new Date()));
                        AlertDialog alert = new AlertDialog.Builder(mContext).create();
                        alert.setTitle("");
                        alert.setMessage(getResources().getString(
                                R.string.submit_result));
                        alert.setIcon(android.R.drawable.ic_dialog_alert);
                        alert.setButton(AlertDialog.BUTTON_POSITIVE, getResources()
                                        .getString(R.string.alert_btn_ok),
                                (dialog, which) -> {
                                    makeResult();
                                    timer.stop();
                                });

                        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getResources()
                                        .getString(R.string.alert_btn_cancel),
                                (dialog, which) -> {

                                });

                        alert.show();
                    } else if (exerciseStatus == 1) {

                        exerciseView.setVisibility(View.GONE);
                        resultView.setVisibility(View.VISIBLE);
                    }
                    break;
                // case R.id.check:
                // curQuestionId = 0;
                // pageIndicator.setCurrIndicator(0);
                // refreshQuestion();
                //
                // exerciseView.setVisibility(View.VISIBLE);
                // resultView.setVisibility(View.GONE);
                //
                // reDoButton.setVisibility(View.VISIBLE);
                // exerciseResult.setVisibility(View.GONE);
                // break;

                case R.id.back:
                case R.id.pageIndicator:
                    exerciseView.setVisibility(View.GONE);
                    resultView.setVisibility(View.VISIBLE);
                    break;
                case R.id.answer_a:
                    if (exerciseStatus == 0) {
                        ConfigManager.Instance().putBoolean("is_exercising", true);

                        colorback();

                        answerAText.setTextColor(Color.WHITE);
                        answerAIcon.setTextColor(Color.WHITE);
                        answerA.setBackgroundResource(R.drawable.multiple_yellow_bg);

//                        answerAIcon.setBackgroundResource(R.drawable.yellow_ball);
//                        answerAIcon.setTextColor(Color.WHITE);

                        makeChoice("a");
                    }

                    break;
                case R.id.answer_b:
                    if (exerciseStatus == 0) {
                        ConfigManager.Instance().putBoolean("is_exercising", true);

                        colorback();

//                        answerBIcon.setBackgroundResource(R.drawable.yellow_ball);
//                        answerBIcon.setTextColor(Color.WHITE);

                        answerBText.setTextColor(Color.WHITE);
                        answerBIcon.setTextColor(Color.WHITE);
                        answerB.setBackgroundResource(R.drawable.multiple_yellow_bg);

                        makeChoice("b");
                    }

                    break;
                case R.id.answer_c:
                    if (exerciseStatus == 0) {
                        ConfigManager.Instance().putBoolean("is_exercising", true);

                        colorback();

                        answerCText.setTextColor(Color.WHITE);
                        answerCIcon.setTextColor(Color.WHITE);
                        answerC.setBackgroundResource(R.drawable.multiple_yellow_bg);

//                        answerCIcon.setBackgroundResource(R.drawable.yellow_ball);
//                        answerCIcon.setTextColor(Color.WHITE);

                        makeChoice("c");
                    }

                    break;
                case R.id.answer_d:
                    if (exerciseStatus == 0) {
                        ConfigManager.Instance().putBoolean("is_exercising", true);

                        colorback();

                        answerDText.setTextColor(Color.WHITE);
                        answerDIcon.setTextColor(Color.WHITE);
                        answerD.setBackgroundResource(R.drawable.multiple_yellow_bg);

//                        answerDIcon.setBackgroundResource(R.drawable.yellow_ball);
//                        answerDIcon.setTextColor(Color.WHITE);

                        makeChoice("d");
                    }

                    break;
                case R.id.image_result:

                    setQuestion(0);
                    break;
                default:
                    break;
            }
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

            }
        }
    };

    /**
     * 提交单选做题记录
     */
    public void makeResult() {
        if (!UserInfoManager.getInstance().isLogin()) {
            LoginUtil.startToLogin(getActivity());
        } else {
            exerciseStatus = 1;
            question.setVisibility(View.VISIBLE);
            ConfigManager.Instance().putBoolean("is_exercising", false);

            int rightsum = 0;// 正确的题数

            ExerciseRecord exerciseRecord = null;
            for (int i = 0; i < questionNum; i++) {
                exerciseRecord = exerciseRecordMap.get(i);
                exerciseRecord.voaId = voaId;
                exerciseRecord.TestNumber = i + 1;
                String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
                exerciseRecord.uid = Integer.parseInt(uid);

                if (exerciseRecord.UserAnswer
                        .equals(exerciseRecord.RightAnswer)) {
                    exerciseRecord.AnswerResut = 1;
                    rightsum++;
                } else {
                    exerciseRecord.AnswerResut = 0;
                }
            }

            int rightrate = (int) ((rightsum / Float.valueOf(questionNum)) * 100);

            circleProgress.setCricleProgressColor(0xff1ABC9C);
            circleProgress.setMax(questionNum);
            circleProgress.setProgress(rightsum);
            questionNumValueText.setText(": " + questionNum);
            rightRateValueText.setText(": " + rightrate + "%");
            spendTimeValueText.setText(getSpendTime());
            exerciseView.setVisibility(View.GONE);
            resultView.setVisibility(View.VISIBLE);

            //本地做题记录
            multipleRecordOp.updateRightNum(voaId, rightsum);
            new Thread(new UpdateExerciseRecordThread()).start();

            //设置数据刷新
            ConceptHomeRefreshUtil.getInstance().setRefreshState(true);

            resultTextViews.setResultMap(exerciseRecordMap);
            resultTextViews.refResultTextView();

            for (int i = 0; i < exerciseRecordMap.size(); i++) {
                list_multipleResult.add(exerciseRecordMap.get(i));
            }
            multipleResultAdapter.notifyDataSetChanged();
        }
    }

    private final void makeChoice(String answer) {
        ExerciseRecord exerciseRecord = exerciseRecordMap.get(curQuestionId);
        exerciseRecord.UserAnswer = answer;
        exerciseRecordMap.put(curQuestionId, exerciseRecord);
    }

    // 根据提供的答案设置相应testrecord的答案
    private final void makeAnswer(ExerciseRecord exerciseRecord) {
        String answer = exerciseRecord.UserAnswer;
        if (answer.trim().length() != 0) {
            int answerN = answer.charAt(0) - 'a';
            switch (answerN) {
                case 0:
                    answerAText.setTextColor(Color.WHITE);
                    answerAIcon.setTextColor(Color.WHITE);
                    answerA.setBackgroundResource(R.drawable.multiple_yellow_bg);

                    break;
                case 1:

                    answerBText.setTextColor(Color.WHITE);
                    answerBIcon.setTextColor(Color.WHITE);
                    answerB.setBackgroundResource(R.drawable.multiple_yellow_bg);


                    break;
                case 2:
                    answerCText.setTextColor(Color.WHITE);
                    answerCIcon.setTextColor(Color.WHITE);
                    answerC.setBackgroundResource(R.drawable.multiple_yellow_bg);

                    break;
                case 3:

                    answerDText.setTextColor(Color.WHITE);
                    answerDIcon.setTextColor(Color.WHITE);
                    answerD.setBackgroundResource(R.drawable.multiple_yellow_bg);

                    break;
            }
        }

        if (exerciseStatus == 1) {
            int rightN = exerciseRecord.RightAnswer.charAt(0) - 'a';
            switch (rightN) {
                case 0:

                    answerAText.setTextColor(Color.WHITE);
                    answerAIcon.setTextColor(Color.WHITE);
                    answerA.setBackgroundResource(R.drawable.multiple_green_bg);

                    break;
                case 1:

                    answerBText.setTextColor(Color.WHITE);
                    answerBIcon.setTextColor(Color.WHITE);
                    answerB.setBackgroundResource(R.drawable.multiple_green_bg);

                    break;
                case 2:
                    answerCText.setTextColor(Color.WHITE);
                    answerCIcon.setTextColor(Color.WHITE);
                    answerC.setBackgroundResource(R.drawable.multiple_green_bg);

                    break;
                case 3:
                    answerDText.setTextColor(Color.WHITE);
                    answerDIcon.setTextColor(Color.WHITE);
                    answerD.setBackgroundResource(R.drawable.multiple_green_bg);

                    break;
            }

            if (answer.trim().length() != 0) {
                int answerN = answer.charAt(0) - 'a';
                if (answerN != rightN) {
                    switch (answerN) {
                        case 0:
                            answerAText.setTextColor(Color.WHITE);
                            answerAIcon.setTextColor(Color.WHITE);
                            answerA.setBackgroundResource(R.drawable.multiple_red_bg);

                            break;
                        case 1:
                            answerBText.setTextColor(Color.WHITE);
                            answerBIcon.setTextColor(Color.WHITE);
                            answerB.setBackgroundResource(R.drawable.multiple_red_bg);

                            break;
                        case 2:
                            answerCText.setTextColor(Color.WHITE);
                            answerCIcon.setTextColor(Color.WHITE);
                            answerC.setBackgroundResource(R.drawable.multiple_red_bg);

                            break;
                        case 3:
                            answerDText.setTextColor(Color.WHITE);
                            answerDIcon.setTextColor(Color.WHITE);
                            answerD.setBackgroundResource(R.drawable.multiple_red_bg);

                            break;
                    }
                }
            }
        }
    }

    // 选项的颜色复位
    private final void colorback() {

        answerA.setBackgroundResource(R.drawable.multiple_white_bg);
        answerB.setBackgroundResource(R.drawable.multiple_white_bg);
        answerC.setBackgroundResource(R.drawable.multiple_white_bg);
        answerD.setBackgroundResource(R.drawable.multiple_white_bg);

        answerAIcon.setTextColor(Color.BLACK);
        answerBIcon.setTextColor(Color.BLACK);
        answerCIcon.setTextColor(Color.BLACK);
        answerDIcon.setTextColor(Color.BLACK);

        answerAText.setTextColor(Color.BLACK);
        answerBText.setTextColor(Color.BLACK);
        answerCText.setTextColor(Color.BLACK);
        answerDText.setTextColor(Color.BLACK);

    }

    // 获取所用时间
    private final String getSpendTime() {
        endTime = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        endDate = df.format(new Date());

        int hour;
        int minute;
        int second;
        hour = (int) ((endTime - startTime) / 3600000);
        minute = (int) ((endTime - startTime) % 3600000) / 60000;
        second = (int) (((endTime - startTime) % 3600000) % 60000) / 1000;
        String string = ": ";
        if (hour != 0) {
            string = string + hour + "h";
        }
        if (minute != 0) {
            string = string + minute + "m";
        }
        if (second != 0) {
            string = string + second + "s";
        }
        return string;
    }


    void refreshQuestion() {
        if (questionNum > 0) {
            // 显示题型和问题
            MultipleChoice multipleChoice = multipleChoiceMap.get(curQuestionId);
            select_view.setVisibility(View.VISIBLE);
            testTypeTextView.setText("选择题");
            testTypeTextView.setTextSize(Constant.textSize);


            question_index.setText(multipleChoice.indexId + ". ");
            questionTextView.setText(multipleChoice.question);

            // 显示选项
            answerAText.setText(multipleChoice.choiceA);
            answerBText.setText(multipleChoice.choiceB);
            answerCText.setText(multipleChoice.choiceC);
            answerDText.setText(multipleChoice.choiceD);

            ExerciseRecord exerciseRecord = exerciseRecordMap
                    .get(curQuestionId);
            if (exerciseRecord == null) {
                exerciseRecord = new ExerciseRecord();
            }

            exerciseRecord.RightAnswer = multipleChoice.answer;
            exerciseRecordMap.put(curQuestionId, exerciseRecord);

            if (curQuestionId != (questionNum - 1) && exerciseStatus == 0) {
                exerciseResult.setVisibility(View.GONE);
            } else {
                exerciseResult.setVisibility(View.VISIBLE);
            }

            if (exerciseStatus == 1) {
                backButton.setVisibility(View.GONE);
                timer.setVisibility(View.GONE);
            }

            colorback();

            setAnswer(exerciseRecordMap.get(curQuestionId));
        }
    }

    // 根据所提供的testrecord记录，设置曾经选过选项的颜色（用于用户前后切换试题时的显示）
    private void setAnswer(ExerciseRecord exerciseRecord) {
        if (exerciseRecord != null) {
            makeAnswer(exerciseRecord);
        }
    }

    public void onResume() {
        super.onResume();
    }

    // 点击答案后回看原题
    public void setQuestion(int index) {
        curQuestionId = index;
        pageIndicator.setCurrIndicator(index);
        refreshQuestion();

        exerciseView.setVisibility(View.VISIBLE);
        resultView.setVisibility(View.GONE);
        exerciseResult.setText("查看成绩");
    }

    public JSONObject transformToJson() {
        List<ExerciseRecord> exerciseRecordList = new ArrayList<ExerciseRecord>();

        ExerciseRecord tempExerciseRecord = null;
        for (int i = 0; i < exerciseRecordMap.size(); i++) {
            tempExerciseRecord = exerciseRecordMap.get(i);
            tempExerciseRecord.BeginTime = date.get(i);
            tempExerciseRecord.TestTime = date.get(i + 1);
            if (tempExerciseRecord != null) {
                exerciseRecordList.add(tempExerciseRecord);
            }
        }

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;

        for (ExerciseRecord exerciseRecord : exerciseRecordList) {
            jsonObject = new JSONObject();
            try {
                jsonObject.put("uid", exerciseRecord.uid);
                jsonObject.put("LessonId", exerciseRecord.voaId);
                jsonObject.put("TestNumber", exerciseRecord.TestNumber);
                jsonObject.put("BeginTime", exerciseRecord.BeginTime);
                jsonObject.put("UserAnswer", exerciseRecord.UserAnswer);
                jsonObject.put("RightAnswer", exerciseRecord.RightAnswer);
                jsonObject.put("AnswerResut", exerciseRecord.AnswerResut);
                jsonObject.put("TestTime", exerciseRecord.TestTime);
                jsonObject.put("AppName", Constant.AppName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }

        JSONObject jsonRoot = new JSONObject();

        try {
            jsonRoot.put("datalist", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonRoot;
    }

    class UpdateExerciseRecordThread implements Runnable {
        @Override
        public void run() {

            ClientSession.Instace().asynGetResponse(
                    new UpdateExerciseRecordRequest(transformToJson()),
                    new IResponseReceiver() {
                        @Override
                        public void onResponse(BaseHttpResponse response,
                                               BaseHttpRequest request, int rspCookie) {
                            UpdateExerciseRecordResponse tr = (UpdateExerciseRecordResponse) response;

                            if (tr != null && tr.result.equals("0") == false) {
                                Looper.prepare();
                                if (tr.jifen.equals("0"))
                                    Toast.makeText(mContext,
                                            "数据提交成功!", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(mContext,
                                            "数据提交成功，恭喜您获得了" + tr.jifen + "分",
                                            Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    }, null, mNetStateReceiver);
        }
    }

    private void getData() {
        String data = JSONFIleUtils.getOriginalFundData(mContext, "multiple_choice.json");
        Log.e("data", data + "");
        List<MultipleChoice> choices;
        choices = new Gson().fromJson(data, new TypeToken<List<MultipleChoice>>() {
        }.getType());
        multipleChoiceMap.clear();
        if (choices != null && choices.size() > 0) {
            for (MultipleChoice multipleChoice : choices) {
                if (multipleChoice.voaId == voaId) {
                    multipleChoice.answer = tansformAnswer(multipleChoice.answer);
                    multipleChoiceMap.add(multipleChoice);
                }
            }
        }


    }

    public String tansformAnswer(String answer) {
        String result = "";

        switch (Integer.valueOf(answer)) {
            case 1:
                result = "a";
                break;
            case 2:
                result = "b";
                break;
            case 3:
                result = "c";
                break;
            case 4:
                result = "d";
                break;
        }

        return result;
    }

}
