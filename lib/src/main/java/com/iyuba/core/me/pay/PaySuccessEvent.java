package com.iyuba.core.me.pay;

public class PaySuccessEvent {

    private int code;

    public PaySuccessEvent(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
