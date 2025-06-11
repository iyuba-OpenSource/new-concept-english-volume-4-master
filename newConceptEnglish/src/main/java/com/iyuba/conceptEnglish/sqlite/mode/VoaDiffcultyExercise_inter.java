package com.iyuba.conceptEnglish.sqlite.mode;

import com.google.gson.annotations.SerializedName;

//专门用于接收接口和本地预存数据的模型（使用时转换完成数据，然后转到VoaDiffcultyExercise中）
public class VoaDiffcultyExercise_inter {
    /**
     * number : 1
     * note : This is a wonderful garden!
     * ques_num : 0
     * answer : What a wonderful garden (this is)!
     * desc_CH : 改写下列句子,用 What来引导下列感叹句。
     * column :
     * id : 2002
     * desc_EN : Write these sentences again. Each sentence must begin with What.
     * type : 0
     */

    @SerializedName("id")
    public String id;
    @SerializedName("desc_EN")
    public String descEN;
    @SerializedName("desc_CH")
    public String descCN;
    @SerializedName("number")
    public String number;
    @SerializedName("column")
    public String column;
    @SerializedName("note")
    public String note;
    @SerializedName("type")
    public String type;
    @SerializedName("ques_num")
    public String quesNum;
    @SerializedName("answer")
    public String answer;

    @Override
    public String toString() {
        return "VoaDiffcultyExercise{" +
                "id=" + id +
                ", descEN='" + descEN + '\'' +
                ", descCN='" + descCN + '\'' +
                ", number=" + number +
                ", column=" + column +
                ", note='" + note + '\'' +
                ", type=" + type +
                ", quesNum=" + quesNum +
                ", answer='" + answer + '\'' +
                '}';
    }
}
