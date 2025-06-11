package com.iyuba.conceptEnglish.lil.concept_other.me_localNews.novel;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ItemChapterNovelBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.local.entity.ChapterCollectEntity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title:
 * @date: 2023/7/7 11:07
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NovelLocalNewsAdapter extends RecyclerView.Adapter<NovelLocalNewsAdapter.NovelHolder> {

    private Context context;
    private List<ChapterCollectEntity> list;

    //编辑操作
    private boolean isEditStatus = false;
    //保存的数据
    private Map<Integer,ChapterCollectEntity> editMap = new HashMap<>();

    public NovelLocalNewsAdapter(Context context, List<ChapterCollectEntity> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NovelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChapterNovelBinding binding = ItemChapterNovelBinding.inflate(LayoutInflater.from(context),parent,false);
        return new NovelHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NovelHolder holder, int position) {
        if (holder==null){
            return;
        }

        ChapterCollectEntity bean = list.get(position);
        holder.indexView.setVisibility(View.GONE);
        String chapterName = bean.title;
        if (!TextUtils.isEmpty(chapterName)){
            chapterName = chapterName.replace("\r\n","").trim();
        }else {
            chapterName = "";
        }
        String chapterDesc = bean.desc;
        if (!TextUtils.isEmpty(chapterDesc)){
            chapterDesc = chapterDesc.replace("\r\n","").trim();
        }else {
            chapterDesc = "";
        }

        //这里因为level3的数据存在问题，多了一个\r\n(数据存在问题真是服气了)
        holder.titleView.setText(chapterName);
        holder.titleCnView.setText(chapterDesc);

        holder.itemView.setOnClickListener(v->{
            if (listener!=null){
                listener.onClick(bean);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (longListener!=null){
                    longListener.onClick(bean);
                }
                return true;
            }
        });


        //设置编辑状态样式
        if (isEditStatus){
            holder.editView.setVisibility(View.VISIBLE);

            ChapterCollectEntity editData = editMap.get(position);
            if (editData!=null){
                holder.editView.setImageResource(R.drawable.check_box_checked);
            }else {
                holder.editView.setImageResource(R.drawable.check_box);
            }
        }else {
            holder.editView.setVisibility(View.GONE);
        }
        holder.editView.setOnClickListener(v->{
            ChapterCollectEntity editData = editMap.get(position);
            if (editData!=null){
                holder.editView.setImageResource(R.drawable.check_box);
                editMap.remove(position);
            }else {
                holder.editView.setImageResource(R.drawable.check_box_checked);
                editMap.put(position,bean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class NovelHolder extends RecyclerView.ViewHolder{

        private ImageView editView;
        private TextView indexView;
        private TextView titleView;
        private TextView titleCnView;

        public NovelHolder(ItemChapterNovelBinding binding){
            super(binding.getRoot());

            editView = binding.editView;
            indexView = binding.index;
            titleView = binding.title;
            titleCnView = binding.titleCn;
        }
    }

    //刷新数据
    public void refreshData(List<ChapterCollectEntity> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //刷新编辑数据
    public void refreshEditData(List<ChapterCollectEntity> refreshList){
        this.list = refreshList;
        this.isEditStatus = false;
        editMap.clear();
        notifyDataSetChanged();
    }

    //设置编辑状态
    public void refreshEditStatus(boolean isEdit){
        this.isEditStatus = isEdit;
        editMap.clear();
        notifyDataSetChanged();
    }

    //获取编辑数据
    public List<ChapterCollectEntity> getEditList(){
        List<ChapterCollectEntity> editList = new ArrayList<>();
        if (editMap!=null&&editMap.keySet().size()>0){
            for (Integer position:editMap.keySet()){
                editList.add(editMap.get(position));
            }
        }
        return editList;
    }

    //回调
    public OnSimpleClickListener<ChapterCollectEntity> listener;

    public void setListener(OnSimpleClickListener<ChapterCollectEntity> listener) {
        this.listener = listener;
    }

    //长按回调
    public OnSimpleClickListener<ChapterCollectEntity> longListener;

    public void setLongListener(OnSimpleClickListener<ChapterCollectEntity> longListener) {
        this.longListener = longListener;
    }
}
