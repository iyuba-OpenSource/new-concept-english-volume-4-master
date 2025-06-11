package com.iyuba.core.lil.view;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.permissions.XXPermissions;
import com.iyuba.lib.R;

import java.util.List;

/**
 * @title:
 * @date: 2023/11/27 10:01
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class PermissionMsgAdapter extends RecyclerView.Adapter<PermissionMsgAdapter.PermissionViewHolder> {

    private Context context;
    private List<Pair<String,Pair<String,String>>> list;

    public PermissionMsgAdapter(Context context, List<Pair<String,Pair<String,String>>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PermissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_permission_msg,parent,false);
        return new PermissionViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull PermissionViewHolder holder, int position) {
        if (holder==null){
            return;
        }

        Pair<String,String> pair = list.get(position).second;
        String showText = pair.first+"："+pair.second;
        holder.textView.setText(showText);
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class PermissionViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;

        public PermissionViewHolder(View rootView){
            super(rootView);

            textView = rootView.findViewById(R.id.showText);
        }
    }

    //刷新数据显示
    public void refreshData(List<Pair<String,Pair<String,String>>> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }
}
