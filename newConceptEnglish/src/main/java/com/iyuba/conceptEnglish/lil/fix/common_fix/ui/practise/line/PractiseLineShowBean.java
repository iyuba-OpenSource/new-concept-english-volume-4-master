package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.line;

import java.util.List;

/**
 * 新版练习题的展示数据模型
 */
public class PractiseLineShowBean {
    //习题类型
    public static final int listen = 1;//听力
    public static final int eval = 2;//评测
    public static final int word = 3;//单词
    public static final int practise = 4;//练习
    public static final int box = 5;//宝箱

    private int voaId;//课程id
    private int unitIndex;//单元序号
    private String title;//标题
    private List<PractisePassBean> passList;

    public PractiseLineShowBean(int voaId, int unitIndex, String title, List<PractisePassBean> passList) {
        this.voaId = voaId;
        this.unitIndex = unitIndex;
        this.title = title;
        this.passList = passList;
    }

    public int getVoaId() {
        return voaId;
    }

    public int getUnitIndex() {
        return unitIndex;
    }

    public String getTitle() {
        return title;
    }

    public List<PractisePassBean> getPassList() {
        return passList;
    }

    static class PractisePassBean{
        private int showType;//类型

        private boolean isPass;//是否通关
        private boolean isClick;//是否点击

        private long rightCount;//正确数
        private long totalCount;//总数

        public PractisePassBean(int showType, boolean isPass, boolean isClick, long rightCount, long totalCount) {
            this.showType = showType;
            this.isPass = isPass;
            this.isClick = isClick;
            this.rightCount = rightCount;
            this.totalCount = totalCount;
        }

        public int getShowType() {
            return showType;
        }

        public boolean isPass() {
            return isPass;
        }

        public boolean isClick() {
            return isClick;
        }

        public long getRightCount() {
            return rightCount;
        }

        public long getTotalCount() {
            return totalCount;
        }
    }
}
