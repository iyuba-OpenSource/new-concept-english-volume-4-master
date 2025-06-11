package com.iyuba.conceptEnglish.sqlite.mode;

/**
 * Created by iyuba on 2018/12/4.
 */

public class RecycleViewItemData<T> {
    //用来装载不同类型的item数据bean
    T t;
    //item数据bean的类型
    int dataType;

    public RecycleViewItemData() {
    }

    public RecycleViewItemData(T t, int dataType) {
        this.t = t;
        this.dataType = dataType;
    }

    public T getT () {
        return t;
    }

    public void setT (T t) {
        this.t = t;
    }

    public int getDataType () {
        return dataType;
    }

    public void setDataType (int dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "RecycleViewItemData{" +
                "t=" + t +
                ", dataType=" + dataType +
                '}';
    }
}

