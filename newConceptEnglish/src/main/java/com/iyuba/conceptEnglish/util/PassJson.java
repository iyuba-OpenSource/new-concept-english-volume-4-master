package com.iyuba.conceptEnglish.util;

import android.util.Log;

import com.iyuba.conceptEnglish.sqlite.mode.TestRecord;
import com.iyuba.conceptEnglish.sqlite.mode.WordPassUser;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.LogUtils;
import com.iyuba.core.lil.user.UserInfoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class PassJson {


    public static String buildJsonForTestRecord(List<TestRecord> tRecords) throws JSONException {

        String jsondata;
        JSONObject jsonRoot = new JSONObject();
        JSONArray json = new JSONArray();

        for (int i = 0; i < tRecords.size(); i++) {
            JSONObject jsonObj = new JSONObject();
            TestRecord tRecord = tRecords.get(i);
            jsonObj.put("uid", tRecord.uid);
            jsonObj.put("LessonId", tRecord.LessonId);
            jsonObj.put("TestNumber", tRecord.TestNumber);
            jsonObj.put("BeginTime", tRecord.BeginTime);
            jsonObj.put("TestTime", tRecord.TestTime);
            jsonObj.put("RightAnswer", tRecord.RightAnswer);
            jsonObj.put("UserAnswer", tRecord.UserAnswer);
            jsonObj.put("AnswerResut", tRecord.AnswerResult);
            jsonObj.put("index", tRecord.index);
            //把每个数据当作一对象添加到数组里
            json.put(jsonObj);

        }
        jsonRoot.put("datalist", json);
        jsondata = jsonRoot.toString();
        Log.e("JSON", jsondata);
        return jsondata;
        //调用解析JSON方法
        //parserJson(jsondata);
    }

    /**
     * 将答题记录和用户的成绩一起传递大数据
     * --
     * Warn:这里有一个问题,账户A的数据上传失败,同一设备登录账户B,上传数据的时候,账户A的数据可能上传B数据呢.
     * Solve:上传数据时比对账号是否相同
     */
    public static String buildJsonForTestRecordDouble(List<WordPassUser> allWordsData, int voaid, int bookid) throws JSONException {
        //TODO 可能需要回滚
        String jsondata;
        JSONObject jsonRoot = new JSONObject();
        JSONArray array = new JSONArray();

        String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
        int lesson = 0;
        lesson = parseLessonByVoaId(allWordsData.get(0).voa_id, bookid);//修改了lesson，注意引用的部分
        String strMd5 = uid + Constant.APPID + lesson + "iyubaExam" + getCurTime();
        String sign = MD5.getMD5ofStr(strMd5);

        //基本信息
        jsonRoot.put("uid", Integer.parseInt(uid));
        jsonRoot.put("appId", Constant.APPID); //应用ID
        jsonRoot.put("lesson", lesson); //课程类型
        jsonRoot.put("sign", sign);
        jsonRoot.put("format", "json"); //返回格式
        jsonRoot.put("DeviceId", android.os.Build.MODEL); //设备ID
        jsonRoot.put("mode", 2);


        //测试记录
        for (int i = 0; i < allWordsData.size(); i++) {
            JSONObject jsonObj = new JSONObject();
            WordPassUser wordError = allWordsData.get(i);
            //String LessonId = isChildWord?wordError.voa_id+"":wordError.voa_id + "" + wordError.position;
            String LessonId;//小学英语,初中英语,青少版传对应的unit_id; 新概念的unitid = voaid;传voaid即可
            if (lesson > 277 && lesson < 1000){
                //青少版
                LessonId = allWordsData.get(i).unitId + "";
            }else {
                //全四册
                LessonId = voaid + "";
            }
            jsonObj.put("TestTime", getCurTime()); //提交数据时间
            jsonObj.put("TestMode", "W");
            jsonObj.put("LessonId", LessonId); //课程id 1001
            jsonObj.put("Category", "单词闯关");
            jsonObj.put("AnswerResut", wordError.answer); //答案的正确与否  正确：1 错误：0
            //暂时无用
            jsonObj.put("BeginTime", getCurTime());
            jsonObj.put("TestId", wordError.position); //小学英语,初中英语,青少版,新概念英语统一传position
            jsonObj.put("RightAnswer", wordError.word);
            jsonObj.put("UserAnswer", wordError.word);
            //把每个数据当作一对象添加到数组里
            array.put(jsonObj);
        }
        jsonRoot.put("testList", array);
        //成绩分析记录
        jsonRoot.put("scoreList", new JSONArray());
        jsondata = jsonRoot.toString();
        Log.e("JSON", jsondata);
        return jsondata;
    }



    private static int parseLessonByVoaId(int voaId, int bookid) {
        //0：错误值
        int returnCode = 0;
        if (voaId / 1000 == 321) {
            //青少版单词
            return bookid;
        } else {
            //全四册单词
            int firstNum = voaId / 1000;
            switch (firstNum) {
                case 1:
                    returnCode = 1;
                    break;
                case 2:
                    returnCode = 2;
                    break;
                case 3:
                    returnCode = 3;
                    break;
                case 4:
                    returnCode = 4;
                    break;
                default:
                    break;
            }
        }
        return returnCode;
    }


    public static JSONObject getScoreDetail(int typeid, String s) {
        JSONObject itemObject = new JSONObject();
        //Score里存储的数据格式  题目总数++正确数量++Category(能力)
        String[] res = s.split("\\+\\+");
        try {
            itemObject.put("lessontype", getLessonType(typeid));
            itemObject.put("category", res[2]);
            itemObject.put("testCnt", res[1]);//每一个模块答对的个数
            int score = Integer.parseInt(res[0]) == 0 ? 0 : Integer.parseInt(res[1]) * 100 / Integer.parseInt(res[0]);
            itemObject.put("Score", score + "");//得分转化为百分制
            LogUtils.e(getLessonType(typeid) + "       " + res[2] + "     " + score);
//                json.put(itemObject);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemObject;
    }

    public static String buildJsonForTestRecordSingle(TestRecord tRecord) throws JSONException {
        String jsondata;
        JSONObject jsonRoot = new JSONObject();
        JSONArray json = new JSONArray();

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("uid", tRecord.uid);
        jsonObj.put("LessonId", tRecord.LessonId);
        jsonObj.put("TestNumber", tRecord.TestNumber);
        jsonObj.put("BeginTime", tRecord.BeginTime);
        jsonObj.put("TestTime", tRecord.TestTime);
        jsonObj.put("RightAnswer", tRecord.RightAnswer);
        jsonObj.put("UserAnswer", tRecord.UserAnswer);
        jsonObj.put("AnswerResut", tRecord.AnswerResult);
        //把每个数据当作一对象添加到数组里
        json.put(jsonObj);
        jsonRoot.put("datalist", json);
        jsondata = jsonRoot.toString();
        Log.e("JSON", jsondata);
        return jsondata;
        //调用解析JSON方法
        //parserJson(jsondata);
    }

    /**
     * 根据数据表里存放的编号,获取lessonType
     *
     * @param type 数据存储时的编号   0写作  1单词  语法....
     * @return
     */
    private static String getLessonType(int type) {
        String lessontype = "";
        switch (type) {

            case Constant.ABILITY_TETYPE_WRITE:
                lessontype = Constant.ABILITY_WRITE;
                break;

            case Constant.ABILITY_TETYPE_WORD:
                lessontype = Constant.ABILITY_WORD;
                break;

            case Constant.ABILITY_TETYPE_GRAMMER:
                lessontype = Constant.ABILITY_GRAMMER;
                break;

            case Constant.ABILITY_TETYPE_LISTEN:
                lessontype = Constant.ABILITY_LISTEN;
                break;

            case Constant.ABILITY_TETYPE_SPEAK:
                lessontype = Constant.ABILITY_SPEAK;
                break;

            case Constant.ABILITY_TETYPE_READ:
                lessontype = Constant.ABILITY_READ;
                break;
        }
        LogUtils.e("这里的lessonType    " + lessontype);
        return lessontype;
    }

    private static String getCurTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return df.format(System.currentTimeMillis());
    }

}
