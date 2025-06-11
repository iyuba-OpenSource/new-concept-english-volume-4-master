package com.iyuba.conceptEnglish.sqlite.mode;

import androidx.annotation.Keep;
import android.text.SpannableStringBuilder;

import com.google.gson.annotations.SerializedName;

/**
 * 
 */
@Keep
public class VoaDetail {

	@SerializedName("voa_id")
	public int voaId;
	@SerializedName("para_id")
	public String paraId=""; // 段落ID
	@SerializedName("index_N")
	public String lineN=""; // 句子行数
    @SerializedName("start_time")
	public double startTime; // 开始时间
	@SerializedName("end_time")
	public double endTime; // 结束时间
	public double timing; // 所用时间



	public String sentence=""; // 句子内容
	public String imgPath=""; // 图片
    @SerializedName("sentence_cn")
	public String sentenceCn=""; // 中文句子内容
	public String imgwords = "";
	public String chosnWords="";

	public boolean isRead = false;
	public boolean isListen = false;
	public boolean isCommit = false;
	public SpannableStringBuilder readResult;
	public EvaluateBean evaluateBean;
	private int readScore = 0;
	private int shuoshuoId;

	public String pathLocal;

	private int realIndex; //搜索首页需要

	public VoaDetail(){

	}

	public VoaDetail(String sentence,  String sentenceCn,String imgPath) {
		this.sentence = sentence;
		this.imgPath = imgPath;
		this.sentenceCn = sentenceCn;
	}


	public int getShuoshuoId() {
		return shuoshuoId;
	}

	public void setShuoshuoId(int shuoshuoId) {
		this.shuoshuoId = shuoshuoId;
	}

	public int getRealIndex() {
		return realIndex;
	}

	public void setRealIndex(int realIndex) {
		this.realIndex = realIndex;
	}







	public void setReadScore(int score) {
		this.readScore = score;
	}

	public int getReadScore() {
		return readScore;
	}

	public EvaluateBean getEvaluateBean() {
		return evaluateBean;
	}

	public void setEvaluateBean(EvaluateBean evaluateBean) {
		this.evaluateBean = evaluateBean;
	}

	@Override
	public String toString() {
		return "VoaDetail{" +
				"voaId=" + voaId +
				", paraId='" + paraId + '\'' +
				", lineN='" + lineN + '\'' +
				", startTime=" + startTime +
				", endTime=" + endTime +
				", timing=" + timing +
				", sentence='" + sentence + '\'' +
				", imgPath='" + imgPath + '\'' +
				", sentenceCn='" + sentenceCn + '\'' +
				", imgwords='" + imgwords + '\'' +
				", chosnWords='" + chosnWords + '\'' +
				", isRead=" + isRead +
				", isListen=" + isListen +
				", isCommit=" + isCommit +
				", readResult=" + readResult +
				", evaluateBean=" + evaluateBean +
				", readScore=" + readScore +
				", shuoshuoId=" + shuoshuoId +
				", pathLocal='" + pathLocal + '\'' +
				", realIndex=" + realIndex +
				'}';
	}
}
