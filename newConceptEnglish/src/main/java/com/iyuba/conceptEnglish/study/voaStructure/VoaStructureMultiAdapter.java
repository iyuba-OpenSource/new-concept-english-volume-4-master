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

import java.util.ArrayList;
import java.util.List;

/**
 * @desction:
 * @date: 2023/3/20 19:01
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class VoaStructureMultiAdapter extends RecyclerView.Adapter<VoaStructureMultiAdapter.VSNItemHolder> {

    private Context context;
    private List<VoaStructureKVBean> list;

    private List<RecyclerView.ViewHolder> holderList = new ArrayList<>();

    public VoaStructureMultiAdapter(Context context, List<VoaStructureKVBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VSNItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise_edittext,parent,false);
        return new VSNItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VSNItemHolder holder, int position) {
        if (holder==null){
            return;
        }

        VoaStructureKVBean bean = list.get(position);
        holder.index.setText(String.valueOf(position+1));
        holder.editText.setText(bean.getText());
        holder.editText.setBackgroundResource(bean.getResId());
        if (bean.isInput()){
            holder.editText.setEnabled(true);
        }else {
            holder.editText.setEnabled(false);
        }

        //存储
        holderList.add(holder);
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }


    class VSNItemHolder extends RecyclerView.ViewHolder{

        public TextView index;
        public EditText editText;

        public VSNItemHolder(View view){
            super(view);

            index = view.findViewById(R.id.index);
            editText = view.findViewById(R.id.answer);
        }
    }

    //刷新数据
    public void refreshData(List<VoaStructureKVBean> refreshList){
        this.holderList.clear();
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //获取holder
    public List<RecyclerView.ViewHolder> getHolder(){
        return holderList;
    }
}
