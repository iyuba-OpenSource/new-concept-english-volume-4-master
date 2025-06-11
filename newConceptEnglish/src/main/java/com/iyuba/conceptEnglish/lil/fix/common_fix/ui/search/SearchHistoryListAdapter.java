package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.databinding.ItemSearchHistoryBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;

/**
 * @title: 新搜索-历史记录
 * @date: 2023/11/20 11:12
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class SearchHistoryListAdapter extends RecyclerView.Adapter<SearchHistoryListAdapter.HistoryHolder> {

    private Context context;
    private String[] list;

    public SearchHistoryListAdapter(Context context, String[] list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchHistoryBinding binding = ItemSearchHistoryBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HistoryHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
        if (holder==null){
            return;
        }

        String wordStr = list[position];
        holder.textView.setText(wordStr);
        holder.itemView.setOnClickListener(v->{
            if (onHistoryItemClickListener!=null){
                onHistoryItemClickListener.onClick(wordStr);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onHistoryItemClickListener!=null){
                    onHistoryItemClickListener.onDelete(wordStr);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.length;
    }

    class HistoryHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public HistoryHolder(ItemSearchHistoryBinding binding){
            super(binding.getRoot());

            textView = binding.textView;
        }
    }

    //回调
    private OnHistoryItemClickListener onHistoryItemClickListener;

    public interface OnHistoryItemClickListener{
        //选中
        void onClick(String wordStr);
        //删除
        void  onDelete(String wordStr);
    }

    public void setOnHistoryItemClickListener(OnHistoryItemClickListener onHistoryItemClickListener) {
        this.onHistoryItemClickListener = onHistoryItemClickListener;
    }

    //刷新数据
    public void refreshData(String[] refreshArray){
        this.list = refreshArray;
        notifyDataSetChanged();
    }
}
