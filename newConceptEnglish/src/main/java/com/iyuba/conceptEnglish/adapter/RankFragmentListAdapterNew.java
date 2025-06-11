package com.iyuba.conceptEnglish.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.activity.CommentActivity;
import com.iyuba.conceptEnglish.fragment.RankFragment;
import com.iyuba.conceptEnglish.sqlite.mode.RankBean;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.thread.GitHubImageLoader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RankFragmentListAdapterNew extends RecyclerView.Adapter<RankFragmentListAdapterNew.RankAppViewHolder> {

    private Context mContext;
    private List<RankBean> rankUserList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Pattern p;
    private Matcher m;
    private String rankType;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RankFragmentListAdapterNew(Context mContext, List<RankBean> rankUserList, String rankType) {
        this.mContext = mContext;
        this.rankUserList = rankUserList;
        this.rankType = rankType;

    }


    @NonNull
    @Override
    public RankAppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RankAppViewHolder(LayoutInflater.from(mContext).inflate(R.layout.rank_fragment_info_item,viewGroup,false));

    }
    @Override
    public void onBindViewHolder(@NonNull RankAppViewHolder rankAppViewHolder, int position) {

            final RankBean ru = rankUserList.get(position);
            String firstChar;
            if (ru.getName() == null || "".equals(ru.getName()))
                ru.setName(ru.getUid());


            firstChar = getFirstChar(ru.getName());
            switch (ru.getRanking()) {
                case "1":
                    rankAppViewHolder.rankLogoText.setVisibility(View.INVISIBLE);
                    rankAppViewHolder.rankLogoImage.setVisibility(View.VISIBLE);
                    rankAppViewHolder.rankLogoImage.setImageResource(R.drawable.rank_gold);

                    if (ru.getImgSrc().equals("http://static1." + Constant.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                        rankAppViewHolder.userImage.setVisibility(View.INVISIBLE);
                        rankAppViewHolder.userImageText.setVisibility(View.VISIBLE);
                        p = Pattern.compile("[a-zA-Z]");
                        m = p.matcher(firstChar);
                        if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                            rankAppViewHolder.userImageText.setBackgroundResource(R.drawable.rank_blue);
                            rankAppViewHolder.userImageText.setText(firstChar);
                            rankAppViewHolder.userName.setText(ru.getName());

                            setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
                        } else {
                            rankAppViewHolder.userImageText.setBackgroundResource(R.drawable.rank_green);
                            rankAppViewHolder.userImageText.setText(firstChar);
                            rankAppViewHolder.userName.setText(ru.getName());

                            setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
                        }
                    } else {
                        rankAppViewHolder.userImage.setVisibility(View.VISIBLE);
                        rankAppViewHolder.userImageText.setVisibility(View.INVISIBLE);
                        GitHubImageLoader.getInstance().setRawPic(ru.getImgSrc(), rankAppViewHolder.userImage,
                                R.drawable.noavatar_small);
                        rankAppViewHolder.userName.setText(ru.getName());

                        setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
                    }
                    break;
                case "2":
                    rankAppViewHolder.rankLogoText.setVisibility(View.INVISIBLE);
                    rankAppViewHolder.rankLogoImage.setVisibility(View.VISIBLE);
                    rankAppViewHolder.rankLogoImage.setImageResource(R.drawable.rank_silvery);

                    if (ru.getImgSrc().equals("http://static1." + Constant.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                        rankAppViewHolder.userImage.setVisibility(View.INVISIBLE);
                        rankAppViewHolder.userImageText.setVisibility(View.VISIBLE);
                        p = Pattern.compile("[a-zA-Z]");
                        m = p.matcher(firstChar);
                        if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                            rankAppViewHolder.userImageText.setBackgroundResource(R.drawable.rank_blue);
                            rankAppViewHolder.userImageText.setText(firstChar);
                            rankAppViewHolder.userName.setText(ru.getName());
                            setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
                        } else {
                            rankAppViewHolder.userImageText.setBackgroundResource(R.drawable.rank_green);
                            rankAppViewHolder.userImageText.setText(firstChar);
                            rankAppViewHolder.userName.setText(ru.getName());

                            setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
                        }
                    } else {
                        rankAppViewHolder.userImage.setVisibility(View.VISIBLE);
                        rankAppViewHolder.userImageText.setVisibility(View.INVISIBLE);
                        GitHubImageLoader.getInstance().setRawPic(ru.getImgSrc(), rankAppViewHolder.userImage,
                                R.drawable.noavatar_small);
                        rankAppViewHolder.userName.setText(ru.getName());

                        setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
                    }
                    break;
                case "3":
                    rankAppViewHolder.rankLogoText.setVisibility(View.INVISIBLE);
                    rankAppViewHolder.rankLogoImage.setVisibility(View.VISIBLE);
                    rankAppViewHolder.rankLogoImage.setImageResource(R.drawable.rank_copper);

                    if (ru.getImgSrc().equals("http://static1." + Constant.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                        rankAppViewHolder.userImage.setVisibility(View.INVISIBLE);
                        rankAppViewHolder.userImageText.setVisibility(View.VISIBLE);
                        p = Pattern.compile("[a-zA-Z]");
                        m = p.matcher(firstChar);
                        if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                            rankAppViewHolder.userImageText.setBackgroundResource(R.drawable.rank_blue);
                            rankAppViewHolder.userImageText.setText(firstChar);
                            rankAppViewHolder.userName.setText(ru.getName());

                            setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
//                        userInfo.setText(  "News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                        userWords.setText("Words:" +ru.getWords());
                        } else {
                            rankAppViewHolder.userImageText.setBackgroundResource(R.drawable.rank_green);
                            rankAppViewHolder.userImageText.setText(firstChar);
                            rankAppViewHolder.userName.setText(ru.getName());
                            setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
//                        userInfo.setText("News:" + ru.getCnt() + "\nWPM:" + ru.getWpm());
//                        userWords.setText("Words:" + ru.getWords());
                        }
                    } else {
                        rankAppViewHolder.userImageText.setVisibility(View.INVISIBLE);
                        rankAppViewHolder.userImage.setVisibility(View.VISIBLE);
                        GitHubImageLoader.getInstance().setRawPic(ru.getImgSrc(), rankAppViewHolder.userImage,
                                R.drawable.noavatar_small);
                        rankAppViewHolder.userName.setText(ru.getName());

                        setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
                    }
                    break;
                case "4":
                    ;
                case "5":

                    rankAppViewHolder.rankLogoImage.setVisibility(View.INVISIBLE);
                    rankAppViewHolder.rankLogoText.setVisibility(View.VISIBLE);
                    rankAppViewHolder.rankLogoText.setText(ru.getRanking());
                    rankAppViewHolder.rankLogoText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    rankAppViewHolder.rankLogoText.setSingleLine(true);

                    if (ru.getImgSrc().equals("http://static1." + Constant.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                        rankAppViewHolder.userImage.setVisibility(View.INVISIBLE);
                        rankAppViewHolder.userImageText.setVisibility(View.VISIBLE);
                        p = Pattern.compile("[a-zA-Z]");
                        m = p.matcher(firstChar);
                        if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                            rankAppViewHolder.userImageText.setBackgroundResource(R.drawable.rank_blue);
                            rankAppViewHolder.userImageText.setText(firstChar);
                            rankAppViewHolder.userName.setText(ru.getName());


                            setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
                        } else {
                            rankAppViewHolder.userImageText.setBackgroundResource(R.drawable.rank_green);
                            rankAppViewHolder.userImageText.setText(firstChar);
                            rankAppViewHolder.userName.setText(ru.getName());

                            setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
                        }
                    } else {
                        rankAppViewHolder.userImageText.setVisibility(View.INVISIBLE);
                        rankAppViewHolder.userImage.setVisibility(View.VISIBLE);
                        GitHubImageLoader.getInstance().setRawPic(ru.getImgSrc(), rankAppViewHolder.userImage,
                                R.drawable.noavatar_small);
                        rankAppViewHolder.userName.setText(ru.getName());

                        setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
                    }
                    break;
                default:
                    rankAppViewHolder.rankLogoImage.setVisibility(View.INVISIBLE);
                    rankAppViewHolder.rankLogoText.setVisibility(View.VISIBLE);
                    rankAppViewHolder.rankLogoText.setText(ru.getRanking());
                    rankAppViewHolder.rankLogoText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    rankAppViewHolder.rankLogoText.setSingleLine(true);
                    rankAppViewHolder.rankLogoText.setSelected(true);
                    rankAppViewHolder.rankLogoText.setFocusable(true);
                    rankAppViewHolder.rankLogoText.setFocusableInTouchMode(true);
                    //抢占了item的焦点 不能点击

                    if (ru.getImgSrc().equals("http://static1." + Constant.IYUBA_CN + "uc_server/images/noavatar_middle.jpg")) {
                        rankAppViewHolder.userImage.setVisibility(View.INVISIBLE);
                        rankAppViewHolder.userImageText.setVisibility(View.VISIBLE);
                        p = Pattern.compile("[a-zA-Z]");
                        m = p.matcher(firstChar);
                        if (m.matches()) {
//                        Toast.makeText(Main.this,"输入的是字母", Toast.LENGTH_SHORT).show();
                            rankAppViewHolder.userImageText.setBackgroundResource(R.drawable.rank_blue);
                            rankAppViewHolder.userImageText.setText(firstChar);
                            rankAppViewHolder.userName.setText(ru.getName());

                            setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
                        } else {
                            rankAppViewHolder.userImageText.setBackgroundResource(R.drawable.rank_green);
                            rankAppViewHolder.userImageText.setText(firstChar);
                            rankAppViewHolder.userName.setText(ru.getName());

                            setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
                        }
                    } else {
                        rankAppViewHolder.userImageText.setVisibility(View.INVISIBLE);
                        rankAppViewHolder.userImage.setVisibility(View.VISIBLE);
                        GitHubImageLoader.getInstance().setRawPic(ru.getImgSrc(), rankAppViewHolder.userImage,
                                R.drawable.noavatar_small);
                        rankAppViewHolder.userName.setText(ru.getName());

                        setinfo(rankAppViewHolder.userInfo, rankAppViewHolder.userWords, ru);
                    }
                    break;

            }

        rankAppViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ("口语".equals(rankType)) {
                        Intent intent = new Intent();
                        intent.putExtra("uid", ru.getUid());
                        intent.putExtra("voaId", RankFragment.TAG_VOAID);
                        intent.putExtra("userName", ru.getName());
                        intent.putExtra("userPic", ru.getImgSrc());
                        intent.putExtra("type", type);
                        intent.setClass(mContext, CommentActivity.class);
                        mContext.startActivity(intent);
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


    private void setinfo(TextView tv1, TextView tv2, RankBean rankUser) {


        switch (rankType) {
            case "阅读":

                tv1.setText("文章数:" + rankUser.getCnt() + "\nWPM:" + rankUser.getWpm());
                tv2.setText("单词数:" + rankUser.getWords());

                break;
            case "听力":
                tv1.setText("文章数:" + rankUser.getTotalEssay() + "\n单词数:" + rankUser.getTotalWord());

                int min = Integer.valueOf(rankUser.getTotalTime()) / 60;
                tv2.setText(min + "分钟"); //学习时间
                break;
            case "口语":


                double scores_avg = 0;
                if (rankUser.getCount().equals("0")) {
                    scores_avg = 0;
                } else {

                    scores_avg = Double.valueOf(rankUser.getScores()) / Double.valueOf(rankUser.getCount());
                }

                DecimalFormat df = new DecimalFormat("0.00");

                tv1.setText("总分:" + rankUser.getScores() + "\n平均分:" + df.format(scores_avg));
                tv2.setText("句子数:" + rankUser.getCount());
                break;
            case "学习":

                tv1.setText("文章数:" + rankUser.getTotalEssay() + "\n单词数:" + rankUser.getTotalWord());

                double hour = Double.valueOf(rankUser.getTotalTime()) / 3600;
                DecimalFormat df2 = new DecimalFormat("0.00");

                tv2.setText(df2.format(hour) + "小时"); //学习时间
                break;
            case "测试":


                double right = 0;
                if (Double.valueOf(rankUser.getTotalTest()) == 0) {
                    right = 0;
                } else {
                    right = Double.valueOf(rankUser.getTotalRight()) / Double.valueOf(rankUser.getTotalTest());
                }


                DecimalFormat df3 = new DecimalFormat("0.00");

                tv2.setText("总题数:" + rankUser.getTotalTest());

                tv1.setText("正确数:" + rankUser.getTotalRight() + "\n正确率:" + df3.format(right));
                break;

        }
    }

    class RankAppViewHolder extends RecyclerView.ViewHolder{
        ImageView rankLogoImage,userImage;
        TextView rankLogoText,userImageText,userName,userInfo,userWords;

        public RankAppViewHolder(@NonNull View itemView) {
            super(itemView);

             rankLogoImage = itemView.findViewById(R.id.rank_logo_image);
             rankLogoText = itemView.findViewById(R.id.rank_logo_text);
             userImage = itemView.findViewById(R.id.user_image);
             userImageText = itemView.findViewById(R.id.user_image_text);
             userName = itemView.findViewById(R.id.rank_user_name);
             userInfo = itemView.findViewById(R.id.rank_user_info);
             userWords = itemView.findViewById(R.id.rank_user_words);
        }
    }

}
