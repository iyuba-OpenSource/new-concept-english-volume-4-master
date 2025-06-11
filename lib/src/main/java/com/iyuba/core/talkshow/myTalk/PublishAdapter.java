package com.iyuba.core.talkshow.myTalk;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.data.model.Ranking;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.core.talkshow.lesson.watch.WatchDubbingActivity;
import com.iyuba.lib.R;

import java.util.List;

public class PublishAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Ranking> mItemList;

    public boolean isShowDelete;

    public void setData(List<Ranking> itemList) {
        mItemList = itemList;
        notifyDataSetChanged();
    }

    public void addData(List<Ranking> itemList) {
        mItemList.addAll(itemList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ListHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_talk_publish_my, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ListHolder listHolder = (ListHolder) viewHolder;
        listHolder.setData(mItemList.get(i));
        listHolder.setListener(mItemList.get(i),i);
    }

    @Override
    public int getItemCount() {
        return mItemList!=null?mItemList.size():0;
    }

    public class ListHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView tvTitle;
        TextView tvTitle2;
        TextView tvAgreeNum;
        ImageView ivDelete;

        public ListHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTitle2 = itemView.findViewById(R.id.tv_title2);
            tvAgreeNum = itemView.findViewById(R.id.tv_agree_num);
            ivDelete = itemView.findViewById(R.id.delete_iv);
        }

        public void setData(Ranking data) {
            String classImage = "http://"+Constant.staticStr+ Constant.IYUBA_CN+"images/voa/"+data.topicId+".jpg";

            /*Drawable drawable = itemView.getContext().getResources().getDrawable(R.drawable.loading);
            Glide.with(itemView.getContext()).load(classImage)
                    .asBitmap()
                    .placeholder(drawable)
                    .error(drawable)
                    .dontAnimate()  //防止加载网络图片变形
                    .into(image);*/
            LibGlide3Util.loadImg(itemView.getContext(),classImage,R.drawable.loading,image);
            tvTitle.setText(data.titleCn);
            tvTitle2.setText(data.createDate);
            tvAgreeNum.setText(String.valueOf(data.agreeCount));

            if (isShowDelete){
                ivDelete.setVisibility(View.VISIBLE);
                if (data.isDelete){
                    ivDelete.setImageDrawable(itemView.getResources().getDrawable(R.drawable.check_on));
                }else {
                    ivDelete.setImageDrawable(itemView.getResources().getDrawable(R.drawable.checkbox_unchecked));
                }
            }else {
                ivDelete.setVisibility(View.GONE);
            }
        }

        public void setListener(final Ranking data, final int position) {
            final int uid = UserInfoManager.getInstance().getUserId();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShowDelete){
                        data.isDelete =!data.isDelete;
                        //notifyItemChanged(position);
                        notifyDataSetChanged();
                    }else {
                        TalkLesson lesson = new TalkLesson();
                        lesson.Id = String.valueOf(data.topicId);
                        Intent intent = WatchDubbingActivity.buildIntent(itemView.getContext(), data, lesson, uid);
                        itemView.getContext().startActivity(intent);
                    }
                }
            });
        }

    }
}
