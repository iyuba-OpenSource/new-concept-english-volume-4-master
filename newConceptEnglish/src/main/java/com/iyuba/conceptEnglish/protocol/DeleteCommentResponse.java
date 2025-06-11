package com.iyuba.conceptEnglish.protocol;

import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ivotsm on 2017/3/13.
 */

public class DeleteCommentResponse extends BaseJSONResponse {
    public String ResultCode = "";
    public String Message = "";

    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        try {
            JSONObject jsonObjectRoot = new JSONObject(bodyElement);
            ResultCode = jsonObjectRoot.getString("ResultCode");
            Message = jsonObjectRoot.getString("Message");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return true;
    }
}
