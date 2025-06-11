package com.iyuba.conceptEnglish.protocol;

import android.util.Log;

import com.iyuba.conceptEnglish.fragment.RankFragment;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.me.pay.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ivotsm on 2017/3/13.
 */

public class UserCommentRequest extends BaseJSONRequest {
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public UserCommentRequest(String uid, String topic, String voaId,String type) {
        String sign = MD5.getMD5ofStr(uid + "getWorksByUserId" + df.format(new Date()));
        if (voaId.equals(RankFragment.TAG_VOAID)) {
            /* 此分支不可达 */
            //排行榜模块
            setAbsoluteURI("http://voa." + Constant.IYUBA_CN + "voa/getWorksByUserId.jsp?uid=" +
                    uid +
                    "&topic=" +
                    topic +
                    "&shuoshuoType=2,4&sign=" +
                    sign + "&type=" +
                    type);
            Log.e("userComment", "http://voa." + Constant.IYUBA_CN + "voa/getWorksByUserId.jsp?uid=" +
                    uid +
                    "&topic=" +
                    topic +
                    "&shuoshuoType=4&sign=" +
                    sign + "&type=" +
                    type);

        } else {

            setAbsoluteURI("http://voa." + Constant.IYUBA_CN + "voa/getWorksByUserId.jsp?uid=" +
                    uid +
                    "&topic=" +
                    topic +
                    "&shuoshuoType=2,4&sign=" +
                    sign +
                    "&topicId=" +
                    voaId);
            Log.e("userComment", "http://voa." + Constant.IYUBA_CN + "voa/getWorksByUserId.jsp?uid=" +
                    uid +
                    "&topic=" +
                    topic +
                    "&shuoshuoType=2,4&sign=" +
                    sign +
                    "&topicId=" +
                    voaId);
        }
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new UserCommentResponse();
    }
}
