package com.iyuba.configation;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.util.ResourceBundle;

public class Constant {

    public volatile static String IYUBA_CN_IN = "iyuba.cn";
    public volatile static String IYUBA_CN = IYUBA_CN_IN+"/";
    public volatile static String IYUBA_COM_IN = "iyuba.com.cn";
    public volatile static String IYUBA_COM = IYUBA_COM_IN+"/";
    public static final String staticStr="staticvip.";
    public static String userSpeech="iuserspeech."+IYUBA_CN_IN+":9001/";
    public static String IYUBA="iyuba";
    public static String evalUrl="http://"+Constant.userSpeech+"test/concept/";

    //所有的公司类型(作为参考)
    public static final String AIYUBA = "北京爱语吧";
    public static final String AIYUYAN = "爱语言";
    public static final String HUASHENG = "上海画笙";
    public static final String SHANDONG = "山东爱语吧信息";
    public static final String JNZMLM = "济南珠穆朗玛";
    //设置不同包名的公司类型
    public static String getCompanyName(Context context){
        String packageName = context.getPackageName();
        switch (packageName){
            case Constant.package_concept2://全四册
            case Constant.package_englishfm://全四册
                return AIYUBA;
            case Constant.package_nce://新概念英语
            case Constant.package_newconcepttop://新概念
            case Constant.package_conceptStory://人工智能学外语
                return AIYUYAN;
            case Constant.package_learnNewEnglish://新概念微课
                return SHANDONG;
            default:
                return AIYUBA;
        }
    }

    //济南珠穆朗玛固定使用的类型名称（切换包名需要修改）
    public static final String JNZULM_TYPE = "新概念英语全四册";


    //群聊
    public static int GROUP_ID = 10113;//10103 1群 10113 2群
    public static String GROUP_NAME = "新概念英语官方群";

    //评测类型
    public static String EVAL_TYPE = "concept";



    //有道广告id
    /*public static String YOUDAO_STREAM_ID = "5542d99e63893312d28d7e49e2b43559";
    public static String YOUDAO_WELCOME_ID = "9755487e03c2ff683be4e2d3218a2f2b";
    public static String YOUDAO_BANNER_ID = "230d59b7c0a808d01b7041c2d127da95";*/

    //TODO:第一个和第三个的接口要同步
    public static String EVAL_PREFIX = "http://"+Constant.userSpeech+ "voa/"; //评测返回，排行榜，评论,口语圈等语音前缀
//    public static String EVALUATE_URL = "https://"+Constant.userSpeech + Constant.IYUBA_CN + "test/eval/"; //语音评测
    public static String EVALUATE_URL_NEW = "http://"+Constant.userSpeech+"test/concept/"; //语音评测

    public static String APP_ICON = "http://app." + Constant.IYUBA_CN + "android/images/newconcept/newconcept.png";

    public static String APPID = "222";// 爱语吧id
    public static int APP_ID = 222;// 爱语吧id

    //PDF相关
    public static String PDF_PREFIX = "http://apps." + IYUBA_CN + IYUBA;


    //	public static String envir = ConfigManager.Instance().loadString("envir",
//			RuntimeManager.getContext().getExternalFilesDir(null).toString() + "/");// 文件夹路径
    public final static String envir = Environment.getExternalStorageDirectory() + "/iyuba/concept2/";//文件夹路径
    public static String APPName = "英语口语秀青少版";// 应用名称
    public static String AppName = "concept";// 爱语吧承认的英文缩写

    //public static String APPID = "224";
    public static String appfile = "newconcept";// 更新时的前缀名
    public static String append = ".mp3";// 文件append
    public static String videoAddr = envir + "/audio/";// 音频文件存储位置
    public static String picSrcAddr = envir + "/pic/";// 音频文件存储位置
    private static String simRecordAddr = envir + "/audio/sound";
    private static String recordTag = ".amr";// 录音（跟读所用）的位置
//	public static String picAddr = RuntimeManager.getContext()
//			.getExternalCacheDir().getAbsolutePath();// imagedownloader默认缓存图片位置

    public static String picAddr = Environment.getExternalStorageDirectory() + "/iyuba/concept2/";
    public static String iconAddr = envir + "icon.png";
    public static String iconWxMiniProgramAddr = envir + "iconWxMiniProgram.png";
    public static String userAddr = envir + "/user.jpg";// 用户头像，已废弃
    public static String recordAddr = envir + "/sound.amr";// 跟读音频
    public static String voiceCommentAddr = envir + "/voicecomment.amr";// 语音评论
    public static String screenShotAddr = envir + "/screenshot.jpg";// 截图位置
    public static int price = 900;// 应用内终身价格

    /**
     * 上传能力测评使用的url
     */
    public static  String URL_UPDATE_EXAM_RECORD = "http://daxue." + Constant.IYUBA_CN + "ecollege/updateExamRecordNew.jsp";

    //以下为智能能力测试所用常量
    public static String[] ABILITY_TYPE_ARR = {"写作", "单词", "语法", "听力", "口语", "阅读"};
    /**
     * 写作能力测试代码
     */
    public static final int ABILITY_TETYPE_WRITE = 0;
    public static final String ABILITY_WRITE = "X";
    public static final String[] WRITE_ABILITY_ARR = {"写作表达", "写作结构", "写作逻辑", "写作素材"};

    /**
     * 单词测试代码
     */
    public static final int ABILITY_TETYPE_WORD = 1;
    public static final String ABILITY_WORD = "W";
    public static final String[] WORD_ABILITY_ARR = {"中英力", "英中力", "发音力", "音义力", "拼写力", "应用力"};


    /**
     * 语法能力测试代码
     */
    public static final int ABILITY_TETYPE_GRAMMER = 2;
    public static final String ABILITY_GRAMMER = "G";
    public static final String[] GRAM_ABILITY_ARR = {"名词", "代词", "形容词副词", "动词", "时态", "句子"};

    /**
     * 听力能力测试代码
     */
    public static final int ABILITY_TETYPE_LISTEN = 3;
    public static final String ABILITY_LISTEN = "L";
    public static final String[] LIS_ABILITY_ARR = {"准确辨音", "听能逻辑", "音义匹配", "听写"};

    /**
     * 口语能力测试代码
     */
    public static final int ABILITY_TETYPE_SPEAK = 4;
    public static final String ABILITY_SPEAK = "S";
    public static final String[] SPEAK_ABILITY_ARR = {"发音", "表达", "素材", "逻辑"};

    /**
     * 阅读能力测试代码
     */
    public static final int ABILITY_TETYPE_READ = 5;
    public static final String ABILITY_READ = "R";
    public static final String[] READ_ABILITY_ARR = {"词汇认知", "句法理解", "语义和逻辑", "语篇"};


    /**
     * 单选能力测试
     */
    public static final int ABILITY_TESTTYPE_SINGLE = 1;
    /**
     * 填空题
     */
    public static final int ABILITY_TESTTYPE_BLANK = 2;
    /**
     * 选择填空
     */
    public static final int ABILITY_TESTTYPE_BLANK_CHOSE = 3;
    /**
     * 图片选择
     */
    public static final int ABILITY_TESTTYPE_CHOSE_PIC = 4;

    /**
     * 语音评测
     */
    public static final int ABILITY_TESTTYPE_VOICE = 5;
    /**
     * 多选
     */
    public static final int ABILITY_TESTTYPE_MULTY = 6;
    /**
     * 判断题目
     */
    public static final int ABILITY_TESTTYPE_JUDGE = 7;
    /**
     * 单词拼写
     */
    public static final int ABILITY_TESTTYPE_BLANK_WORD = 8;

    /**
     * 新概念能力测试 听力url前缀  http://static2." + Constant.IYUBA_CN + "IELTS/sounds/16819.mp3
     */
    public static  String ABILITY_AUDIO_URL_PRE = "http://static2." + Constant.IYUBA_CN + "NewConcept1/sounds/";
    /**
     * 新概念能力测试 附件url前缀 http://static2." + Constant.IYUBA_CN + "IELTS/attach/9081.txt
     */
    public static  String ABILITY_ATTACH_URL_PRE = "http://static2." + Constant.IYUBA_CN + "NewConcept1/attach/";
    /**
     * 新概念能力测试 图片url前缀  http://static2." + Constant.IYUBA_CN + "IELTS/images/
     */
    public static  String ABILITY_IMAGE_URL_PRE = "http://static2." + Constant.IYUBA_CN + "NewConcept1/images/";

    /*****************************************获取包名**********************************/
    private static String packageName="";
    public static void inflatePackageName(String name){
        packageName=name;
    }

    /*****************************************mob相关内容******************************/
    public static String getMobKey(){
        String key="";
        switch (packageName){
            case package_englishfm:
                key="38dc92932989f";
                break;
            case package_concept2:
                key="387636ce5cf38";
                break;
            case package_newconcepttop:
                key="38dcb24a38dfe";
                break;
            case package_nce:
                key="32af398f38ffc";
                break;
            case package_youth:
                key="3425dfbeddbe0";
                break;
            case package_conceptStory:
                key="351c56226df5e";
                break;
            case package_learnNewEnglish:
//                key="351c56226df5e";
                key = "3894dee8ae2ce";
                break;
        }
        return key;
    }

    public static String getMobSecret(){
        String secret="";
        switch (packageName){
            case package_englishfm:
                secret="2fc3c917bf3e4c432cd6e01f9aaa924f";
                break;
            case package_concept2:
                secret="a51c6d507c607fb99787b44153789a76";
                break;
            case package_newconcepttop:
                secret="18e2f874d14bc29826a7c186e8e2c7b5";
                break;
            case package_nce:
                secret="e87c7b1c9b143940ca932798960da4c4";
                break;
            case package_youth:
                secret="a0c6c6115787b8f0f047a1af595e1789";
                break;
            case package_conceptStory:
                secret="53b70716ec4e24bc43daffd46354b73a";
                break;
            case package_learnNewEnglish:
//                secret="53b70716ec4e24bc43daffd46354b73a";
                secret = "c6bf427d0a213c3eb802984896b63a73";
                break;
        }
        return secret;
    }

    /*******************************************微信的key******************************/
    //不同包名下设置不同的微信key--用于微信小程序分享和微信支付
    public static String getWxKey(){
        String key="";
        switch (packageName){
            case package_englishfm:
                key="wxb6e1fe24ad997516";
                break;
            case package_concept2:
                key="wx6ce5ac6bcb03a302";
                break;
            case package_newconcepttop:
                key="wx042f53ade6e16462";
                break;
            case package_nce:
                key="wx8961aa40cad433b6";
                break;
            case package_youth:
                key="";
                break;
            case package_conceptStory:
                key="wx2abcc13b8f9e61cf";
                break;
            case package_learnNewEnglish:
                key="wx3a50214d8b7b29fb";
                break;
        }
        return key;
    }

    /**
     * fm包名的
     * */
    public final static String  package_englishfm="com.iyuba.englishfm";
    /**
     * concept2包名的-大版本
     * */
    public final static String  package_concept2="com.iyuba.concept2";

    /**
     *  newconcepttop包名的-另一个大版本
     * */
    public final static String  package_newconcepttop="com.iyuba.newconcepttop";

    /**
     * nce包名的-目前只是vivo
     * */
    public final static String  package_nce="com.iyuba.nce";
    /**
     * youth包名的-没用过
     * */
    public final static String package_youth="com.iyuba.youth";
    /**
     * conceptStory包名的-人工智能-华为上的
     * */
    public final static String package_conceptStory="com.iyuba.conceptStory";
    /**
     * learnNewEnglish包名的-英语微课-华为上的
     * */
    public final static String package_learnNewEnglish="com.iyuba.learnNewEnglish";


    public static int recordId;// 学习记录篇目id，用于主程序
    public static String recordStart;// 学习记录开始时间，用于主程序

    public static int normalColor = 0xff414141;
    public final static int readColor = 0xff2983c1;
    public final static int unreadCnColor = 0xff8A8A8A;
    public final static int selectColor = 0xffde5e5b;
    public final static int unselectColor = 0xff444444;
    public final static int optionItemSelect = 0x7fbdfaf1;
    public final static int optionItemUnselect = 0xff8ab6da;

    public static int textColor = 0xff2983c1;
    public static int textSize = 16;

    public static int mode;// 播放模式
    public static int type;// 听歌播放模式
    public static int download;// 是否下载

    public static String appUpdateUrl = "http://api." + Constant.IYUBA_CN + "mobile/android/newconcept/islatest.plain?currver=%1$s&package=%2$s";// 升级地址
    public static String feedBackUrl = "http://api." + Constant.IYUBA_CN + "mobile/android/newconcept/feedback.xml?uid=";// 反馈
    public  static String sound = "http://static2." + Constant.IYUBA_CN + "newconcept/";
    public  static String sound_vip = "http://staticvip2." + Constant.IYUBA_CN + "newconcept/";
    public  static String wordUrl = "http://word." + Constant.IYUBA_CN + "words/apiWord.jsp?q=";

    public static String addCreditsUrl = "http://api." + Constant.IYUBA_CN + "credits/updateScore.jsp?";


    //爱语微课所用

    //移动课堂所需的相关API
    public static String MOB_CLASS_DOWNLOAD_PATH = "http://static3." + Constant.IYUBA_CN + "resource/";
    public  static String MOB_CLASS_PAYEDRECORD_PATH = "http://app." + Constant.IYUBA_CN + "pay/apiGetPayRecord.jsp?";
    public  static String MOB_CLASS_PACK_IMAGE = "http://static3." + Constant.IYUBA_CN + "resource/packIcon/";
    public  static String MOB_CLASS_PACK_TYPE_IMAGE = "http://static3." + Constant.IYUBA_CN + "resource/nmicon/";

    public  static String MOB_CLASS_COURSE_IMAGE = "http://static3." + Constant.IYUBA_CN + "resource/";

    public final static String reqPackDesc = "class.jichu";
    public final static int IO_BUFFER_SIZE = 100 * 1024;

    public  static String PIC_BASE_URL = "http://app." + Constant.IYUBA_CN + "dev/";

    public  static String MOB_CLASS_COURSE_RESOURCE_DIR = "http://static3." + Constant.IYUBA_CN + "resource/package";
    public  static String MOB_CLASS_COURSE_RESOURCE_APPEND = ".zip";

    public  static String MOB_CLASS_PACK_BGPIC = "http://static3." + Constant.IYUBA_CN + "resource/categoryIcon/";

    public final static String JLPT1_APPID = "205";//日语一级id
    public final static String JLPT2_APPID = "206";//日语二级id
    public final static String JLPT3_APPID = "203";//日语三级id
    public final static String CET4_APPID = "207";//日语三级id
    public final static String CET6_APPID = "208";//日语三级id

    //日志音频地址 ，非VIP
    public static String AUDIO_ADD = "http://"+Constant.staticStr + Constant.IYUBA_CN + "sounds";
    //日志音频地址 ，VIP
    public  static String AUDIO_VIP_ADD = "http://staticvip." + Constant.IYUBA_CN + "sounds";

    //日志视频地址 ，VIP
    public  static String VIDEO_VIP_ADD = "http://staticvip." + Constant.IYUBA_CN + "video";
    //日志视频地址 ，非VIP
    public  static String VIDEO_ADD = "http://staticvip." + Constant.IYUBA_CN + "video";
    public  static String IMAGE_DOWN_PATH = "http://api."+Constant.IYUBA_COM+"v2/api.iyuba?protocol=10005&size=big&uid=";
    public  static String PIC_ABLUM__ADD = "http://static1." + Constant.IYUBA_CN + "data/attachment/album/";

    public final static String MicroClassReqPackId = "21";

    public static final int testtype = 4;
    public static  String urlPerfix = "http://cetsounds." + Constant.IYUBA_CN + "" + testtype + "/";
    public static  String vipurlPerfix = "http://cetsoundsvip." + Constant.IYUBA_CN + "" + testtype + "/";

    public static String userimage = "http://api."+Constant.IYUBA_COM+"v2/api.iyuba?protocol=10005&uid=";//用户头像获取地址

    // 听歌中用

    public  static String detailUrl = "http://apps." + Constant.IYUBA_CN + "afterclass/getText.jsp?SongId=";//原文地址
    public  static String lrcUrl = "http://apps." + Constant.IYUBA_CN + "afterclass/getLyrics.jsp?SongId=";//原文地址，听歌专用
    public  static String searchUrl = "http://apps." + Constant.IYUBA_CN + "afterclass/searchApi.jsp?key=";//查询
    public  static String titleUrl = "http://apps." + Constant.IYUBA_CN + "afterclass/getSongList.jsp?maxId=";//新闻列表，主程序用
    public  static String vipurl = "http://staticvip." + Constant.IYUBA_CN + "sounds/song/";//vip地址
    public  static String songurl = "http://"+Constant.staticStr + Constant.IYUBA_CN + "sounds/song/";//普通地址
    public  static String soundurl = "http://static2." + Constant.IYUBA_CN + "go/musichigh/";//1000之前解析地址

    //以上为爱语微课相关
    public static void reLoadData() {
//		envir = ConfigManager.Instance().loadString("envir");// 文件夹路径
        videoAddr = envir + "/audio/";// 音频文件存储位置
        recordAddr = envir + "/sound.amr";// 跟读音频
        voiceCommentAddr = envir + "/voicecomment.amr";// 语音评论
        screenShotAddr = envir + "/screenshot.jpg";// 截图位置
    }


    public static String getsimRecordAddr(Context context) {
        //这里区分获取数据
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            return context.getExternalFilesDir(null).getPath()+"/audio/sound";
        }

        return simRecordAddr;
    }

    public static String getrecordTag() {
        return recordTag;
    }

    public static String NEW_DUBBING_PREFIX = "http://"+Constant.userSpeech+"";

    public static String getNewDubbingUrl(String id) {
        return NEW_DUBBING_PREFIX + id;
    }

    public static int category=0;

    public interface Voa {
        int DEFAULT_UID = 0;
        String VIP_VIDEO_PREFIX = "http://staticvip."+Constant.IYUBA_CN+"video/voa/";
        String VIDEO_PREFIX = "http://"+Constant.staticStr+Constant.IYUBA_CN+"video/voa/";
        String VIDEO_PREFIX_NEW = "http://"+Constant.userSpeech+"video/voa/";
        String VIP_SOUND_PREFIX = "http://staticvip."+Constant.IYUBA_CN+"sounds/voa/";
        String SOUND_PREFIX = "http://"+Constant.staticStr+Constant.IYUBA_CN+"sounds/voa/";
        String MP4_SUFFIX = ".mp4";
        String MP3_SUFFIX = ".mp3";
        String WAV_SUFFIX = ".wav";
        String AAC_SUFFIX = ".aac";
        String AMR_SUFFIX = ".amr";
        String JPG_SUFFIX = ".jpg";

        String SEPARATOR = "/";
        String TMP_PREFIX = "tmp";
        String SILENT_AAC_NAME = "silent.aac";
        String MERGE_AAC_NAME = "merge.aac";
        int SILENT_PIECE_TIME = 100;
        String COMMENT_VOICE_NAME = "comment_voice";
        String COMMENT_VOICE_SUFFIX = ".amr";
        int MAX_DIFFICULTY = 5;
        String FEEDBACK_END = "\n来自口语秀";
        String MERGE_MP3_NAME = "merge.mp3";
    }
}