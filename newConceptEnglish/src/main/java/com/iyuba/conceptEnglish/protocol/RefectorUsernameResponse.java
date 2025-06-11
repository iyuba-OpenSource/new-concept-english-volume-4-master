package com.iyuba.conceptEnglish.protocol;

import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ivotsm on 2017/2/22.
 */

public class RefectorUsernameResponse extends BaseJSONResponse {
    public String result;
    public String message;
    public String uid;
    public String username;
    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
        try {
            JSONObject jsonRoot = new JSONObject(bodyElement);
            result=jsonRoot.getString("result");
            message=jsonRoot.getString("message");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
