package com.iyuba.core.common.data.model;


import com.google.gson.annotations.SerializedName;
import com.iyuba.configation.ConfigManager;
import com.iyuba.module.toolbox.SingleParser;

import java.util.List;

import io.reactivex.Single;

public class ChildWordUpData implements SingleParser<List<VoaWord2>> {
    @SerializedName("result")
    public int result;
    @SerializedName("bookVersion")
    public int bookVersion;
    @SerializedName("data")
    public List<VoaWord2> list;

    @Override
    public Single<List<VoaWord2>> parse() {
        if (result==(200)) {
            String bookId = ConfigManager.Instance().getCurrBookId();
            ConfigManager.Instance().setWordUpDataVersion(bookId,bookVersion);
            return Single.just(list);
        }else if (result==201){
            return Single.error(new Throwable("无需更新下载"));
        }else {
            return Single.error(new Throwable("网络请求失败"));
        }
    }
}