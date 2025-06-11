package com.iyuba.conceptEnglish.manager;

import android.text.TextUtils;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_concept;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.LocalMarkEntity_conceptDownload;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;
import com.iyuba.conceptEnglish.widget.subtitle.Subtitle;
import com.iyuba.conceptEnglish.widget.subtitle.SubtitleSum;
import com.iyuba.configation.entity.enumconcept.BookType;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理voa数据的统一类。
 *
 * @author chentong
 */
public class VoaDataManager {

    private static VoaDataManager instance;

    private VoaDataManager() {

    }

    public static synchronized VoaDataManager Instace() {
        if (instance == null) {
            instance = new VoaDataManager();
        }
        return instance;
    }


    public int playLocalType; // 0:默认(首页列表)  1：已下载  2：已收藏 3: 试听
    public Voa voaTemp;
    public List<Voa> voasTemp = new ArrayList<Voa>();
    public List<VoaDetail> voaDetailsTemp = new ArrayList<>();
    public SubtitleSum subtitleSum;

    public void setSubtitleSum(Voa voa, List<VoaDetail> textDetailsTemp) {
        if (textDetailsTemp == null||voa==null) {
            return;
        }
        this.voaDetailsTemp = textDetailsTemp;
        subtitleSum = new SubtitleSum();
        subtitleSum.voaId = voa.voaId;
        subtitleSum.articleTitle = voa.title;
        subtitleSum.isCollect = false; // 查询是否被收藏
        subtitleSum.mp3Url = voa.sound;

        if (subtitleSum.subtitles == null) {
            subtitleSum.subtitles = new ArrayList<Subtitle>();
            subtitleSum.subtitles.clear();
        }

        for (int i = 0; i < textDetailsTemp.size(); i++) {
            Subtitle st = new Subtitle();
            st.articleTitle = voa.title;

            if (TextUtils.isEmpty(textDetailsTemp.get(i).sentenceCn)) {
                st.content = textDetailsTemp.get(i).sentence;
            } else {
                st.content = textDetailsTemp.get(i).sentence + "\n"
                        + textDetailsTemp.get(i).sentenceCn;
            }

            st.pointInTime = textDetailsTemp.get(i).startTime;
            subtitleSum.subtitles.add(st);
        }
    }

    public void changeLanguage(boolean isOnlyEnglish) {
        if (voaDetailsTemp == null) {
            return;
        }

        if (subtitleSum==null||subtitleSum.subtitles==null){
            return;
        }

        for (int i = 0; i < voaDetailsTemp.size(); i++) {
            Subtitle st = subtitleSum.subtitles.get(i);

            if (isOnlyEnglish) {//只有英文
                st.content = voaDetailsTemp.get(i).sentence;
            } else {
                if (TextUtils.isEmpty(voaDetailsTemp.get(i).sentenceCn)) {
                    st.content = voaDetailsTemp.get(i).sentence;
                } else {
                    st.content = voaDetailsTemp.get(i).sentence + "\n" + voaDetailsTemp.get(i).sentenceCn;
                }

            }
        }
    }

    public void setPlayLocalType(int playLocalType) {
        this.playLocalType = playLocalType;
    }

    public static synchronized VoaDataManager getInstance() {
        if (instance == null) {
            instance = new VoaDataManager();
        }
        return instance;
    }


    //合并当前类型到voa数据中（类型数据为临时数据，但是是可用的）
    public List<Voa> margeTypeToVoa(List<Voa> list,String lessonType){
        List<Voa> tempList = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                Voa voa = list.get(i);

                //合并当前类型
                voa.lessonType = lessonType;
                voa.position = i;
                //合并数据信息
                margeSingleDataToVoa(voa);
                tempList.add(voa);
            }
        }
        return tempList;
    }

    //获取当前的类型（仅限于当前的数据）
    public String transCurTypeToShow(BookType curType){
        String lessonType = TypeLibrary.BookType.conceptFourUS;

        switch (curType){
            case AMERICA:
                return TypeLibrary.BookType.conceptFourUS;
            case ENGLISH:
                return TypeLibrary.BookType.conceptFourUK;
            case YOUTH:
                return TypeLibrary.BookType.conceptJunior;
        }
        return lessonType;
    }

    //合并单个数据的信息(将数据的收藏、下载和阅读信息合并)
    public Voa margeSingleDataToVoa(Voa curVoa){
        if (curVoa==null){
            return curVoa;
        }

        //查询当前的下载数据
        LocalMarkEntity_conceptDownload tempData = ConceptDataManager.getLocalMarkSingleDownload(curVoa.voaId,curVoa.lessonType,UserInfoManager.getInstance().getUserId());
        if (tempData==null||TextUtils.isEmpty(tempData.isDownload)){
            curVoa.isDownload = "0";
        }else {
            curVoa.isDownload = tempData.isDownload;
        }

        //查询当前的阅读和收藏数据
        if (UserInfoManager.getInstance().isLogin()){
            LocalMarkEntity_concept showData = ConceptDataManager.getLocalMarkSingle(curVoa.voaId,curVoa.lessonType, UserInfoManager.getInstance().getUserId());
            if (showData==null){
                curVoa.isRead = "0";
                curVoa.isCollect = "0";
            }else {
                if (TextUtils.isEmpty(showData.isRead)){
                    curVoa.isRead = "0";
                }else {
                    curVoa.isRead = showData.isRead;
                }
                if (TextUtils.isEmpty(showData.isCollect)){
                    curVoa.isCollect = "0";
                }else {
                    curVoa.isCollect = showData.isCollect;
                }
            }
        }else {
            curVoa.isRead = "0";
            curVoa.isCollect = "0";
        }
        return curVoa;
    }
}
