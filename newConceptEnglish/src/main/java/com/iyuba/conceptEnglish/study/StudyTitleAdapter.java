package com.iyuba.conceptEnglish.study;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ItemStudyTitleBinding;

import java.util.List;

/**
 * @title: 学习界面-标题适配器
 * @date: 2023/5/16 11:09
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class StudyTitleAdapter extends RecyclerView.Adapter<StudyTitleAdapter.TitleHolder> {

    private Context context;
    private List<Pair<String,String>> list;

    //选中的位置
    private int selectIndex = 0;

    public StudyTitleAdapter(Context context, List<Pair<String,String>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TitleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStudyTitleBinding binding = ItemStudyTitleBinding.inflate(LayoutInflater.from(context),parent,false);
        return new TitleHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TitleHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        Pair<String,String> text = list.get(position);
        holder.textView.setText(text.second);

        if (selectIndex == position){
            holder.textView.setBackgroundResource(R.drawable.tab_orange);
            if (list.size()<=5){
                holder.textView.setTextSize(16);
            }else {
                holder.textView.setTextSize(14);
            }
            holder.textView.setTextColor(context.getResources().getColor(R.color.white));
        }else {
            holder.textView.setBackgroundResource(R.drawable.tab_grey);
            if (list.size()<=5){
                holder.textView.setTextSize(14);
            }else {
                holder.textView.setTextSize(12);
            }
            holder.textView.setTextColor(context.getResources().getColor(R.color.black));
        }

        holder.itemView.setOnClickListener(v->{
            if (selectIndex == position){
                return;
            }

            if (listener!=null){
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class TitleHolder extends RecyclerView.ViewHolder{

        private TextView textView;

        public TitleHolder(ItemStudyTitleBinding binding){
            super(binding.getRoot());

            textView = binding.textview;
        }
    }

    //刷新数据
    public void refreshData(List<Pair<String,String>> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //刷新选中数据
    public void refreshIndex(int index){
        this.selectIndex = index;
        notifyDataSetChanged();
    }

    //获取当前选中的名称
    public String getSelectTitle(){
        return list.get(selectIndex).second;
    }

    //选中回调
    private OnSimpleClickListener listener;

    public interface OnSimpleClickListener{
        void onClick(int position);
    }

    public void setListener(OnSimpleClickListener listener) {
        this.listener = listener;
    }
}
