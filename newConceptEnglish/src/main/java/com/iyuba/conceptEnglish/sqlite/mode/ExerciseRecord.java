package com.iyuba.conceptEnglish.sqlite.mode;

public class ExerciseRecord {
	public int uid;
	public int voaId;
	public int TestNumber;     //题号
	public String BeginTime;
	public String UserAnswer = "";    //用户答案
	public String RightAnswer ;    //正确答案
	public int AnswerResut = 2;    //正确与否：0：错误；1：正确  2 未提交 -1 无答案
	public String TestTime;    //测试时间

	@Override
	public String toString() {
		return "ExerciseRecord{" +
				"uid=" + uid +
				", voaId=" + voaId +
				", TestNumber=" + TestNumber +
				", BeginTime='" + BeginTime + '\'' +
				", UserAnswer='" + UserAnswer + '\'' +
				", RightAnswer='" + RightAnswer + '\'' +
				", AnswerResut=" + AnswerResut +
				", TestTime='" + TestTime + '\'' +
				'}';
	}
}
