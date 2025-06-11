package com.iyuba.conceptEnglish.lil.fix.junior.choose;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ItemChooseJuniorPublishBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;

import java.util.List;
import java.util.Map;

/**
 * @title: 大类型的适配器
 * @date: 2023/5/22 09:43
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class JuniorChoosePublishAdapter extends RecyclerView.Adapter<JuniorChoosePublishAdapter.PublishHolder> {

    private Context context;
    private List<Pair<String,List<Pair<String,String>>>> list;

    //选中的数据
    private int  selectIndex = 0;

    public JuniorChoosePublishAdapter(Context context, List<Pair<String, List<Pair<String, String>>>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PublishHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChooseJuniorPublishBinding binding = ItemChooseJuniorPublishBinding.inflate(LayoutInflater.from(context),parent,false);
        return new PublishHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PublishHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        String text = list.get(position).first;
        holder.textView.setText(text);

        if (selectIndex == position){
            holder.textView.setBackgroundResource(R.drawable.shape_corner_theme_10dp);
            holder.textView.setTextColor(ResUtil.getInstance().getColor(R.color.white));
        }else {
            holder.textView.setBackgroundResource(0);
            holder.textView.setTextColor(ResUtil.getInstance().getColor(R.color.colorPrimary));
        }

        holder.itemView.setOnClickListener(v->{
            if (selectIndex==position){
                return;
            }

//            selectIndex = position;
//            notifyDataSetChanged();

            if (listener!=null){
                listener.onClick(position,list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class PublishHolder extends RecyclerView.ViewHolder{

        private TextView textView;

        public PublishHolder(ItemChooseJuniorPublishBinding binding){
            super(binding.getRoot());

            textView = binding.textView;
        }
    }

    //刷新数据和位置
    public void refreshDataAndIndex(List<Pair<String,List<Pair<String,String>>>> refreshList,int index){
        this.list = refreshList;
        this.selectIndex = index;
        notifyDataSetChanged();
    }

    //回调
//    private OnSimpleClickListener<Pair<String,List<Pair<String,String>>>> listener;
//
//    public void setListener(OnSimpleClickListener<Pair<String,List<Pair<String,String>>>> listener) {
//        this.listener = listener;
//    }

    private onPublishClickListener listener;

    public interface onPublishClickListener{
        void onClick(int position,Pair<String,List<Pair<String,String>>> pair);
    }

    public void setListener(onPublishClickListener listener) {
        this.listener = listener;
    }

    //刷新选中的位置
    public void refreshSelectIndex(int index){
        this.selectIndex = index;
        notifyDataSetChanged();
    }
}
