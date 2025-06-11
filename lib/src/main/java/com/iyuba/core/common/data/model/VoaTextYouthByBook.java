package com.iyuba.core.common.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VoaTextYouthByBook implements Parcelable {


    @SerializedName("ImgPath")
    @Nullable
    public String imgPath;
    @SerializedName("EndTiming")
    public  float endTiming;  //停止时间(总的时间)
    @SerializedName("ParaId")
    public  int paraId;
    @SerializedName("IdIndex")
    public  int idIndex;
    @SerializedName("sentence_cn")
    public  String sentenceCn;
    @SerializedName("ImgWords")
    @Nullable
    public  String imgWords;
    @SerializedName("Timing")
    public  float timing;   // 开头的间隔时间(总的时间)
    @SerializedName("Sentence")
    public  String sentence;

    private int score = 0;
    @SerializedName("voaid")
    private int voaid;
    private String filename;
    private SpannableStringBuilder parseData=null;
    /* 建议此两项改成isA，isB，到底是什么意思？ */
    private boolean iscore = false;
    private boolean isshowbq=false;

    public  int progress;
    public  int progress2;

    public boolean isEvaluate;
    public boolean isDataBase;//是否是数据库数据

    public List<SendEvaluateResponse.WordsBean> words;

    public int getVoaId() {
        return voaid;
    }

    public void setIscore(boolean iscore) {
        this.iscore = iscore;
    }

    public boolean isIscore() {
        return iscore;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public boolean isIsshowbq() {
        return isshowbq;
    }

    public void setIsshowbq(boolean isshowbq) {
        this.isshowbq = isshowbq;
    }

    public SpannableStringBuilder getParseData() {
        return parseData;
    }

    public void setParseData(SpannableStringBuilder parseData) {
        this.parseData = parseData;
    }

    public void setVoaId(int voaId) {
        this.voaid = voaId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSentenceEn(){
        sentence = sentence.replace("  "," ");
        return sentence;
    }

    public VoaTextYouthByBook(){

    }

    protected VoaTextYouthByBook(Parcel in) {
        voaid = in.readInt();
        imgPath = in.readString();
        endTiming = in.readFloat();
        paraId = in.readInt();
        idIndex = in.readInt();
        sentenceCn = in.readString();
        imgWords = in.readString();
        timing = in.readFloat();
        sentence = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(voaid);
        dest.writeString(imgPath);
        dest.writeFloat(endTiming);
        dest.writeInt(paraId);
        dest.writeInt(idIndex);
        dest.writeString(sentenceCn);
        dest.writeString(imgWords);
        dest.writeFloat(timing);
        dest.writeString(sentence);
    }

    public static final Creator<VoaTextYouthByBook> CREATOR = new Creator<VoaTextYouthByBook>() {
        @Override
        public VoaTextYouthByBook createFromParcel(Parcel in) {
            return new VoaTextYouthByBook(in);
        }

        @Override
        public VoaTextYouthByBook[] newArray(int size) {
            return new VoaTextYouthByBook[size];
        }
    };
}
