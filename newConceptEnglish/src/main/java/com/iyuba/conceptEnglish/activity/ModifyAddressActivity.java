package com.iyuba.conceptEnglish.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.protocol.ModifyAddressRequest;
import com.iyuba.conceptEnglish.protocol.ModifyAddressResponse;
import com.iyuba.conceptEnglish.protocol.UserPositionResponse;
import com.iyuba.conceptEnglish.protocol.UserPostionRequest;
import com.iyuba.conceptEnglish.sqlite.db.UserAddrInfo;
import com.iyuba.conceptEnglish.util.ExeProtocol;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.lil.user.UserInfoManager;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Setter;

/**
 * Created by ivotsm on 2017/3/1.
 * 图书网城-我的地址
 */

public class ModifyAddressActivity extends BasisActivity {
    private Context mContext;
    private UserAddrInfo userAddrInfo = new UserAddrInfo();
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.telephone)
    EditText phone;
    @BindView(R.id.qq)
    EditText qq;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.confirm)
    TextView confirm;
    @BindView(R.id.titlebar_back_button)
    ImageButton backBtn;
    @BindView(R.id.titlebar_title)
    TextView title;
    @BindView(R.id.titlebar_overflow_button)
    ImageButton overflowBtn;
    @BindView(R.id.tipsView)
    TextView tipsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_addr_modify);

        mContext = this;
        ButterKnife.bind(this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title.setText("我的地址");
        overflowBtn.setVisibility(View.GONE);

        handler.sendEmptyMessage(1);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2024/1/31 这里根据通管局的要求(历趣-fm包名-新概念群组)
                // TODO: 2024/1/31 需要将qq和邮箱不进行校验
//                if (name.getText().toString().equals("") || phone.getText().toString().equals("") || qq.getText().toString().equals("") || email.getText().toString().equals("") || address.getText().toString().equals("")) {
                if (name.getText().toString().equals("") || phone.getText().toString().equals("") || address.getText().toString().equals("")) {
                    Toast.makeText(mContext, "请完善个人信息", Toast.LENGTH_SHORT).show();
                } else {
                    userAddrInfo.name = name.getText().toString();
                    userAddrInfo.realName = name.getText().toString();
                    userAddrInfo.phone = phone.getText().toString();
                    userAddrInfo.qq = qq.getText().toString();
                    userAddrInfo.email = email.getText().toString();
                    userAddrInfo.address = address.getText().toString();

                    try {
                        ExeProtocol.exe(
                                new ModifyAddressRequest(userAddrInfo, String.valueOf(UserInfoManager.getInstance().getUserId())),
                                new ProtocolResponse() {

                                    @Override
                                    public void finish(BaseHttpResponse bhr) {
                                        ModifyAddressResponse response = (ModifyAddressResponse) bhr;
                                        if (response.result.equals("1")) {

                                            Intent intent = new Intent();
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("userInfo", userAddrInfo);
                                            intent.putExtra("userInfo", bundle);
                                            setResult(0, intent);
                                            handler.sendEmptyMessage(0);
                                        }
                                    }

                                    @Override
                                    public void error() {

                                    }
                                });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
            }
        });


        //显示信息
        String showTips = "(*)为必填项，其他为选填项";
        String tipsText = "(*)";
        int index = showTips.indexOf(tipsText);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(showTips);
        builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)),index,index+tipsText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tipsView.setText(builder);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(mContext,"修改成功!",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 1:
                    ExeProtocol.exe(
                            new UserPostionRequest(String.valueOf(UserInfoManager.getInstance().getUserId())),
                            new ProtocolResponse() {

                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    UserPositionResponse response = (UserPositionResponse) bhr;
                                    userAddrInfo = response.userInfo;
                                    handler.sendEmptyMessage(2);
                                }

                                @Override
                                public void error() {

                                }
                            });

                    break;
                case 2:
                    name.setText(userAddrInfo.realName);
                    phone.setText(userAddrInfo.phone);
                    qq.setText(userAddrInfo.qq);
                    email.setText(userAddrInfo.email);
                    address.setText(userAddrInfo.address);
                    break;
            }
        }
    };
}
