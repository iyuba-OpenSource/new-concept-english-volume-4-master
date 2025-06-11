package com.iyuba.core.talkshow.lesson.recommend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.iyuba.core.talkshow.lesson.LessonPlayActivity;
import com.iyuba.lib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/29 0029.
 */

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.RecommendHolder> {

    private VoaCallback mVoaCallback;
    private List<TalkLesson> mVoaList;
    private List<TalkLesson> mVoaListAll;

    public RecommendAdapter() {
        mVoaListAll = new ArrayList<>();
        this.mVoaList = new ArrayList<>();
    }

    @Override
    public RecommendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voa, parent, false);
        return new RecommendHolder(view);
    }

    @Override
    public void onBindViewHolder(RecommendHolder holder, int position) {

        try {
            TalkLesson voa = mVoaList.get(position);
            Glide.with(holder.itemView.getContext())
                    .load(voa.Pic)
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .into(holder.pic);

            if(voa.Title != null) {
                holder.titleCn.setText(voa.Title);
            }
            holder.readCount.setText(voa.ReadCount);
            holder.setListener(voa);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mVoaList.size();
    }

    public void setVoaCallback(VoaCallback mVoaCallback) {
        this.mVoaCallback = mVoaCallback;
    }

    public void setVoaList(List<TalkLesson> mVoaList,String voaId) {
        mVoaListAll = mVoaList;
        this.mVoaList.clear();
        for (TalkLesson talkLesson:mVoaList){
            if (!talkLesson.Id.equals(voaId)){
                this.mVoaList.add(talkLesson);
            }
        }
    }

    class RecommendHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView titleCn;
        TextView readCount;

        RecommendHolder(View itemView) {
            super(itemView);

            pic = itemView.findViewById(R.id.pic);
            titleCn = itemView.findViewById(R.id.titleCn);
            readCount = itemView.findViewById(R.id.readCount);
        }

        public void setListener(final TalkLesson voa){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!UserInfoManager.getInstance().isVip()){
                        new AlertDialog.Builder(itemView.getContext())
                                .setTitle("提示")
                                .setMessage("开通会员可以体验全部配音课程")
                                .setNeutralButton("立即开通", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        NewVipCenterActivity.start(itemView.getContext(), NewVipCenterActivity.VIP_APP);
                                    }
                                })
                                .setNegativeButton("取消",null)
                                .show();
                    }else {
                        itemView.getContext().startActivity(LessonPlayActivity.buildIntent(itemView.getContext(),
                                voa, mVoaListAll));//给的应该是全的
                    }
                }
            });
        }
    }

    interface VoaCallback {
        void onVoaClicked(TalkLesson voa);
    }
}
