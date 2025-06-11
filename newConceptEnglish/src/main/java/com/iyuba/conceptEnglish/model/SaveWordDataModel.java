package com.iyuba.conceptEnglish.model;

import android.content.Context;

import com.iyuba.conceptEnglish.sqlite.db.WordChildDBManager;
import com.iyuba.conceptEnglish.sqlite.mode.VoaWord;
import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
import com.iyuba.core.common.data.model.VoaWord2;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SaveWordDataModel {
    private Context mContext;
    private VoaWordOp mVoaWordOp;

    public SaveWordDataModel(Context context) {
        mContext = context;
        mVoaWordOp = new VoaWordOp(mContext);
    }

    public void saveWordData(List<VoaWord2> list) {
        //存储到 child_voa_word 表中
        WordChildDBManager.getInstance().saveData(list);
        //存储到 voa_word 表中，供闯关时使用和gamestageview 中使用
        List<VoaWord> saveList = new CopyOnWriteArrayList<>();
        for (VoaWord2 word2 : list) {
            VoaWord voaWord = new VoaWord();
            voaWord.voaId = word2.voaId;
            voaWord.unitId = Integer.parseInt(word2.unitId);
            voaWord.book_id = Integer.parseInt(word2.bookId);
            voaWord.word = word2.word;
            voaWord.def = word2.def;
            voaWord.pron = word2.pron;
            voaWord.examples = word2.examples;
            voaWord.audio = word2.audio;
            voaWord.position = word2.position;
            saveList.add(voaWord);
        }
        mVoaWordOp.saveData(saveList);
    }
}
