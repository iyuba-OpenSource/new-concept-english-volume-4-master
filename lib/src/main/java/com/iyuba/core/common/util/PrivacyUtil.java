package com.iyuba.core.common.util;

import com.iyuba.configation.Constant;
import com.iyuba.core.lil.util.ResLibUtil;

import timber.log.Timber;

import static com.iyuba.configation.Constant.AIYUBA;
import static com.iyuba.configation.Constant.AIYUYAN;
import static com.iyuba.configation.Constant.HUASHENG;
import static com.iyuba.configation.Constant.JNZMLM;
import static com.iyuba.configation.Constant.JNZULM_TYPE;
import static com.iyuba.configation.Constant.SHANDONG;

import android.util.Log;

public class PrivacyUtil {

    /**
     * 使用说明 和 隐私协议
     *
     * @return
     */
    public static String getPrivacyUrl() {
        //公司类型
        String companyName = Constant.getCompanyName(ResLibUtil.getInstance().getContext());

        int index = 0;
        switch (companyName) {
            case AIYUBA:
                index = 1;
                break;
            case AIYUYAN:
                index = 3;
                break;
            case HUASHENG:
                index = 2;
                break;
            case Constant.SHANDONG:
                index = 4;
                break;
            case JNZMLM:
                return "http://www.qomolama.com.cn/protocoluse.jsp?company=5&apptype="+JNZULM_TYPE;
        }
        return "http://"+Constant.userSpeech+"/api/protocoluse.jsp?apptype=" + Constant.APPName + "&company="+ index;
    }

    /**
     * 隐私协议
     *
     * @return
     */
    public static String getSeparatedSecretUrl() {
        //公司类型
        String companyName = Constant.getCompanyName(ResLibUtil.getInstance().getContext());

        String url;
        if (AIYUBA.equals(companyName)) {
            url = "http://"+Constant.userSpeech+"api/protocolpri.jsp?apptype=" + Constant.APPName + "&company=1";
        } else if (AIYUYAN.equals(companyName)) {
            url = "http://"+Constant.userSpeech+"api/protocolpri.jsp?apptype=" + Constant.APPName + "&company=3";
        } else if (HUASHENG.equals(companyName)) {
            url = "http://"+Constant.userSpeech+"api/protocolpri.jsp?apptype=" + Constant.APPName + "&company=2";
        } else if (SHANDONG.equals(companyName)){
            url = "http://"+Constant.userSpeech+"api/protocolpri.jsp?apptype=" + Constant.APPName + "&company=4";
        }else if (JNZMLM.equals(companyName)){
            url = "http://www.qomolama.com.cn/protocolpri.jsp?company=5&apptype="+JNZULM_TYPE;
        } else {
            url = "http://"+Constant.userSpeech+"api/protocolpri.jsp?apptype=" + Constant.APPName + "&company=4";
        }
        Timber.tag("隐私协议________").d(url);
//        return "https://www.ibbc.net.cn/protocolpri.jsp?company=1&apptype=%E6%96%B0%E6%A6%82%E5%BF%B5%E8%8B%B1%E8%AF%AD%E5%85%A8%E5%9B%9B%E5%86%8C";
//        return "http://www.bbe.net.cn/protocolpri.jsp?apptype=%E6%96%B0%E6%A6%82%E5%BF%B5%E8%8B%B1%E8%AF%AD%E5%85%A8%E5%9B%9B%E5%86%8C&company=%E5%B1%B1%E4%B8%9C%E7%88%B1%E8%AF%AD%E5%90%A7%E4%BF%A1%E6%81%AF%E7%A7%91%E6%8A%80%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8";
        return url;
    }

    /**
     * 使用说明
     * 万云天使用协议  https://www.ibbc.net.cn/protocoluse.jsp?company=1&apptype=%E6%96%B0%E6%A6%82%E5%BF%B5%E8%8B%B1%E8%AF%AD%E5%85%A8%E5%9B%9B%E5%86%8C
     * 万云天隐私政策  https://www.ibbc.net.cn/protocolpri.jsp?company=1&apptype=%E6%96%B0%E6%A6%82%E5%BF%B5%E8%8B%B1%E8%AF%AD%E5%85%A8%E5%9B%9B%E5%86%8C
     *
     *
     * 隐私：http://www.bbe.net.cn/protocolpri.jsp?apptype=%E6%96%B0%E6%A6%82%E5%BF%B5%E8%8B%B1%E8%AF%AD%E5%85%A8%E5%9B%9B%E5%86%8C&company=%E5%B1%B1%E4%B8%9C%E7%88%B1%E8%AF%AD%E5%90%A7%E4%BF%A1%E6%81%AF%E7%A7%91%E6%8A%80%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8
     *
     * 使用条款：http://www.bbe.net.cn/protocoluse.jsp?apptype=%E6%96%B0%E6%A6%82%E5%BF%B5%E8%8B%B1%E8%AF%AD%E5%85%A8%E5%9B%9B%E5%86%8C&company=%E5%B1%B1%E4%B8%9C%E7%88%B1%E8%AF%AD%E5%90%A7%E4%BF%A1%E6%81%AF%E7%A7%91%E6%8A%80%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8
     * @return
     */
    public static String getSeparatedProtocolUrl() {
//        return "https://www.ibbc.net.cn/protocoluse.jsp?company=1&apptype=%E6%96%B0%E6%A6%82%E5%BF%B5%E8%8B%B1%E8%AF%AD%E5%85%A8%E5%9B%9B%E5%86%8C";
//        return "http://www.bbe.net.cn/protocoluse.jsp?apptype=%E6%96%B0%E6%A6%82%E5%BF%B5%E8%8B%B1%E8%AF%AD%E5%85%A8%E5%9B%9B%E5%86%8C&company=%E5%B1%B1%E4%B8%9C%E7%88%B1%E8%AF%AD%E5%90%A7%E4%BF%A1%E6%81%AF%E7%A7%91%E6%8A%80%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8";
        return getPrivacyUrl();
    }


    /**
     * 会员服务协议
     */
    public static String getVipAgreementUrl(){
        //http://iuserspeech.iyuba.cn:9001/api/vipServiceProtocol.jsp?company=%E5%8C%97%E4%BA%AC%E7%88%B1%E8%AF%AD%E5%90%A7&type=app
        String companyName = Constant.getCompanyName(ResLibUtil.getInstance().getContext());
        switch (companyName){
            case AIYUBA:
                companyName = "北京爱语吧";
                break;
            case AIYUYAN:
                companyName = "爱语言(北京)";
                break;
            case HUASHENG:
                companyName = "上海画笙";
                break;
            case Constant.SHANDONG:
                companyName = "山东爱语吧";
                break;
            case JNZMLM:
                companyName = "济南珠穆朗玛";
                break;
        }

        return "http://iuserspeech."+Constant.IYUBA_CN_IN+":9001/api/vipServiceProtocol.jsp?company="+companyName+"&type=app";
    }

}
