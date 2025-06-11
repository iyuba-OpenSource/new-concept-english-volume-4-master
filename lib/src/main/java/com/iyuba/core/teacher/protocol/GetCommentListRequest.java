package com.iyuba.core.teacher.protocol;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

public class GetCommentListRequest extends BaseJSONRequest {
	private String format = "json";
	
	public GetCommentListRequest(int qid) {
		setAbsoluteURI("http://www." + Constant.IYUBA_CN + "question/getQuestionDetail.jsp?format=json&authortype=2&questionid="
				+qid
				);
		Log.e("iyuba","http://www." + Constant.IYUBA_CN + "question/getQuestionDetail.jsp?format=json&authortype=2&questionid="
				+qid);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new GetCommentListResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
