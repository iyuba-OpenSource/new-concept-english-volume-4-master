package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList.wordTrain.train_cnToEn;

import android.text.TextUtils;
import android.util.Pair;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.WordBreakEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.WordEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.util.DBTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.util.RemoteTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title:
 * @date: 2023/5/26 09:21
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class CnToEnPresenter extends BasePresenter<CnToEnView> {

    @Override
    public void detachView() {
        super.detachView();
    }

    //获取单词数据
    private List<WordBean> getWordData(String types, String bookId, String id){
        List<WordBean> list = new ArrayList<>();

        /*if (types.equals(TypeLibrary.BookType.conceptFour)
                ||types.equals(TypeLibrary.BookType.conceptJunior)){
            //新概念
            if (types.equals(TypeLibrary.BookType.conceptFour)){
                //全四册
                List<WordEntity_conceptFour> fourList = DataManager.getInstance().searchConceptFourWordByVoaIdFromDB(id);
                if (fourList!=null&&fourList.size()>0){
                    return DBTransUtil.conceptFourWordToWordData(types,fourList);
                }
            }else if (types.equals(TypeLibrary.BookType.conceptJunior)){
                //青少版
                List<WordEntity_conceptJunior> juniorList = DataManager.getInstance().searchConceptJuniorWordByUnitIdFromDB(id);
                if (juniorList!=null&&juniorList.size()>0){
                    return DBTransUtil.conceptJuniorWordToWordData(types,juniorList);
                }
            }
        }else*/
            if (types.equals(TypeLibrary.BookType.junior_primary)
                ||types.equals(TypeLibrary.BookType.junior_middle)){
            //中小学
            List<WordEntity_junior> juniorList = JuniorDataManager.searchWordByUnitIdFromDB(bookId,id);
            if (juniorList!=null&&juniorList.size()>0){
                return DBTransUtil.juniorWordToWordData(types,juniorList);
            }
        }

        return list;
    }

    //根据单词数据进行处理
    public List<Pair<WordBean,List<WordBean>>> getRandomWordShowData(String types,String bookId,String id){
        //本课程的单词数据
        List<WordBean> oldList = getWordData(types, bookId,id);
        //本课程的单词内容
        List<Pair<Integer,WordBean>> oldPairList = new ArrayList<>();
        //复制上边的数据，进行答案处理
        List<Pair<Integer,WordBean>> oldPairCloneList = new ArrayList<>();

        //新的随机数据
        List<Pair<WordBean,List<WordBean>>> randomList = new ArrayList<>();

        if (oldList==null||oldList.size()==0){
            return randomList;
        }

        //1.将单词数据转为map数据
        for (int i = 0; i < oldList.size(); i++) {
            oldPairList.add(new Pair<>(i,oldList.get(i)));
            oldPairCloneList.add(new Pair<>(i,oldList.get(i)));
        }

        //2.将数据转换为随机数据
        while (oldPairList.size()>0){
            //获取随机数据
            int randomInt = (int) (Math.random()*oldPairList.size());
            Pair<Integer,WordBean> randomPair = oldPairList.get(randomInt);

            //将原来的数据中删除选中的数据
            oldPairList.remove(randomPair);

            //获取答案数据(获取不重复的3个数据，然后将标准答案放在随机的位置)
            List<WordBean> answerList = new ArrayList<>();
            Map<String,WordBean> answerMap = new HashMap<>();
            int answerCount = Math.min(oldPairCloneList.size(), 3);
            while (answerMap.keySet().size()<answerCount){
                //这里使用拷贝的数据，因为oldPairList数据逐渐被删除，后面会导致数据不足
                int answerInt = (int) (Math.random()*oldPairCloneList.size());
                Pair<Integer,WordBean> answerPair = oldPairCloneList.get(answerInt);

                if (!answerPair.first.equals(randomPair.first)
                        && answerMap.get(answerPair.first)==null){
                    answerMap.put(answerPair.second.getDef(), answerPair.second);
                }
            }
            for (String key:answerMap.keySet()){
                answerList.add(answerMap.get(key));
            }
            int keyInt = (int) (Math.random()*answerList.size());
            answerList.add(keyInt, randomPair.second);

            //组合数据显示
            randomList.add(new Pair<>(randomPair.second,answerList));
        }

        return randomList;
    }

    //将闯关数据保存在数据库
    public void saveWordBreakDataToDB(long userId,Map<WordBean,WordBean> map){
        if (map!=null&&map.keySet().size()>0){
            List<WordBreakEntity> list = RemoteTransUtil.transWordBreakToDB(userId, map);
            CommonDataManager.saveWordBreakDataToDB(list);

            //新判断是否更新
            int rightCount = 0;
            int totalCount = map.keySet().size();
            for (WordBean key:map.keySet()){
                String keyWord = key.getWord();
                String resultWord = map.get(key).getWord();

                if (keyWord.equals(resultWord)){
                    rightCount++;
                }
            }

            if (rightCount != totalCount){
                return;
            }

            //获取当前数据库的位置，如果大于当前的则设置为最新的
            WordBreakEntity firstEntity = list.get(0);
            String passId = CommonDataManager.searchWordBreakPassIdDataFromDB(firstEntity.types, firstEntity.bookId, userId);
            if (TextUtils.isEmpty(passId)){
                CommonDataManager.saveWordBreakPassDataToDB(RemoteTransUtil.transWordBreakProgressToDB(firstEntity.types,firstEntity.bookId,firstEntity.id,userId));
            }else {
                long newId = Long.parseLong(firstEntity.id);
                long oldId = Long.parseLong(passId);

                if (newId>oldId){
                    CommonDataManager.saveWordBreakPassDataToDB(RemoteTransUtil.transWordBreakProgressToDB(firstEntity.types,firstEntity.bookId,firstEntity.id,userId));
                }
            }
        }
    }
}
