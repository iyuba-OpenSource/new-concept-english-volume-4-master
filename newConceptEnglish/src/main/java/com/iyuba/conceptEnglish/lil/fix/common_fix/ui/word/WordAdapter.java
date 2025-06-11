package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ItemWordBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordProgressBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/10 19:03
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordHolder> {

    private Context context;
    private List<WordProgressBean> list;

    public WordAdapter(Context context, List<WordProgressBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWordBinding binding = ItemWordBinding.inflate(LayoutInflater.from(context),parent,false);
        return new WordHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WordHolder holder, int position) {
        if (holder==null){
            return;
        }

        WordProgressBean bean = list.get(position);
        String showText = bean.getLessonName()+"\n"+bean.getRight()+"/"+bean.getSize();
        holder.progressView.setText(showText);

        if (bean.isPass()){
            holder.progressView.setBackgroundResource(R.drawable.shape_corner_theme_10dp);
        }else {
            holder.progressView.setBackgroundResource(R.drawable.shape_corner_gray_10dp);
        }

        holder.itemView.setOnClickListener(v->{
            if (listener!=null){
                listener.onClick(new Pair<>(position,bean));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class WordHolder extends RecyclerView.ViewHolder{

        private TextView progressView;

        public WordHolder(ItemWordBinding binding){
            super(binding.getRoot());

            progressView = binding.wordProgress;
        }
    }

    //刷新数据
    public void refreshData(List<WordProgressBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //回调
    private OnSimpleClickListener<Pair<Integer,WordProgressBean>> listener;

    public void setListener(OnSimpleClickListener<Pair<Integer,WordProgressBean>> listener) {
        this.listener = listener;
    }
}
