package com.iyuba.conceptEnglish.sqlite.mode;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class VoaWord implements Serializable {

	@SerializedName("voa_id")
	public String voaId;
	public String word;
	public String def;
	public int examples;
	public String audio;
	public String pron;
	public String answer;
	public int position;
	public int unitId;// 单元 id
	public int book_id;// 课本 id

// 8个参数

}

//		voa_id: "321031",
//		word: "meet",
//def: "v. 与……见面",
//		examples: "1",
//		audio: "http://res.iciba.com/resource/amp3/oxford/0/21/53/2153aadb03f8449fcdab68faa4daf00e.mp3",
//		pron: "mi:t",

//position: "1",

//updateTime: "2020-03-18 14:30:30.0",
//book_id: "280",
//version: "0",
//videoUrl: "http://static2."+Constant.IYUBA_CN+"video/321/321031/321031_10_1.mp4",
//idindex: "10",
//Sentence_cn: "你好，很高兴见到你。",
//pic_url: "280/1/1.jpg",
//unit_id: "1",
//Sentence: "Hello! Nice to meet you!",
//Sentence_audio: "http://"+Constant.staticStr+Constant.IYUBA_CN+"sounds/v
