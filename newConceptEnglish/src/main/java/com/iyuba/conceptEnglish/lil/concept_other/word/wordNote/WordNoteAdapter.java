package com.iyuba.conceptEnglish.lil.concept_other.word.wordNote;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.conceptEnglish.BuildConfig;
import com.iyuba.conceptEnglish.lil.fix.common_fix.model.remote.bean.Word_note;
import com.iyuba.lib.databinding.ItemWordLibBinding;
import com.iyuba.lib.databinding.ItemWordNoteBinding;

import java.util.List;

public class WordNoteAdapter extends RecyclerView.Adapter<WordNoteAdapter.NoteHolder> {

    private Context context;
    private List<Word_note.TempWord> list;

    //是否显示释义
    private boolean isShowDef = true;

    public WordNoteAdapter(Context context, List<Word_note.TempWord> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWordNoteBinding binding = ItemWordNoteBinding.inflate(LayoutInflater.from(context),parent,false);
        return new NoteHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        if (holder==null){
            return;
        }

        Word_note.TempWord tempWord = list.get(position);
        holder.wordView.setText(tempWord.word);
        if (TextUtils.isEmpty(tempWord.pronunciation)){
            holder.pornView.setText("");
        }else {
            holder.pornView.setText("["+tempWord.pronunciation+"]");
        }
        if (TextUtils.isEmpty(tempWord.definition)){
            holder.defView.setVisibility(View.GONE);
        }else {
            if (isShowDef){
                holder.defView.setVisibility(View.VISIBLE);
            }else {
                holder.defView.setVisibility(View.GONE);
            }
            holder.defView.setText(tempWord.definition);
        }
        holder.dateView.setText(tempWord.createDate);

        holder.playView.setOnClickListener(v->{
            if (onWordNoteClickListener!=null){
                onWordNoteClickListener.onPlayAudio(tempWord.audioUrl);
            }
        });
        /*holder.checkView.setOnClickListener(v->{

        });*/
        holder.itemView.setOnClickListener(v->{
            if (onWordNoteClickListener!=null){
                onWordNoteClickListener.onItemClick(tempWord);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (onWordNoteClickListener!=null){
                onWordNoteClickListener.onLongDeleteClick(tempWord);
            }

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class NoteHolder extends RecyclerView.ViewHolder{

        private ImageView checkView;
        private ImageView playView;
        private TextView wordView;
        private TextView pornView;
        private TextView defView;
        private TextView dateView;

        public NoteHolder(ItemWordNoteBinding binding){
            super(binding.getRoot());

            checkView = binding.checkBoxIsDelete;
            checkView.setVisibility(View.GONE);
            playView = binding.wordSpeaker;
            wordView = binding.wordKey;
            pornView = binding.wordPron;
            defView = binding.wordDef;
            dateView = binding.wordDate;

            if (BuildConfig.DEBUG){
                dateView.setVisibility(View.VISIBLE);
            }else {
                dateView.setVisibility(View.GONE);
            }
        }
    }

    //刷新数据
    public void refreshData(List<Word_note.TempWord> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //增加数据
    public void addData(List<Word_note.TempWord> addList){
        this.list.addAll(addList);
        notifyDataSetChanged();
    }

    //获取当前展示的单词
    public List<Word_note.TempWord> getShowList(){
        return list;
    }

    //刷新释义显示
    public void setShowDef(boolean showDef){
        this.isShowDef = showDef;
        notifyDataSetChanged();
    }

    //接口回调
    public OnWordNoteClickListener onWordNoteClickListener;
    public interface OnWordNoteClickListener{
        //点击item
        void onItemClick(Word_note.TempWord word);
        //播放
        void onPlayAudio(String playUrl);
        //长按删除
        void onLongDeleteClick(Word_note.TempWord word);
    }

    public void setOnWordNoteClickListener(OnWordNoteClickListener onWordNoteClickListener) {
        this.onWordNoteClickListener = onWordNoteClickListener;
    }
}
