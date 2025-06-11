package com.iyuba.conceptEnglish.protocol;

import java.io.IOException;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;

public class FavorUpdateRequest extends BaseXMLRequest {
		
	private StringBuilder uri = new StringBuilder("http://daxue." + Constant.IYUBA_CN + "appApi/updateCollect.jsp?");

	public FavorUpdateRequest(String userid, int voaid, String type) {
		uri.append("userId=" + userid);
		uri.append("&voaId=" + voaid);
		uri.append("&sentenceId=0");
		uri.append("&type=" + type);
		uri.append("&groupName=Iyuba");
		uri.append("&sentenceFlg=0");
		uri.append("&appName=concept");
		
		setAbsoluteURI(uri.toString());
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {

	}

	@Override
	public BaseHttpResponse createResponse() {
		return new FavorUpdateResponse();
	}

}
