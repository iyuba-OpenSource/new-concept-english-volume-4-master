package com.iyuba.core.common.sqlite.mode;
/**
 * 单词
 * 
 * @author 陈彤
 */
public class Word {
	public String userid;
	public String key = ""; // 关键�?
	public String lang = "";
	public String audioUrl = ""; // 多媒体网络路�?
	public String pron = ""; // 音标
	public String def = ""; // 解释
	public String examples = ""; // 作为用户闯关后，正确或错误
	public String createDate = ""; // 创建时间
	public boolean isDelete = false;
	public String delete;

	@Override
	public String toString() {
		return "Word{" +
				"userid='" + userid + '\'' +
				", key='" + key + '\'' +
				", lang='" + lang + '\'' +
				", audioUrl='" + audioUrl + '\'' +
				", pron='" + pron + '\'' +
				", def='" + def + '\'' +
				", examples='" + examples + '\'' +
				", createDate='" + createDate + '\'' +
				", isDelete=" + isDelete +
				", delete='" + delete + '\'' +
				'}';
	}
}
