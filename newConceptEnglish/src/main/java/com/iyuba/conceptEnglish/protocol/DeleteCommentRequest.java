package com.iyuba.conceptEnglish.protocol;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by ivotsm on 2017/3/13.
 */

public class DeleteCommentRequest extends BaseJSONRequest {
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public DeleteCommentRequest(String commentId) {
        setAbsoluteURI("http://voa."+Constant.IYUBA_CN+"voa/UnicomApi?" +
                "id=" + commentId +
                "&protocol=61003");
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new DeleteCommentResponse();
    }
}
