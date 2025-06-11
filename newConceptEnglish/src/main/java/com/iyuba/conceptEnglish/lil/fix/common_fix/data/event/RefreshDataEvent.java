package com.iyuba.conceptEnglish.lil.fix.common_fix.data.event;

/**
 * @title: 刷新数据
 * @date: 2023/4/27 15:35
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class RefreshDataEvent {

    private String type;//刷新的数据类型

    private String tips;//传递的通知
    private String msg;//传递的消息

    public RefreshDataEvent(String type) {
        this.type = type;
    }

    public RefreshDataEvent(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public RefreshDataEvent(String type, String tips, String msg) {
        this.type = type;
        this.tips = tips;
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public String getTips() {
        return tips;
    }

    public String getMsg() {
        return msg;
    }
}
