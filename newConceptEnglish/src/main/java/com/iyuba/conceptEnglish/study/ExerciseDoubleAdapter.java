package com.iyuba.conceptEnglish.study;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.iyuba.conceptEnglish.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExerciseDoubleAdapter extends RecyclerView.Adapter<ExerciseDoubleAdapter.ViewHolder> {

    private List<String> list;
    private Context mContext;


    public ExerciseDoubleAdapter(List<String> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_exercise_double, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        Glide.with(mContext).load(list.get(i)).placeholder(R.drawable.failed_image).dontAnimate()
                .into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_view)
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
