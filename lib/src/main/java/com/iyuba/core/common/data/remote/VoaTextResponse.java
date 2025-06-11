package com.iyuba.core.common.data.remote;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.iyuba.core.common.data.model.VoaText;
import com.iyuba.module.toolbox.SingleParser;

import java.util.List;

import io.reactivex.Single;

public class VoaTextResponse implements SingleParser<List<VoaText>> {
    @SerializedName("total")
    public String total;
    @SerializedName("voatext")
    public List<VoaText> voaTexts;

    @Override
    public Single<List<VoaText>> parse() {
        if (voaTexts != null) {
            return Single.just(voaTexts);
        } else {
            return Single.error(new Throwable("request failed."));
        }
    }
}
