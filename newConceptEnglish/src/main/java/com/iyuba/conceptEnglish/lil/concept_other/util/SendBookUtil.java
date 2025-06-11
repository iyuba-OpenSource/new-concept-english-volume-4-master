package com.iyuba.conceptEnglish.lil.concept_other.util;

import android.os.Build;
import android.text.TextUtils;
import android.util.Pair;

/**
 * @title: 根据包名判断是否显示送书
 * @date: 2023/11/22 09:41
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class SendBookUtil {

    /*****************************手机品牌********************/
    //当前是否为小米手机
    private static boolean isXiaomiPhone(){
        String brand = Build.BRAND.toLowerCase();
        if (brand.equals("xiaomi")
                ||brand.equals("redmi")){
            return true;
        }
        return false;
    }

    //当前是否为华为手机
    private static boolean isHuaweiPhone(){
        String brand = Build.BRAND.toLowerCase();
        if (brand.equals("huawei")
                ||brand.equals("honor")){
            return true;
        }
        return false;
    }

    //当前是否为魅族手机
    private static boolean isMeizuPhone(){
        String brand = Build.BRAND.toLowerCase();
        if (brand.equals("meizu")){
            return true;
        }
        return false;
    }

    //当前是否为oppo手机
    private static boolean isOppoPhone(){
        String brand = Build.BRAND.toLowerCase();
        if (brand.equals("oppo")
                ||brand.equals("oneplus")
                ||brand.equals("realme")){
            return true;
        }
        return false;
    }

    //当前是否为vivo手机
    private static boolean isVivoPhone(){
        String brand = Build.BRAND.toLowerCase();
        if (brand.equals("vivo")
                ||brand.equals("iqoo")){
            return true;
        }
        return false;
    }

    //当前是否为三星手机
    private static boolean isSamsungPhone(){
        String brand = Build.BRAND.toLowerCase();
        if (brand.equals("samsung")){
            return true;
        }
        return false;
    }

    /****************************包名********************************/
    //com.iyuba.concept2
    private static final String concept2Name = "com.iyuba.concept2";

    //com.iyuba.englishfm
    private static final String fmName = "com.iyuba.englishfm";

    //com.iyuba.newconcepttop
    private static final String topName = "com.iyuba.newconcepttop";

    //com.iyuba.nce
    private static final String nceName = "com.iyuba.nce";

    //com.suzhou.concept
    private static final String suzhouName = "com.suzhou.concept";

    //com.iyuba.learnNewEnglish
    private static final String mocName = "com.iyuba.learnNewEnglish";

    //根据包名判断是否显示送书(同个大版本类型处理，不符合直觉)
    public static Pair<Boolean,String> showSendBook(String packageName){
        Pair<Boolean,String> showSendBookPair = new Pair<>(false,packageName);

        if (TextUtils.isEmpty(packageName)){
            return showSendBookPair;
        }

        switch (packageName){
            case concept2Name:
            case fmName:
                if (isVivoPhone()||isOppoPhone()||isMeizuPhone()){
                    showSendBookPair = new Pair<>(true,concept2Name);
                }

                if (isXiaomiPhone()||isSamsungPhone()){
                    showSendBookPair = new Pair<>(true,fmName);
                }
                break;
            case topName:
                if (isSamsungPhone()||isXiaomiPhone()||isOppoPhone()||isMeizuPhone()||isVivoPhone()){
                    showSendBookPair = new Pair<>(true,topName);
                }
                break;
            case nceName:
                if (isVivoPhone()){
                    showSendBookPair = new Pair<>(true,nceName);
                }
                break;
            case suzhouName:
                if (isXiaomiPhone()){
                    showSendBookPair = new Pair<>(true,suzhouName);
                }
                break;
            case mocName:
                if (isHuaweiPhone()){
                    showSendBookPair = new Pair<>(true,mocName);
                }
                break;
            default:
                showSendBookPair = new Pair<>(false,packageName);
                break;
        }
        return showSendBookPair;
    }

    //根据包名判断是否显示送书(当前包名上架的应用市场处理，符合直觉)
    public static Pair<Boolean,String> showSendBookNew(String packageName){
        Pair<Boolean,String> showSendBookPair = new Pair<>(false,packageName);

        if (TextUtils.isEmpty(packageName)){
            return showSendBookPair;
        }

        switch (packageName){
            case concept2Name:
                if (isVivoPhone()||isOppoPhone()||isMeizuPhone()){
                    showSendBookPair = new Pair<>(true,concept2Name);
                }
                break;
            case fmName:
                if (isXiaomiPhone()||isSamsungPhone()){
                    showSendBookPair = new Pair<>(true,fmName);
                }
                break;
            case topName:
                if (isSamsungPhone()||isXiaomiPhone()||isOppoPhone()||isMeizuPhone()||isVivoPhone()){
                    showSendBookPair = new Pair<>(true,topName);
                }
                break;
            case nceName:
                if (isVivoPhone()){
                    showSendBookPair = new Pair<>(true,nceName);
                }
                break;
            case suzhouName:
                if (isXiaomiPhone()){
                    showSendBookPair = new Pair<>(true,suzhouName);
                }
                break;
            case mocName:
                if (isHuaweiPhone()){
                    showSendBookPair = new Pair<>(true,mocName);
                }
                break;
            default:
                showSendBookPair = new Pair<>(false,packageName);
                break;
        }
        return showSendBookPair;
    }
}
