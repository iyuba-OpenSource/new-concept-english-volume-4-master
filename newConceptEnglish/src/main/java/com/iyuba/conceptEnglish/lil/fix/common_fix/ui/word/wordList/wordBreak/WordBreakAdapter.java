package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList.wordBreak;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ItemWordBreakBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/26 09:23
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordBreakAdapter extends RecyclerView.Adapter<WordBreakAdapter.WordBreakHolder> {

    private Context context;
    private List<WordBean> list;

    //当前标志的单词
    private String showWord = "";
    //当前选中的位置
    private int selectIndex = -1;
    //是否禁止点击
    private boolean isClicked = true;

    public WordBreakAdapter(Context context, List<WordBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WordBreakHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWordBreakBinding binding = ItemWordBreakBinding.inflate(LayoutInflater.from(context),parent,false);
        return new WordBreakHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WordBreakHolder holder, int position) {
        if (holder==null){
            return;
        }

        WordBean wordBean = list.get(position);
        holder.desc.setText(wordBean.getDef());

        if (wordBean.getWord().equals(showWord)){
            holder.desc.setBackgroundResource(R.drawable.word_exercise_right);
        }else {
            if (selectIndex==position){
                holder.desc.setBackgroundResource(R.drawable.word_exercise_error);
            }else {
                holder.desc.setBackgroundResource(R.drawable.word_exercise_white);
            }
        }

        holder.itemView.setOnClickListener(v->{
            if (!isClicked){
                return;
            }

            if (listener!=null){
                listener.onClick(new Pair<>(position,wordBean));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class WordBreakHolder extends RecyclerView.ViewHolder{

        private TextView desc;

        public WordBreakHolder(ItemWordBreakBinding binding){
            super(binding.getRoot());

            desc = binding.desc;
        }
    }

    //刷新数据
    public void refreshData(List<WordBean> refreshList){
        this.list = refreshList;
        this.selectIndex = -1;
        this.showWord = "";
        this.isClicked = true;
        notifyDataSetChanged();
    }

    //刷新答案显示
    public void refreshAnswer(int index,String showWord){
        this.selectIndex = index;
        this.showWord = showWord;
        this.isClicked = false;
        notifyDataSetChanged();
    }

    //回调
    public OnSimpleClickListener<Pair<Integer,WordBean>> listener;

    public void setListener(OnSimpleClickListener<Pair<Integer, WordBean>> listener) {
        this.listener = listener;
    }
}
