package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.dubbing.dubbingRank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ItemRankingTalkBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Dubbing_rank;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.ImageUtil;
import com.iyuba.core.common.widget.circularimageview.CircularImageView;

import java.util.List;

/**
 * @title:
 * @date: 2023/6/13 16:04
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class DubbingRankAdapter extends RecyclerView.Adapter<DubbingRankAdapter.DubbingRankHolder> {

    private Context context;
    private List<Dubbing_rank> list;

    public DubbingRankAdapter(Context context, List<Dubbing_rank> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DubbingRankHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRankingTalkBinding binding = ItemRankingTalkBinding.inflate(LayoutInflater.from(context),parent,false);
        return new DubbingRankHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DubbingRankHolder holder, int position) {
        if (holder==null){
            return;
        }

        Dubbing_rank rank = list.get(position);
        holder.name.setText(rank.getUserName());
        holder.time.setText(rank.getCreateDate());
        ImageUtil.loadCircleImg(rank.getImgSrc(),0,holder.userPic);
        holder.score.setText(rank.getScore()+"分");
        holder.agree.setText(rank.getAgreeCount());
        if (position==0){
            holder.index.setBackgroundResource(R.drawable.rank_first);
            holder.index.setText("");
        }else if (position==1){
            holder.index.setBackgroundResource(R.drawable.rank_second);
            holder.index.setText("");
        }else if (position==2){
            holder.index.setBackgroundResource(R.drawable.rank_third);
            holder.index.setText("");
        }else {
            holder.index.setBackgroundResource(0);
            holder.index.setText(String.valueOf(position+1));
        }

        holder.itemView.setOnClickListener(v->{
            if (listener!=null){
                listener.onClick(rank);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class DubbingRankHolder extends RecyclerView.ViewHolder{

        private TextView index;
        private CircularImageView userPic;
        private TextView name;
        private TextView time;
        private TextView score;
        private TextView agree;

        public DubbingRankHolder(ItemRankingTalkBinding binding){
            super(binding.getRoot());

            index = binding.rank;
            userPic = binding.photo;
            name = binding.usernameTv;
            time = binding.timeTv;
            score = binding.tvScore;
            agree = binding.thumbsNum;
        }
    }

    //刷新数据
    public void refreshData(List<Dubbing_rank> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //增加数据
    public void addData(List<Dubbing_rank> addList){
        this.list.addAll(addList);
        notifyDataSetChanged();
    }

    /*********************回调数据*******************/
    private OnSimpleClickListener<Dubbing_rank> listener;

    public void setListener(OnSimpleClickListener<Dubbing_rank> listener) {
        this.listener = listener;
    }
}
