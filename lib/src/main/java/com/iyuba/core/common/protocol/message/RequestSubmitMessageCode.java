/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

/**
 * @author yao 验证短信验证码
 */
public class RequestSubmitMessageCode extends BaseJSONRequest {

	public RequestSubmitMessageCode(String userphone) {
		setAbsoluteURI("http://api."+ Constant.IYUBA_COM+"sendMessage3.jsp?format=json"
				+ "&userphone=" + userphone);
		Log.e("RequestSubmit", "http://api."+Constant.IYUBA_COM+"sendMessage3.jsp?format=json"
				+ "&userphone=" + userphone);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseSubmitMessageCode();
	}

}
