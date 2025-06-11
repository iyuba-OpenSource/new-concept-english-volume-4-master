package com.iyuba.conceptEnglish.study;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.ExerciseRecord;
import com.iyuba.conceptEnglish.sqlite.mode.VoaStructureExercise;
import com.iyuba.conceptEnglish.sqlite.op.VoaStructureExerciseOp;
import com.iyuba.conceptEnglish.util.JSONFIleUtils;
import com.iyuba.conceptEnglish.util.StringEqualsUtil;
import com.iyuba.conceptEnglish.widget.indicator.ExercisePageIndicator;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关键句型习题
 *
 * Lesson 4 An exciting trip
 * 0. I just ____ (receive) a letter from my brother,He is in Australia.He ____ (be) there for six months.Tim is an engineer.He is working for a big firm and he already ____ (visit) a great number of different places in Australia.He just ____ (buy) an Australian car and ____ (go) to Alice Spring###My brother never ____ (be) abroad before, so he is finding this trip very exciting.____________0. I just ____ (receive) a letter from my brother,He is in Australia.He ____ (be) there for six months.Tim is an engineer.He is working for a big firm and he already ____ (visit) a great number of different places in Australia.He just ____ (buy) an Australian car and ____ (go) to Alice Spring###My brother never ____ (be) abroad before, so he is finding this trip very exciting.
 *
 * Lesson 2 Breakfast or lunch ?
 */
public class VoaStructureExerciseFragment extends Fragment {
    public static VoaStructureExerciseFragment instance;

    private Map<Integer, VoaStructureExercise> structureMap = new HashMap<>();
    private List<Map<Integer, ExerciseRecord>> exerciseRecordList;
    private VoaStructureExerciseOp structureExerciseOp;
    private VoaStructureExercise structureExercise;
    private int typeNum;
    private int curTypeId;
    private int questionNum;
    private int curQuestionId;

    // 公共
    // private CustomDialog waittingDialog;
    private View bottomLayout;
    private RelativeLayout formerQuestion, nextQuestion;
    private Button formerQuestionButton, nextQuestionButton;
    private ExercisePageIndicator pageIndicator;

    private ImageView exerciseResult;
    private View noExerciseView;

    // 普通填空题
    private TextView blankType;
    private ScrollView blankFilling;
    private TextView questionTextView;
    private RelativeLayout rightAnswerLayout;
    private EditText userAnswerEditText;
    private TextView rightAnswerTextView;

    // 段落填空题
    private TextView passageType;
    private TextView passageQuestion;
    private ScrollView passAnswerScroll;
    private ScrollView passQuestionScroll;
    private List<EditText> answerTextList;
    private List<LinearLayout> answerList;
    private List<LinearLayout> answerLayoutList;
    private final static int TOTAL_NUM = 18;

    private List<String> describeList = new ArrayList<String>();
    private int curDescribeId = 0;

    private Context mContext;
    private int voaId;

    private static long mLastClickTime = 0;

    private View rootView;
    //英音  --------


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.voa_structure_exercise, container, false);
        }
        instance = this;
        mContext = getActivity();

        initStructureExercise();
        return rootView;
    }


    private void initStructureExercise() {
        voaId = VoaDataManager.Instace().voaTemp.voaId;

        noExerciseView = rootView.findViewById(R.id.no_exercise_view);
        exerciseResult = (ImageView) rootView.findViewById(R.id.result);

        bottomLayout = rootView.findViewById(R.id.bottom);
        formerQuestion = (RelativeLayout) rootView.findViewById(R.id.former_question);
        nextQuestion = (RelativeLayout) rootView.findViewById(R.id.next_question);
        formerQuestionButton = (Button) rootView.findViewById(R.id.former_question_button);
        nextQuestionButton = (Button) rootView.findViewById(R.id.next_question_button);
        pageIndicator = (ExercisePageIndicator) rootView.findViewById(R.id.pageIndicator);

        // 普通填空题
        blankType = (TextView) rootView.findViewById(R.id.blank_type);
        blankFilling = (ScrollView) rootView.findViewById(R.id.blank_filling);
        questionTextView = (TextView) rootView.findViewById(R.id.question);
        userAnswerEditText = (EditText) rootView.findViewById(R.id.user_answer);
        rightAnswerLayout = (RelativeLayout) rootView.findViewById(R.id.right_answer_body);
        rightAnswerTextView = (TextView) rootView.findViewById(R.id.right_answer);

        // 段落填空题
        passageType = (TextView) rootView.findViewById(R.id.passage_type);
        passageQuestion = (TextView) rootView.findViewById(R.id.passage_filling_question);
        passAnswerScroll = (ScrollView) rootView.findViewById(R.id.answer_scroll);
        passQuestionScroll = (ScrollView) rootView.findViewById(R.id.passage_filling_question_scroll);
        answerTextList = new ArrayList<EditText>();
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text1));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text2));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text3));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text4));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text5));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text6));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text7));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text8));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text9));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text10));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text11));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text12));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text13));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text14));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text15));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text16));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text17));
        answerTextList.add((EditText) rootView.findViewById(R.id.answer_text18));

        answerList = new ArrayList<LinearLayout>();
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer1));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer2));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer3));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer4));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer5));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer6));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer7));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer8));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer9));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer10));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer11));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer12));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer13));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer14));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer15));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer16));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer17));
        answerList.add((LinearLayout) rootView.findViewById(R.id.answer18));

        answerLayoutList = new ArrayList<LinearLayout>();
        answerLayoutList.add((LinearLayout) rootView.findViewById(R.id.answer_layout1));
        answerLayoutList.add((LinearLayout) rootView.findViewById(R.id.answer_layout2));
        answerLayoutList.add((LinearLayout) rootView.findViewById(R.id.answer_layout3));
        answerLayoutList.add((LinearLayout) rootView.findViewById(R.id.answer_layout4));
        answerLayoutList.add((LinearLayout) rootView.findViewById(R.id.answer_layout5));
        answerLayoutList.add((LinearLayout) rootView.findViewById(R.id.answer_layout6));
        answerLayoutList.add((LinearLayout) rootView.findViewById(R.id.answer_layout7));
        answerLayoutList.add((LinearLayout) rootView.findViewById(R.id.answer_layout8));
        answerLayoutList.add((LinearLayout) rootView.findViewById(R.id.answer_layout9));

        structureExerciseOp = new VoaStructureExerciseOp(mContext);
        structureExerciseOp.updateOther();
        structureMap = structureExerciseOp.findData(voaId);
//        getData();
        typeNum = getTypeNum();
        questionNum = structureMap.size();

        if (questionNum == 0) {
            noExerciseView.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.GONE);
            blankFilling.setVisibility(View.GONE);
            passageType.setVisibility(View.GONE);
            passAnswerScroll.setVisibility(View.GONE);
            passQuestionScroll.setVisibility(View.GONE);
        } else {
            bottomLayout.setVisibility(View.VISIBLE);
            blankFilling.setVisibility(View.VISIBLE);
            passageType.setVisibility(View.VISIBLE);
            passAnswerScroll.setVisibility(View.VISIBLE);
            passQuestionScroll.setVisibility(View.VISIBLE);
            noExerciseView.setVisibility(View.GONE);

            formerQuestion.setOnClickListener(olc);
            nextQuestion.setOnClickListener(olc);
            formerQuestionButton.setOnClickListener(olc);
            nextQuestionButton.setOnClickListener(olc);
            exerciseResult.setOnClickListener(olc);
            pageIndicator.setOnClickListener(olc);
            pageIndicator.setIndicator(typeNum);
            pageIndicator.setCurrIndicator(0);

            setStructureExercise();
        }
    }

    public int getTypeNum() {
        int num = 0;
        for (VoaStructureExercise exercise : structureMap.values()) {
            if (TextUtils.isEmpty(exercise.descEN)) {
                num++;
            }
        }
        return num;
    }

    public void setStructureExercise() {
        exerciseRecordList = new ArrayList<Map<Integer, ExerciseRecord>>();
        curTypeId = 0;
        curQuestionId = 0;
        structureExercise = structureMap.get(curQuestionId);
        if (structureExercise != null && !TextUtils.isEmpty(structureExercise.descEN)) {
            describeList.add(structureExercise.descEN + structureExercise.descCN);
        }
        refreshQuestion();
    }

    public void setButton() {
        Map<Integer, ExerciseRecord> recordMap = null;

        recordMap = exerciseRecordList.get(curQuestionId);

        if (recordMap.get(1).AnswerResut != 2) {
            exerciseResult.setVisibility(View.GONE);
        } else {
            exerciseResult.setVisibility(View.VISIBLE);
        }
    }

    OnClickListener olc = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // 点击上一题
                case R.id.former_question_button:
                case R.id.former_question:
                    if (curQuestionId != 0) {
                        rightAnswerLayout.setVisibility(View.GONE);

                        getUserAnswer();

                        VoaStructureExercise lastExercise = structureExercise;
                        structureExercise = structureMap.get(--curQuestionId);

                        // 设置describleEN
                        if (lastExercise.descEN.trim().length() != 0) {
                            if (structureExercise.descEN.trim().length() == 0) {
                                pageIndicator.setCurrIndicator(--curTypeId);

                                describeList.add(structureExercise.descEN + structureExercise.descCN);
                                curDescribeId--;
                            }
                        }

                        refreshQuestion();
                    } else {
                        CustomToast.showToast(mContext, "已是第一题", 1000);
                    }
                    break;
                case R.id.next_question_button:
                case R.id.next_question:
                    if (curQuestionId < questionNum - 1) {
                        rightAnswerLayout.setVisibility(View.GONE);

                        getUserAnswer();

                        structureExercise = structureMap.get(++curQuestionId);

                        if (structureExercise.descEN.trim().length() != 0) {
                            pageIndicator.setCurrIndicator(++curTypeId);

                            describeList.add(structureExercise.descEN + structureExercise.descCN);
                            curDescribeId++;
                        }

                        refreshQuestion();
                    } else {
                        CustomToast.showToast(mContext, "已是最后一题", 1000);
                    }
                    break;
                case R.id.result:// 点击成绩按钮
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(getActivity());
                    } else {
                        exerciseResult.setVisibility(View.GONE);

                        getUserAnswer();
                        judgeUserAnswer();
                        setAnswer();
                    }
                    break;
                // case R.id.pageIndicator:
                // exerciseView.setVisibility(View.GONE);
                // resultView.setVisibility(View.VISIBLE);
                // break;
                default:
                    break;
            }
        }
    };

    public void refreshQuestion() {
        createRecord();
        setButton();
        Log.e("questionNumber", structureExercise + "");
        if (structureExercise.quesNum > 1) {
            refreshPassageFilling();
        } else {
            refreshBlankFilling();
        }
    }

    /**
     * 单空题
     * */
    public void refreshBlankFilling() {
        // TODO
        blankFilling.setVisibility(View.VISIBLE);
        pageIndicator.setVisibility(View.GONE);
        passAnswerScroll.setVisibility(View.GONE);
        passQuestionScroll.setVisibility(View.GONE);
        passageType.setVisibility(View.GONE);

        SpannableStringBuilder style = null;

        blankType.setTextColor(Constant.normalColor);
        blankType.setTextSize(Constant.textSize);


        try {

            String describe = describeList.get(curDescribeId);
            style = transformString(describe);
            blankType.setText(style);
        } catch (Exception e) {

            e.printStackTrace();
            blankType.setText("");
        }


        if (structureExercise.note.trim().length() == 0) {
            questionTextView.setVisibility(View.GONE);
        } else {
            questionTextView.setVisibility(View.VISIBLE);

            String note = structureExercise.number + ". "
                    + structureExercise.note;
            style = transformString(note);
            questionTextView.setText(style);
            questionTextView.setTextColor(Constant.normalColor);
            questionTextView.setTextSize(Constant.textSize);
        }

        userAnswerEditText.setText("");
        userAnswerEditText.setEnabled(true);
        userAnswerEditText.setTextColor(Constant.normalColor);
        userAnswerEditText.setTextSize(Constant.textSize);

        setBlankFillingRecord();
    }

    /**
     * 根据填空数量生成对应的EditText
     * */
    public void refreshPassageFilling() {
        blankFilling.setVisibility(View.GONE);
        passageType.setVisibility(View.VISIBLE);
        passAnswerScroll.setVisibility(View.VISIBLE);
        passQuestionScroll.setVisibility(View.VISIBLE);

        SpannableStringBuilder style = null;

        passageType.setTextColor(Constant.normalColor);
        passageType.setTextSize(Constant.textSize);


        String describe = describeList.get(curDescribeId);
        style = transformString(describe);
        passageType.setText(style);
        if (structureExercise.note.length() == 0) {
            questionTextView.setVisibility(View.GONE);
        } else {
            questionTextView.setVisibility(View.VISIBLE);

            String note = structureExercise.note;
            style = transformPassageQuestion(note);
            Log.e("段落填空", style.toString());
            passageQuestion.setText(style);
            passageQuestion.setTextColor(Constant.normalColor);
            passageQuestion.setTextSize(Constant.textSize);

            setAnswerTextVisible();
            setPassageFillingRecord();
        }
    }

    // TODO
    public void setAnswerTextVisible() {
        int quesNum = structureExercise.quesNum;
        int answerNum = quesNum;
        int answerLayoutNum = quesNum / 2;

        if (answerNum % 2 == 1) {
            answerLayoutNum++;
            answerList.get(answerNum).setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < answerNum; i++) {
            answerList.get(i).setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < TOTAL_NUM / 2; i++) {
            if (i < answerLayoutNum) {
                answerLayoutList.get(i).setVisibility(View.VISIBLE);
            } else {
                answerLayoutList.get(i).setVisibility(View.GONE);
            }
        }
    }

    public void setPassageFillingRecord() {
        Map<Integer, ExerciseRecord> recordMap = exerciseRecordList.get(curQuestionId);
        ExerciseRecord record = null;

        int questionNum = structureExercise.quesNum;

        for (int i = 0; i < questionNum; i++) {
            EditText tempEditText = answerTextList.get(i);
            record = recordMap.get(i + 1);
            if (record == null)
                continue;
            tempEditText.setText(record.UserAnswer);
            if (record.AnswerResut != 2) {
                if (record.AnswerResut == 1) {
                    tempEditText.setBackgroundResource(R.drawable.green_item);
                } else {
                    tempEditText.setBackgroundResource(R.drawable.red_item);
                }
            } else {
                tempEditText.setBackgroundResource(R.drawable.gray_item);
            }
        }
    }

    public void createRecord() {
        Map<Integer, ExerciseRecord> recordMap = new HashMap<>();

        String[] answer = structureExercise.answer.split("###");

        int questionNum = structureExercise.quesNum;
        Log.e("我想要的数据", questionNum + "====");
        if (questionNum > 1) {
            for (int i = 1; i <= questionNum; i++) {
                ExerciseRecord record = new ExerciseRecord();
                record.voaId = voaId;
                record.TestNumber = i;
                try {
                    record.RightAnswer = (i-1)<answer.length?answer[i - 1]:answer[i];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                record.AnswerResut = 2; // 未提交
                recordMap.put(i, record);
            }
        } else {
            ExerciseRecord record = new ExerciseRecord();
            record.voaId = voaId;
            record.TestNumber = 1;
            record.RightAnswer = answer[0];

            recordMap.put(1, record);
        }

        exerciseRecordList.add(recordMap);
    }

    public SpannableStringBuilder transformString(String str) {
        String[] strs = str.split("\\+\\+\\+");
        str = str.replaceAll("\\+\\+\\+", "");
        int from = 0;
        int to;

        SpannableStringBuilder style = new SpannableStringBuilder(str);

        if (strs.length > 1) {
            for (int i = 0; i < strs.length - 2; i = i + 2) {
                from += strs[i].length();
                to = from + strs[i + 1].length();
                style.setSpan(new StyleSpan(
                                android.graphics.Typeface.BOLD_ITALIC), from, to,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                from += strs[i + 1].length();
            }
        }
        if (str.contains("就划线部分提问")) {
            UnderlineSpan underline = new UnderlineSpan();
            int start = str.indexOf("（") + 1;
            int end = str.indexOf("）");
            style.setSpan(underline, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return style;
    }

    public SpannableStringBuilder transformPassageQuestion(String str) {
        String[] strs = str.split("____");
        for (int i = 1; i <= strs.length; i++) {
            str = str.replaceFirst("____", i + ".     ");
        }

        int start = 0;
        int end = 0;

        SpannableStringBuilder spannable = new SpannableStringBuilder(str);

        if (strs.length > 1) {
            for (int i = 0; i < strs.length - 1; i++) {
                start += strs[i].length();
                if (i < 9) {
                    end = start + 7;
                } else {
                    end = start + 8;
                }

                CharacterStyle span = new UnderlineSpan();
                spannable.setSpan(span, start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = end;
            }
        }

        return spannable;
    }

    public SpannableStringBuilder transformPassageQuestionWithAnswer(String str) {
        String[] answer = structureExercise.answer.split("###");

        String[] strs = str.split("____");
        if (TextUtils.isEmpty(structureExercise.answer)){
            answer = new String[strs.length];

            for (int i = 0; i < answer.length; i++) {
                answer[i] = "";
            }
        }

        for (int i = 1; i <= answer.length; i++) {
            str = str.replaceFirst("____", i + "." + answer[i - 1]);
        }

        int start = 0;
        int end = 0;

        SpannableStringBuilder spannable = new SpannableStringBuilder(str);

        if (strs.length > 1) {
            for (int i = 0; i < strs.length - 1; i++) {
                //这里距离长度是会变化的
                int length = 2;//这里是1.这个数据的长度
                if (i>8){
                    length = 3;
                }

                start += strs[i].length() + length;
                end = start + answer[i].length();

                spannable.setSpan(new ForegroundColorSpan(0xff26D197), start,
                        end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                start += answer[i].length();
            }
        }

        return spannable;
    }

    public void getUserAnswer() {
        int questionNum = structureExercise.quesNum;

        ExerciseRecord record = null;
        if (questionNum > 1) {
            for (int i = 0; i < questionNum; i++) {
                EditText editTemp = answerTextList.get(i);

                record = exerciseRecordList.get(curQuestionId).get(i + 1);
                record.UserAnswer = String.valueOf(editTemp.getText()).trim();
            }
        } else {
            record = exerciseRecordList.get(curQuestionId).get(1);
            record.UserAnswer = String.valueOf(userAnswerEditText.getText());
        }
    }

    public void judgeUserAnswer() {
        Map<Integer, ExerciseRecord> exerciseRecordMap = null;
        exerciseRecordMap = exerciseRecordList.get(curQuestionId);

        for (ExerciseRecord record : exerciseRecordMap.values()) {
            if (TextUtils.isEmpty(record.RightAnswer)){
                record.RightAnswer = "";
            }

            if (record.RightAnswer.equals("")) {
                record.AnswerResut = -1;
            } else if (StringEqualsUtil.isEquals(record.RightAnswer.trim(),record.UserAnswer.trim())) {
                record.AnswerResut = 1;
            } else {
                record.AnswerResut = 0;
            }
        }
    }

    public void onResume() {
        super.onResume();

        hideInputWindow();
    }

    public void setAnswer() {
        if (structureExercise.quesNum > 1) {
            setPassageFillingAnswer();
        } else {
            setBlankFillingRecord();
        }
    }

    // TODO
    public void setPassageFillingAnswer() {
        SpannableStringBuilder style = transformPassageQuestionWithAnswer(structureExercise.note);

        passageQuestion.setText(style);
        setPassageFillingRecord();
    }

    public void setBlankFillingRecord() {
        Map<Integer, ExerciseRecord> recordMap = exerciseRecordList
                .get(curQuestionId);
        ExerciseRecord record = recordMap.get(1);
        userAnswerEditText.setText(record.UserAnswer);

        int result = record.AnswerResut;
        if (result == 0) {
            userAnswerEditText.setBackgroundResource(R.drawable.red_item);
        } else if (result == 1) {
            userAnswerEditText.setBackgroundResource(R.drawable.green_item);
        } else {
            userAnswerEditText.setBackgroundResource(R.drawable.gray_item);
        }

        if (record.AnswerResut != 2) {
            String rightAnswer = record.RightAnswer;

            if (rightAnswer.trim().equals("")) {
                CustomToast.showToast(mContext, R.string.has_no_answer, 1000);
            } else {
                rightAnswer = rightAnswer.replaceAll("\\#\\#\\#", "   ");
                SpannableStringBuilder style = transformString(rightAnswer);
                rightAnswerTextView.setText(style);
                rightAnswerTextView.setTextColor(Constant.normalColor);
                rightAnswerTextView.setTextSize(Constant.textSize);

                rightAnswerLayout.setVisibility(View.VISIBLE);
            }

            userAnswerEditText.setEnabled(false);
        } else {
            rightAnswerLayout.setVisibility(View.GONE);
        }
    }

    public static boolean isFastClick() {
        // 当前时间
        long currentTime = System.currentTimeMillis();
        // 两次点击的时间差
        long time = currentTime - mLastClickTime;
        if (time > 100 || mLastClickTime == 0) {
            return true;
        }

        return false;
    }


    public boolean isShowInputWindow() {
        View view = getActivity().getCurrentFocus();

        switch (view.getId()) {
            case R.id.answer_text1:
            case R.id.answer_text2:
            case R.id.answer_text3:
            case R.id.answer_text4:
            case R.id.answer_text5:
            case R.id.answer_text6:
            case R.id.answer_text7:
            case R.id.answer_text8:
            case R.id.answer_text9:
            case R.id.answer_text10:
            case R.id.answer_text11:
            case R.id.answer_text12:
            case R.id.answer_text13:
            case R.id.answer_text14:
            case R.id.answer_text15:
            case R.id.answer_text16:
            case R.id.answer_text17:
            case R.id.answer_text18:
            case R.id.user_answer:
                return true;
        }

        return false;
    }

    public void hideInputWindow() {
        blankType.setFocusable(true);
        blankType.setFocusableInTouchMode(true);
        blankType.requestFocus(); // 初始不让EditText得焦点
        blankType.requestFocusFromTouch();

        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);

        if (imm.isActive()) {
            ((InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private void getData() {
        String data = JSONFIleUtils.getOriginalFundData(mContext, "voa_structure_exercise.json");
        List<VoaStructureExercise> exerciseList = new Gson().fromJson(data, new TypeToken<List<VoaStructureExercise>>() {
        }.getType());

        if (exerciseList != null && exerciseList.size() > 0) {
            for (int i = 0; i < exerciseList.size(); i++) {
                if (voaId == exerciseList.get(i).id) {
                    structureMap.put(i, exerciseList.get(i));
                }
            }
        }
    }


}
