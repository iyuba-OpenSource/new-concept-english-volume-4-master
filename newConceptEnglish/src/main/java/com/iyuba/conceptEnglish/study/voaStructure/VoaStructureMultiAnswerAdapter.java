package com.iyuba.conceptEnglish.study.voaStructure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;

import java.util.List;

/**
 * @desction:
 * @date: 2023/3/21 14:17
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class VoaStructureMultiAnswerAdapter extends RecyclerView.Adapter<VoaStructureMultiAnswerAdapter.AnswerHolder> {

    private Context context;
    private List<String> list;

    public VoaStructureMultiAnswerAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AnswerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise_edittext,parent,false);
        return new AnswerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerHolder holder, int position) {
        if (holder==null){
            return;
        }

        String answer = list.get(position);
        holder.indexView.setText(String.valueOf(position+1));
        holder.editText.setBackgroundResource(R.drawable.gray_item);
        holder.editText.setText(answer);
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class AnswerHolder extends RecyclerView.ViewHolder{

        private TextView indexView;
        private EditText editText;

        public AnswerHolder(View view){
            super(view);

            indexView = view.findViewById(R.id.index);
            editText = view.findViewById(R.id.answer);
            editText.setEnabled(false);
        }
    }

    //刷新数据
    public void refreshData(List<String> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }
}
