package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.line;

/**
 * 练习题的
 */
public class PractiseLineEvent {
    //类型
    public static final String event_word = "word";//单词
    public static final String event_listen = "listen";//听力
    public static final String event_eval = "eval";//评测
    public static final String event_practise = "practise";//练习题
    public static final String event_box = "box";//宝箱

    private String type;

    public PractiseLineEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
