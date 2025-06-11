package com.iyuba.core.common.data.local;

public class EvaluateScore {

    public String userId;

    public String voaId;

    public String paraId;

    public String score;

    public int progress;
    public int progress2;

    public int fluent;
    public String url;

    public float beginTime;
    public float endTime;
    public float duration;

    public int getScore(){
        return Integer.parseInt(score);
    }
    public int getParaId(){
        return Integer.parseInt(paraId);
    }

    @Override
    public String toString() {
        return "EvaluateScore{" +
                "userId='" + userId + '\'' +
                ", voaId='" + voaId + '\'' +
                ", paraId='" + paraId + '\'' +
                ", score='" + score + '\'' +
                ", progress=" + progress +
                ", progress2=" + progress2 +
                ", fluent=" + fluent +
                ", url='" + url + '\'' +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                '}';
    }
}
