package com.iyuba.conceptEnglish.protocol;

import java.io.IOException;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;

public class FavorSynRequest extends BaseXMLRequest {

	private StringBuilder uri = new StringBuilder("http://daxue." + Constant.IYUBA_CN + "appApi/getCollect.jsp?");
	
	public FavorSynRequest(String userid) {
		uri.append("userId=" + userid);
		uri.append("&groupName=Iyuba");
		uri.append("&type=voa");
		uri.append("&sentenceFlg=0");
		uri.append("&appName=concept");
		
		setAbsoluteURI(uri.toString());
	}
	
	public FavorSynRequest(String userid, int pageNum) {
		uri.append("userId=" + userid);
		uri.append("&groupName=Iyuba");
		uri.append("&type=voa");
		uri.append("&sentenceFlg=0");
		uri.append("&appName=concept");
		uri.append("&pageNum=" + pageNum);
		uri.append("&pageCounts=10");
		
		setAbsoluteURI(uri.toString());
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {

	}

	@Override
	public BaseHttpResponse createResponse() {
		return new FavorSynResponse();
	}

}
