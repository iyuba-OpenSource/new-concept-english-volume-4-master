package com.iyuba.conceptEnglish.lil.concept_other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;

import java.util.List;

/**
 * @desction:
 * @date: 2023/3/23 18:20
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class MainBottomAdapter extends RecyclerView.Adapter<MainBottomAdapter.BottomHolder> {

    private Context context;
    private List<MainBottomBean> list;

    //选中位置
    private int selectIndex = 0;

    public MainBottomAdapter(Context context, List<MainBottomBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BottomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View bottomView = LayoutInflater.from(context).inflate(R.layout.item_main_bottom,parent,false);
        return new BottomHolder(bottomView);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder==null){
            return;
        }

        MainBottomBean bean = list.get(position);
        holder.textView.setText(bean.getText());

        if (selectIndex==position){
            holder.imageView.setImageResource(bean.getNewResId());
            holder.textView.setTextSize(13);
            holder.textView.setTextColor(context.getResources().getColor(R.color.colorPrimary));

            //临时包名设置
//            if ("com.iyuba.peiyin".equals(context.getPackageName())){
//                holder.imageView.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
//            }
        }else {
            holder.imageView.setImageResource(bean.getOldResId());
            holder.textView.setTextColor(Color.parseColor("#8a8a8a"));
            holder.textView.setTextSize(12);

            //临时包名设置
//            if ("com.iyuba.peiyin".equals(context.getPackageName())){
//                holder.imageView.setColorFilter(Color.GRAY);
//            }
        }

        holder.itemView.setOnClickListener(v->{
            this.selectIndex = position;
            notifyDataSetChanged();

            if (onClickListener!=null){
                onClickListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class BottomHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView textView;

        public BottomHolder(View view){
            super(view);

            imageView = view.findViewById(R.id.image_headline);
            textView = view.findViewById(R.id.tv_headline);
        }
    }

    //接口
    public OnClickListener onClickListener;

    public interface OnClickListener{
        void onClick(int position);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //设置当前选中的位置
    public void setIndex(int index){
        this.selectIndex = index;
        notifyDataSetChanged();
    }
}
