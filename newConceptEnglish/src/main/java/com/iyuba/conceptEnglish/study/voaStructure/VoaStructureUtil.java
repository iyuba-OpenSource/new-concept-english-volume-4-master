package com.iyuba.conceptEnglish.study.voaStructure;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.sqlite.mode.ExerciseRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @desction:
 * @date: 2023/3/20 16:50
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class VoaStructureUtil {

    //转换题目
    public static SpannableStringBuilder transformString(String str) {
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

    //转换多填空的问题
    public static SpannableStringBuilder transformPassageQuestion(String str) {
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

    //转换多填空的问题和答案
    public static SpannableStringBuilder transformPassageQuestionWithAnswer(String str,String answerStr) {
        String[] answer = answerStr.split("###");

        String[] strs = str.split("____");
        if (TextUtils.isEmpty(answerStr)){
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

    //根据文本转换为空格数据
    public static List<String> transTextToMultiBlank(String text){
        return null;
    }

    //根据数据转换为空格数据
    public static List<VoaStructureKVBean> transIntDataToMultiBlank(int num){
        List<VoaStructureKVBean> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            list.add(new VoaStructureKVBean("", R.drawable.gray_item,true));
        }
        return list;
    }

    //根据用户答案转换为答案数据
    public static List<VoaStructureKVBean> transUserAnswerToMultiBlank(Map<Integer, ExerciseRecord> map){
        List<VoaStructureKVBean> tempList = new ArrayList<>();
        for (int i = 0; i < map.keySet().size(); i++) {
            ExerciseRecord record = map.get(i);

            int resId = R.drawable.gray_item;
            if (record.AnswerResut!=2){
                if (record.AnswerResut==1){
                    resId = R.drawable.green_item;
                }else {
                    resId = R.drawable.red_item;
                }
            }

            tempList.add(new VoaStructureKVBean(record.UserAnswer,resId,false));
        }

        return tempList;
    }

    //根据标准答案转换为答案数据
    public static List<String> transAnswerToMultiBlank(String answerText){
        List<String> list = new ArrayList<>();

        String reg = "###";
        String[] answer = answerText.split(reg);
        if (answer.length>0){
            for (int i = 0; i < answer.length; i++) {
                list.add(answer[i]);
            }
        }

        return list;
    }
}
