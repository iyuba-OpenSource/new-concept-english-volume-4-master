package com.iyuba.conceptEnglish.lil.concept_other.word.wordNote;

import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_note;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BaseView;

import java.util.List;

public interface WordNoteView extends BaseView {

    //数据回调
    void onWordShow(List<Word_note.TempWord> list,String showMsg);

    //收藏/取消收藏单词
    void onCollectWord(boolean isSuccess,String showMsg);
}
