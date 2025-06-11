package com.iyuba.conceptEnglish.protocol;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.TextAttr;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ivotsm on 2017/2/22.
 */

public class SearchRequest extends BaseJSONRequest {


    public SearchRequest(String key,int pages,int pageNum,int flg) {


        setAbsoluteURI("http://cms." + Constant.IYUBA_CN + "cmsApi/searchNewsApi.jsp?format=json&key=" + TextAttr.encode(key) +
                "&pages=" + pages +
                "&pageNum=" + pageNum +
                "&parentID=0" +
                "&flg="+flg);
        Log.e("SearchRequest", "http://cms." + Constant.IYUBA_CN + "cmsApi/searchNewsApi.jsp?format=json&key=" +
                key +
                "&pages=" + pages +
                "&pageNum=" + pageNum +
                "&parentID=0" +
                "&flg="+flg);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new SearcheResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
