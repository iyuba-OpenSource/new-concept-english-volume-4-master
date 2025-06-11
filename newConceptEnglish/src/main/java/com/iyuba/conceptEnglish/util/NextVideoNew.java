package com.iyuba.conceptEnglish.util;

import com.iyuba.conceptEnglish.manager.VoaDataManager;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 下一曲
 *
 * @author wxx 2019.06.17
 */
public class NextVideoNew {

    private ArrayList<Integer> LOCALID = new ArrayList<Integer>();
    private int position;
    private List<Voa> voaList = new ArrayList<Voa>();


    public NextVideoNew(int ID) {
        voaList = VoaDataManager.Instace().voasTemp;
        if (voaList != null) {
            for (int i = 0; i < voaList.size(); i++) {
                LOCALID.add(voaList.get(i).voaId);
            }
        }
        position = LOCALID.indexOf(ID);
    }

    public int following() {
        if (position + 1 < voaList.size() && LOCALID != null && position + 1 < LOCALID.size()) {
            return LOCALID.get(position + 1);
        } else {
            if (LOCALID != null && LOCALID.size() > 0)
                return LOCALID.get(0);
            else
                return 0;
        }
    }

    public int nextVideo() {
//        Random rnd = new Random();
//        int nextAEID = rnd.nextInt(LOCALID.size() * 10) / 10;
//        while (nextAEID == position && LOCALID.size() != 1) {
//            nextAEID = rnd.nextInt(LOCALID.size() * 10) / 10;
//        }
//        return  LOCALID.get(nextAEID);

        //这里貌似存在问题，修改逻辑如下
        Random rnd = new Random();
        int nextAEID = rnd.nextInt(LOCALID.size() * 10) / 10;
        if (nextAEID == position && LOCALID.size()>nextAEID){
            if (nextAEID == LOCALID.size()-1){
                nextAEID--;
            }else {
                nextAEID++;
            }
        }
        return  LOCALID.get(nextAEID);
    }
}
