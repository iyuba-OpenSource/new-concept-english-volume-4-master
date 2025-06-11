package com.iyuba.core.common.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class GetQQServiceResponse extends BaseJSONResponse {
    public JSONObject jsonObjectRoot;


    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        // TODO Auto-generated method stub

        try {
            jsonObjectRoot = new JSONObject(bodyElement);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

}
