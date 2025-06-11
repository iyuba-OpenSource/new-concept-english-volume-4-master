package com.iyuba.conceptEnglish.protocol;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.TextAttr;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 发送发表评论请求
 *
 * @author ct
 *
 */

public class ExpressionRequest extends BaseJSONRequest {
	String voaid = "0";

	public ExpressionRequest(String userid, String voaid, String comments,
							 String userName) {
		this.voaid = voaid;
//		comments=makeComment(comments);
		comments = TextAttr.encode(TextAttr.encode(comments));
		String url = "http://daxue." + Constant.IYUBA_CN + "appApi/UnicomApi?" +
				"platform=android&format=xml&protocol=60002" +
				"&userid=" + userid + "&voaid=" + voaid
				+ "&content=" + comments
				+ "&shuoshuotype=0&appName=concept";
		setAbsoluteURI(url);
	}

	public ExpressionRequest(String userid, String voaid, String comments,
							 String userName, int to) {
		this.voaid = voaid;
		comments = TextAttr.encode(TextAttr.encode(comments));
		setAbsoluteURI("http://daxue." + Constant.IYUBA_CN + "appApi/UnicomApi?" +
				"platform=ios&format=xml&protocol=60002" +
				"&userId=" + userid + "&toId=" + to + "&voaid=" + voaid
				+ "&comment=" + comments
				+ "&shuoshuotype=0&appName=concept");
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {


	}

	@Override
	public BaseHttpResponse createResponse() {
		return new ExpressionResponse();
	}
}
