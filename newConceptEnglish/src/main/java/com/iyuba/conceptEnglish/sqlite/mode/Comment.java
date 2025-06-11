package com.iyuba.conceptEnglish.sqlite.mode;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;

public class Comment implements Comparable<Comment>{
    public String id;
    public String imgsrc = "";
    public String userId; // 用户ID
    public SoftReference<Bitmap> picbitmap;
    public int agreeCount;
    public int againstCount;
    public String shuoshuo;
    public int shuoshuoType;
    public String username = "";
    public String createdate;
    public int index = 0;
    public int score = 0;

    @Override
    public int compareTo(@NonNull Comment o) {

        int i = this.index - o.index;//先按照年龄排序
        return i;

    }
}
