package com.iyuba.conceptEnglish.lil.fix.concept.bgService;

import com.iyuba.conceptEnglish.sqlite.mode.Voa;

import java.util.List;

/**
 * @title: 新概念-后台播放会话
 * @date: 2023/10/27 11:08
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ConceptBgPlaySession {
    private static ConceptBgPlaySession instance;

    public static ConceptBgPlaySession getInstance(){
        if (instance==null){
            synchronized (ConceptBgPlaySession.class){
                if (instance==null){
                    instance = new ConceptBgPlaySession();
                }
            }
        }
        return instance;
    }

    //当前全部的章节列表数据
    private List<Voa> voaList;

    public List<Voa> getVoaList() {
        return voaList;
    }

    public void setVoaList(List<Voa> voaList) {
        this.voaList = voaList;
    }

    //当前选中的位置（将要去的位置）
    private int playPosition = -1;

    public int getPlayPosition() {
        return playPosition;
    }

    public void setPlayPosition(int playPosition) {
        this.playPosition = playPosition;
    }

    //获取当前的数据（只能在外边用）
    public Voa getCurData(){
        if (voaList!=null&&voaList.size()>0&&playPosition!=-1){
            return voaList.get(playPosition);
        }
        return null;
    }

    //这里增加临时数据位，表示当前数据为临时数据，仅仅播放一次
    private boolean tempData = false;

    public void setTempData(boolean tempData) {
        this.tempData = tempData;
    }

    public boolean isTempData() {
        return tempData;
    }
}
