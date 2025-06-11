package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.search;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ListitemVoaHomeBinding;
import com.iyuba.conceptEnglish.lil.fix.common_fix.data.library.TypeLibrary;
import com.iyuba.conceptEnglish.lil.fix.common_fix.manager.ConceptBookChooseManager;
import com.iyuba.conceptEnglish.sqlite.mode.Voa;
import com.iyuba.conceptEnglish.util.CommonUtils;
import com.iyuba.core.lil.util.LibGlide3Util;

import java.util.List;

/**
 * @title:
 * @date: 2023/11/16 16:49
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class SearchArticleAdapter extends RecyclerView.Adapter<SearchArticleAdapter.ArticleHolder> {

    private Context context;
    private List<Voa> list;

    public SearchArticleAdapter(Context context, List<Voa> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ArticleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemVoaHomeBinding binding = ListitemVoaHomeBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ArticleHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleHolder holder, int position) {
        if (holder==null){
            return;
        }

        Voa tempVoa = list.get(position);
        if (TextUtils.isEmpty(tempVoa.pic)){
            holder.voaPic.setImageResource(R.drawable.shape_btn_bg);

            int index = tempVoa.voaId % 1000;
            holder.voaIndex.setVisibility(View.VISIBLE);
            if (ConceptBookChooseManager.getInstance().getBookType().equals(TypeLibrary.BookType.conceptJunior)) {
                holder.voaIndex.setText(String.valueOf(CommonUtils.getUnitFromTitle(tempVoa.title)));
            } else {
                holder.voaIndex.setText(String.valueOf(index));
            }
        }else {
            holder.voaIndex.setVisibility(View.GONE);
            LibGlide3Util.loadImg(context,tempVoa.pic,R.drawable.shape_btn_bg, holder.voaPic);
        }

        holder.title.setText(tempVoa.title);
        holder.titleCn.setText(tempVoa.titleCn);

        holder.itemView.setOnClickListener(v->{
            if (onSearchArticleItemListener!=null){
                onSearchArticleItemListener.onClick(tempVoa);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ArticleHolder extends RecyclerView.ViewHolder{

        private TextView lesson;
        private LinearLayout bottomLayout;
        private ImageView cbCheck;
        private RelativeLayout download;

        private LinearLayout style1Layout;
        private RelativeLayout style2Layout;
        private ImageView voaPic;
        private TextView voaIndex;
        private TextView title;
        private TextView titleCn;

        public ArticleHolder(ListitemVoaHomeBinding binding){
            super(binding.getRoot());

            lesson = binding.lesson;
            lesson.setVisibility(View.GONE);
            bottomLayout = binding.bottomLayout;
            bottomLayout.setVisibility(View.GONE);
            cbCheck = binding.checkBoxIsDelete;
            cbCheck.setVisibility(View.GONE);
            download = binding.download;
            download.setVisibility(View.GONE);

            style1Layout = binding.style1Layout;
            style1Layout.setVisibility(View.GONE);
            style2Layout = binding.style2Layout;
            style2Layout.setVisibility(View.VISIBLE);
            voaIndex = binding.voaIndex;
            voaPic = binding.voaPic;
            title = binding.title;
            titleCn = binding.titleCn;
        }
    }

    //回调接口
    private OnSearchArticleItemListener onSearchArticleItemListener;

    public interface OnSearchArticleItemListener{
        void onClick(Voa voa);
    }

    public void setOnSearchArticleItemListener(OnSearchArticleItemListener onSearchArticleItemListener) {
        this.onSearchArticleItemListener = onSearchArticleItemListener;
    }

    //刷新数据
    public void refreshList(List<Voa> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }
}
