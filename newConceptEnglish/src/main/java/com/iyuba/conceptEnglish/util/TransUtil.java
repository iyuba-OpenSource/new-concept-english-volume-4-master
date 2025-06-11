package com.iyuba.conceptEnglish.util;

import android.text.TextUtils;

import com.iyuba.conceptEnglish.sqlite.mode.VoaDiffcultyExercise;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDiffcultyExercise_inter;

import java.util.ArrayList;
import java.util.List;

/**
 * @desction:
 * @date: 2023/3/16 14:04
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class TransUtil {

    //将VoaDiffcultyExercise这个类型的数据，由接口和预存中转换成数据库模式
    public static List<VoaDiffcultyExercise> transDiffExercise(List<VoaDiffcultyExercise_inter> list){
        List<VoaDiffcultyExercise> newList = new ArrayList<>();

        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                VoaDiffcultyExercise_inter temp = list.get(i);

                //转换数据
                VoaDiffcultyExercise vde = new VoaDiffcultyExercise();
                vde.id = TextUtils.isEmpty(temp.id)?0:Integer.parseInt(temp.id);
                vde.descEN = temp.descEN;
                vde.descCN = temp.descCN;
                vde.number = TextUtils.isEmpty(temp.number)?0:Integer.parseInt(temp.number);

                //这个参数有个bug，因为数据库中为int，但是实际上数据不为int，直接设置为0
                vde.column = 0;

                vde.note = temp.note;
                vde.type = TextUtils.isEmpty(temp.type)?0:Integer.parseInt(temp.type);
                vde.quesNum = TextUtils.isEmpty(temp.quesNum)?0:Integer.parseInt(temp.quesNum);
                vde.answer = temp.answer;

                newList.add(vde);
            }
        }

        return newList;
    }
}
