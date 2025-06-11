package com.iyuba.core.common.data.model;

import com.google.gson.annotations.SerializedName;
import com.iyuba.module.toolbox.SingleParser;

import io.reactivex.Single;

public class UploadUserInfoResponse implements SingleParser<UploadUserInfoResponse> {
    @SerializedName("result")
    public int result;
    @SerializedName("message")
    public String message = "";

    @Override
    public Single<UploadUserInfoResponse> parse() {
        if (result == 200){
            return Single.just(this);
        }
        return Single.error(new Throwable(message));
    }
}
