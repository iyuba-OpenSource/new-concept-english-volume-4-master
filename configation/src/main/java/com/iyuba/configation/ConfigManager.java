package com.iyuba.configation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


import com.iyuba.configation.entity.enumconcept.BookType;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * @author gyx
 * <p>
 * 功能：配置文件管理
 */
public class ConfigManager {
    private volatile static ConfigManager instance;

    public static final String CONFIG_NAME = "config";

    private Context context;

    private SharedPreferences.Editor editor;

    private SharedPreferences preferences;

    public static ConfigManager Instance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private ConfigManager() {

        this.context = RuntimeManager.getContext();

        openEditor();
    }

    private ConfigManager(Context context) {

        this.context = context;

        openEditor();
    }

    // 创建或修改配置文件
    public void openEditor() {
        int mode = Activity.MODE_PRIVATE;
        preferences = context.getSharedPreferences(CONFIG_NAME, mode);
        editor = preferences.edit();
    }

    public void putBoolean(String name, boolean value) {
        if (preferences==null){
            openEditor();
        }

        editor.putBoolean(name, value);
        editor.commit();
    }

    public void putFloat(String name, float value) {
        if (preferences==null){
            openEditor();
        }

        editor.putFloat(name, value);
        editor.apply();
    }

    public void putInt(String name, int value) {
        if (preferences==null){
            openEditor();
        }

        editor.putInt(name, value);
        editor.apply();
    }

    public void putLong(String name, long value) {
        if (preferences==null){
            openEditor();
        }

        editor.putLong(name, value);
        editor.apply();
    }

    public void putString(String name, String value) {
        if (preferences==null){
            openEditor();
        }

        editor.putString(name, value);
        editor.apply();
    }

    public boolean loadAutoPlay(){
        return loadBoolean("ListenStudyReportDialog",true);
    }

    public void putAutoPlay(boolean flag){
        putBoolean("ListenStudyReportDialog",flag);
    }

    /**
     * 对象存储
     *
     * @param name
     * @param value
     * @throws IOException
     */
    public void putString(String name, Object value) throws IOException {
        // 把值对象以流的形式转化为字符串。
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(value);
        String productBase64 = new String(Base64.encodeBase64(baos
                .toByteArray()));
        putString(name, productBase64);
        oos.close();
    }

    public boolean loadBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public boolean loadBoolean(String key, boolean value) {
        return preferences.getBoolean(key, value);
    }

    public float loadFloat(String key) {
        return preferences.getFloat(key, 0);
    }

    public float loadFloat(String key, float value) {
        return preferences.getFloat(key, value);
    }

    /**
     * firstSendBookFlag：app的启动次数
     *
     * @param key
     * @return
     */
    public int loadInt(String key) {
        return preferences.getInt(key, 0);
    }

    public int loadInt(String key, int defValue) {
        return preferences.getInt(key, defValue);
    }

    public long loadLong(String key) {
        return preferences.getLong(key, 0);
    }

    public String loadString(String key) {
        return preferences.getString(key, "");
    }

    public String loadString(String key, String name) {
        return preferences.getString(key, name);
    }

    public void removeKey(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 读取对象格式
     *
     * @param key
     * @return
     * @throws IOException
     * @throws StreamCorruptedException
     * @throws ClassNotFoundException
     */
    public Object loadObject(String key) throws StreamCorruptedException,
            IOException, ClassNotFoundException {
        String objBase64String = loadString(key);
        byte[] b = Base64.decodeBase64(objBase64String.getBytes());
        InputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais); // something wrong
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

//    public String getUserId() {
//        return preferences.getString("userId", "0");
//    }
//
//    public void setUserId(String userId) {
//        putString("userId", userId);
//    }
//
//    public String getUserName() {
//        return preferences.getString("userName", "");
//    }
//
//    public void setUserName(String userName) {
//        putString("userName", userName);
//    }
//
//    public void setNickName(String userName) {
//        putString("nickName", userName);
//    }
//
//    public String getNickName() {
//        return preferences.getString("nickName", "");
//    }
//
//    public int getIsVip() {
//        return preferences.getInt("isvip", 0);
//    }
//
//    public void setIsVip(int isvip) {
//        putInt("isvip", isvip);
//    }
//
//    public String getMoney() {
//        return preferences.getString("money", "0");
//    }
//
//    public void setMoney(String money) {
//        putString("money", money);
//    }

    public boolean isShowTip() { //播放文章播放器速度
        return loadBoolean("showtip", false);
    }

    public void setShowTip(boolean showtip) {
        putBoolean("showtip", showtip);
    }

    public void setQQGroup(String qqGroup) {
        putString("qqGroup", qqGroup);
    }

    public String getQQGroup() {
        return loadString("qqGroup", "");
    }

    public String getQQKey() {
        return loadString("qqKey", "");
    }

    public void setQQKey(String qqKey) {
        putString("qqKey", qqKey);
    }

    /**
     * 是否是美音
     *
     * @return
     */
    public boolean isAmercan() { //播放文章播放器速度
        return loadBoolean("isAmerican", true);
    }

    /**
     * 是否是青少版
     *
     * @return
     */
    public boolean isYouth() {
        return loadBoolean("isYouth", false);
    }

    /**
     * 设置是否是青少版
     * @param isAmerican
     */
    public void setYouth(boolean isAmerican) {
        putBoolean("isYouth", isAmerican);
    }

    /**
     * 获取当前课本名称
     */
    public String getCurrentBookName() {
        return loadString("currentBookName");
    }

    /**
     * 设置当前课本名称
     */
    public void setCurrentBookName(String bookName) {
        putString("currentBookName", bookName);
    }

    public BookType getBookType(){
        if (isYouth()){
            return BookType.YOUTH;
        }
        if (isAmercan()){
            return BookType.AMERICA;
        }else {
            return BookType.ENGLISH;
        }

    }

    public void setAmerican(boolean isAmerican) {
        putBoolean("isAmerican", isAmerican);
    }

    public int getCurrPassFirst() { //播放文章播放器速度
        return loadInt("currPassFirst", 1);
    }

    public void setCurrPassFirst(int currPass) {
        putInt("currPassFirst", currPass);
    }


    public int getCurrPassSecond() { //播放文章播放器速度
        return loadInt("currPassSecond", 1);
    }

    public void setCurrPassSecond(int currPass) {
        putInt("currPassSecond", currPass);
    }

    public int getCurrPassThird() { //播放文章播放器速度
        return loadInt("currPassThird", 1);
    }

    public void setCurrPassThird(int currPass) {
        putInt("currPassThird", currPass);
    }

    public int getCurrPassFourth() {
        return loadInt("currPassFourth", 1);
    }

    public void setCurrPassFourth(int currPass) {
        putInt("currPassFourth", currPass);
    }

    public boolean getIsNewRegister() {
        return loadBoolean("newRegister", false);
    }

    public void setIsNewRegister(boolean newRegister) {
        putBoolean("newRegister", newRegister);
    }


    // key:currBookForPass
    // value：课本id
    public int getCurrBookforPass() {
        return loadInt("currBookForPass", 1);
    }

    // key:currBookForPass
    // value：课本id
    public String getCurrBookId() { //单词闯关 的关数
        int lesson = loadInt("currBookForPass", 1);
        return String.valueOf(lesson);
//        switch (lesson) {
//            case 5:
//                return "278";
//            case 6:
//                return "279";
//            case 7:
//                return "280";
//            case 8:
//                return "281";
//            case 9:
//                return "282";
//            case 10:
//                return "283";
//            case 11:
//                return "284";
//            case 12:
//                return "285";
//            case 13:
//                return "286";
//            case 14:
//                return "287";
//            case 15:
//                return "288";
//            case 16:
//                return "289";
//            default:
//                return String.valueOf(lesson);
//        }
    }

    public String getCurrBookTitle() { //单词闯关 的标题
        int lesson = loadInt("currBookForPass", 1);
        switch (lesson) {
            case 278:
                return "新概念青少版StarterA";
            case 279:
                return "新概念青少版StarterB";
            case 280:
                return "新概念青少版1A";
            case 281:
                return "新概念青少版1B";
            case 282:
                return "新概念青少版2A";
            case 283:
                return "新概念青少版2B";
            case 284:
                return "新概念青少版3A";
            case 285:
                return "新概念青少版3B";
            case 286:
                return "新概念青少版4A";
            case 287:
                return "新概念青少版4B";
            case 288:
                return "新概念青少版5A";
            case 289:
                return "新概念青少版5B";
            default:
                return "新概念青少版";
        }
    }


    //key：currBookForPass
    //value：课本id
    public void setCurrBookforPass(int currBookForPass) {
        putInt("currBookForPass", currBookForPass);
    }

    public boolean isShowDef() { //单词显示解释
        return loadBoolean("isShowDef", true);
    }

    public void setShowDef(boolean isShowDef) {
        putBoolean("isShowDef", isShowDef);
    }

    public int getWordSort() { //单词排序 0: 首字母排序  1：时间排序
        return loadInt("wordSort", 0);
    }

    public void setWordSort(int isShowDef) {
        putInt("wordSort", isShowDef);
    }

    public boolean isWordPassLoad() { //单词闯关数据是否导入本地数据库
        return loadBoolean("isWordPassLoad", false);
    }

    public void setWordPassLoad() {
        putBoolean("isWordPassLoad", true);
    }


    public String getUpdates() { //单词闯关数据是否导入本地数据库
        return loadString("updateUid", "");
    }

    public void setUpdates(String uids) {
        putString("updateUid", uids);
    }


    //定时停止播放
    public int getAlarmItem() {
        return loadInt("alarmItem", 0);
    }

    public void setAlarmItem(int item) {
        putInt("alarmItem", item);
    }

    //设置定时停止播放
    public long getDelayTime(){
        int showIndex = getAlarmItem();
        int minute = 0;
        switch (showIndex){
            case 0:
                minute = 0;
                break;
            case 1:
                minute = 10;
                break;
            case 2:
                minute = 20;
                break;
            case 3:
                minute = 30;
                break;
            case 4:
                minute = 45;
                break;
            case 5:
                minute = 60;
                break;
        }

        long delayTime = 0;
        if (minute>0){
            //换算成时间，并增加到当前时间
            long alarmTime = minute*60*1000L+System.currentTimeMillis();
            //转换成分钟样式
            delayTime = alarmTime/1000/60;
            delayTime = delayTime*60*1000;
        }
        return delayTime;
    }


    public int getfontSizeLevel() {
        return loadInt("fontSizeLevel", 0);
    }

    public void setfontSizeLevel(int fontSizeLevel) {
        putInt("fontSizeLevel", fontSizeLevel);
    }


    public int getWordUpDataVersion(String bookId) {
        return loadInt(bookId+"child_word_version", 0);
    }

    public void setWordUpDataVersion(String bookId,int version) {
        putInt(bookId+"child_word_version", version);
    }

    public boolean isShowFulfill() {
        return loadBoolean("showFulfill", true);
    }

    public void setIsShowFulfill(boolean flag) {
        putBoolean("showFulfill", flag);
    }

    public boolean getAccountIsShowFulfill(String uid) {
        return loadBoolean(uid + "showFulfill", true);
    }

    public void setAccountIsShowFulfill(int uid,boolean flag) {
        if (!"0".equals(uid)) {
            putBoolean(uid + "showFulfill", flag);
        }
    }

    public boolean isFirstLoginForFulFill() {
        return loadBoolean("firstLoginForFulfill", true);
    }

    public void setFulFillLoginStatus() {
        putBoolean("firstLoginForFulfill", false);
    }

    public void testResetLoginStatus() {
        putBoolean("firstLoginForFulfill", true);
    }

    /* 为了修复旧版本升级，标题未更新的问题设置的 */
    public boolean isForceLoading() {return loadBoolean("forceLoading", true);}

    public void setForceLoading() {putBoolean("forceLoading", false);}

    public void setSendListenReport(boolean isSend){
        putBoolean("sendListenReport", isSend);
    }

    public boolean getSendListenReport(){
        return loadBoolean("sendListenReport", true);
    }

    public void setsendEvaReport(boolean isSend){
        putBoolean("sendEvaReport", isSend);
    }

    public boolean getsendEvaReport(){
        return loadBoolean("sendEvaReport", true);
    }

    private final String banner_Img="banner_Img";
    private final String splash_Img="splash_Img";

    //http://app.iyuba.cn/dev/upload/1679381438179.jpg
    public String getBannerImg() {
//        return loadString(banner_Img,"http://app."+Constant.IYUBA_CN_IN+"/dev/upload/1666766657667.jpg");
        return "http://app."+Constant.IYUBA_CN_IN+"/dev/upload/1679381438179.jpg";
    }

    public void setBannerImg(String bannerImg) {
        putString(banner_Img,bannerImg);
    }

    //http://app.iyuba.cn/dev/upload/1679379374314.jpg
    public String getSplashImg() {
//        return loadString(splash_Img,"http://app."+Constant.IYUBA_CN_IN+"/dev/upload/1666766335869.jpg");
        return "http://app."+Constant.IYUBA_CN_IN+"/dev/upload/1679379374314.jpg";
    }

    public void setSplashImg(String splashImg) {
        putString(splash_Img,splashImg);
    }
}
