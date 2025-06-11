package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.study.word;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.databinding.ItemFixWordshowBinding;
import com.iyuba.conceptEnglish.databinding.ListitemVoaWordBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.configation.Constant;

import java.util.List;

/**
 * @title:
 * @date: 2023/8/15 11:49
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordShowAdapter extends RecyclerView.Adapter<WordShowAdapter.WordShowHolder> {

    private Context context;
    private List<WordBean> list;

    public WordShowAdapter(Context context, List<WordBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WordShowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFixWordshowBinding binding = ItemFixWordshowBinding.inflate(LayoutInflater.from(context),parent,false);
        return new WordShowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WordShowHolder holder, int position) {
        if (holder==null){
            return;
        }

        WordBean bean = list.get(position);
        holder.word.setText(bean.getWord());
        holder.def.setText(bean.getDef());


        if(!TextUtils.isEmpty(bean.getPron()) && !TextUtils.isEmpty(bean.getPron())
                && !bean.getPron().equals("null")) {
            Typeface mFace = Typeface.createFromAsset(context.getAssets(), "fonts/segoeui.ttf");
            holder.pron.setTypeface(mFace);
            holder.pron.setText(Html.fromHtml("[" + bean.getPron().trim() + "]"));
        } else {
            holder.pron.setText("");
        }

        holder.play.setOnClickListener(v->{
            if (onWordItemClickListener!=null){
                onWordItemClickListener.onPlay(bean);
            }
        });
        holder.itemView.setOnClickListener(v->{
            if (onWordItemClickListener!=null){
                onWordItemClickListener.onItem(bean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class WordShowHolder extends RecyclerView.ViewHolder{

        private ImageView play;
        private TextView word;
        private TextView pron;
        private TextView def;

        public WordShowHolder(ItemFixWordshowBinding binding){
            super(binding.getRoot());

            play = binding.wordSpeaker;
            word = binding.wordKey;
            pron = binding.wordPron;
            def = binding.wordDef;
        }
    }

    //刷新数据
    public void refreshData(List<WordBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //点击回调
    private OnWordItemClickListener onWordItemClickListener;

    interface OnWordItemClickListener{
        //点击播放
        void onPlay(WordBean bean);

        //点击条目
        void onItem(WordBean bean);
    }

    public void setOnWordItemClickListener(OnWordItemClickListener onWordItemClickListener) {
        this.onWordItemClickListener = onWordItemClickListener;
    }
}
