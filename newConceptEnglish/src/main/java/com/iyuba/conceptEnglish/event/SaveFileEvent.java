package com.iyuba.conceptEnglish.event;

/**
 * Created by iyuba on 2018/12/20.
 */

public class SaveFileEvent {
    public boolean isExists;

    public SaveFileEvent(boolean isExists) {
        this.isExists = isExists;
    }
}
