package com.iyuba.conceptEnglish.activity.guide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/16 09:21
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class GuideAdapter extends RecyclerView.Adapter<GuideAdapter.GuideImageHolder> {

    private Context context;
    private List<Integer> list;

    public GuideAdapter(Context context, List<Integer> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public GuideImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_guide_image,parent,false);
        return new GuideImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuideImageHolder holder, int position) {
        if (holder==null){
            return;
        }

        int resId = list.get(position);
        holder.imageView.setImageResource(resId);
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class GuideImageHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        public GuideImageHolder(View view){
            super(view);

            imageView = view.findViewById(R.id.image);
        }
    }
}
