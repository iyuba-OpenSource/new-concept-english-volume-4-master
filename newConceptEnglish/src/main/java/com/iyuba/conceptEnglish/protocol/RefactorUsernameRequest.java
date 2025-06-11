package com.iyuba.conceptEnglish.protocol;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ivotsm on 2017/2/22.
 */

public class RefactorUsernameRequest extends BaseJSONRequest {

    public RefactorUsernameRequest(String uid,String oldUsername,String newUsername,String sign) {
        setAbsoluteURI("http://api."+Constant.IYUBA_COM+"v2/api.iyuba?format=json" +
                "&protocol=10012" +
                "&uid="+uid +
                "&oldUsername="+oldUsername +
                "&username="+newUsername +
                "&sign="+sign);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new RefectorUsernameResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
