package com.iyuba.core.common.retrofitapi.result;

/**
 * 苏州爱语吧科技有限公司
 *
 * @Date: 2022/12/27
 * @Author: han rong cheng
 */
public class VXTokenResponse {
    private String result;
    private String message;
    private String token;

    public VXTokenResponse(){}
    public VXTokenResponse(String result, String message, String token) {
        this.result = result;
        this.message = message;
        this.token = token;
    }


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "VXTokenResponse{" +
                "result='" + result + '\'' +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
