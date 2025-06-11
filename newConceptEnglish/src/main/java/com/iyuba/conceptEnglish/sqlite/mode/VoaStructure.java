package com.iyuba.conceptEnglish.sqlite.mode;

import com.google.gson.annotations.SerializedName;

public class VoaStructure {
//	public int id;
//	public String descEN;
//	public String descCH;
//	public String number;
//	public String note;

	@SerializedName("id")
	public int id;
	@SerializedName("desc_EN")
	public String descEN;
	@SerializedName("desc_CH")
	public String descCH;
	@SerializedName("number")
	public String number;
	@SerializedName("note")
	public String note;
}
