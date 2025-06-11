package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.word.wordList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.databinding.ItemPassWordCollectBinding;
import com.iyuba.conceptEnglish.databinding.ItemWordListBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.WordBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.util.AnimUtil;

import java.util.List;

/**
 * @title:
 * @date: 2023/5/11 18:48
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordListHolder> {

    private Context context;
    private List<WordBean> list;

    public WordListAdapter(Context context, List<WordBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WordListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPassWordCollectBinding binding = ItemPassWordCollectBinding.inflate(LayoutInflater.from(context),parent,false);
        return new WordListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WordListHolder holder, int position) {
        if (holder==null){
            return;
        }

        WordBean bean = list.get(position);
        holder.word.setText(bean.getWord());
        String pron = TextUtils.isEmpty(bean.getPron())?"":"["+bean.getPron()+"]";
        holder.pron.setText(pron);

        holder.def.setText(bean.getDef());

        //进行反转操作
        holder.frontLayout.setOnClickListener(v->{
            AnimUtil.FlipAnimatorXViewShow(holder.frontLayout,holder.backLayout,500);
        });
        holder.backLayout.setOnClickListener(v->{
            AnimUtil.FlipAnimatorXViewShow(holder.backLayout,holder.frontLayout,500);
        });
        holder.detail.setOnClickListener(v->{
            if (listener!=null){
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class WordListHolder extends RecyclerView.ViewHolder{

        //正面
        private LinearLayout frontLayout;
        private TextView word;
        private TextView pron;

        //反面
        private LinearLayout backLayout;
        private TextView def;
        private ImageView detail;


        public WordListHolder(ItemPassWordCollectBinding binding){
            super(binding.getRoot());

            frontLayout = binding.llJp;
            word = binding.txtJp;
            pron = binding.txtPron;

            backLayout = binding.llCh;
            def = binding.txtCh;
            detail = binding.imgDetail;
        }
    }

    //刷新数据
    public void refreshData(List<WordBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //回调
    private OnSimpleClickListener<Integer> listener;

    public void setListener(OnSimpleClickListener<Integer> listener) {
        this.listener = listener;
    }
}
