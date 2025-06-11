package com.iyuba.conceptEnglish.lil.fix.common_fix.ui.practise.line;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.databinding.ItemPractiseLineBinding;
import com.iyuba.conceptEnglish.sqlite.mode.Book;

import java.util.List;

public class PractiseLineAdapter extends RecyclerView.Adapter<PractiseLineAdapter.LineHolder> {

    private Context context;
    private List<PractiseLineShowBean> list;

    public PractiseLineAdapter(Context context, List<PractiseLineShowBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public LineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPractiseLineBinding binding = ItemPractiseLineBinding.inflate(LayoutInflater.from(context), parent, false);
        return new LineHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LineHolder holder, int position) {
        if (holder == null) {
            return;
        }

        PractiseLineShowBean showBean = list.get(position);
        if (position % 2 == 0) {
            holder.headerPicView.setVisibility(View.GONE);
            holder.headerLessonView.setVisibility(View.VISIBLE);
            holder.headerLessonView.setText("Lesson " + showBean.getUnitIndex());

            holder.footerLessonView.setVisibility(View.INVISIBLE);
            holder.footerPicView.setVisibility(View.VISIBLE);
        } else {
            holder.headerPicView.setVisibility(View.VISIBLE);
            holder.headerLessonView.setVisibility(View.INVISIBLE);

            holder.footerLessonView.setVisibility(View.VISIBLE);
            holder.footerPicView.setVisibility(View.GONE);
            holder.footerLessonView.setText("Lesson " + showBean.getUnitIndex());
        }

        //设置默认的样式
        holder.wordView.setImageResource(R.drawable.ic_practise_locked);
        holder.listenView.setImageResource(R.drawable.ic_practise_locked);
        holder.evalView.setImageResource(R.drawable.ic_practise_locked);
        holder.practiseView.setImageResource(R.drawable.ic_practise_locked);
        holder.boxView.setImageResource(R.drawable.ic_practise_box_lock);

        //判断第一个路径是否显示
        if (position==0){
            holder.spacePath.setVisibility(View.INVISIBLE);
        }else {
            holder.spacePath.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < showBean.getPassList().size(); i++) {
            PractiseLineShowBean.PractisePassBean passBean = showBean.getPassList().get(i);

            if (passBean.isClick()) {
                switch (passBean.getShowType()) {
                    case PractiseLineShowBean.word:
                        holder.wordView.setImageResource(R.drawable.ic_practise_word);
                        break;
                    case PractiseLineShowBean.listen:
                        holder.listenView.setImageResource(R.drawable.ic_practise_listen);
                        break;
                    case PractiseLineShowBean.eval:
                        holder.evalView.setImageResource(R.drawable.ic_practise_eval);
                        break;
                    case PractiseLineShowBean.practise:
                        holder.practiseView.setImageResource(R.drawable.ic_practise_practise);
                        break;
                    case PractiseLineShowBean.box:
                        if (passBean.isPass()){
                            holder.boxView.setImageResource(R.drawable.ic_practise_box_open);
                        }else {
                            holder.boxView.setImageResource(R.drawable.ic_practise_box_lock);
                        }
                        break;
                }
            }
        }

        //点击操作
        holder.wordView.setOnClickListener(v->{
            if (onItemClickListener!=null){
                onItemClickListener.onItemClick(holder.wordView, showBean.getVoaId(), showBean.getUnitIndex(),showBean.getTitle(),showBean.getPassList().get(0),isNextLevelExist(position,showBean.getPassList()),position+1);
            }
        });
        holder.listenView.setOnClickListener(v->{
            if (onItemClickListener!=null){
                onItemClickListener.onItemClick(holder.listenView, showBean.getVoaId(), showBean.getUnitIndex(),showBean.getTitle(),showBean.getPassList().get(1),isNextLevelExist(position,showBean.getPassList()),position+1);
            }
        });
        holder.evalView.setOnClickListener(v->{
            if (onItemClickListener!=null){
                onItemClickListener.onItemClick(holder.evalView, showBean.getVoaId(), showBean.getUnitIndex(),showBean.getTitle(),showBean.getPassList().get(2),isNextLevelExist(position,showBean.getPassList()),position+1);
            }
        });
        holder.practiseView.setOnClickListener(v->{
            if (onItemClickListener!=null){
                onItemClickListener.onItemClick(holder.practiseView, showBean.getVoaId(), showBean.getUnitIndex(),showBean.getTitle(),showBean.getPassList().get(3),isNextLevelExist(position,showBean.getPassList()),position+1);
            }
        });
        holder.boxView.setOnClickListener(v->{
            if (onItemClickListener!=null){
                onItemClickListener.onItemClick(holder.boxView, showBean.getVoaId(), showBean.getUnitIndex(),showBean.getTitle(),showBean.getPassList().get(4),isNextLevelExist(position,showBean.getPassList()),position+1);
            }
        });

        //测试功能-点击课程牌跳转
        holder.headerLessonView.setOnClickListener(v->{
            //获取上一个或者下一个数据
            int lastPosition = position<=0?list.size()-1:position-1;
            boolean lastCheck = lastPosition==list.size()-1?true:false;

            int nextPosition = position>=list.size()-1?0:position+1;
            boolean nextCheck = nextPosition==0?true:false;

            if (onPositionJumpClickListener!=null){
                onPositionJumpClickListener.onClick(new Pair<>(lastCheck,lastPosition),new Pair<>(nextCheck,nextPosition));
            }
        });
        holder.footerLessonView.setOnClickListener(v->{
            //获取上一个或者下一个数据
            int lastPosition = position<=0?list.size()-1:position-1;
            boolean lastCheck = lastPosition==list.size()-1?true:false;

            int nextPosition = position>=list.size()-1?0:position+1;
            boolean nextCheck = nextPosition==0?true:false;

            if (onPositionJumpClickListener!=null){
                onPositionJumpClickListener.onClick(new Pair<>(lastCheck,lastPosition),new Pair<>(nextCheck,nextPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class LineHolder extends RecyclerView.ViewHolder {

        //上部
        private TextView headerLessonView;
        private ConstraintLayout headerPicView;

        private ImageView listenView;
        private ImageView evalView;

        //第一个路径
        private ImageView spacePath;

        //下部
        private TextView footerLessonView;
        private ConstraintLayout footerPicView;

        private ImageView wordView;
        private ImageView practiseView;
        private ImageView boxView;

        public LineHolder(ItemPractiseLineBinding binding) {
            super(binding.getRoot());

            headerLessonView = binding.rightText;
            headerPicView = binding.rightPic;
            footerLessonView = binding.leftText;
            footerPicView = binding.leftPic;

            spacePath = binding.spaceStart;

            listenView = binding.listenPractisePic;
            evalView = binding.evalPractisePic;
            wordView = binding.wordPractisePic;
            practiseView = binding.practisePractisePic;
            boxView = binding.boxPractisePic;
        }
    }

    //刷新数据
    public void refreshData(List<PractiseLineShowBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //接口
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view,int voaId, int unitIndex, String title, PractiseLineShowBean.PractisePassBean passBean,boolean isNextLevelExist,int nextLevelPosition);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //判断是否存在下个关卡
    private boolean isNextLevelExist(int position,List<PractiseLineShowBean.PractisePassBean> passList){
        if (position>=list.size()-1){
            return false;
        }

        //循环判断是否已经pass了
        boolean isNextLevel = true;
        for (int i = 0; i < passList.size(); i++) {
            PractiseLineShowBean.PractisePassBean passBean = passList.get(i);
            if (!passBean.isPass()){
                isNextLevel = false;
                break;
            }
        }

        return isNextLevel;
    }

    //测试功能
    private OnPositionJumpClickListener onPositionJumpClickListener;

    public interface OnPositionJumpClickListener{
        void onClick(Pair<Boolean,Integer> lastPair, Pair<Boolean,Integer> nextPair);
    }

    public void setOnPositionJumpClickListener(OnPositionJumpClickListener onPositionJumpClickListener) {
        this.onPositionJumpClickListener = onPositionJumpClickListener;
    }
}
