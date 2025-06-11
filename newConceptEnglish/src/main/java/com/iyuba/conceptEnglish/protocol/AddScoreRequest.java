package com.iyuba.conceptEnglish.protocol;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 打卡积分或红包
 */

public class AddScoreRequest extends BaseJSONRequest {
    String url;

    public AddScoreRequest(String userID, String appid, String time, String srid, int idindex) {
        //srid 分享领红包传82，打卡传81
        if ("81".equals(srid)) {
            //打卡
            url = "http://api." + Constant.IYUBA_CN + "credits/updateScore.jsp?mobile=1&srid=" + srid + "&flag=" + time + "&uid=" + userID
                    + "&appid=" + appid;
        } else {
            url = "http://api." + Constant.IYUBA_CN + "credits/updateScore.jsp?mobile=1&srid=" + srid + "&flag=" + time + "&uid=" + userID
                    + "&appid=" + appid + "&idindex=" + idindex;
        }
        setAbsoluteURI(url);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new AddScoreResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
