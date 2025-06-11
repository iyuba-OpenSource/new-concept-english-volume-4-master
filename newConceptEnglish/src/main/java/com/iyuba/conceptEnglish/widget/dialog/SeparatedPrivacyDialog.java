package com.iyuba.conceptEnglish.widget.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.iyuba.conceptEnglish.R;
import com.iyuba.conceptEnglish.manager.sharedpreferences.InfoHelper;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.util.PrivacyUtil;


public class SeparatedPrivacyDialog {


    public AlertDialog showDialog(final Context context,OnAgreeListener listener) {
        String privacy1 = "1.为了更方便您使用我们的软件，我们会根据您使用的具体功能时申请必要的权限，如摄像头，存储权限，录音权限等。\n";
        String privacy2 = "2.使用本app需要您了解并同意";
        String privacy3 = "用户协议及隐私政策";
        String privacy4 = "，点击同意即代表您已阅读并同意该协议";
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(context, Web.class);
                String url = PrivacyUtil.getSeparatedProtocolUrl();
                intent.putExtra("url", url);
                intent.putExtra("title", "用户协议");
                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(R.color.colorPrimary));
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(context, Web.class);
                String url = PrivacyUtil.getSeparatedSecretUrl();
                intent.putExtra("url", url);
                intent.putExtra("title", "隐私政策");
                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(R.color.colorPrimary));
            }
        };
        //前提：privacy3="用户协议及隐私政策"
        int start = privacy1.length() + privacy2.length();
        int end = start + 4;
        int start2 = end + 1;
        int end2 = start2 + 4;

        SpannableStringBuilder strBuilder = new SpannableStringBuilder();
        strBuilder.append(privacy1);
        strBuilder.append(privacy2);
        strBuilder.append(privacy3);
        strBuilder.append(privacy4);
        strBuilder.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        strBuilder.setSpan(clickableSpan2, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .create();

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_privacy, null);
        dialog.setView(view);

        TextView textView = view.findViewById(R.id.text_link);

        textView.setText(strBuilder);

        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView agreeNo = view.findViewById(R.id.text_no_agree);
        TextView agree = view.findViewById(R.id.text_agree);

        agreeNo.setOnClickListener(v -> listener.onNoAgree());

        agree.setOnClickListener(v -> {
            InfoHelper.getInstance().setHidePrivacy(true);
            listener.onAgree();
            dialog.dismiss();
        });
        return dialog;

    }

    public interface OnAgreeListener{
        void onAgree();
        void onNoAgree();
    }
}
