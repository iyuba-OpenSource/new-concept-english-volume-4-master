package com.iyuba.conceptEnglish.sqlite.mode;

public class WordPassUser {

    public int voa_id;
    public int position;
    public String word;
    public int uid;
    public int is_upload;
    public int answer;
    public int unitId;

    @Override
    public String toString() {
        return "WordPassUser{" +
                "voa_id=" + voa_id +
                ", position=" + position +
                ", word='" + word + '\'' +
                ", uid=" + uid +
                ", is_upload=" + is_upload +
                ", answer=" + answer +
                ", unitId=" + unitId +
                '}';
    }
}