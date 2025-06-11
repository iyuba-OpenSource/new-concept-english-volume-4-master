package com.iyuba.core.event;

/**
 * Created by Administrator on 2016/12/6 0006.
 */

public class TalkClassEvent {
    public String classId;
    public String className;

    public TalkClassEvent(String classId,String className) {
        this.classId = classId;
        this.className = className;
    }
}
