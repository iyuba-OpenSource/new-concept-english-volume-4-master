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
import com.iyuba.conceptEnglish.databinding.ItemChooseJuniorTypeBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ResUtil;
import com.iyuba.conceptEnglish.lil.fix.common_mvp.util.ToastUtil;

import java.util.List;

/**
 * @title: 小类型的适配器
 * @date: 2023/5/22 09:43
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class JuniorChooseTypeAdapter extends RecyclerView.Adapter<JuniorChooseTypeAdapter.TypeHolder> {

    private Context context;
    private List<Pair<String,String>> list;

    //选中的数据
    private int selectIndex = 0;

    public JuniorChooseTypeAdapter(Context context, List<Pair<String, String>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChooseJuniorTypeBinding binding = ItemChooseJuniorTypeBinding.inflate(LayoutInflater.from(context),parent,false);
        return new TypeHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        String text = list.get(position).second;
        holder.textView.setText(text);

        if (selectIndex == position){
            holder.textView.setBackgroundResource(R.drawable.shape_corner_theme_border_10dp);
            holder.textView.setTextColor(ResUtil.getInstance().getColor(R.color.colorPrimary));
        }else {
            holder.textView.setBackgroundResource(0);
            holder.textView.setTextColor(ResUtil.getInstance().getColor(R.color.black));
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

    class TypeHolder extends RecyclerView.ViewHolder{

        private TextView textView;

        public TypeHolder(ItemChooseJuniorTypeBinding binding){
            super(binding.getRoot());

            textView  = binding.textView;
        }
    }

    //刷新数据和位置
    public void refreshDataAndIndex(List<Pair<String,String>> refreshList,int index){
        this.list = refreshList;
        this.selectIndex = index;
        notifyDataSetChanged();
    }

    //回调
//    private OnSimpleClickListener<Pair<String,String>> listener;
//
//    public void setListener(OnSimpleClickListener<Pair<String, String>> listener) {
//        this.listener = listener;
//    }

    private onTypeClickListener listener;

    public interface onTypeClickListener{
        void onClick(int position,Pair<String,String> pair);
    }

    public void setListener(onTypeClickListener listener) {
        this.listener = listener;
    }


    //当前选中的数据
    public Pair<String,String> getCurSelectData(){
        return list.get(selectIndex);
    }

    //刷新选中的位置
    public void refreshSelectIndex(int index){
        this.selectIndex = index;
        notifyDataSetChanged();
    }
}
