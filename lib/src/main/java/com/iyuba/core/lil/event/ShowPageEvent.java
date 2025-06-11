package com.iyuba.core.lil.event;

public class ShowPageEvent {

    //界面类型
    public static final String Page_WalletList = "WalletList";//钱包列表

    //类型
    private String showPage;

    public ShowPageEvent(String showPage) {
        this.showPage = showPage;
    }

    public String getShowPage() {
        return showPage;
    }
}
