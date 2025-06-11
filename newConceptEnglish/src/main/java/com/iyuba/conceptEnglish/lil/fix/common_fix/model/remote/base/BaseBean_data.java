package com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.base;

/**
 * @desction: 基础类型数据
 * @date: 2023/2/24 15:53
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class BaseBean_data<T> {

    private String result;
    private String message;
    private String total;
    private String size;
    private T data;

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public String getTotal() {
        return total;
    }

    public String getSize() {
        return size;
    }

    public T getData() {
        return data;
    }
}
