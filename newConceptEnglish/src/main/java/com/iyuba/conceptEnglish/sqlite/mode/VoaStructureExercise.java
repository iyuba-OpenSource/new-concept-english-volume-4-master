package com.iyuba.conceptEnglish.sqlite.mode;

import com.google.gson.annotations.SerializedName;


public class VoaStructureExercise {

//	public int id;
//
//	public String descEN;
//
//	public String descCN;
//
//	public int number;
//
//	public int column;
//
//	public String note;
//
//	public int type;
//
//	public int quesNum;
//
//	public String answer;

	@SerializedName("id")
	public int id;
	@SerializedName("desc_EN")
	public String descEN;
	@SerializedName("desc_CH")
	public String descCN;
	@SerializedName("column")
	public String column;
	@SerializedName("number")
	public int number;
	@SerializedName("note")
	public String note;
	@SerializedName("type")
	public int type;
	@SerializedName("ques_num")
	public int quesNum;
	@SerializedName("answer")
	public String answer;

	@Override
	public String toString() {
		return "VoaStructureExercise{" +
				"id=" + id +
				", descEN='" + descEN + '\'' +
				", descCN='" + descCN + '\'' +
				", column='" + column + '\'' +
				", number=" + number +
				", note='" + note + '\'' +
				", type=" + type +
				", quesNum=" + quesNum +
				", answer='" + answer + '\'' +
				'}';
	}
}
