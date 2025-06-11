package com.iyuba.conceptEnglish.study.voaStructure;

/**
 * @desction:
 * @date: 2023/3/20 19:03
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class VoaStructureKVBean {

    private String text;
    private int resId;
    private boolean isInput;

    public VoaStructureKVBean(String text, int resId, boolean isInput) {
        this.text = text;
        this.resId = resId;
        this.isInput = isInput;
    }

    public String getText() {
        return text;
    }

    public int getResId() {
        return resId;
    }

    public boolean isInput() {
        return isInput;
    }
}
