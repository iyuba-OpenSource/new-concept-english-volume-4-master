package com.iyuba.core.common.protocol.news;

import java.io.IOException;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;

public class WordSynRequest extends BaseXMLRequest {
    String user;

    public WordSynRequest(String userId, int pageCounts, int page) {
        this.user = userId;
        setAbsoluteURI("http://word." + Constant.IYUBA_CN + "words/wordListService.jsp?u="
                + userId + "&pageCounts=" + pageCounts + "&pageNumber=" + page);
    }

    @Override
    protected void fillBody(XmlSerializer serializer) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public BaseHttpResponse createResponse() {
        // TODO Auto-generated method stub
        return new WordSynResponse(user);
    }

}
