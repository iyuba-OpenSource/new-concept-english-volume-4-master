package com.iyuba.core.common.retrofitapi;

/**
 * 苏州爱语吧科技有限公司
 *
 * @Date: 2022/12/27
 * @Author: han rong cheng
 */
public class UidResponse {
    private String result,uid,message;

    public UidResponse() {}
    public UidResponse(String result, String uid, String message) {
        this.result = result;
        this.uid = uid;
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UidResponse{" +
                "result='" + result + '\'' +
                ", uid='" + uid + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
