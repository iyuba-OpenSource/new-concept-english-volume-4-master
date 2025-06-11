package com.iyuba.conceptEnglish.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.protocol.BookDetailRequest;
import com.iyuba.conceptEnglish.protocol.BookDetailResponse;
import com.iyuba.conceptEnglish.sqlite.mode.BookDetail;
import com.iyuba.conceptEnglish.sqlite.op.ShopCartOp;
import com.iyuba.conceptEnglish.util.ExeProtocol;
import com.iyuba.conceptEnglish.widget.BabyPopWindow;
import com.iyuba.conceptEnglish.widget.BadgeView;
import com.iyuba.core.common.activity.login.LoginUtil;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.lil.user.UserInfoManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 图书网城-详情界面
 * Created by ivotsm on 2017/2/22.
 */

public class BookDetailActivity extends BasisActivity {
    private Context mContext;
    private BookDetail bookDetail;
    private BabyPopWindow popWindow;
    private BadgeView commentBadge;
    private ShopCartOp shopCartOp;
    private int goodsNum = 0;

    @BindView(R.id.book_scroll)
    ScrollView bookScroll;
    @BindView(R.id.titlebar_back_button)
    ImageButton backBtn;
    @BindView(R.id.book)
    TextView book;
    @BindView(R.id.micro_class)
    TextView microClass;
    @BindView(R.id.app)
    TextView app;
    @BindView(R.id.edit_img)
    ImageView editImg;
    @BindView(R.id.edit_info)
    TextView editInfo;
    @BindView(R.id.total_price)
    TextView totalPrice;
    @BindView(R.id.origin_price)
    TextView originPrice;
    @BindView(R.id.book_name)
    TextView bookName;
    @BindView(R.id.author_name)
    TextView authorName;
    @BindView(R.id.press_name)
    TextView pressName;
    @BindView(R.id.desc)
    TextView desc;
    @BindView(R.id.content_img)
    ImageView contentImg;
    @BindView(R.id.content_info)
    TextView contentInfo;
    @BindView(R.id.author_img)
    ImageView authorImg;
    @BindView(R.id.class_img)
    ImageView classImg;
    @BindView(R.id.app_img)
    ImageView appImg;
    @BindView(R.id.book_introduction)
    TextView bookIntro;
    @BindView(R.id.micro_class_introduction)
    TextView microClassIntro;
    @BindView(R.id.app_introduction)
    TextView appIntro;
    @BindView(R.id.customer_service)
    ImageView customerService;
    @BindView(R.id.shop_cart)
    ImageView shopCart;
    @BindView(R.id.add_goods)
    TextView addGoods;
    @BindView(R.id.buy_now)
    TextView buyNow;
    @BindView(R.id.back_ground)
    LinearLayout backBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);

        mContext = this;
        ButterKnife.bind(this);

        shopCartOp = new ShopCartOp(mContext);

        if (UserInfoManager.getInstance().isLogin()) {
            goodsNum = 0;
        } else {
            goodsNum = shopCartOp.getUserCartNum(String.valueOf(UserInfoManager.getInstance().getUserId()));
        }


        commentBadge = new BadgeView(mContext);
        commentBadge.setTextSize(8);
        commentBadge.setBackground(7, Color.parseColor("#d3321b"));
        commentBadge.setText(String.valueOf(goodsNum));
        popWindow = new BabyPopWindow(this, String.valueOf(UserInfoManager.getInstance().getUserId()));
        String id = getIntent().getStringExtra("bookId");

        ExeProtocol.exe(
                new BookDetailRequest(id),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        BookDetailResponse response = (BookDetailResponse) bhr;
                        if (response.result.equals("1")) {
                            bookDetail = response.bookDetail;
                            handler.sendEmptyMessage(0);
                        }
                    }

                    @Override
                    public void error() {

                    }
                });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookScroll.scrollTo(0, (int) bookIntro.getY());
                book.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
                microClass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                app.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            }
        });
        microClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookScroll.scrollTo(0, (int) microClassIntro.getY());
                book.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                microClass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
                app.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            }
        });
        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookScroll.scrollTo(0, (int) appIntro.getY());
                book.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                microClass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                app.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
            }
        });

        addGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!UserInfoManager.getInstance().isLogin()) {
                    LoginUtil.startToLogin(mContext);
                } else {
                    popWindow.setValue(bookDetail, backBg);
                    backBg.setVisibility(View.VISIBLE);
                    popWindow.showAsDropDown(view);
                }
            }
        });

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!UserInfoManager.getInstance().isLogin()) {
                    LoginUtil.startToLogin(mContext);
                } else {
                    bookDetail.num = 1;
                    ArrayList<BookDetail> bookDetails = new ArrayList<BookDetail>();
                    bookDetails.add(bookDetail);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("books", bookDetails);
                    Intent intent = new Intent();
                    intent.putExtra("books", bundle);
                    intent.setClass(mContext, OrderConfirmActivity.class);
                    startActivity(intent);
                }
            }
        });

        shopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserInfoManager.getInstance().isLogin()) {
                    LoginUtil.startToLogin(mContext);
                } else {
                    startActivity(new Intent(mContext, ShopCartActivity.class));
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Log.d("退出显示", this.getClass().getName());
            }
        });

        customerService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "mqqwpa://im/chat?chat_type=wpa&uin=";
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url + "572828703")));
            }
        });

        commentBadge.setTargetView(shopCart);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    GitHubImageLoader.getInstance().setRawPic(bookDetail.getEditImg(), editImg,
                            R.drawable.failed_image);
                    editInfo.setText(bookDetail.editInfo);
                    totalPrice.setText("¥" + bookDetail.totalPrice);
                    bookName.setText(bookDetail.name);
                    authorName.setText(bookDetail.bookAuthor);
                    pressName.setText(bookDetail.publishHouse);
                    desc.setText(bookDetail.desc);
                    GitHubImageLoader.getInstance().setRawPic(bookDetail.contentImg, contentImg,
                            R.drawable.failed_image);
                    contentInfo.setText(bookDetail.contentInfo);
                    GitHubImageLoader.getInstance().setRawPic(bookDetail.authorImg, authorImg,
                            R.drawable.failed_image);
                    GitHubImageLoader.getInstance().setRawPic(bookDetail.classImg, classImg,
                            R.drawable.failed_image);
                    GitHubImageLoader.getInstance().setRawPic(bookDetail.appImg, appImg,
                            R.drawable.failed_image);
//                    bookScroll.scrollTo(0, 200);
                    break;
            }
        }
    };
}
