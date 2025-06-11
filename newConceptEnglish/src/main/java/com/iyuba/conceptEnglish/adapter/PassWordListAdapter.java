package com.iyuba.conceptEnglish.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.sqlite.op.WordErrorOP;
import com.iyuba.conceptEnglish.sqlite.op.WordPassUserOp;
import com.iyuba.conceptEnglish.util.AnimUtil;
import com.iyuba.core.common.data.model.VoaWord2;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by iyuba on 2018/11/7.
 */

public class PassWordListAdapter extends RecyclerView.Adapter {
    private List<VoaWord2> mWords;
    private Context mContext;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private WordPassUserOp wordPassUserOp;
    private WordErrorOP wordErrorOP;
    private boolean isGreaterThanFour = false;

    public PassWordListAdapter(Context context, List<VoaWord2> list) {
        this.mContext = context;
        this.mWords = list;
        wordPassUserOp = new WordPassUserOp(mContext);
        wordErrorOP = new WordErrorOP(mContext);
    }

    public boolean isGreaterThanFour() {
        return isGreaterThanFour;
    }

    public void setGreaterThanFour(boolean greaterThanFour) {
        isGreaterThanFour = greaterThanFour;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pass_word_collect, parent, false);
        RecyclerView.ViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final VoaWord2 wordBean = mWords.get(position);
        ((MyViewHolder) holder).txt_ch.setText(wordBean.def);
        ((MyViewHolder) holder).txt_jp.setText(wordBean.word);
        // 0: 错误  1：正确  2：未答题
        int wordStatus = 2;
        try {
//            int voaId=0;
//            if (isGreaterThanFour){
//                voaId= CommonUtils.bookTranslateServiceToLocalForPass(wordBean.bookId)*1000
//                        + Integer.parseInt(wordBean.unitId);
//            }else {
//                voaId=Integer.parseInt(wordBean.voaId);
//            }
            int voaId=Integer.parseInt(wordBean.voaId);
            wordStatus = wordPassUserOp.isError(voaId, wordBean.position);
            if (wordErrorOP.isError(voaId, wordBean.word)) {
                wordStatus = 0;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        switch (wordStatus) {
            case 0:
                ((MyViewHolder) holder).txt_jp.setTextColor(mContext.getResources().getColor(R.color.red));
                break;
            case 1:
                ((MyViewHolder) holder).txt_jp.setTextColor(mContext.getResources().getColor(R.color.green_71));
                break;
            case 2:
                ((MyViewHolder) holder).txt_jp.setTextColor(mContext.getResources().getColor(R.color.black));
                break;
        }

        if (!TextUtils.isEmpty(wordBean.pron) && !wordBean.pron.equals("null")) {
            String str = '[' + wordBean.pron + ']';
            try {
                ((MyViewHolder) holder).txtPron.setText(URLDecoder.decode(str, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                ((MyViewHolder) holder).txtPron.setText(str);
            }
        }
        ((MyViewHolder) holder).llCh.setOnClickListener(v -> AnimUtil.FlipAnimatorXViewShow(((MyViewHolder) holder).llCh, ((MyViewHolder) holder).llJp, 500));

        ((MyViewHolder) holder).llJp.setOnClickListener(v -> AnimUtil.FlipAnimatorXViewShow(((MyViewHolder) holder).llJp, ((MyViewHolder) holder).llCh, 500));
        ((MyViewHolder) holder).wordDetail.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClickListener(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWords == null ? 0 : mWords.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_jp)
        TextView txt_jp;
        @BindView(R.id.txt_ch)
        TextView txt_ch;
        @BindView(R.id.ll_jp)
        LinearLayout llJp;
        @BindView(R.id.ll_ch)
        LinearLayout llCh;
        @BindView(R.id.txt_pron)
        TextView txtPron;
        @BindView(R.id.img_detail)
        ImageView wordDetail;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClickListener(View holder, int position);
    }


}
