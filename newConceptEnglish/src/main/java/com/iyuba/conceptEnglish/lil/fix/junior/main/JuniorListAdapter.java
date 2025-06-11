package com.iyuba.conceptEnglish.lil.fix.junior.main;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ItemChapterJuniorBinding;
import com.iyuba.conceptEnglish.lil.concept_other.util.HelpUtil;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.bean.BookChapterBean;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.listener.OnSimpleClickListener;
import com.iyuba.conceptEnglish.lil.fix.common_fix.util.ImageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title:
 * @date: 2023/5/19 18:17
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class JuniorListAdapter extends RecyclerView.Adapter<JuniorListAdapter.JuniorHolder> {

    private Context context;
    private List<BookChapterBean> list;

    //编辑操作
    private boolean isEditStatus = false;
    //保存的数据
    private Map<Integer,BookChapterBean> editMap = new HashMap<>();

    public JuniorListAdapter(Context context, List<BookChapterBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public JuniorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChapterJuniorBinding binding = ItemChapterJuniorBinding.inflate(LayoutInflater.from(context),parent,false);
        return new JuniorHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JuniorHolder holder, int position) {
        if (holder==null){
            return;
        }

        BookChapterBean bean = list.get(position);
        ImageUtil.loadRoundImg(bean.getPicUrl(),0,holder.pic);
        holder.title.setText(HelpUtil.transTitleStyle(bean.getTitleEn()));
        holder.desc.setText(bean.getTitleCn());

        holder.itemView.setOnClickListener(v->{
            if (listener!=null){
                listener.onClick(new Pair<>(position,bean));
            }
        });

        //设置编辑状态样式
        if (isEditStatus){
            holder.editView.setVisibility(View.VISIBLE);

            BookChapterBean editData = editMap.get(position);
            if (editData!=null){
                holder.editView.setImageResource(R.drawable.check_box_checked);
            }else {
                holder.editView.setImageResource(R.drawable.check_box);
            }
        }else {
            holder.editView.setVisibility(View.GONE);
        }
        holder.editView.setOnClickListener(v->{
            BookChapterBean editData = editMap.get(position);
            if (editData!=null){
                holder.editView.setImageResource(R.drawable.check_box);
                editMap.remove(position);
            }else {
                holder.editView.setImageResource(R.drawable.check_box_checked);
                editMap.put(position,bean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class JuniorHolder extends RecyclerView.ViewHolder{

        private ImageView editView;

        private ConstraintLayout contentLayout;
        private ImageView pic;
        private TextView title;
        private TextView desc;
        private ImageView download;

        private ConstraintLayout bottomLayout;
        private LinearLayout readLayout;
        private ImageView readPic;
        private TextView readText;

        private LinearLayout evalLayout;
        private ImageView evalPic;
        private TextView evalText;

        private LinearLayout wordLayout;
        private ImageView wordPic;
        private TextView wordText;

        private LinearLayout mocLayout;
        private ImageView mocPic;
        private TextView mocText;

        private LinearLayout exerciseLayout;
        private ImageView exercisePic;
        private TextView exerciseText;

        public JuniorHolder(ItemChapterJuniorBinding binding){
            super(binding.getRoot());

            editView = binding.editView;

            contentLayout = binding.contentLayout;
            pic = binding.pic;
            title = binding.title;
            desc = binding.desc;
            download = binding.download;

            bottomLayout = binding.bottomLayout;
            readLayout = binding.readLayout;
            readPic = binding.readPic;
            readText = binding.readText;

            evalLayout = binding.evalLayout;
            evalPic = binding.evalPic;
            evalText = binding.evalText;

            wordLayout = binding.wordLayout;
            wordPic = binding.wordPic;
            wordText = binding.wordText;

            mocLayout = binding.mocLayout;
            mocPic = binding.mocPic;
            mocText = binding.mocText;

            exerciseLayout = binding.exerciseLayout;
            exercisePic = binding.exercisePic;
            exerciseText = binding.exerciseText;
        }
    }

    //刷新数据
    public void refreshData(List<BookChapterBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //刷新编辑数据
    public void refreshEditData(List<BookChapterBean> refreshList){
        this.list = refreshList;
        this.isEditStatus = false;
        editMap.clear();
        notifyDataSetChanged();
    }

    //设置编辑状态
    public void refreshEditStatus(boolean isEdit){
        this.isEditStatus = isEdit;
        editMap.clear();
        notifyDataSetChanged();
    }

    //获取编辑数据
    public List<BookChapterBean> getEditList(){
        List<BookChapterBean> editList = new ArrayList<>();
        if (editMap!=null&&editMap.keySet().size()>0){
            for (Integer position:editMap.keySet()){
                editList.add(editMap.get(position));
            }
        }
        return editList;
    }

    //回调
    public OnSimpleClickListener<Pair<Integer,BookChapterBean>> listener;

    public void setListener(OnSimpleClickListener<Pair<Integer,BookChapterBean>> listener) {
        this.listener = listener;
    }
}
