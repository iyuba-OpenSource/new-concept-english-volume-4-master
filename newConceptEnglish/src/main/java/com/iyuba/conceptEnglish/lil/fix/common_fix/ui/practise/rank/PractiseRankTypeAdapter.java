package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.rank;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ItemPractiseRankTypeBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;

import java.util.List;

public class PractiseRankTypeAdapter extends RecyclerView.Adapter<PractiseRankTypeAdapter.TypeHolder> {

    private Context context;
    private List<Pair<String,String>> list;

    //当前位置
    private int selectIndex = 0;

    public PractiseRankTypeAdapter(Context context, List<Pair<String, String>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPractiseRankTypeBinding binding = ItemPractiseRankTypeBinding.inflate(LayoutInflater.from(context),parent,false);
        return new TypeHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        Pair<String,String> pairData = list.get(position);
        holder.tabItem.setText(pairData.second);
        if (position==selectIndex){
            holder.tabItem.setTextColor(context.getResources().getColor(R.color.white));
            holder.tabItem.setBackgroundResource(R.drawable.shape_rctg_complete_green);
        }else {
            holder.tabItem.setTextColor(context.getResources().getColor(R.color.black));
            holder.tabItem.setBackgroundResource(0);
        }
        holder.itemView.setOnClickListener(v->{
            if (selectIndex==position){
                return;
            }

            selectIndex = position;
            notifyDataSetChanged();

            if (onSimpleClickListener!=null){
                onSimpleClickListener.onClick(pairData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class TypeHolder extends RecyclerView.ViewHolder{

        private TextView tabItem;

        public TypeHolder(ItemPractiseRankTypeBinding binding){
            super(binding.getRoot());

            tabItem = binding.tabItem;
        }
    }

    //刷新数据
    public void refreshList(List<Pair<String,String>> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //接口
    private OnSimpleClickListener<Pair<String,String>> onSimpleClickListener;

    public void setOnSimpleClickListener(OnSimpleClickListener<Pair<String, String>> onSimpleClickListener) {
        this.onSimpleClickListener = onSimpleClickListener;
    }
}
