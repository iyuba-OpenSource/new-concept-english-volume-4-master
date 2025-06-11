package com.iyuba.conceptEnglish.protocol;

import android.util.Log;

import com.iyuba.conceptEnglish.util.MD5;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2017/1/3.
 */

public class GetRankTestInfoRequest extends BaseJSONRequest {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String date = sdf.format(new Date());

    public GetRankTestInfoRequest(String uid, String type, String start, String total) {
//        String sign = URLEncoder.encode(uid + type + start + total + date);
        String sign = MD5.getMD5ofStr(uid + type + start + total + date);
        setAbsoluteURI("http://daxue." + Constant.IYUBA_CN + "ecollege/getTestRanking.jsp?uid=" +
                uid +
                "&type=" +
                type +
                "&start=" +
                start +
                "&total=" +
                total +
                "&sign=" +
                sign );
        Log.e("GetRankTestInfoRequest", "http://daxue." + Constant.IYUBA_CN + "ecollege/getTestRanking.jsp?uid=" +
                uid +
                "&type=" +
                type +
                "&start=" +
                start +
                "&total=" +
                total +
                "&sign=" +
                sign );

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new GetRankTestInfoResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
