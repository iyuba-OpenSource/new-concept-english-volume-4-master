package com.iyuba.core.common.data.remote;

import com.google.gson.annotations.SerializedName;
import com.iyuba.core.common.data.model.VoaText;
import com.iyuba.core.common.data.model.VoaTextYouthByBook;
import com.iyuba.module.toolbox.SingleParser;

import java.util.List;

import io.reactivex.Single;

public class VoaTextBySeriesResponse implements SingleParser<List<VoaTextYouthByBook>> {
    @SerializedName("result")
    public String result;
    @SerializedName("textList")
    public List<VoaTextYouthByBook> textList;

    @Override
    public Single<List<VoaTextYouthByBook>> parse() {
        if (textList != null) {
            return Single.just(textList);
        } else {
            return Single.error(new Throwable("request failed."));
        }
    }
}
