package com.iyuba.core.teacher.protocol;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

public class NoticeRequest extends BaseJSONRequest {
	private String format = "json";
	
	public NoticeRequest(String uid, int isnew ,int pageNum) {
		setAbsoluteURI("http://www." + Constant.IYUBA_CN + "question/getNotice.jsp?"
				+ "format=" + format
				+ "&uid="+uid
				+"&pageNum="+pageNum+"&isNew="+isnew
				);
		
		Log.e("iyuba", "http://www." + Constant.IYUBA_CN + "question/getNotice.jsp?"
				+ "format=" + format
				+ "&uid="+uid
				+"&pageNum="+pageNum+"&isNew="+isnew);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new NoticeResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
