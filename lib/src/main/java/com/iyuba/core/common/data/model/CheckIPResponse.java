package com.iyuba.core.common.data.model;

import android.text.TextUtils;

import com.iyuba.module.toolbox.SingleParser;

import io.reactivex.Single;

public class CheckIPResponse implements SingleParser<CheckIPResponse> {

    /**
     * result : 200
     * province : 北京市
     * city : 北京市
     * adcode : 110000
     * infocode : 10000
     * rectangle : 116.0119343,39.66127144;116.7829835,40.2164962
     * status : 1
     * info : OK
     */

    private String result;
    private String province;
    private String city;
    private String adcode;
    private String infocode;
    private String rectangle;
    private String status;
    private String info;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public String getRectangle() {
        return rectangle;
    }

    public void setRectangle(String rectangle) {
        this.rectangle = rectangle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public Single<CheckIPResponse> parse() {
        if (!TextUtils.isEmpty(result)&&result.equals("200")){
            return Single.just(this);
        }
        return Single.error(new Throwable(result));
    }
}
