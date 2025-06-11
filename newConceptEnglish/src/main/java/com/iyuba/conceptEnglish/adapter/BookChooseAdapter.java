//package com.iyuba.conceptEnglish.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.iyuba.ConstantNew;
//import com.iyuba.conceptEnglish.R;
//import com.iyuba.conceptEnglish.activity.BookChooseActivity;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BookChooseAdapter extends RecyclerView.Adapter<BookChooseAdapter.MyViewHolder> {
//
//    private List<BookChooseActivity.PhotoBean> mList = new ArrayList<>();
//
//    private Context mContext;
//
//    public BookChooseAdapter(List<BookChooseActivity.PhotoBean> mList, Context mContext) {
//        this.mList = mList;
//        this.mContext = mContext;
//    }
//
//    public void refreshData(List<BookChooseActivity.PhotoBean> mList){
//        this.mList = mList;
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.item_book_choose, parent, false);
//        MyViewHolder myViewHolder = new MyViewHolder(view);
//        if (listener !=null){
//            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onClick(v, myViewHolder.getAdapterPosition());
//                }
//            });
//        }
//        return myViewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        BookChooseActivity.PhotoBean bean = mList.get(position);
//        if (bean.getBookImageId() != -1) {
//            holder.bookTitle.setText(bean.getTitle());
//            Glide.with(mContext)
//                    .load(bean.getBookImageId())
//                    .into(holder.bookImage);
//        } else {
//            holder.bookTitle.setText(bean.getTitle());
//            Glide.with(mContext)
//                    .load(bean.getBookImageUrl())
//                    .error(R.drawable.failed_image)
//                    .into(holder.bookImage);
//        }
//
//        holder.showLayout.setVisibility(View.VISIBLE);
//        holder.tencentLayout.setVisibility(View.GONE);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mList.size();
//    }
//
//    static class MyViewHolder extends RecyclerView.ViewHolder {
//
//        private LinearLayout showLayout;
//        private ImageView bookImage;
//        private TextView bookTitle;
//
//        private RelativeLayout tencentLayout;
//        private TextView bookText;
//
//        public MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//            showLayout = itemView.findViewById(R.id.showLayout);
//            bookImage = itemView.findViewById(R.id.book_image);
//            bookTitle = itemView.findViewById(R.id.book_title);
//
//            tencentLayout = itemView.findViewById(R.id.tencentLayout);
//            bookText = itemView.findViewById(R.id.book_image_text);
//        }
//    }
//
//    private OnClickListener listener;
//
//    public void setOnClickListener(OnClickListener listener) {
//        this.listener = listener;
//    }
//
//
//    public interface OnClickListener {
//        void onClick(View view, int position);
//    }
//}
