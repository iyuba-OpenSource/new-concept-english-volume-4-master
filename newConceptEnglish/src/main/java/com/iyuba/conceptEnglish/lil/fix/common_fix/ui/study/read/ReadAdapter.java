package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.read;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ItemReadBinding;
import com.iyuba.conceptEnglish.lil.concept_other.util.HelpUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.ChapterDetailBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.StudySettingManager;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.view.SelectWordTextView;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/23 09:09
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ReadAdapter extends RecyclerView.Adapter<ReadAdapter.ReadHolder> {

    private Context context;
    private List<ChapterDetailBean> list;

    //选中的位置
    private int selectIndex = 0;
    //显示文本的类型
    private String showTextType = null;

    //文字设置
    private int textSize = 16;

    public ReadAdapter(Context context, List<ChapterDetailBean> list) {
        this.context = context;
        this.list = list;

        showTextType = StudySettingManager.getInstance().getTextType();
    }

    @NonNull
    @Override
    public ReadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReadBinding binding = ItemReadBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ReadHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadHolder holder, int position) {
        if (holder==null){
            return;
        }

        ChapterDetailBean bean = list.get(position);
        holder.sentenceView.setText(HelpUtil.transTitleStyle(bean.getSentence()));
        holder.sentenceCnView.setText(bean.getSentenceCn());

        holder.sentenceView.setTextSize(textSize);
        holder.sentenceView.setTextSize(textSize);

        if (selectIndex == position){
            holder.sentenceView.setTextColor(Color.parseColor("#2983c1"));
            holder.sentenceCnView.setTextColor(Color.parseColor("#2983c1"));
        }else {
            holder.sentenceView.setTextColor(ResUtil.getInstance().getColor(R.color.black));
            holder.sentenceCnView.setTextColor(ResUtil.getInstance().getColor(R.color.black));
        }

        if (showTextType.equals(TypeLibrary.TextShowType.ALL)){
            holder.sentenceView.setVisibility(View.VISIBLE);
            holder.sentenceCnView.setVisibility(View.VISIBLE);
        }else if (showTextType.equals(TypeLibrary.TextShowType.EN)){
            holder.sentenceView.setVisibility(View.VISIBLE);
            holder.sentenceCnView.setVisibility(View.GONE);
        }else if (showTextType.equals(TypeLibrary.TextShowType.CN)){
            holder.sentenceView.setVisibility(View.GONE);
            holder.sentenceCnView.setVisibility(View.VISIBLE);
        }

//        holder.sentenceView.setTextpageSelectTextCallBack(new TextPageSelectTextCallBack() {
//            @Override
//            public void selectTextEvent(String selectText) {
//                //查询单词并且收藏操作
//                if (onWordSearchListener!=null){
//                    onWordSearchListener.onWordSearch(selectText);
//                }
//            }
//
//            @Override
//            public void cancelWordCard() {
//
//            }
//
//            @Override
//            public void selectParagraph(int paragraph) {
//
//            }
//        });

        holder.sentenceView.setOnClickWordListener(new SelectWordTextView.OnClickWordListener() {
            @Override
            public void onClickWord(String selectText) {
                //查询单词并且收藏操作
                if (onWordSearchListener!=null){
                    onWordSearchListener.onWordSearch(selectText);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ReadHolder extends RecyclerView.ViewHolder{

        private SelectWordTextView sentenceView;
        private TextView sentenceCnView;

        public ReadHolder(ItemReadBinding binding){
            super(binding.getRoot());

            sentenceView = binding.sentence;
            sentenceCnView = binding.sentenceCn;
        }
    }

    //刷新数据
    public void refreshData(List<ChapterDetailBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //刷新选中的位置
    public void refreshIndex(int index){
        this.selectIndex = index;
        notifyDataSetChanged();
    }

    //刷新显示文本的类型
    public void refreshShowTextType(String showType){
        this.showTextType = showType;
        notifyDataSetChanged();
    }

    //刷新文字大小
    public void refreshShowTextSize(int sizeLevel){
        this.textSize = sizeLevel;
        if (list!=null&&list.size()>0){
            notifyDataSetChanged();
        }
    }

    //获取当前选中的位置
    public int getSelectIndex(){
        return selectIndex;
    }

    //回调单词查询
    private onWordSearchListener onWordSearchListener;

    public interface onWordSearchListener{
        //查询操作
        void onWordSearch(String word);
    }

    public void setOnWordSearchListener(ReadAdapter.onWordSearchListener onWordSearchListener) {
        this.onWordSearchListener = onWordSearchListener;
    }
}
