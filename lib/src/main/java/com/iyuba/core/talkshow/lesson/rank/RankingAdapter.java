package com.iyuba.core.talkshow.lesson.rank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.core.common.data.model.Ranking;
import com.iyuba.core.common.widget.circularimageview.CircularImageView;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.lib.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingHolder> {

    private List<Ranking> mRankingList;
    private RankingCallback mRankingCallback;

    private Context context;

    public RankingAdapter(Context mContent) {
        mRankingList = new ArrayList<>();
        this.context = mContent;
    }

    @Override
    public RankingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking_talk_lib, parent, false);
        return new RankingHolder(view);
    }

    @Override
    public void onBindViewHolder(RankingHolder holder, int position) {
        final Ranking ranking = mRankingList.get(position);
        setRanking(holder.tRank, position,holder.itemView.getContext());
//        Glide.with(context)
////                .load(ranking.imgSrc)
////                .centerCrop()
////                .placeholder(R.drawable.loading)
////                .into(holder.iPhoto);
//        ImageLoader.getInstance().displayImage(ranking.imgSrc, holder.iPhoto);
        LibGlide3Util.loadImg(context,ranking.imgSrc,0,holder.iPhoto);
        holder.tThumbs.setText(String.valueOf(ranking.agreeCount));
        holder.tUsername.setText(ranking.userName);
        holder.tTime.setText(ranking.createDate);
        holder.tvScore.setText(Math.round(ranking.score) + "分");
        //不应该进行点赞操作，接口也不好使
//        holder.rlAgree.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mRankingCallback!=null){
//                    mRankingCallback.onClickThumbs(ranking.id);
//                }
//            }
//        });

        holder.rankLayout.setOnClickListener(v->{
            mRankingCallback.onClickLayout(ranking, position);
        });
    }

    private void setRanking(TextView tRank, int position,Context context) {
        switch (position) {
            case 0:
                tRank.setText("");
                tRank.setBackground(context.getResources().getDrawable(R.drawable.rank_first));
                break;
            case 1:
                tRank.setText("");
                tRank.setBackground(context.getResources().getDrawable(R.drawable.rank_second));
                break;
            case 2:
                tRank.setText("");
                tRank.setBackground(context.getResources().getDrawable(R.drawable.rank_third));
                break;
            default:
                tRank.setText(String.valueOf(position+1));
                tRank.setBackground(null);
                break;
        }
    }

    public void setRankingList(List<Ranking> mRankingList) {
        this.mRankingList = mRankingList;
       // Collections.sort(this.mRankingList, new SortByScore());
    }

    public void setRankingCallback(RankingCallback mRankingCallback) {
        this.mRankingCallback = mRankingCallback;
    }

    @Override
    public int getItemCount() {
        return mRankingList.size();
    }

    class RankingHolder extends RecyclerView.ViewHolder {

        TextView tRank;
        CircularImageView iPhoto;
        TextView tUsername;
        TextView tTime;
        TextView tThumbs;
        TextView tvScore;
        RelativeLayout rlAgree;
        RelativeLayout rankLayout;

        RankingHolder(View itemView) {
            super(itemView);

            tRank = itemView.findViewById(R.id.rank);
            iPhoto = itemView.findViewById(R.id.photo);
            tUsername = itemView.findViewById(R.id.username_tv);
            tTime = itemView.findViewById(R.id.time_tv);
            tThumbs = itemView.findViewById(R.id.thumbs_num);
            tvScore = itemView.findViewById(R.id.tv_score);
            rlAgree = itemView.findViewById(R.id.rl_agree);
            rankLayout = itemView.findViewById(R.id.ranking_layout);
        }
    }

    interface RankingCallback {
        void onClickThumbs(int id);

        void onClickLayout(Ranking ranking, int pos);
    }

    //根据分数从高到低进行排序 modified 2018.7.16
    private class SortByScore implements Comparator{
        @Override
        public int compare(Object o1, Object o2) {
            Ranking s1 = (Ranking) o1;
            Ranking s2 = (Ranking) o2;
            if (s1.score < s2.score) //小的在后面
                return 1;
            return -1;
        }
    }
}
