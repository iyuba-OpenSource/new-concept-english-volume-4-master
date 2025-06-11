package com.iyuba.conceptEnglish.sqlite.mode;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class MultipleChoice {

    @SerializedName("voa_id")
    public int voaId;
    @SerializedName("index_id")
    public int indexId;
    @SerializedName("question")
    public String question;
    @SerializedName("choice_A")
    public String choiceA;
    @SerializedName("choice_B")
    public String choiceB;
    @SerializedName("choice_C")
    public String choiceC;
    @SerializedName("choice_D")
    public String choiceD;
    @SerializedName("answer")
    public String answer;
}
