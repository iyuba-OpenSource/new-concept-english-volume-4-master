package com.iyuba.core.common.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.text.SpannableStringBuilder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VoaText implements Parcelable {


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
    private int voaId;
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
        return voaId;
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
        this.voaId = voaId;
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

    public VoaText(){

    }

    protected VoaText(Parcel in) {
        imgPath = in.readString();
        endTiming = in.readFloat();
        paraId = in.readInt();
        idIndex = in.readInt();
        sentenceCn = in.readString();
        imgWords = in.readString();
        timing = in.readFloat();
        sentence = in.readString();
        voaId=in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //使用Parcelable的瑕点：手动write read 数据
        dest.writeString(imgPath);
        dest.writeFloat(endTiming);
        dest.writeInt(paraId);
        dest.writeInt(idIndex);
        dest.writeString(sentenceCn);
        dest.writeString(imgWords);
        dest.writeFloat(timing);
        dest.writeString(sentence);
        dest.writeInt(voaId);
    }

    public static final Creator<VoaText> CREATOR = new Creator<VoaText>() {
        @Override
        public VoaText createFromParcel(Parcel in) {
            return new VoaText(in);
        }

        @Override
        public VoaText[] newArray(int size) {
            return new VoaText[size];
        }
    };

    @Override
    public String toString() {
        return "VoaText{" +
                "imgPath='" + imgPath + '\'' +
                ", endTiming=" + endTiming +
                ", paraId=" + paraId +
                ", idIndex=" + idIndex +
                ", sentenceCn='" + sentenceCn + '\'' +
                ", imgWords='" + imgWords + '\'' +
                ", timing=" + timing +
                ", sentence='" + sentence + '\'' +
                ", score=" + score +
                ", voaId=" + voaId +
                ", filename='" + filename + '\'' +
                ", parseData=" + parseData +
                ", iscore=" + iscore +
                ", isshowbq=" + isshowbq +
                ", progress=" + progress +
                ", progress2=" + progress2 +
                ", isEvaluate=" + isEvaluate +
                ", isDataBase=" + isDataBase +
                ", words=" + words +
                '}';
    }
}
