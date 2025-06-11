//package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.read.service;
//
//import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
//import com.iyuba.conceptEnglish.sqlite.mode.Voa;
//
//import java.util.List;
//
///**
// * @title: 新概念-后台播放会话
// * @date: 2023/10/27 11:08
// * @author: liang_mu
// * @email: liang.mu.cn@gmail.com
// * @description:
// */
//public class JuniorBgPlaySession {
//    private static JuniorBgPlaySession instance;
//
//    public static JuniorBgPlaySession getInstance(){
//        if (instance==null){
//            synchronized (JuniorBgPlaySession.class){
//                if (instance==null){
//                    instance = new JuniorBgPlaySession();
//                }
//            }
//        }
//        return instance;
//    }
//
//    //当前全部的章节列表数据
//    private List<BookChapterBean> voaList;
//
//    public List<BookChapterBean> getVoaList() {
//        return voaList;
//    }
//
//    public void setVoaList(List<BookChapterBean> voaList) {
//        this.voaList = voaList;
//    }
//
//    //当前选中的位置（将要去的位置）
//    private int playPosition = -1;
//
//    public int getPlayPosition() {
//        return playPosition;
//    }
//
//    public void setPlayPosition(int playPosition) {
//        this.playPosition = playPosition;
//    }
//
//    //上一个的选中的位置（上一个的位置）
//    private int playPrePosition = -2;
//
//    public int getPlayPrePosition() {
//        return playPrePosition;
//    }
//
//    public void setPlayPrePosition(int playPrePosition) {
//        this.playPrePosition = playPrePosition;
//    }
//
//    //获取当前的数据
//    public BookChapterBean getCurData(){
//        if (voaList!=null&&voaList.size()>0&&playPosition!=-1){
//            return voaList.get(playPosition);
//        }
//        return null;
//    }
//}
