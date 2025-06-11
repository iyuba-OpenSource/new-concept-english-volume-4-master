package com.iyuba.core.common.protocol;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 打卡积分或红包
 */

public class GetQQServiceRequest extends BaseJSONRequest {

    public GetQQServiceRequest(String url) {


        setAbsoluteURI(url);

        Log.e("BaseRequest", url);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new GetQQServiceResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
