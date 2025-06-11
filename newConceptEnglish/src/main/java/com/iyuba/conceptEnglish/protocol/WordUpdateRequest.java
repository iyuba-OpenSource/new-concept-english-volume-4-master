package com.iyuba.conceptEnglish.protocol;

import java.io.IOException;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;

public class WordUpdateRequest extends BaseXMLRequest {
	
	String userId;
	public static final String MODE_INSERT="insert";
	public static final String MODE_DELETE="delete";
	String groupname="Iyuba";
	String word;
	public WordUpdateRequest(String userId,String update_mode,String word){
		this.userId=userId;
		this.word=word;
		setAbsoluteURI("http://word." + Constant.IYUBA_CN + "words/updateWord.jsp?userId="+this.userId+"&mod="+update_mode+"&groupName="+groupname+"&word="+this.word);
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {

	}

	@Override
	public BaseHttpResponse createResponse() {
		return new WordUpdateResponse();
	}

}
