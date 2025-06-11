package com.iyuba.conceptEnglish.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.entity.OfficialAccountListResponse;
import com.iyuba.conceptEnglish.widget.CircleImageView;
import com.iyuba.configation.Constant;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class OfficialAccountListAdapter extends RecyclerView.Adapter<OfficialAccountListAdapter.MyViewHolder> {

    private Context mContext;
    private List<OfficialAccountListResponse.AccountBean> mList;

    public OfficialAccountListAdapter(Context mContext, List<OfficialAccountListResponse.AccountBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_official_account, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=myViewHolder.getAdapterPosition();
                try {
                    String url=URLEncoder.encode(mList.get(position).getUrl(),"utf-8");
                    jumpToWechatAction(url);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OfficialAccountListResponse.AccountBean accountBean = mList.get(position);
        Glide.with(mContext)
                .load(accountBean.getImage_url())
                .error(R.drawable.load_failed)
                .into(holder.circleImageView);
//                .placeholder(R.drawable.loading)

        holder.titleTxt.setText(accountBean.getTitle());
        holder.remarkTxt.setText(String.format("%s   %s", accountBean.getNewsfrom(), accountBean.getCreateTime().substring(0, 10)));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView circleImageView;
        public TextView titleTxt;
        public TextView remarkTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.comment_image);
            titleTxt = itemView.findViewById(R.id.title_txt);
            remarkTxt = itemView.findViewById(R.id.remark_txt);

        }

    }

    private void jumpToWechatAction(String id) {
//        String appPayId = "wx6ce5ac6bcb03a302";
        String appPayId = Constant.getWxKey();
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, appPayId);
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = "gh_a8c17ad593be";
        req.path = "/pages/gzhDetails/gzhDetails?url=" + id;////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);
    }
}
