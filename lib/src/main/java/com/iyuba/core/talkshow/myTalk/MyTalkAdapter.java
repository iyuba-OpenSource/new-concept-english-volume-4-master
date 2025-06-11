package com.iyuba.core.talkshow.myTalk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.core.talkshow.lesson.LessonPlayActivity;
import com.iyuba.lib.R;

import java.util.List;

public class MyTalkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<TalkLesson> mItemList;

    public boolean isShowDelete;

    public void setData(List<TalkLesson> itemList) {
        mItemList = itemList;
        notifyDataSetChanged();
    }

    public void addData(List<TalkLesson> itemList) {
        mItemList.addAll(itemList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ListHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_talk_lesson_my, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ListHolder listHolder = (ListHolder) viewHolder;
        listHolder.setData(mItemList.get(i));
        listHolder.setListener(mItemList.get(i));
    }

    @Override
    public int getItemCount() {
        if (mItemList != null) {
            return mItemList.size();
        }
        return 0;
    }

    public class ListHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView tvTitle;
        TextView tvTitle2;
        ImageView ivDelete;


        public ListHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTitle2 = itemView.findViewById(R.id.tv_title2);
            ivDelete = itemView.findViewById(R.id.delete_iv);
        }

        public void setData(TalkLesson data) {
            /*Drawable drawable = itemView.getContext().getResources().getDrawable(R.drawable.loading);
            Glide.with(itemView.getContext()).load(data.Pic)
                    .asBitmap()
                    .placeholder(drawable)
                    .error(drawable)
                    .dontAnimate()  //防止加载网络图片变形
                    .into(image);*/
            LibGlide3Util.loadImg(itemView.getContext(),data.Pic,R.drawable.loading,image);
            tvTitle.setText(data.Title);
            tvTitle2.setText(data.DescCn);

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

        public void setListener(final TalkLesson data) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShowDelete){
                        data.isDelete =!data.isDelete;
                        notifyDataSetChanged();
                    }else {
                        itemView.getContext().startActivity(LessonPlayActivity.buildIntent(itemView.getContext(),
                                data, mItemList));
                    }
                }
            });

        }

    }
}
