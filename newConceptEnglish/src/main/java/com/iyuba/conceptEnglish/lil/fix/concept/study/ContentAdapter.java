package com.iyuba.conceptEnglish.lil.fix.concept.study;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ItemFixSectionBinding;
import com.iyuba.conceptEnglish.lil.concept_other.util.HelpUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.view.SelectWordTextView;
import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail;

import java.util.List;

/**
 * @title:
 * @date: 2023/11/2 09:00
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentHolder> {

    private Context context;
    private List<VoaDetail> list;

    //设置选中的位置
    private int selectIndex = 0;
    //当前语言显示类型
    private String showLanguage = TypeLibrary.TextShowType.ALL;
    //文本大小
    private int textSize = 16;

    public ContentAdapter(Context context, List<VoaDetail> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFixSectionBinding binding = ItemFixSectionBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ContentHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentHolder holder, int position) {
        if (holder==null){
            return;
        }

        VoaDetail voaDetail = list.get(position);
        holder.sentenceView.setText(HelpUtil.transTitleStyle(voaDetail.sentence));
        holder.sentenceCnView.setText(voaDetail.sentenceCn);

        holder.sentenceView.setTextSize(textSize);
        holder.sentenceCnView.setTextSize(textSize);

        if (selectIndex==position){
            holder.sentenceView.setTextColor(0xff2983c1);
            holder.sentenceCnView.setTextColor(0xff2983c1);
        }else {
            holder.sentenceView.setTextColor(context.getResources().getColor(R.color.black));
            holder.sentenceCnView.setTextColor(context.getResources().getColor(R.color.gray));
        }

        if (showLanguage.equals(TypeLibrary.TextShowType.ALL)){
            holder.sentenceView.setVisibility(View.VISIBLE);
            holder.sentenceCnView.setVisibility(View.VISIBLE);
        }else if (showLanguage.equals(TypeLibrary.TextShowType.EN)){
            holder.sentenceView.setVisibility(View.VISIBLE);
            holder.sentenceCnView.setVisibility(View.GONE);
        }else if (showLanguage.equals(TypeLibrary.TextShowType.CN)){
            holder.sentenceView.setVisibility(View.GONE);
            holder.sentenceCnView.setVisibility(View.VISIBLE);
        }

        holder.sentenceView.setOnClickWordListener(new SelectWordTextView.OnClickWordListener() {
            @Override
            public void onClickWord(String word) {
                if (onWordSelectListener!=null){
                    onWordSelectListener.onSelect(word);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ContentHolder extends RecyclerView.ViewHolder{

        private SelectWordTextView sentenceView;
        private TextView sentenceCnView;

        public ContentHolder(ItemFixSectionBinding binding){
            super(binding.getRoot());

            sentenceView = binding.sentence;
            sentenceView.setSelectedColorEnable(false);
            sentenceCnView = binding.sentenceCn;
        }
    }

    //刷新数据
    public void refreshData(List<VoaDetail> detailList){
        this.list = detailList;
        notifyDataSetChanged();
    }

    //刷新选中位置
    public void refreshIndex(int index){
        if (selectIndex==index){
            return;
        }
        this.selectIndex = index;
        Log.d("当前位置", "refreshIndex: --"+selectIndex);
        notifyDataSetChanged();
    }

    //获取当前选中的位置
    public int getSelectIndex(){
        return selectIndex;
    }

    //刷新语言类型
    public void refreshLanguage(String languageType){
        this.showLanguage = languageType;
        notifyDataSetChanged();
    }

    //刷新文本大小
    public void refreshTextSize(int showTextSize){
        this.textSize = showTextSize;
        if (list!=null&&list.size()>0){
            notifyDataSetChanged();
        }
    }

    //设置单词回调
    private OnWordSelectListener onWordSelectListener;

    public interface OnWordSelectListener{
        void onSelect(String selectText);
    }

    public void setOnWordSelectListener(OnWordSelectListener onWordSelectListener) {
        this.onWordSelectListener = onWordSelectListener;
    }
}
