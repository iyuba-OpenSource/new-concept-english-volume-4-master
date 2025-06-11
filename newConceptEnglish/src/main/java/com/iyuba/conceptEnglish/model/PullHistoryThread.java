package com.iyuba.conceptEnglish.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.protocol.UploadTestRecordRequest;
import com.iyuba.conceptEnglish.sqlite.db.WordChildDBManager;
import com.iyuba.conceptEnglish.sqlite.mode.DownPassDataBean;
import com.iyuba.conceptEnglish.sqlite.op.VoaOp;
import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
import com.iyuba.conceptEnglish.sqlite.op.WordPassOp;
import com.iyuba.conceptEnglish.sqlite.op.WordPassUserOp;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.NetWorkState;
import com.iyuba.core.event.VipChangeEvent;
import com.iyuba.core.lil.user.UserInfoManager;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class PullHistoryThread extends Thread {

    private Context mContext;
    private WordPassUserOp wordPassUserOp;
    private VoaWordOp mVoaWordOp;
    private CallBack mCallback;
    private String[] mLessonNameList;
    /**
     * key: lesson*100 + lessonId
     * value: voaId
     */
    private Map<Integer,Integer> voaMapCathe =new Hashtable<>();

    public PullHistoryThread(Context context, String[] lessonNameList, CallBack callBack) {
        mContext = context;
        wordPassUserOp = new WordPassUserOp(mContext);
        mVoaWordOp = new VoaWordOp(mContext);
        mCallback = callBack;
        mLessonNameList = lessonNameList;
    }

    private boolean getWrongStatus( DownPassDataBean bean,boolean flag){
        boolean status=false;
        String lessonId="";
        int total;
        if (flag){
            total=bean.getTotalRight();
            if (total>0){
                lessonId = bean.getDataRight().get(0).getLessonId();
//                int localWrong=wordPassUserOp.getRightOrWrongCount(false,lessonId);
//                status=total>localWrong;
            }

            //total==0的时候未必不往下执行，或者把条件反过来
            /*       38         0               0           0
            * if(net.right>=local.right  ||  net.wrong>=local.wrong){
            *
            *
            * }
            *
            * */
        }else {
            total=bean.getTotalWrong();
            if (total>0){
                lessonId = bean.getDataWrong().get(0).getLessonId();
//                int localWrong=wordPassUserOp.getRightOrWrongCount(false,lessonId);
//                status=total>localWrong;
            }
        }
        if (!lessonId.isEmpty()){
            int localCount=wordPassUserOp.getRightOrWrongCount(flag,lessonId);
            status=total>localCount;
        }
        return status;
    }

    @Override
    public void run() {
        if (NetWorkState.isConnectingToInternet()){
            List<DownPassDataBean> beans = new ArrayList<>();
            for (String lessonName : mLessonNameList) {
                String url = "http://daxue."+Constant.IYUBA_CN+"ecollege/getExamDetailNew.jsp?" +
                        "appId=222&lesson=" + lessonName + "&TestMode=W&mode=2&format=json";
                String uid = String.valueOf(UserInfoManager.getInstance().getUserId());
                url += "&uid=" + uid;
                String sign = com.iyuba.conceptEnglish.util.MD5.getMD5ofStr(uid + lessonName + "2W" + Constant.APPID + getCurTime());
                url += "&sign=" + sign;
                DownPassDataBean bean = new UploadTestRecordRequest().getData(url);
                //至少有一个不为0
                if (bean.getTotalRight()==0&&bean.getTotalWrong()==0){
                    if (bean.getResult()==1){
                        callOut(true);
                    }else {
                        callOut(false);
                    }
                    return;
                }
                beans.add(bean);
            }

            for (DownPassDataBean bean : beans) {
                if (!bean.getDataRight().isEmpty()){

                    String rightLessonId = bean.getDataRight().get(0).getLessonId();
                    boolean compRight= bean.getTotalRight()>wordPassUserOp.getRightOrWrongCount(true,rightLessonId);
                    //正确的
//                    String rightLesson = bean.getDataRight().get(0).getLessonId();
//                    int localRight=wordPassUserOp.getRightOrWrongCount(true,rightLesson);
                    if (bean.getDataRight() != null && bean.getDataRight().size() > 0 &&compRight) {
                        for (DownPassDataBean.DataWrongBean wrongBean : bean.getDataWrong()) {

                            String lessonId = wrongBean.getLessonId();
                            String lesson = wrongBean.getLesson();

                            savePass(lesson, lessonId);
                        }

                        for (int i = 0; i < bean.getDataRight().size(); i++) {
                            DownPassDataBean.DataRightBean rightBean = bean.getDataRight().get(i);

                            String lessonId = rightBean.getLessonId();
                            String lesson = rightBean.getLesson();
                            savePass(lesson, lessonId);

                            int voaId = createVoaId(Integer.parseInt(rightBean.getLesson()),Integer.parseInt(rightBean.getLessonId()));
                            int position = TextUtils.isEmpty(rightBean.getTestId()) ? 0 : Integer.parseInt(rightBean.getTestId());

                            //Timber.d("get VoaId and Position: %d, %d", voaId, position);
                            if (position != 0) {
                                wordPassUserOp.updateWord(voaId, rightBean.getUserAnswer(), position, rightBean.getScore(),rightBean.getLessonId(),"0");
                            }
                        }

                        // TODO: 2023/11/15 这里处理下相关的数据，获取最大的数据，并且放在相关的数据中
                        //仅用于全四册的数据

                        //这里处理下，判断当前保存下来的单词最大的voaId是多少
                        int maxVoaId = 0;
                        for (int i = 0; i < bean.getDataRight().size(); i++) {
                            DownPassDataBean.DataRightBean rightBean = bean.getDataRight().get(i);
                            int tempVoaId = 0;
                            if (!TextUtils.isEmpty(rightBean.getLessonId())){
                                tempVoaId = Integer.parseInt(rightBean.getLessonId());
                            }

                            if (tempVoaId>maxVoaId){
                                maxVoaId = tempVoaId;
                            }
                        }

                        //放在数据库中
                        int bookId = Integer.parseInt(mLessonNameList[0]);
                        new WordPassOp(mContext).updateVoaId(maxVoaId+1,bookId);
                    }

                    saveSPdata();
                }

                //错误的
                if (bean.getDataWrong().isEmpty()){
                    if (bean.getResult()==1){
                        callOut(true);
                    }else {
                        callOut(false);
                    }
                    return;
                }
                String wrongLesson = bean.getDataWrong().get(0).getLessonId();
                int localWrong=wordPassUserOp.getRightOrWrongCount(false,wrongLesson);
                if (bean.getDataWrong() != null && bean.getDataWrong().size() > 0  && bean.getDataWrong().size()>=localWrong) {
                    for (DownPassDataBean.DataWrongBean wrongBean : bean.getDataWrong()) {

                        int voaId = createVoaId(Integer.parseInt(wrongBean.getLesson()),Integer.parseInt(wrongBean.getLessonId()));

                        int position = TextUtils.isEmpty(wrongBean.getTestId()) ? 0 : Integer.parseInt(wrongBean.getTestId());
                        Timber.d("get VoaId and Position: %d, %d", voaId, position);
                        if (position != 0) {
                            wordPassUserOp.updateWord(voaId, wrongBean.getUserAnswer(), position, wrongBean.getScore(),wrongBean.getLessonId(),"0");
                        }
                    }
                }
            }
        }
        callOut(true);
    }

    private void callOut(boolean isSuccess){
        if (mCallback != null) {
            mCallback.callback(isSuccess);
        }
    }

    private int createVoaId(int lesson,int lessonId){
        int voaId = 1001;
        if (lessonId > 1000){
            //全四册 lessonid 就是 voaid
            voaId=lessonId;
        }else {
            //青少版逻辑
            if (!voaMapCathe.containsKey(lesson*100 + lessonId)){
                voaId=mVoaWordOp.getVoaidByBookIdAndUnit(lesson,lessonId);
                voaMapCathe.put(lesson*100 + lessonId,voaId);
            }else {
                voaId= voaMapCathe.get(lesson*100 + lessonId);
            }
        }
        return voaId;
    }

    private static String getCurTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(System.currentTimeMillis());
    }


    private int pass1 = 1001, pass2 = 2001, pass3 = 3001, pass4 = 4001;
    private HashMap<String, Integer> bookList = new HashMap<String, Integer>();

    /**
     * @param lesson   1,278   (全四册 和 青少版)
     * @param lessonId 1001,321001 (全四册 和 青少版)
     */
    private void savePass(String lesson, String lessonId) {
        int lessonInt;
        int lessonIdInt;
        try {
            lessonInt = Integer.parseInt(lesson);
            lessonIdInt = Integer.parseInt(lessonId);
        } catch (Exception e) {
            return;
        }

        if (lessonInt >= 278) {
            //青少版
            int unitId;
            if (lessonIdInt / 1000 == 321) {
                unitId = mVoaWordOp.getUnitIdByVoaid(lessonId);
            } else {
                unitId = lessonIdInt;
            }

            if (!bookList.containsKey(lesson)) {
                bookList.put(lesson, unitId);
            } else if (bookList.get(lesson) < unitId) {
                bookList.remove(lesson);
                bookList.put(lesson, unitId);
            }
        } else {
            if (lessonId.length() < 4) {
                return;
            }
            //全四册
            int newPass = Integer.parseInt(lessonId.substring(0, 4));

            switch (newPass / 1000) {
                case 1:
                    if (pass1 < newPass) {
                        Timber.d("Setpass1 : %d", newPass);
                        pass1 = newPass;
                    }
                    break;
                case 2:
                    if (pass2 < newPass) {
                        Timber.d("Setpass2 : %d", newPass);
                        pass2 = newPass;
                    }
                    break;
                case 3:
                    if (pass3 < newPass)
                        pass3 = newPass;
                    break;
                case 4:
                    if (pass4 < newPass)
                        pass4 = newPass;
                    break;
            }
        }

    }

    /**
     * 当我化身代码幽灵在屎山代码里穿梭时，发现了这个大粪包：
     * 哪个老低能儿闭门造车写的这烂代码？？？
     * val curr="wordPassOp.getCurrPassNum(localPass)最大的第144关"
     * pass1=1001再小也比${curr}大
     * */
    private int getCurrPassNum(int localPass){
        WordPassOp wordPassOp = new WordPassOp(mContext);
        int oldPass = wordPassOp.getCurrPassNum(localPass);
        if (oldPass<1000){
            oldPass=oldPass+1000;
        }
        return oldPass;
    }

    private void saveSPdata() {
        //全四册的闯关等级记录逻辑
        WordPassOp wordPassOp = new WordPassOp(mContext);

        if (pass1 > getCurrPassNum(1)) {
            wordPassOp.updateVoaId(pass1, 1);
        }
        if (pass2 > getCurrPassNum(2)) {
            wordPassOp.updateVoaId(pass2, 2);
        }

        if (pass3 > getCurrPassNum(3)) {
            wordPassOp.updateVoaId(pass3, 3);
        }

        if (pass4 > getCurrPassNum(4)) {
            wordPassOp.updateVoaId(pass4, 4);
        }

        // 获取键值对的迭代器 青少版的闯关等级记录逻辑
        for (Map.Entry<String, Integer> stringIntegerEntry : bookList.entrySet()) {
            String key = (String) ((Map.Entry) stringIntegerEntry).getKey();// 数的等级  278 280
            int value = (Integer) ((Map.Entry) stringIntegerEntry).getValue() + 1;//unitId 1-15 16-24
            int oldPass = wordPassOp.getCurrPassNum(Integer.parseInt(key));
            if (value > oldPass) {
                wordPassOp.updateVoaId(value, Integer.parseInt(key));//更新的关数
            }
        }

        EventBus.getDefault().post(new VipChangeEvent());
    }


    public interface CallBack {
        void callback(boolean isSuccess);
    }
}
