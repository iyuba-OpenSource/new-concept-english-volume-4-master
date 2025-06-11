package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.rank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ItemPractiseRankBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.ScreenUtil;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.jn.yyz.practise.model.bean.TestRankingBean;

import java.util.List;

public class PractiseRankAdapter extends RecyclerView.Adapter<PractiseRankAdapter.RankHolder> {

    private Context context;
    private List<TestRankingBean.DataDTO> list;

    public PractiseRankAdapter(Context context, List<TestRankingBean.DataDTO> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RankHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPractiseRankBinding binding = ItemPractiseRankBinding.inflate(LayoutInflater.from(context),parent,false);
        return new RankHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RankHolder holder, int position) {
        if (holder==null){
            return;
        }

        TestRankingBean.DataDTO rankData = list.get(position);
        //设置间距
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        params.setMargins(ScreenUtil.dip2px(context,10),ScreenUtil.dip2px(context,5),ScreenUtil.dip2px(context,10),ScreenUtil.dip2px(context,5));
        holder.itemView.setLayoutParams(params);
        holder.itemView.setBackgroundResource(R.drawable.shape_bg_white);
        //设置显示
        LibGlide3Util.loadCircleImg(context,"http://static1.iyuba.cn/uc_server/"+rankData.getImage(),R.drawable.ic_w_head,holder.pic);
        holder.name.setText(rankData.getUsername());
        holder.experience.setText(rankData.getExp()+"经验");
        //设置排行榜序号显示
        if (position==0){
            holder.indexText.setVisibility(View.INVISIBLE);
            holder.indexPic.setVisibility(View.VISIBLE);
            holder.indexPic.setImageResource(com.jn.yyz.practise.R.drawable.top1);
        }else if (position==1){
            holder.indexText.setVisibility(View.INVISIBLE);
            holder.indexPic.setVisibility(View.VISIBLE);
            holder.indexPic.setImageResource(com.jn.yyz.practise.R.drawable.top2);
        }else if (position==2){
            holder.indexText.setVisibility(View.INVISIBLE);
            holder.indexPic.setVisibility(View.VISIBLE);
            holder.indexPic.setImageResource(com.jn.yyz.practise.R.drawable.top3);
        }else {
            holder.indexPic.setVisibility(View.GONE);
            holder.indexText.setVisibility(View.VISIBLE);
            holder.indexText.setText(String.valueOf(rankData.getRanking()));
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class RankHolder extends RecyclerView.ViewHolder{

        private TextView indexText;
        private ImageView indexPic;

        private ImageView pic;
        private TextView name;
        private TextView grade;
        private TextView experience;

        public RankHolder(ItemPractiseRankBinding binding){
            super(binding.getRoot());

            indexText = binding.indexText;
            indexPic = binding.indexPic;

            pic = binding.pic;
            name = binding.name;
            grade = binding.grade;
            grade.setVisibility(View.GONE);
            experience = binding.experience;
        }
    }

    //刷新数据
    public void refreshData(List<TestRankingBean.DataDTO> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //增加数据
    public void addData(List<TestRankingBean.DataDTO> addList){
        this.list.addAll(addList);
        notifyDataSetChanged();
    }

    //接口
}
