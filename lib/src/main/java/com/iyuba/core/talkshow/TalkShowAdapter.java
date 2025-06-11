package com.iyuba.core.talkshow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.data.model.TalkLesson;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.util.LibGlide3Util;
import com.iyuba.core.me.activity.NewVipCenterActivity;
import com.iyuba.core.talkshow.lesson.LessonPlayActivity;
import com.iyuba.core.talkshow.myTalk.MyTalkActivity;
import com.iyuba.core.talkshow.talkClass.TalkClassActivity;
import com.iyuba.lib.R;

import java.util.ArrayList;
import java.util.List;

public class TalkShowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MAX_NO_VIP_LESSON = 3;
    private final int TOP_TYPE = 2000;
    private final int COMMON_TYPE = 2111;


    private List<TalkLesson> mItemList;

    public void setData(List<TalkLesson> itemList) {
        if (itemList == null) {
            mItemList = new ArrayList<>();
        } else {
            mItemList = itemList;
        }
        //mItemList.add(0, null);
        notifyDataSetChanged();
    }

    public void addData(List<TalkLesson> itemList) {
        mItemList.addAll(itemList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
//        if (position == 0) {
//            return TOP_TYPE;
//        }
        return COMMON_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//        switch (viewType) {
//            case TOP_TYPE:
//                return new TitleHolder(LayoutInflater.from(viewGroup.getContext())
//                        .inflate(R.layout.item_talk_top, viewGroup, false));
//            case COMMON_TYPE:
//                return new ListHolder(LayoutInflater.from(viewGroup.getContext())
//                        .inflate(R.layout.item_talk_lesson, viewGroup, false));
//            default:
        return new ListHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_talk_lesson, viewGroup, false));
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ListHolder) {
            ListHolder listHolder = (ListHolder) viewHolder;
            listHolder.setData(mItemList.get(i));
            listHolder.setListener(mItemList.get(i), i);
        } else if (viewHolder instanceof TitleHolder) {
            TitleHolder titleHolder = (TitleHolder) viewHolder;
            titleHolder.setListener();
        }
    }

    @Override
    public int getItemCount() {
        if (mItemList != null) {
            return mItemList.size();
        }
        return 0;
    }

    public class TitleHolder extends RecyclerView.ViewHolder {
        LinearLayout llLesson;
        LinearLayout llMyTalk;

        public TitleHolder(@NonNull View itemView) {
            super(itemView);

            llLesson = itemView.findViewById(R.id.ll_lesson);
            llMyTalk = itemView.findViewById(R.id.ll_my_talk);
        }

        public void setListener() {
            llLesson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) itemView.getContext()).startActivityForResult(new Intent(itemView.getContext(),
                            TalkClassActivity.class), 99);
                }
            });
            llMyTalk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), MyTalkActivity.class));
                }
            });
        }

    }

    public class ListHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView tvTitle;
        TextView tvTitle2;
        TextView tvReadNum;

        public ListHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTitle2 = itemView.findViewById(R.id.tv_title2);
            tvReadNum = itemView.findViewById(R.id.tv_read_num);
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
            tvReadNum.setText("浏览量" + data.ReadCount);
        }

        public void setListener(final TalkLesson data, final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //先判断是否登录
                    if (!UserInfoManager.getInstance().isLogin()) {
                        LoginUtil.startToLogin(itemView.getContext());
                        return;
                    }

                    //startLessonPlay(data);
                    if (position <= MAX_NO_VIP_LESSON - 1 || UserInfoManager.getInstance().isVip()) {
                        startLessonPlay(data);
                    } else {//不是会员，且所看课程数大于3
                        new AlertDialog.Builder(itemView.getContext())
                                .setTitle("提示")
                                .setMessage("开通会员可以体验全部配音课程")
                                .setNeutralButton("立即开通", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        NewVipCenterActivity.start(itemView.getContext(), NewVipCenterActivity.VIP_APP);
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    }
                }
            });
        }

        private void startLessonPlay(TalkLesson data) {
            List<TalkLesson> lessons = new ArrayList<>(mItemList);//新建 list对象！！！！
            //lessons.remove(0);
            itemView.getContext().startActivity(LessonPlayActivity.buildIntent(itemView.getContext(),
                    data, lessons));
        }

    }
}
