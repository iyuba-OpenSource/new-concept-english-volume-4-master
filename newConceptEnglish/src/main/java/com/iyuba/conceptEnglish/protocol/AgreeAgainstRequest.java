package com.iyuba.conceptEnglish.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

public class AgreeAgainstRequest extends BaseJSONRequest {


    public AgreeAgainstRequest(String protocol, String commnetId, int type) {
        super();
        if (type != 2) {
            setAbsoluteURI("http://daxue." + Constant.IYUBA_CN + "appApi//UnicomApi?" + "protocol=" + protocol + "&id=" + commnetId);
            Log.e("zanRequest", "http://daxue." + Constant.IYUBA_CN + "appApi//UnicomApi?" + "protocol=" + protocol + "&id=" + commnetId);
        } else {
            setAbsoluteURI("http://voa." + Constant.IYUBA_CN + "voa/UnicomApi?id=" + commnetId + "&protocol=" + protocol);
            Log.e("zanRequest", "http://voa." + Constant.IYUBA_CN + "voa/UnicomApi?id=" + commnetId + "&protocol=" + protocol);
        }
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new AgreeAgainstResponse();
    }

}
