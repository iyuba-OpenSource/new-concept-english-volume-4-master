package com.iyuba.conceptEnglish.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.databinding.DialogStudyReportItemWordsBinding;
import com.iyuba.core.common.data.model.VoaWord2;

import java.util.List;

/**
 * @title: 听力学习报告的全部单词显示
 * @date: 2023/11/6 17:40
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ListenStudyReportAllAdapter extends RecyclerView.Adapter<ListenStudyReportAllAdapter.AllHolder> {

    private Context context;
    private List<VoaWord2> list;

    public ListenStudyReportAllAdapter(Context context, List<VoaWord2> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AllHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DialogStudyReportItemWordsBinding binding = DialogStudyReportItemWordsBinding.inflate(LayoutInflater.from(context),parent,false);
        return new AllHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AllHolder holder, int position) {
        if (holder==null){
            return;
        }

        VoaWord2 voaWord2 = list.get(position);
        holder.wordView.setText(voaWord2.word);
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class AllHolder extends RecyclerView.ViewHolder{

        private TextView wordView;

        public AllHolder(DialogStudyReportItemWordsBinding binding){
            super(binding.getRoot());

            wordView = binding.word;
        }
    }
}
