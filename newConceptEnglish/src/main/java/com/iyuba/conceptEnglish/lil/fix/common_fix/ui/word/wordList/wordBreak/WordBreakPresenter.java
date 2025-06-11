package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList.wordBreak;

import android.text.TextUtils;
import android.util.Pair;

import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.CommonDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.ConceptDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.dataManager.JuniorDataManager;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.WordBreakEntity;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.WordEntity_conceptFour;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.concept.WordEntity_conceptJunior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.junior.WordEntity_junior;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.util.DBTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.util.RemoteTransUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.mvp.BasePresenter;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.sqlite.db.WordChildDBManager;
import com.iyuba.conceptEnglish.sqlite.op.VoaWordOp;
import com.iyuba.conceptEnglish.sqlite.op.WordOp;
import com.iyuba.conceptEnglish.util.ConceptApplication;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.data.model.VoaWord2;
import com.iyuba.core.common.data.model.Word;

import java.util.ArrayList;
import java.util.Collections;
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
public class WordBreakPresenter extends BasePresenter<WordBreakView> {

    @Override
    public void detachView() {
        super.detachView();
    }

    //获取单词数据
    private List<WordBean> getWordData(String types, String bookId, String id){
        List<WordBean> list = new ArrayList<>();

        switch (types){
            case TypeLibrary.BookType.junior_primary:
            case TypeLibrary.BookType.junior_middle:
                //中小学
                List<WordEntity_junior> juniorList = JuniorDataManager.searchWordByUnitIdFromDB(bookId,id);
                if (juniorList!=null&&juniorList.size()>0){
                    return DBTransUtil.juniorWordToWordData(types,juniorList);
                }
                break;
            case TypeLibrary.BookType.conceptFour:
            case TypeLibrary.BookType.conceptFourUS:
            case TypeLibrary.BookType.conceptFourUK:
                //新概念全四册
//                List<WordEntity_conceptFour> conceptFourList = ConceptDataManager.searchConceptFourWordByVoaIdFromDB(id);
//                if (conceptFourList!=null&&conceptFourList.size()>0){
//                    return DBTransUtil.conceptFourWordToWordData(types,conceptFourList);
//                }
                //这里优化处理下，别用自己的数据库，用已经存在的数据库进行处理
                List<VoaWord2> fourList = new VoaWordOp(ResUtil.getInstance().getContext()).findDataByVoaId(Integer.parseInt(id));
                if (fourList!=null&&fourList.size()>0){
                    //转换成需要的数据
                    return DBTransUtil.oldDbConceptFourWordToWordData(types,fourList);
                }else {
                    //加载数据（太懒了，没有加载数据，后面再说吧）
                    return new ArrayList<>();
                }
            case TypeLibrary.BookType.conceptJunior:
                //新概念青少版
//                List<WordEntity_conceptJunior> conceptJuniorList = ConceptDataManager.searchConceptJuniorWordByUnitIdFromDB(id);
//                if (conceptJuniorList!=null&&conceptJuniorList.size()>0){
//                    return DBTransUtil.conceptJuniorWordToWordData(types,conceptJuniorList);
//                }
                //这里优化处理下，别用自己的数据库，用已经存在的数据库进行处理
                List<VoaWord2> youngList = new VoaWordOp(ConceptApplication.getContext()).findDataByVoaId(Integer.parseInt(id));
//                List<VoaWord2> youngList = WordChildDBManager.getInstance().findDataByBookIdAndVoaId(bookId,id);
                if (youngList!=null&&youngList.size()>0){
                    //转换成需要的数据
                    return DBTransUtil.oldDbConceptFourWordToWordData(types,youngList);
                }else {
                    //这里偷懒了，按理说应该从远程接口中获取数据，这里没有弄，后面处理哈
                    return new ArrayList<>();
                }
        }

        return list;
    }

    //根据单词数据进行处理
    /*public List<Pair<WordBean,List<WordBean>>> getRandomWordShowData(String types,String bookId,String id){
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
                    answerMap.put(answerPair.second.getWord(), answerPair.second);
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
    }*/

    //新的单词数据处理(旧版本因为如果单词数据不够的话，会存在显示问题，因此需要处理下)
    public List<Pair<WordBean,List<WordBean>>> getRandomWordShowData(String types,String bookId,String id){
        //本课程的单词数据
        List<WordBean> oldList = getWordData(types, bookId,id);
        //随机数据
        List<Pair<WordBean,List<WordBean>>> randomList = new ArrayList<>();

        //先混乱排序下，不要按照顺序显示
        Collections.shuffle(oldList);

        //然后每隔数据从数据库中随机获取下单词内容进行处理下
        //这里是整个单词库的数据，随机选
        int startTd = (int) (Math.random()*2+1)*100;
        int endId = (int) (Math.random()*3+3)*100;
//        List<VoaWord2> bookWordList = new VoaWordOp(ResUtil.getInstance().getContext()).findDataByBookId(startTd,endId);
        List<VoaWord2> bookWordList = new VoaWordOp(ResUtil.getInstance().getContext()).findDataByBookIdRandom100();
        for (int i = 0; i < oldList.size(); i++) {
            //当前的数据
            WordBean curData = oldList.get(i);

            //先随机从数据库中获取三个数据
            List<WordBean> showList = new ArrayList<>();
            int choiceCount = 3;
            while (choiceCount>0){
                int randomIndex = (int) (Math.random()*bookWordList.size());
                VoaWord2 selectData = bookWordList.get(randomIndex);
                if (!selectData.word.equals(curData.getWord())){
                    WordBean wordBean = DBTransUtil.oldDbConceptFourWordToSingleWordData(TypeLibrary.BookType.conceptFour,selectData,0);
                    showList.add(wordBean);
                    choiceCount--;
                }
            }
            showList.add(curData);
            //随机处理下
            Collections.shuffle(showList);
            //保存
            randomList.add(new Pair<>(curData,showList));
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

            //这里修改下逻辑，当数据大于80%时，显示通过
            int passCount = Math.round(totalCount*0.8f);
            if (rightCount < passCount){
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
