package com.iyuba.conceptEnglish.protocol;


import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ivotsm on 2017/2/22.
 */

public class SearcheResponse extends BaseJSONResponse {
    public JSONObject jsonRoot;

    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
        try {
            jsonRoot = new JSONObject(bodyElement);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
