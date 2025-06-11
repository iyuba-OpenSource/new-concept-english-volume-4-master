package com.iyuba.conceptEnglish.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.iyuba.conceptEnglish.sqlite.mode.EvaluateBean;

import java.util.ArrayList;
import java.util.List;

public class ResultParse {
    private static final String TAG = ResultParse.class.getSimpleName();


    private static SpannableStringBuilder spannable;


    public static SpannableStringBuilder getSenResultLocal(String[] style, String s) {
        spannable = new SpannableStringBuilder();
        try {
            String[] words = s.trim().split(" ");
            List<String> stringList = new ArrayList<>();
            for (int i = 0; i < words.length; i++) {
                if (!"".equals(words[i])) {
                    stringList.add(words[i]);
                }
            }
            Log.e("单词个数", stringList.size() + "--" + words);
            for (int i = 0; i < stringList.size(); i++) {
                EvaluateBean.WordsBean word = new EvaluateBean.WordsBean();
                word.setContent(stringList.get(i));

                if (i >= style.length) {
                    word.setScore(0);
                } else {
                    word.setScore(Float.parseFloat(style[i]));
                }
                setWordEvaluate(word);
            }
        } catch (Exception e) {
            e.printStackTrace();
            spannable = new SpannableStringBuilder(s);
        }
        return spannable;
    }


    public static SpannableStringBuilder getSenResultEvaluate(List<EvaluateBean.WordsBean> result, String s) {
        spannable = new SpannableStringBuilder();
        Log.e("单词个数", result.size() + "");

        for (EvaluateBean.WordsBean word : result) {
            setWordEvaluate(word);
        }

        return spannable;
    }


    public static void setWordEvaluate(EvaluateBean.WordsBean word) {
        String wordStr = word.getContent();

        if (word.getScore() < 2.5) {
            spannable.append(getSpannable(wordStr, Color.RED)).append(" ");
        } else if (word.getScore() > 4) {
            spannable.append(getSpannable(wordStr, 0xff079500)).append(" ");
        } else {
            spannable.append(getSpannable(wordStr, Color.BLACK)).append(" ");
        }
    }


    public static SpannableStringBuilder getSpannable(String wordStr, int spanColor) {
        Log.e("单词分数--word", wordStr);
        SpannableStringBuilder child = new SpannableStringBuilder(wordStr.trim());
        if (wordStr.length()==0){
            return child;
        }
        String s_str = String.valueOf(wordStr.charAt(0));
        String e_str = String.valueOf(wordStr.charAt(wordStr.length() - 1));
        String rule = "\\p{P}";
        int start = 0;
        int end = wordStr.length() - 1;
        if (s_str.matches(rule)) {
            start++;
        }
        if (e_str.matches(rule)) {
            end--;
        }
        if (start > end) {
            child.setSpan(new ForegroundColorSpan(Color.BLACK), 0, wordStr.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        } else {
            Log.e(wordStr, start + "jieshu   " + end);
            if (start > 0) {
                child.setSpan(new ForegroundColorSpan(Color.BLACK), 0, start, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            if (end < wordStr.length() - 1) {
                child.setSpan(new ForegroundColorSpan(Color.BLACK), end, wordStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            child.setSpan(new ForegroundColorSpan(spanColor), start, end + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        Log.e("单词分数", child.toString());
        return child;
    }


}
