package com.iyuba.conceptEnglish.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.CommentActivity;
import com.iyuba.conceptEnglish.sqlite.mode.RankUser;
import com.iyuba.conceptEnglish.util.ConceptApplication;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.thread.GitHubImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.iyuba.headnewslib.adapter.ViewHolder;

/**
 * Created by Administrator on 2017/1/4.
 */

public class RankListAdapterNew extends RecyclerView.Adapter<RankListAdapterNew.RankViewHolder> {

    private Context mContext;
    private List<RankUser> rankUserList = new ArrayList<>();
    private Pattern p;
    private Matcher m;
    private String curVoaId;

    public RankListAdapterNew(Context mContext, List<RankUser> rankUserList) {
        this.mContext = mContext;
        this.rankUserList = rankUserList;
    }

    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RankViewHolder(LayoutInflater.from(mContext).inflate(R.layout.rank_info_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RankViewHolder rankViewHolder, @SuppressLint("RecyclerView") final int position) {

        RankUser ru = rankUserList.get(position);
        String firstChar;


        firstChar = getFirstChar(ru.getName());
        switch (ru.getRanking()) {
            case "1":
                rankViewHolder.rankLogoText.setVisibility(View.INVISIBLE);
                rankViewHolder.rankLogoImage.setVisibility(View.VISIBLE);
                rankViewHolder.rankLogoImage.setImageResource(R.drawable.rank_gold);

                if (ru.getImgSrc().equals("http://static1." + Constant.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                    rankViewHolder.userImage.setVisibility(View.INVISIBLE);
                    rankViewHolder.userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        rankViewHolder.userImageText.setBackgroundResource(R.drawable.rank_blue);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid());
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    } else {
                        rankViewHolder.userImageText.setBackgroundResource(R.drawable.rank_green);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid());
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    }
                } else {
                    rankViewHolder.userImage.setVisibility(View.VISIBLE);
                    rankViewHolder.userImageText.setVisibility(View.INVISIBLE);
                    GitHubImageLoader.getInstance().setRawPic(ru.getImgSrc(), rankViewHolder.userImage,
                            R.drawable.noavatar_small);
                    if (ru.getName() != null && !"".equals(ru.getName()))
                        rankViewHolder.userName.setText(ru.getName());
                    else
                        rankViewHolder.userName.setText(ru.getUid());
                    rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                }
                break;
            case "2":
                rankViewHolder.rankLogoText.setVisibility(View.INVISIBLE);
                rankViewHolder.rankLogoImage.setVisibility(View.VISIBLE);
                rankViewHolder.rankLogoImage.setImageResource(R.drawable.rank_silvery);

                if (ru.getImgSrc().equals("http://static1." + Constant.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                    rankViewHolder.userImage.setVisibility(View.INVISIBLE);
                    rankViewHolder.userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        rankViewHolder.userImageText.setBackgroundResource(R.drawable.rank_blue);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid());
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    } else {
                        rankViewHolder.userImageText.setBackgroundResource(R.drawable.rank_green);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid());
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    }
                } else {
                    rankViewHolder.userImage.setVisibility(View.VISIBLE);
                    rankViewHolder.userImageText.setVisibility(View.INVISIBLE);
                    GitHubImageLoader.getInstance().setRawPic(ru.getImgSrc(), rankViewHolder.userImage,
                            R.drawable.noavatar_small);
                    if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                            && !"null".equals(ru.getName()))
                        rankViewHolder.userName.setText(ru.getName());
                    else
                        rankViewHolder.userName.setText(ru.getUid());
                    rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                }
                break;
            case "3":
                rankViewHolder.rankLogoText.setVisibility(View.INVISIBLE);
                rankViewHolder.rankLogoImage.setVisibility(View.VISIBLE);
                rankViewHolder.rankLogoImage.setImageResource(R.drawable.rank_copper);

                if (ru.getImgSrc().equals("http://static1." + Constant.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                    rankViewHolder.userImage.setVisibility(View.INVISIBLE);
                    rankViewHolder.userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        rankViewHolder.userImageText.setBackgroundResource(R.drawable.rank_blue);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid());
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    } else {
                        rankViewHolder.userImageText.setBackgroundResource(R.drawable.rank_green);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid());
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    }
                } else {
                    rankViewHolder.userImageText.setVisibility(View.INVISIBLE);
                    rankViewHolder.userImage.setVisibility(View.VISIBLE);
                    GitHubImageLoader.getInstance().setRawPic(ru.getImgSrc(), rankViewHolder.userImage,
                            R.drawable.noavatar_small);
                    if (ru.getName() != null && !"".equals(ru.getName()))
                        rankViewHolder.userName.setText(ru.getName());
                    else
                        rankViewHolder.userName.setText(ru.getUid());
                    rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                }
                break;
            default:
                rankViewHolder.rankLogoImage.setVisibility(View.INVISIBLE);
                rankViewHolder.rankLogoText.setVisibility(View.VISIBLE);
                rankViewHolder.rankLogoText.setText(ru.getRanking());
                rankViewHolder.rankLogoText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                rankViewHolder.rankLogoText.setSingleLine(true);
                rankViewHolder.rankLogoText.setSelected(true);
//                rankLogoText.setFocusable(true);
//                rankLogoText.setFocusableInTouchMode(true);

                if (ru.getImgSrc().equals("http://static1." + Constant.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                    rankViewHolder.userImage.setVisibility(View.INVISIBLE);
                    rankViewHolder.userImageText.setVisibility(View.VISIBLE);
                    p = Pattern.compile("[a-zA-Z]");
                    m = p.matcher(firstChar);
                    if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                        rankViewHolder.userImageText.setBackgroundResource(R.drawable.rank_blue);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid());
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    } else {
                        rankViewHolder.userImageText.setBackgroundResource(R.drawable.rank_green);
                        rankViewHolder.userImageText.setText(firstChar);
                        if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                                && !"null".equals(ru.getName()))
                            rankViewHolder.userName.setText(ru.getName());
                        else
                            rankViewHolder.userName.setText(ru.getUid());
                        rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                    }
                } else {
                    rankViewHolder.userImageText.setVisibility(View.INVISIBLE);
                    rankViewHolder.userImage.setVisibility(View.VISIBLE);
                    GitHubImageLoader.getInstance().setRawPic(ru.getImgSrc(), rankViewHolder.userImage,
                            R.drawable.noavatar_small);
                    if (ru.getName() != null && !"".equals(ru.getName()) && !"none".equals(ru.getName())
                            && !"null".equals(ru.getName()))
                        rankViewHolder.userName.setText(ru.getName());
                    else
                        rankViewHolder.userName.setText(ru.getUid());
                    rankViewHolder.userInfo.setText("句子总数:" + ru.getCount() + "\n总分数:" + ru.getScores());
                }
                break;

        }
        rankViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RankUser rankUser = (RankUser) rankUserList.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("uid", rankUser.getUid());
                    intent.putExtra("voaId", curVoaId);
                    intent.putExtra("userName", rankUser.getName());
                    intent.putExtra("userPic", rankUser.getImgSrc());
                    intent.setClass(mContext, CommentActivity.class);
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return rankUserList.size();
    }

    private String getFirstChar(String name) {
        String subString;
        for (int i = 0; i < name.length(); i++) {
            subString = name.substring(i, i + 1);

            p = Pattern.compile("[0-9]*");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是数字", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[a-zA-Z]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                return subString;
            }

            p = Pattern.compile("[\u4e00-\u9fa5]");
            m = p.matcher(subString);
            if (m.matches()) {
//                Toast.makeText(Main.this,"输入的是汉字", Toast.LENGTH_SHORT).show();
                return subString;
            }
        }

        return "A";
    }


    class RankViewHolder extends RecyclerView.ViewHolder {
        ImageView rankLogoImage, userImage;
        TextView rankLogoText, userImageText, userName, userInfo;

        public RankViewHolder(@NonNull View itemView) {
            super(itemView);
            rankLogoImage = itemView.findViewById(R.id.rank_logo_image);
            rankLogoText = itemView.findViewById(R.id.rank_logo_text);
            userImage = itemView.findViewById(R.id.user_image);
            userImageText = itemView.findViewById(R.id.user_image_text);
            userName = itemView.findViewById(R.id.rank_user_name);
            userInfo = itemView.findViewById(R.id.rank_user_info);
        }
    }

    public void setCurVoaId(String curVoaId) {
        this.curVoaId = curVoaId;
    }

    //刷新数据
    public void refreshData(List<RankUser> refreshList){
        this.rankUserList = refreshList;
        notifyDataSetChanged();
    }
}
