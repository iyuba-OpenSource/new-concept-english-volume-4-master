package com.iyuba.core.teacher.protocol;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.TextAttr;

public class DeleteAnswerQuesRequest extends BaseJSONRequest {
	private String format = "json";
	
	public DeleteAnswerQuesRequest(String flg, String id,String uid) {
		
		 
		setAbsoluteURI("http://www." + Constant.IYUBA_CN + "question/delQuestion.jsp?"
				+ "format=" + format
				+ "&flg=" +flg
				+ "&uid=" + uid
				+ "&delId=" + id);
		
		Log.e("iyuba", "http://www." + Constant.IYUBA_CN + "question/delQuestion.jsp?"
				+ "format=" + format
				+ "flg=" +flg
				+ "&uid=" + uid
				+ "&delId=" + id);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new DeleteAnswerQuesResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
